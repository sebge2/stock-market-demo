import { AUTH_URL, getAccessToken } from './auth';

/** Connected user, as returned by the user service. */
export interface Profile {
  /** Login name. */
  username: string;
  /** Display name. */
  fullName: string;
  /** Email address. */
  email: string;
  /** Avatar (PNG encoded in base64), if any. */
  avatar: string | null;
}

/** Loads the profile of the connected user. */
export async function fetchProfile(): Promise<Profile> {
  const response = await fetch(`${AUTH_URL}/profile`, {
    headers: { Authorization: `Bearer ${getAccessToken() ?? ''}` },
  });
  if (!response.ok) {
    throw new Error(`Erreur ${response.status} lors du chargement du profil`);
  }
  return (await response.json()) as Profile;
}

/** Returns the initials of a name (first letters of the first two words). */
export function initialsOf(name: string): string {
  return name
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0])
    .join('')
    .toUpperCase();
}
