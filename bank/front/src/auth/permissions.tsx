import { type ReactNode, createContext, useContext } from 'react';

/** Permissions granted to the connected user. */
const PermissionsContext = createContext<ReadonlySet<string>>(new Set());

/** Provides the permissions of the connected user to the screens below. */
export function PermissionsProvider({ permissions, children }: { permissions: ReadonlySet<string>; children: ReactNode }) {
  return <PermissionsContext.Provider value={permissions}>{children}</PermissionsContext.Provider>;
}

/** Returns a function telling whether the connected user has a permission. */
export function useCan(): (permission: string) => boolean {
  const permissions = useContext(PermissionsContext);
  return (permission) => permissions.has(permission);
}
