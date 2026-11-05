import { AUTH_URL, getAccessToken } from '../auth/auth';
import { ApiError } from '../employees/employees.api';

/** User account as returned by the user service. */
interface UserApi {
  id: string;
  username: string;
  fullName: string;
  email: string;
  roleIds: string[];
  avatar: string | null;
}

/** Largest source image accepted for an avatar (the server reduces it to 128x128 pixels). */
export const MAX_AVATAR_BYTES = 5 * 1024 * 1024;

/** Calls the user service with the current access token. */
async function call(method: 'GET' | 'PUT', path: string, body?: unknown): Promise<UserApi> {
  const response = await fetch(`${AUTH_URL}${path}`, {
    method,
    headers: {
      Authorization: `Bearer ${getAccessToken() ?? ''}`,
      ...(body === undefined ? {} : { 'Content-Type': 'application/json' }),
    },
    body: body === undefined ? undefined : JSON.stringify(body),
  });
  if (!response.ok) {
    throw new ApiError(response.status, `Erreur ${response.status} lors de l'appel à ${path}`);
  }
  return (await response.json()) as UserApi;
}

/** Loads the avatar (base64 PNG) of a user, or null when there is none. */
export async function fetchAvatar(userId: string): Promise<string | null> {
  return (await call('GET', `/users/${userId}`)).avatar;
}

/**
 * Replaces the avatar of a user and returns the stored one (reduced to 128x128 pixels by the server).
 * The source image is a base64 string; an empty string removes the avatar.
 */
export async function saveAvatar(userId: string, avatar: string): Promise<string | null> {
  const user = await call('GET', `/users/${userId}`);
  const saved = await call('PUT', `/users/${userId}`, {
    username: user.username,
    fullName: user.fullName,
    email: user.email,
    roleIds: user.roleIds,
    avatar,
  });
  return saved.avatar;
}

/** Reads a file as a base64 string (without the data URI prefix). */
export function readAsBase64(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(String(reader.result).split(',')[1] ?? '');
    reader.onerror = () => reject(new Error('Lecture du fichier impossible.'));
    reader.readAsDataURL(file);
  });
}
