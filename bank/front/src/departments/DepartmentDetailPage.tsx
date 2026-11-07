import { useMemo, useState } from 'react';
import type { Department, NewDepartment } from '../employees/employees.api';
import { type Employee, STATUS_CLASS, formatDate, initials } from '../employees/employees.data';
import { DepartmentForm } from './DepartmentForm';

/** Props of the department sheet. */
interface DepartmentDetailPageProps {
  /** The displayed department, or undefined when not found. */
  department: Department | undefined;
  /** All departments, used to compute the display code. */
  departments: Department[];
  /** All employees. */
  employees: Employee[];
  /** Whether data is being loaded. */
  loading: boolean;
  /** Saves the changes of the department; rejects when the update fails. */
  onUpdate: (id: string, department: NewDepartment) => Promise<void>;
}

/** Department sheet: identity header, headcount and list of the department employees. */
export function DepartmentDetailPage({ department, departments, employees, loading, onUpdate }: DepartmentDetailPageProps) {
  const [editing, setEditing] = useState(false);
  const members = useMemo(
    () => employees.filter((e) => e.departmentId === department?.id),
    [employees, department],
  );

  if (!department) {
    return (
      <>
        <nav aria-label="Fil d'Ariane" className="breadcrumb">
          <a href="#departments">Départements</a>
        </nav>
        <div className="panel">{loading ? 'Chargement…' : 'Département introuvable.'}</div>
      </>
    );
  }

  const manager = employees.find((e) => e.id === department.managerId);
  const parent = departments.find((d) => d.id === department.parentId);

  return (
    <>
      <nav aria-label="Fil d'Ariane" className="breadcrumb">
        <a href="#departments">Départements</a>
        <svg width="16" height="16" viewBox="0 0 20 20" fill="none" stroke="#5B6B82" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
          <path d="M7.5 4.5L13 10l-5.5 5.5" />
        </svg>
        <span>{department.name}</span>
      </nav>

      <section className="panel profile-header">
        <div className="dept-icon">
          <svg width="32" height="32" viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
            <path d="M4 17V4.5A1.5 1.5 0 0 1 5.5 3h6A1.5 1.5 0 0 1 13 4.5V17" />
            <path d="M13 8h1.5A1.5 1.5 0 0 1 16 9.5V17" />
            <path d="M2.5 17h15" />
            <path d="M7 6.5h.01M10 6.5h.01M7 9.5h.01M10 9.5h.01M7 12.5h.01M10 12.5h.01" />
          </svg>
        </div>
        <div className="profile-main">
          <div className="profile-title">
            <h1>{department.name}</h1>
            <span className={`badge ${department.active ? 'badge-active' : 'badge-inactive'}`}>
              {department.active ? 'Actif' : 'Inactif'}
            </span>
          </div>
          <div className="profile-meta">
            <span>
              Responsable <span className="dark strong">{manager?.name ?? '—'}</span>
            </span>
            {parent && (
              <span>
                Département parent <a href={`#department-${parent.id}`} className="dark strong">{parent.name}</a>
              </span>
            )}
            <span>
              Mis à jour le <span className="mono dark">{formatDate(department.updatedAt)}</span>
            </span>
          </div>
        </div>
        <button type="button" className="btn-outline" disabled={editing} onClick={() => setEditing(true)}>
          Modifier
        </button>
      </section>

      {editing && (
        <DepartmentForm
          title="Modifier le département"
          submitLabel="Enregistrer"
          forbiddenMessage="Vous n'avez pas la permission de modifier ce département."
          initial={{
            name: department.name,
            description: department.description,
            active: department.active,
            managerId: department.managerId,
            parentId: department.parentId,
          }}
          departments={departments}
          currentId={department.id}
          employees={employees}
          onSubmit={async (changes) => {
            await onUpdate(department.id, changes);
            setEditing(false);
          }}
          onCancel={() => setEditing(false)}
        />
      )}

      <section aria-label="Indicateurs du département" className="kpis">
        <div className="kpi">
          <div className="field-label">Effectif</div>
          <div className="kpi-value">{members.length.toLocaleString('fr-FR')}</div>
        </div>
      </section>

      <section aria-labelledby="t-emp" className="card">
        <div className="card-header">
          <h2 id="t-emp">Employés du département</h2>
          <a href="#employees" className="card-link">
            Voir les {members.length.toLocaleString('fr-FR')} employé{members.length > 1 ? 's' : ''}
          </a>
        </div>
        <div className="table-scroll">
          <div className="table table-members">
            <div className="row row-members row-head">
              <div>Employé</div>
              <div>Poste</div>
              <div>Statut</div>
            </div>
            {members.map((e) => (
              <div key={e.id} className="row row-members row-body">
                <div className="person">
                  <div className="avatar">{initials(e.name)}</div>
                  <div>
                    <a href={`#employee-${e.id}`} className="person-name">{e.name}</a>
                  </div>
                </div>
                <div>{e.position}</div>
                <div>
                  <span className={`badge ${STATUS_CLASS[e.status]}`}>{e.status}</span>
                </div>
              </div>
            ))}
            {loading && <div className="empty">Chargement…</div>}
            {!loading && members.length === 0 && <div className="empty">Aucun employé dans ce département.</div>}
          </div>
        </div>
      </section>
    </>
  );
}
