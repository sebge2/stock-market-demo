/** Base URL of the OAuth2 authorization server (bank user service). */
const AUTH_URL = import.meta.env.VITE_AUTH_URL ?? 'http://localhost:9000';
/** Public OAuth2 client identifier of this frontend. */
const CLIENT_ID = 'bank-frontend';
/** Scopes requested at login. */
const SCOPES = 'openid profile employee.read';
/** Path on which the authorization server redirects back after login. */
export const CALLBACK_PATH = '/auth/callback';

const VERIFIER_KEY = 'oauth.verifier';
const STATE_KEY = 'oauth.state';
const TOKEN_KEY = 'oauth.token';

/** Stored access token. */
interface StoredToken {
  /** Bearer access token. */
  accessToken: string;
  /** Expiry timestamp in milliseconds. */
  expiresAt: number;
}

/** Encodes bytes as base64url. */
function base64Url(bytes: Uint8Array): string {
  return btoa(String.fromCharCode(...bytes)).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
}

/** Returns a random base64url string. */
function randomString(): string {
  return base64Url(crypto.getRandomValues(new Uint8Array(32)));
}

/** Returns the redirect URI registered for this frontend. */
function redirectUri(): string {
  return `${window.location.origin}${CALLBACK_PATH}`;
}

/** Returns the current valid access token, or null when the user is not logged in. */
export function getAccessToken(): string | null {
  const raw = sessionStorage.getItem(TOKEN_KEY);
  if (!raw) {
    return null;
  }
  const token = JSON.parse(raw) as StoredToken;
  if (token.expiresAt <= Date.now()) {
    sessionStorage.removeItem(TOKEN_KEY);
    return null;
  }
  return token.accessToken;
}

/** Redirects to the authorization server (authorization code flow with PKCE). */
export async function login(): Promise<void> {
  const verifier = randomString();
  const state = randomString();
  const challenge = base64Url(new Uint8Array(await crypto.subtle.digest('SHA-256', new TextEncoder().encode(verifier))));
  sessionStorage.setItem(VERIFIER_KEY, verifier);
  sessionStorage.setItem(STATE_KEY, state);
  const params = new URLSearchParams({
    response_type: 'code',
    client_id: CLIENT_ID,
    redirect_uri: redirectUri(),
    scope: SCOPES,
    state,
    code_challenge: challenge,
    code_challenge_method: 'S256',
  });
  window.location.assign(`${AUTH_URL}/oauth2/authorize?${params}`);
}

/** Clears the local session. */
export function logout(): void {
  sessionStorage.removeItem(TOKEN_KEY);
}

/** Exchanges the authorization code found in the current URL for an access token. */
export async function completeLogin(): Promise<void> {
  const params = new URLSearchParams(window.location.search);
  const code = params.get('code');
  const verifier = sessionStorage.getItem(VERIFIER_KEY);
  const expectedState = sessionStorage.getItem(STATE_KEY);
  sessionStorage.removeItem(VERIFIER_KEY);
  sessionStorage.removeItem(STATE_KEY);
  if (!code || !verifier || params.get('state') !== expectedState) {
    throw new Error('Connexion invalide : réessayez.');
  }
  const response = await fetch(`${AUTH_URL}/oauth2/token`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams({
      grant_type: 'authorization_code',
      client_id: CLIENT_ID,
      code,
      redirect_uri: redirectUri(),
      code_verifier: verifier,
    }),
  });
  if (!response.ok) {
    throw new Error(`Échec de la connexion (${response.status}).`);
  }
  const body = (await response.json()) as { access_token: string; expires_in: number };
  const token: StoredToken = { accessToken: body.access_token, expiresAt: Date.now() + body.expires_in * 1000 };
  sessionStorage.setItem(TOKEN_KEY, JSON.stringify(token));
}
