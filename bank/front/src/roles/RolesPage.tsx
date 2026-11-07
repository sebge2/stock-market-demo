import { type FormEvent, useEffect, useMemo, useState } from 'react';
import { ApiError } from '../employees/employees.api';
import { formatDate } from '../employees/employees.data';
import {
  type Permission,
  type PermissionChanges,
  type Role,
  type RoleChanges,
  type RoleStatus,
  createPermission,
  createRole,
  deletePermission,
  deleteRole,
  describeError,
  fetchPermissions,
  fetchRoles,
  updateRole,
} from './roles.api';

/** Selectable role statuses. */
const STATUSES: RoleStatus[] = ['Actif', 'Inactif'];

/** Props of the roles and permissions screen. */
interface RolesPageProps {
  /** Section displayed: the roles or the permissions. */
  section: 'roles' | 'permissions';
  /** Called when the user service rejects the access token. */
  onUnauthorized: () => void;
}

/** Formats a timestamp returned by the API as a date. */
function shortDate(timestamp: string): string {
  return formatDate(timestamp.slice(0, 10));
}

/** Plus icon of the action buttons. */
function PlusIcon() {
  return (
    <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" aria-hidden="true">
      <path d="M10 4v12M4 10h12" />
    </svg>
  );
}

/** Form used to create or edit a role (its permissions are managed from the role detail). */
function RoleForm({ title, initial, onSubmit, onCancel }: { title: string; initial: RoleChanges; onSubmit: (c: RoleChanges) => Promise<void>; onCancel: () => void }) {
  const [name, setName] = useState(initial.name);
  const [description, setDescription] = useState(initial.description);
  const [status, setStatus] = useState(initial.status);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const submit = (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    onSubmit({ ...initial, name: name.trim(), description: description.trim(), status }).catch((e: Error) => {
      setError(describeError(e, 'enregistrer ce rôle'));
      setSaving(false);
    });
  };

  return (
    <form aria-label={title} className="panel form" onSubmit={submit}>
      <h2>{title}</h2>
      {error && <div role="alert" className="alert">{error}</div>}
      <div className="form-grid">
        <label className="form-field">
          <span className="field-label">Nom</span>
          <input required maxLength={100} value={name} onChange={(e) => setName(e.target.value)} />
        </label>
        <label className="form-field">
          <span className="field-label">Statut</span>
          <select value={status} onChange={(e) => setStatus(e.target.value as RoleStatus)}>
            {STATUSES.map((s) => (
              <option key={s}>{s}</option>
            ))}
          </select>
        </label>
        <label className="form-field">
          <span className="field-label">Description</span>
          <input maxLength={500} value={description} onChange={(e) => setDescription(e.target.value)} />
        </label>
      </div>
      <div className="form-actions">
        <button type="button" className="btn-secondary" onClick={onCancel} disabled={saving}>Annuler</button>
        <button type="submit" className="btn-primary" disabled={saving || name.trim() === ''}>{saving ? 'Enregistrement…' : 'Enregistrer'}</button>
      </div>
    </form>
  );
}

/** Form used to create a permission. */
function PermissionForm({ onSubmit, onCancel }: { onSubmit: (c: PermissionChanges) => Promise<void>; onCancel: () => void }) {
  const [code, setCode] = useState('');
  const [description, setDescription] = useState('');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const submit = (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    onSubmit({ code: code.trim().toUpperCase(), description: description.trim() }).catch((e: Error) => {
      setError(describeError(e, 'créer cette permission'));
      setSaving(false);
    });
  };

  return (
    <form aria-label="Nouvelle permission" className="panel form" onSubmit={submit}>
      <h2>Nouvelle permission</h2>
      {error && <div role="alert" className="alert">{error}</div>}
      <div className="form-grid">
        <label className="form-field">
          <span className="field-label">Code</span>
          <input required maxLength={100} placeholder="EMPLOYEE_READ" value={code} onChange={(e) => setCode(e.target.value)} />
        </label>
        <label className="form-field">
          <span className="field-label">Description</span>
          <input maxLength={500} value={description} onChange={(e) => setDescription(e.target.value)} />
        </label>
      </div>
      <div className="form-actions">
        <button type="button" className="btn-secondary" onClick={onCancel} disabled={saving}>Annuler</button>
        <button type="submit" className="btn-primary" disabled={saving || code.trim() === ''}>{saving ? 'Enregistrement…' : 'Créer'}</button>
      </div>
    </form>
  );
}

/** Roles and permissions screen: roles list with the permissions of the selected role, and permissions list. */
export function RolesPage({ section, onUnauthorized }: RolesPageProps) {
  const [roles, setRoles] = useState<Role[]>([]);
  const [permissions, setPermissions] = useState<Permission[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [form, setForm] = useState<'new-role' | 'edit-role' | 'new-permission' | null>(null);
  const [toAdd, setToAdd] = useState('');

  useEffect(() => {
    Promise.all([fetchRoles(), fetchPermissions()])
      .then(([loadedRoles, loadedPermissions]) => {
        setRoles(loadedRoles);
        setPermissions(loadedPermissions);
        setSelectedId((current) => current ?? loadedRoles[0]?.id ?? null);
      })
      .catch((e: Error) => {
        if (e instanceof ApiError && e.status === 401) {
          onUnauthorized();
        }
        setError(describeError(e, 'consulter les rôles et les permissions'));
      })
      .finally(() => setLoading(false));
  }, [onUnauthorized]);

  const selected = roles.find((r) => r.id === selectedId);
  const permissionsById = useMemo(() => new Map(permissions.map((p) => [p.id, p])), [permissions]);
  const granted = selected ? selected.permissionIds.map((id) => permissionsById.get(id)).filter((p): p is Permission => p !== undefined) : [];
  const available = selected ? permissions.filter((p) => !selected.permissionIds.includes(p.id)) : [];

  /** Runs a call and displays its failure. */
  const run = (action: string, task: () => Promise<void>) => {
    setError(null);
    task().catch((e: Error) => setError(describeError(e, action)));
  };

  /** Replaces the permissions of the selected role. */
  const setRolePermissions = (permissionIds: string[]) => {
    if (!selected) {
      return;
    }
    run('modifier les permissions de ce rôle', async () => {
      const updated = await updateRole(selected.id, { ...selected, permissionIds });
      setRoles((current) => current.map((r) => (r.id === updated.id ? updated : r)));
      setToAdd('');
    });
  };

  return (
    <>
      <header className="page-header">
        <div className="page-title">
          <div className="eyebrow">Administration</div>
          <h1>Rôles et permissions</h1>
        </div>
        {section === 'roles' ? (
          <button type="button" className="btn-primary" disabled={form !== null} onClick={() => setForm('new-role')}>
            <PlusIcon />
            Nouveau rôle
          </button>
        ) : (
          <button type="button" className="btn-primary" disabled={form !== null} onClick={() => setForm('new-permission')}>
            <PlusIcon />
            Nouvelle permission
          </button>
        )}
      </header>

      {error && <div role="alert" className="alert">{error}</div>}

      <div className="tabs tabs-flat">
        <a href="#roles" aria-current={section === 'roles' ? 'page' : undefined} className={`tab${section === 'roles' ? ' tab-active' : ''}`}>
          Rôles<span className="tab-count">{roles.length}</span>
        </a>
        <a href="#roles/permissions" aria-current={section === 'permissions' ? 'page' : undefined} className={`tab${section === 'permissions' ? ' tab-active' : ''}`}>
          Permissions<span className="tab-count">{permissions.length}</span>
        </a>
      </div>

      {form === 'new-role' && (
        <RoleForm
          title="Nouveau rôle"
          initial={{ name: '', description: '', status: 'Actif', permissionIds: [] }}
          onSubmit={async (changes) => {
            const created = await createRole(changes);
            setRoles((current) => [...current, created]);
            setSelectedId(created.id);
            setForm(null);
          }}
          onCancel={() => setForm(null)}
        />
      )}
      {form === 'new-permission' && (
        <PermissionForm
          onSubmit={async (changes) => {
            const created = await createPermission(changes);
            setPermissions((current) => [...current, created]);
            setForm(null);
          }}
          onCancel={() => setForm(null)}
        />
      )}

      {section === 'roles' ? (
        <>
          <section aria-label="Liste des rôles" className="card" aria-busy={loading}>
            <div className="table-scroll">
              <div className="table">
                <div className="row row-roles row-head">
                  <div>Rôle</div>
                  <div>Description</div>
                  <div className="cell-right">Permissions</div>
                  <div className="cell-right">Créé le</div>
                  <div className="cell-right">Mis à jour le</div>
                  <div>Statut</div>
                </div>
                {roles.map((r) => (
                  <button
                    key={r.id}
                    type="button"
                    aria-pressed={r.id === selectedId}
                    className={`row row-roles row-body row-button${r.id === selectedId ? ' row-selected' : ''}`}
                    onClick={() => {
                      setSelectedId(r.id);
                      setForm(null);
                    }}
                  >
                    <div className="person-name">{r.name}</div>
                    <div>{r.description}</div>
                    <div className="cell-right mono">{r.permissionIds.length}</div>
                    <div className="cell-right mono">{shortDate(r.createdAt)}</div>
                    <div className="cell-right mono">{shortDate(r.updatedAt)}</div>
                    <div>
                      <span className={`badge ${r.status === 'Actif' ? 'badge-active' : 'badge-inactive'}`}>{r.status}</span>
                    </div>
                  </button>
                ))}
                {loading && <div className="empty">Chargement…</div>}
                {!loading && roles.length === 0 && <div className="empty">Aucun rôle.</div>}
              </div>
            </div>
          </section>

          {selected && (
            <section aria-labelledby="role-title" className="panel-stack">
              {form === 'edit-role' ? (
                <RoleForm
                  title="Modifier le rôle"
                  initial={selected}
                  onSubmit={async (changes) => {
                    const updated = await updateRole(selected.id, changes);
                    setRoles((current) => current.map((r) => (r.id === updated.id ? updated : r)));
                    setForm(null);
                  }}
                  onCancel={() => setForm(null)}
                />
              ) : (
                <div className="panel">
                  <div className="page-header">
                    <div className="role-heading">
                      <h2 id="role-title">{selected.name}</h2>
                      <span className={`badge ${selected.status === 'Actif' ? 'badge-active' : 'badge-inactive'}`}>{selected.status}</span>
                    </div>
                    <div className="form-actions">
                      <button type="button" className="btn-outline" disabled={form !== null} onClick={() => setForm('edit-role')}>Modifier le rôle</button>
                      <button
                        type="button"
                        className="btn-secondary"
                        onClick={() => {
                          if (!window.confirm(`Supprimer le rôle « ${selected.name} » ?`)) {
                            return;
                          }
                          run('supprimer ce rôle', async () => {
                            await deleteRole(selected.id);
                            setRoles((current) => current.filter((r) => r.id !== selected.id));
                            setSelectedId(null);
                          });
                        }}
                      >
                        Supprimer
                      </button>
                    </div>
                  </div>
                  <div className="field-grid">
                    <div className="field"><div className="field-label">Description</div><div className="field-value">{selected.description || '—'}</div></div>
                    <div className="field"><div className="field-label">Statut</div><div className="field-value">{selected.status}</div></div>
                    <div className="field"><div className="field-label">Créé le</div><div className="field-value">{shortDate(selected.createdAt)}</div></div>
                    <div className="field"><div className="field-label">Mis à jour le</div><div className="field-value">{shortDate(selected.updatedAt)}</div></div>
                  </div>
                </div>
              )}

              <div className="page-header">
                <h3 className="section-title">Permissions associées <span className="mono">({granted.length})</span></h3>
                <div className="form-actions">
                  <select aria-label="Permission à associer" value={toAdd} onChange={(e) => setToAdd(e.target.value)}>
                    <option value="">Choisir une permission…</option>
                    {available.map((p) => (
                      <option key={p.id} value={p.id}>{p.code}</option>
                    ))}
                  </select>
                  <button type="button" className="btn-primary" disabled={toAdd === ''} onClick={() => setRolePermissions([...selected.permissionIds, toAdd])}>
                    <PlusIcon />
                    Associer une permission
                  </button>
                </div>
              </div>

              <div className="card">
                <div className="table-scroll">
                  <div className="table">
                    <div className="row row-perms row-head">
                      <div>Code</div>
                      <div>Description</div>
                      <div className="cell-right">Créée le</div>
                      <div className="cell-right">Mise à jour le</div>
                      <div />
                    </div>
                    {granted.map((p) => (
                      <div key={p.id} className="row row-perms row-body">
                        <div className="mono">{p.code}</div>
                        <div>{p.description}</div>
                        <div className="cell-right mono">{shortDate(p.createdAt)}</div>
                        <div className="cell-right mono">{shortDate(p.updatedAt)}</div>
                        <div className="cell-right">
                          <button type="button" className="btn-secondary" aria-label={`Retirer ${p.code}`} onClick={() => setRolePermissions(selected.permissionIds.filter((id) => id !== p.id))}>
                            Retirer
                          </button>
                        </div>
                      </div>
                    ))}
                    {granted.length === 0 && <div className="empty">Aucune permission associée à ce rôle.</div>}
                  </div>
                </div>
              </div>
            </section>
          )}
        </>
      ) : (
        <section aria-label="Liste des permissions" className="card" aria-busy={loading}>
          <div className="table-scroll">
            <div className="table">
              <div className="row row-perms row-head">
                <div>Code</div>
                <div>Description</div>
                <div className="cell-right">Créée le</div>
                <div className="cell-right">Mise à jour le</div>
                <div />
              </div>
              {permissions.map((p) => (
                <div key={p.id} className="row row-perms row-body">
                  <div className="mono">{p.code}</div>
                  <div>{p.description}</div>
                  <div className="cell-right mono">{shortDate(p.createdAt)}</div>
                  <div className="cell-right mono">{shortDate(p.updatedAt)}</div>
                  <div className="cell-right">
                    <button
                      type="button"
                      className="btn-secondary"
                      aria-label={`Supprimer ${p.code}`}
                      onClick={() => {
                        if (!window.confirm(`Supprimer la permission « ${p.code} » ?`)) {
                          return;
                        }
                        run('supprimer cette permission', async () => {
                          await deletePermission(p.id);
                          setPermissions((current) => current.filter((x) => x.id !== p.id));
                        });
                      }}
                    >
                      Supprimer
                    </button>
                  </div>
                </div>
              ))}
              {loading && <div className="empty">Chargement…</div>}
              {!loading && permissions.length === 0 && <div className="empty">Aucune permission.</div>}
            </div>
          </div>
        </section>
      )}
    </>
  );
}
