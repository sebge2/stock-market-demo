import { useEffect, useState } from 'react';
import { type Role, describeError, fetchRoles } from '../roles/roles.api';
import { fetchUserRoleIds, saveUserRoleIds } from '../users/users.api';

/** Props of the roles panel of an employee. */
interface EmployeeRolesPanelProps {
  /** Identifier of the user account of the employee. */
  userId: string;
}

/** Panel listing the roles granted to an employee, with the possibility to grant or revoke roles. */
export function EmployeeRolesPanel({ userId }: EmployeeRolesPanelProps) {
  const [roles, setRoles] = useState<Role[]>([]);
  const [roleIds, setRoleIds] = useState<string[]>([]);
  const [toAdd, setToAdd] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    setError(null);
    Promise.all([fetchRoles(), fetchUserRoleIds(userId)])
      .then(([loadedRoles, loadedIds]) => {
        setRoles(loadedRoles);
        setRoleIds(loadedIds);
      })
      .catch((e: Error) => setError(describeError(e, 'consulter les rôles de cet employé')))
      .finally(() => setLoading(false));
  }, [userId]);

  const granted = roles.filter((r) => roleIds.includes(r.id));
  const available = roles.filter((r) => r.status === 'Actif' && !roleIds.includes(r.id));

  /** Saves the new set of roles of the employee. */
  const save = (next: string[]) => {
    setSaving(true);
    setError(null);
    saveUserRoleIds(userId, next)
      .then((saved) => {
        setRoleIds(saved);
        setToAdd('');
      })
      .catch((e: Error) => setError(describeError(e, 'modifier les rôles de cet employé')))
      .finally(() => setSaving(false));
  };

  return (
    <section aria-labelledby="t-roles" className="panel panel-stack" aria-busy={loading}>
      <div className="page-header">
        <h2 id="t-roles">Rôles <span className="mono">({granted.length})</span></h2>
        <div className="form-actions">
          <select aria-label="Rôle à associer" value={toAdd} disabled={saving || loading} onChange={(e) => setToAdd(e.target.value)}>
            <option value="">Choisir un rôle…</option>
            {available.map((r) => (
              <option key={r.id} value={r.id}>{r.name}</option>
            ))}
          </select>
          <button type="button" className="btn-primary" disabled={toAdd === '' || saving} onClick={() => save([...roleIds, toAdd])}>
            Associer un rôle
          </button>
        </div>
      </div>
      {error && <div role="alert" className="alert">{error}</div>}
      <div className="table-scroll">
        <div className="table">
          <div className="row row-perms row-head">
            <div>Rôle</div>
            <div>Description</div>
            <div className="cell-right">Permissions</div>
            <div>Statut</div>
            <div />
          </div>
          {granted.map((r) => (
            <div key={r.id} className="row row-perms row-body">
              <div className="person-name">{r.name}</div>
              <div>{r.description}</div>
              <div className="cell-right mono">{r.permissionIds.length}</div>
              <div>
                <span className={`badge ${r.status === 'Actif' ? 'badge-active' : 'badge-inactive'}`}>{r.status}</span>
              </div>
              <div className="cell-right">
                <button type="button" className="btn-secondary" disabled={saving} aria-label={`Retirer ${r.name}`} onClick={() => save(roleIds.filter((id) => id !== r.id))}>
                  Retirer
                </button>
              </div>
            </div>
          ))}
          {loading && <div className="empty">Chargement…</div>}
          {!loading && granted.length === 0 && <div className="empty">Aucun rôle associé à cet employé.</div>}
        </div>
      </div>
    </section>
  );
}
