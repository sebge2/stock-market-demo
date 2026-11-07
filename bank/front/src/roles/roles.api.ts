import { AUTH_URL, getAccessToken } from '../auth/auth';
import { ApiError } from '../employees/employees.api';

/** Status of a role. */
export type RoleStatus = 'Actif' | 'Inactif';

/** Role as displayed by the screens. */
export interface Role {
  /** Role identifier. */
  id: string;
  /** Role name. */
  name: string;
  /** Role description. */
  description: string;
  /** Role status. */
  status: RoleStatus;
  /** Identifiers of the permissions of the role. */
  permissionIds: string[];
  /** Creation date (ISO format). */
  createdAt: string;
  /** Last update date (ISO format). */
  updatedAt: string;
}

/** Permission as displayed by the screens. */
export interface Permission {
  /** Permission identifier. */
  id: string;
  /** Permission code. */
  code: string;
  /** Permission description. */
  description: string;
  /** Creation date (ISO format). */
  createdAt: string;
  /** Last update date (ISO format). */
  updatedAt: string;
}

/** Data entered to create or edit a role. */
export interface RoleChanges {
  /** Role name. */
  name: string;
  /** Role description. */
  description: string;
  /** Role status. */
  status: RoleStatus;
  /** Identifiers of the permissions of the role. */
  permissionIds: string[];
}

/** Data entered to create or edit a permission. */
export interface PermissionChanges {
  /** Permission code. */
  code: string;
  /** Permission description. */
  description: string;
}

/** Role as returned by the API. */
interface RoleApi {
  id: string;
  name: string;
  description: string | null;
  status: 'ACTIVE' | 'INACTIVE';
  permissionIds: string[];
  createdAt: string;
  updatedAt: string;
}

/** Permission as returned by the API. */
interface PermissionApi {
  id: string;
  code: string;
  description: string | null;
  createdAt: string;
  updatedAt: string;
}

/** Calls the user service with the current access token and returns the JSON body, if any. */
async function call<T>(method: 'GET' | 'POST' | 'PUT' | 'DELETE', path: string, body?: unknown): Promise<T> {
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
  return (response.status === 204 ? undefined : await response.json()) as T;
}

/** Converts a role returned by the API. */
function toRole(role: RoleApi): Role {
  return {
    id: role.id,
    name: role.name,
    description: role.description ?? '',
    status: role.status === 'ACTIVE' ? 'Actif' : 'Inactif',
    permissionIds: role.permissionIds,
    createdAt: role.createdAt,
    updatedAt: role.updatedAt,
  };
}

/** Converts a permission returned by the API. */
function toPermission(permission: PermissionApi): Permission {
  return { ...permission, description: permission.description ?? '' };
}

/** Converts the data of a role to its API representation. */
function toRoleRequest(changes: RoleChanges) {
  return {
    name: changes.name,
    description: changes.description,
    status: changes.status === 'Actif' ? 'ACTIVE' : 'INACTIVE',
    permissionIds: changes.permissionIds,
  };
}

/** Loads all the roles. */
export async function fetchRoles(): Promise<Role[]> {
  return (await call<RoleApi[]>('GET', '/roles')).map(toRole);
}

/** Creates a role. */
export async function createRole(changes: RoleChanges): Promise<Role> {
  return toRole(await call<RoleApi>('POST', '/roles', toRoleRequest(changes)));
}

/** Updates a role (the permissions are replaced by the given ones). */
export async function updateRole(id: string, changes: RoleChanges): Promise<Role> {
  return toRole(await call<RoleApi>('PUT', `/roles/${id}`, toRoleRequest(changes)));
}

/** Deletes a role. */
export async function deleteRole(id: string): Promise<void> {
  await call<void>('DELETE', `/roles/${id}`);
}

/** Loads all the permissions. */
export async function fetchPermissions(): Promise<Permission[]> {
  return (await call<PermissionApi[]>('GET', '/permissions')).map(toPermission);
}

/** Creates a permission. */
export async function createPermission(changes: PermissionChanges): Promise<Permission> {
  return toPermission(await call<PermissionApi>('POST', '/permissions', changes));
}

/** Updates a permission. */
export async function updatePermission(id: string, changes: PermissionChanges): Promise<Permission> {
  return toPermission(await call<PermissionApi>('PUT', `/permissions/${id}`, changes));
}

/** Deletes a permission. */
export async function deletePermission(id: string): Promise<void> {
  await call<void>('DELETE', `/permissions/${id}`);
}

/** Message displayed for a failed call of the user service. */
export function describeError(e: Error, action: string): string {
  if (e instanceof ApiError) {
    if (e.status === 403) {
      return `Vous n'avez pas la permission de ${action}.`;
    }
    if (e.status === 400) {
      return 'Les informations saisies ne sont pas valides.';
    }
    if (e.status === 409) {
      return 'Cet élément existe déjà ou est encore utilisé.';
    }
  }
  return e.message;
}
