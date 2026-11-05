import { getAccessToken } from '../auth/auth';
import { ApiError } from '../employees/employees.api';

/** Base URL of the client service. */
const CLIENT_API_URL = import.meta.env.VITE_CLIENT_API_URL ?? 'http://localhost:8084/api/client';
/** Page size used when loading all clients (the service accepts 100 at most). */
const PAGE_SIZE = 100;

/** Kind of client. */
export type ClientType = 'Particulier' | 'Entreprise';

/** Status of a client. */
export type ClientStatus = 'Actif' | 'Inactif';

/** Client row displayed in the list. */
export interface Client {
  /** Client identifier. */
  id: string;
  /** Identifier of the user account of the client, if any. */
  userId: string | null;
  /** Display name. */
  name: string;
  /** First name (empty for a company). */
  firstName: string;
  /** Last name, or company name. */
  lastName: string;
  /** Kind of client. */
  type: ClientType;
  /** Creation date (ISO format). */
  since: string;
  /** Client status. */
  status: ClientStatus;
}

/** Client as returned by the API. */
interface ClientApi {
  id: string;
  type: 'INDIVIDUAL' | 'COMPANY';
  status: 'ACTIVE' | 'INACTIVE';
  firstName: string | null;
  lastName: string | null;
  userId: string | null;
  createdAt: string;
}

/** Page of results as returned by the API. */
interface PageApi<T> {
  content: T[];
  last: boolean;
}

/** Data entered to edit a client. */
export interface ClientChanges {
  /** Kind of client. */
  type: ClientType;
  /** Client status. */
  status: ClientStatus;
  /** First name. */
  firstName: string;
  /** Last name. */
  lastName: string;
  /** Email address, required on creation (used to create the user account). */
  email?: string;
}

/** Maps a client returned by the API to the displayed model. */
function toClient(c: ClientApi): Client {
  return {
    id: c.id,
    userId: c.userId,
    firstName: c.firstName ?? '',
    lastName: c.lastName ?? '',
    name: [c.firstName, c.lastName].filter(Boolean).join(' ') || '—',
    type: c.type === 'COMPANY' ? 'Entreprise' : 'Particulier',
    since: c.createdAt.slice(0, 10),
    status: c.status === 'ACTIVE' ? 'Actif' : 'Inactif',
  };
}

/** Calls the client service with the current access token. */
async function get<T>(path: string): Promise<T> {
  const response = await fetch(`${CLIENT_API_URL}${path}`, {
    headers: { Authorization: `Bearer ${getAccessToken() ?? ''}` },
  });
  if (!response.ok) {
    throw new ApiError(response.status, `Erreur ${response.status} lors de l'appel à ${path}`);
  }
  return (await response.json()) as T;
}

/** Loads all clients. */
export async function fetchClients(): Promise<Client[]> {
  const clients: ClientApi[] = [];
  for (let page = 0; ; page++) {
    const result = await get<PageApi<ClientApi>>(`/clients?page=${page}&size=${PAGE_SIZE}`);
    clients.push(...result.content);
    if (result.last) {
      break;
    }
  }
  return clients.map(toClient);
}

/** Updates a client and returns it. */
export async function updateClient(id: string, changes: ClientChanges): Promise<Client> {
  const response = await fetch(`${CLIENT_API_URL}/clients/${id}`, {
    method: 'PUT',
    headers: { Authorization: `Bearer ${getAccessToken() ?? ''}`, 'Content-Type': 'application/json' },
    body: JSON.stringify({
      type: changes.type === 'Entreprise' ? 'COMPANY' : 'INDIVIDUAL',
      status: changes.status === 'Actif' ? 'ACTIVE' : 'INACTIVE',
      firstName: changes.firstName,
      lastName: changes.lastName,
    }),
  });
  if (!response.ok) {
    throw new ApiError(response.status, `Erreur ${response.status} lors de la modification du client`);
  }
  return toClient((await response.json()) as ClientApi);
}

/** Creates a client (and its user account in the user service) and returns it. */
export async function createClient(changes: ClientChanges): Promise<Client> {
  const response = await fetch(`${CLIENT_API_URL}/clients`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${getAccessToken() ?? ''}`, 'Content-Type': 'application/json' },
    body: JSON.stringify({
      type: changes.type === 'Entreprise' ? 'COMPANY' : 'INDIVIDUAL',
      status: changes.status === 'Actif' ? 'ACTIVE' : 'INACTIVE',
      firstName: changes.firstName,
      lastName: changes.lastName,
      email: changes.email,
    }),
  });
  if (!response.ok) {
    throw new ApiError(response.status, `Erreur ${response.status} lors de la création du client`);
  }
  return toClient((await response.json()) as ClientApi);
}
