import { type FormEvent, useEffect, useMemo, useState } from 'react';
import { useCan } from '../auth/permissions';
import {
  ApiError,
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
  updatePermission,
  updateRole,
} from './roles.api';

/** Identifier of the role being created (not saved yet). */
const NEW_ROLE = 'new';

/** Props of the roles and permissions screen. */
interface RolesPageProps {
  /** Called when the user service rejects the access token. */
  onUnauthorized: () => void;
}

/** Edited permission: `null` creates a new one. */
type EditedPermission = { id: string | null; changes: PermissionChanges };

/** Formats a timestamp as a long French date. */
const longDate = (timestamp: string) =>
  new Date(timestamp).toLocaleDateString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric' });

/** Formats a timestamp as a long French date with time. */
const dateTime = (timestamp: string) =>
  new Date(timestamp).toLocaleString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });

/** Plural-aware permission counter. */
const permissionCount = (count: number) => `${count} PERMISSION${count > 1 ? 'S' : ''}`;

/** Icon wrapper shared by the screen buttons. */
function Icon({ size = 18, children }: { size?: number; children: React.ReactNode }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      {children}
    </svg>
  );
}

/** Status pill of a role. */
function StatusPill({ status }: { status: RoleStatus }) {
  return (
    <span className="sx-pill">
      <span className={`sx-dot${status === 'Actif' ? '' : ' sx-dot-off'}`} />
      {status}
    </span>
  );
}

/** Form used to create or edit a permission. */
function PermissionForm({ edited, onSubmit, onCancel }: { edited: EditedPermission; onSubmit: (c: PermissionChanges) => Promise<void>; onCancel: () => void }) {
  const [code, setCode] = useState(edited.changes.code);
  const [description, setDescription] = useState(edited.changes.description);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const title = edited.id ? 'Modifier la permission' : 'Nouvelle permission';

  const submit = (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    onSubmit({ code: code.trim().toUpperCase(), description: description.trim() }).catch((e: Error) => {
      setError(describeError(e, 'enregistrer cette permission'));
      setSaving(false);
    });
  };

  return (
    <form aria-label={title} className="sx-card" onSubmit={submit}>
      <h2 className="sx-card-title">{title}</h2>
      {error && <div role="alert" className="sx-alert">{error}</div>}
      <div className="sx-form-grid">
        <div className="sx-field">
          <label htmlFor="p-code">Code</label>
          <input id="p-code" className="sx-input" required maxLength={100} placeholder="MARKET_VIEW" value={code} onChange={(e) => setCode(e.target.value)} />
        </div>
        <div className="sx-field">
          <label htmlFor="p-desc">Description</label>
          <input id="p-desc" className="sx-input" maxLength={500} value={description} onChange={(e) => setDescription(e.target.value)} />
        </div>
      </div>
      <div className="sx-card-foot">
        <button type="button" className="sx-btn sx-btn-ghost" onClick={onCancel} disabled={saving}>Annuler</button>
        <button type="submit" className="sx-btn sx-btn-primary" disabled={saving || code.trim() === ''}>{saving ? 'Enregistrement…' : 'Enregistrer'}</button>
      </div>
    </form>
  );
}

/** Roles and permissions screen: list of roles, edition of the selected role and association of permissions. */
export function RolesPage({ onUnauthorized }: RolesPageProps) {
  const can = useCan();
  const [roles, setRoles] = useState<Role[]>([]);
  const [permissions, setPermissions] = useState<Permission[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [query, setQuery] = useState('');
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [draft, setDraft] = useState<RoleChanges>({ name: '', description: '', status: 'Actif', permissionIds: [] });
  const [saving, setSaving] = useState(false);
  const [edited, setEdited] = useState<EditedPermission | null>(null);

  const selected = roles.find((r) => r.id === selectedId);
  const creating = selectedId === NEW_ROLE;

  /** Loads the draft of the form from a saved role. */
  const draftOf = (role: Role): RoleChanges => ({
    name: role.name,
    description: role.description,
    status: role.status,
    permissionIds: role.permissionIds,
  });

  useEffect(() => {
    Promise.all([can('ROLE_LIST') ? fetchRoles() : Promise.resolve([]), can('PERMISSION_LIST') ? fetchPermissions() : Promise.resolve([])])
      .then(([loadedRoles, loadedPermissions]) => {
        setRoles(loadedRoles);
        setPermissions(loadedPermissions);
        if (loadedRoles[0]) {
          setSelectedId(loadedRoles[0].id);
          setDraft(draftOf(loadedRoles[0]));
        }
      })
      .catch((e: Error) => {
        if (e instanceof ApiError && e.status === 401) {
          onUnauthorized();
        }
        setError(describeError(e, 'consulter les rôles et les permissions'));
      })
      .finally(() => setLoading(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [onUnauthorized]);

  const visibleRoles = useMemo(() => {
    const needle = query.trim().toLowerCase();
    return roles.filter((r) => needle === '' || r.name.toLowerCase().includes(needle) || r.description.toLowerCase().includes(needle));
  }, [roles, query]);

  const canEdit = creating ? can('ROLE_CREATE') : can('ROLE_UPDATE');
  const dirty =
    creating ||
    (selected !== undefined &&
      (draft.name !== selected.name ||
        draft.description !== selected.description ||
        draft.status !== selected.status ||
        draft.permissionIds.length !== selected.permissionIds.length ||
        draft.permissionIds.some((id) => !selected.permissionIds.includes(id))));

  /** Runs a call and displays its failure. */
  const run = (action: string, task: () => Promise<void>) => {
    setError(null);
    return task().catch((e: Error) => setError(describeError(e, action)));
  };

  const select = (role: Role) => {
    setSelectedId(role.id);
    setDraft(draftOf(role));
    setEdited(null);
  };

  const startCreation = () => {
    setSelectedId(NEW_ROLE);
    setDraft({ name: '', description: '', status: 'Actif', permissionIds: [] });
    setEdited(null);
  };

  const save = () => {
    setSaving(true);
    void run('enregistrer ce rôle', async () => {
      const saved = creating ? await createRole(draft) : await updateRole(selected!.id, draft);
      setRoles((current) => (creating ? [...current, saved] : current.map((r) => (r.id === saved.id ? saved : r))));
      setSelectedId(saved.id);
      setDraft(draftOf(saved));
    }).finally(() => setSaving(false));
  };

  const togglePermission = (id: string) =>
    setDraft((current) => ({
      ...current,
      permissionIds: current.permissionIds.includes(id) ? current.permissionIds.filter((p) => p !== id) : [...current.permissionIds, id],
    }));

  return (
    <>
      <header className="sx-page-header">
        <div className="sx-page-title">
          <div className="sx-crumb">Administration / Rôles et permissions</div>
          <h1>Rôles et permissions</h1>
        </div>
        <div className="sx-actions">
          {can('PERMISSION_CREATE') && (
            <button type="button" className="sx-btn sx-btn-secondary" onClick={() => setEdited({ id: null, changes: { code: '', description: '' } })}>
              <Icon><path d="M12 5v14 M5 12h14" /></Icon>
              Nouvelle permission
            </button>
          )}
          {can('ROLE_CREATE') && (
            <button type="button" className="sx-btn sx-btn-primary" onClick={startCreation}>
              <Icon><path d="M12 5v14 M5 12h14" /></Icon>
              Nouveau rôle
            </button>
          )}
        </div>
      </header>

      {error && <div role="alert" className="sx-alert">{error}</div>}

      <div className="sx-columns">
        <div className="sx-aside-col">
          <section aria-label="Liste des rôles" className="sx-card" aria-busy={loading}>
            <div className="sx-card-head">
              <h2 className="sx-card-title">Rôles</h2>
              <span className="sx-count">{roles.length}</span>
            </div>
            <div className="sx-search">
              <label htmlFor="q-roles" className="sx-sr-only">Rechercher un rôle</label>
              <span className="sx-search-icon"><Icon size={20}><circle cx="11" cy="11" r="6.5" /><path d="M20 20l-4.2-4.2" /></Icon></span>
              <input id="q-roles" className="sx-input" type="search" placeholder="Rechercher un rôle" value={query} onChange={(e) => setQuery(e.target.value)} />
            </div>
            <div className="sx-role-list">
              {visibleRoles.map((r) => (
                <button key={r.id} type="button" className="sx-role" aria-pressed={r.id === selectedId} onClick={() => select(r)}>
                  <span className="sx-role-top">
                    <span className="sx-role-name">{r.name}</span>
                    <StatusPill status={r.status} />
                  </span>
                  <span className="sx-role-desc">{r.description || '—'}</span>
                  <span className="sx-role-meta">
                    <span>{permissionCount(r.permissionIds.length)}</span>
                    <span>MODIFIÉ LE {longDate(r.updatedAt).toUpperCase()}</span>
                  </span>
                </button>
              ))}
              {loading && <div className="sx-empty">Chargement…</div>}
              {!loading && visibleRoles.length === 0 && <div className="sx-empty">Aucun rôle.</div>}
            </div>
          </section>
        </div>

        <div className="sx-detail-col">
          {edited && (
            <PermissionForm
              key={edited.id ?? 'new'}
              edited={edited}
              onCancel={() => setEdited(null)}
              onSubmit={async (changes) => {
                if (edited.id) {
                  const updated = await updatePermission(edited.id, changes);
                  setPermissions((current) => current.map((p) => (p.id === updated.id ? updated : p)));
                } else {
                  const created = await createPermission(changes);
                  setPermissions((current) => [...current, created]);
                }
                setEdited(null);
              }}
            />
          )}

          {(selected || creating) && (
            <>
              <section aria-label="Rôle" className="sx-card">
                <div className="sx-card-head">
                  <span className="sx-detail-title">{creating ? 'Nouveau rôle' : selected!.name}</span>
                  {!creating && <StatusPill status={selected!.status} />}
                </div>
                <div className="sx-form-grid">
                  <div className="sx-field">
                    <label htmlFor="r-nom">Nom du rôle</label>
                    <input id="r-nom" className="sx-input" maxLength={100} readOnly={!canEdit} value={draft.name} onChange={(e) => setDraft({ ...draft, name: e.target.value })} />
                  </div>
                  <div className="sx-field">
                    <span className="sx-field-label">Statut</span>
                    <div role="group" aria-label="Statut du rôle" className="sx-toggle">
                      {(['Actif', 'Inactif'] as RoleStatus[]).map((s) => (
                        <button key={s} type="button" aria-pressed={draft.status === s} disabled={!canEdit} onClick={() => setDraft({ ...draft, status: s })}>
                          {s}
                        </button>
                      ))}
                    </div>
                  </div>
                  <div className="sx-field sx-form-wide">
                    <label htmlFor="r-desc">Description</label>
                    <input id="r-desc" className="sx-input" maxLength={500} readOnly={!canEdit} value={draft.description} onChange={(e) => setDraft({ ...draft, description: e.target.value })} />
                  </div>
                  {selected && (
                    <>
                      <div className="sx-field">
                        <label htmlFor="r-cr">Date de création</label>
                        <input id="r-cr" className="sx-input sx-input-readonly" readOnly value={dateTime(selected.createdAt)} />
                      </div>
                      <div className="sx-field">
                        <label htmlFor="r-up">Dernière modification</label>
                        <input id="r-up" className="sx-input sx-input-readonly" readOnly value={dateTime(selected.updatedAt)} />
                      </div>
                    </>
                  )}
                </div>
              </section>

              <section aria-label="Permissions associées" className="sx-card">
                <div className="sx-card-head">
                  <div className="sx-field">
                    <h2 className="sx-card-title">Permissions associées</h2>
                    <span className="sx-hint">
                      {draft.permissionIds.length} permission{draft.permissionIds.length > 1 ? 's' : ''} sur {permissions.length} {draft.permissionIds.length > 1 ? 'sont associées' : 'est associée'} à ce rôle.
                    </span>
                  </div>
                </div>
                <div className="sx-table-wrap">
                  <table className="sx-table">
                    <caption>Permissions et association au rôle</caption>
                    <thead>
                      <tr>
                        <th scope="col" />
                        <th scope="col">Code</th>
                        <th scope="col">Description</th>
                        <th scope="col">Créée le</th>
                        <th scope="col">Modifiée le</th>
                        <th scope="col" />
                      </tr>
                    </thead>
                    <tbody>
                      {permissions.map((p) => {
                        const on = draft.permissionIds.includes(p.id);
                        return (
                          <tr key={p.id} className={on ? 'sx-row-on' : undefined}>
                            <td>
                              <input type="checkbox" aria-label={`Associer ${p.code} au rôle`} checked={on} disabled={!canEdit} onChange={() => togglePermission(p.id)} />
                            </td>
                            <th scope="row"><span className="sx-code">{p.code}</span></th>
                            <td>{p.description}</td>
                            <td className="sx-date">{longDate(p.createdAt)}</td>
                            <td className="sx-date">{longDate(p.updatedAt)}</td>
                            <td>
                              <div className="sx-row-actions">
                                {can('PERMISSION_UPDATE') && (
                                  <button type="button" className="sx-icon-btn" aria-label={`Modifier ${p.code}`} onClick={() => setEdited({ id: p.id, changes: { code: p.code, description: p.description } })}>
                                    <Icon size={20}><path d="M4 20h4L19 9l-4-4L4 16z" /><path d="M13.5 6.5l4 4" /></Icon>
                                  </button>
                                )}
                                {can('PERMISSION_DELETE') && (
                                  <button
                                    type="button"
                                    className="sx-icon-btn"
                                    aria-label={`Supprimer ${p.code}`}
                                    onClick={() => {
                                      if (!window.confirm(`Supprimer la permission « ${p.code} » ?`)) {
                                        return;
                                      }
                                      void run('supprimer cette permission', async () => {
                                        await deletePermission(p.id);
                                        setPermissions((current) => current.filter((x) => x.id !== p.id));
                                        setDraft((current) => ({ ...current, permissionIds: current.permissionIds.filter((id) => id !== p.id) }));
                                        setRoles((current) => current.map((r) => ({ ...r, permissionIds: r.permissionIds.filter((id) => id !== p.id) })));
                                      });
                                    }}
                                  >
                                    <Icon size={20}><path d="M4 7h16" /><path d="M9 7V4h6v3" /><path d="M6 7l1 13h10l1-13" /></Icon>
                                  </button>
                                )}
                              </div>
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                  {permissions.length === 0 && !loading && <div className="sx-empty">Aucune permission.</div>}
                </div>
                <div className="sx-card-foot">
                  {selected && can('ROLE_DELETE') && (
                    <button
                      type="button"
                      className="sx-btn sx-btn-danger"
                      onClick={() => {
                        if (!window.confirm(`Supprimer le rôle « ${selected.name} » ?`)) {
                          return;
                        }
                        void run('supprimer ce rôle', async () => {
                          await deleteRole(selected.id);
                          const remaining = roles.filter((r) => r.id !== selected.id);
                          setRoles(remaining);
                          if (remaining[0]) {
                            select(remaining[0]);
                          } else {
                            setSelectedId(null);
                          }
                        });
                      }}
                    >
                      Supprimer le rôle
                    </button>
                  )}
                  {canEdit && (
                    <>
                      <button
                        type="button"
                        className="sx-btn sx-btn-ghost"
                        disabled={saving || !dirty}
                        onClick={() => (creating ? (roles[0] ? select(roles[0]) : setSelectedId(null)) : select(selected!))}
                      >
                        Annuler
                      </button>
                      <button type="button" className="sx-btn sx-btn-primary" disabled={saving || draft.name.trim() === '' || !dirty} onClick={save}>
                        <Icon><circle cx="12" cy="12" r="9" /><path d="M8 12.5l3 3 5-6" /></Icon>
                        {saving ? 'Enregistrement…' : creating ? 'Créer le rôle' : 'Enregistrer le rôle'}
                      </button>
                    </>
                  )}
                </div>
              </section>
            </>
          )}
        </div>
      </div>
    </>
  );
}
