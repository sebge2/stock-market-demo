import { useEffect, useState } from 'react';
import { AvatarEditor } from '../users/AvatarEditor';
import { fetchAvatar } from '../users/users.api';
import { EmployeeForm } from './EmployeeForm';
import { EmployeeRolesPanel } from './EmployeeRolesPanel';
import type { Department, EmployeeChanges } from './employees.api';
import { type Employee, STATUS_CLASS, formatDate, initials } from './employees.data';

/** Props of the employee sheet. */
interface EmployeeDetailPageProps {
  /** The displayed employee, or undefined when not found. */
  employee: Employee | undefined;
  /** All departments, used to resolve the manager. */
  departments: Department[];
  /** All employees, used to resolve the manager name. */
  employees: Employee[];
  /** Whether data is being loaded. */
  loading: boolean;
  /** Saves the changes of the employee; rejects when the update fails. */
  onUpdate: (id: string, changes: EmployeeChanges) => Promise<void>;
}

/** Formats the time elapsed since the given ISO date as "X ans Y mois". */
function seniority(isoDate: string, now: Date = new Date()): string {
  const hired = new Date(isoDate);
  const months = Math.max(0, (now.getFullYear() - hired.getFullYear()) * 12 + now.getMonth() - hired.getMonth());
  const years = Math.floor(months / 12);
  const rest = months % 12;
  return [years > 0 ? `${years} an${years > 1 ? 's' : ''}` : '', rest > 0 || years === 0 ? `${rest} mois` : ''].filter(Boolean).join(' ');
}

/** A labelled field of the information panel. */
function Field({ label, children, mono }: { label: string; children: React.ReactNode; mono?: boolean }) {
  return (
    <div className="field">
      <div className="field-label">{label}</div>
      <div className={mono ? 'field-value mono' : 'field-value'}>{children}</div>
    </div>
  );
}

/** Identifier of the manager of a department: its own manager, otherwise the one of the nearest ancestor. */
function departmentManagerId(department: Department | undefined, departments: Department[]): string | null {
  const seen = new Set<string>();
  for (let d = department; d && !seen.has(d.id); d = departments.find((p) => p.id === d?.parentId)) {
    if (d.managerId) {
      return d.managerId;
    }
    seen.add(d.id);
  }
  return null;
}

/**
 * Identifier of the hierarchical manager of an employee: the manager of the department, or, for the manager
 * of the department itself, the manager of the nearest parent department with another manager.
 */
function hierarchicalManagerId(employeeId: string, department: Department | undefined, departments: Department[]): string | null {
  const head = departmentManagerId(department, departments);
  if (head !== employeeId) {
    return head;
  }
  const seen = new Set<string>();
  for (let d = departments.find((p) => p.id === department?.parentId); d && !seen.has(d.id); d = departments.find((p) => p.id === d?.parentId)) {
    seen.add(d.id);
    const manager = departmentManagerId(d, departments);
    if (manager !== employeeId) {
      return manager;
    }
  }
  return null;
}

/** Employee sheet: identity header, professional information and history. */
export function EmployeeDetailPage({ employee, departments, employees, loading, onUpdate }: EmployeeDetailPageProps) {
  const [editing, setEditing] = useState(false);
  const [avatar, setAvatar] = useState<string | null>(null);
  const [editingAvatar, setEditingAvatar] = useState(false);
  const userId = employee?.userId ?? null;

  useEffect(() => {
    setAvatar(null);
    if (userId) {
      fetchAvatar(userId).then(setAvatar).catch(() => setAvatar(null));
    }
  }, [userId]);

  if (!employee) {
    return (
      <>
        <nav aria-label="Fil d'Ariane" className="breadcrumb">
          <a href="#employees">Employés</a>
        </nav>
        <div className="panel">{loading ? 'Chargement…' : 'Employé introuvable.'}</div>
      </>
    );
  }

  // A manager belongs to the department they manage, even if their record was saved before that rule existed.
  const department = departments.find((d) => d.managerId === employee.id) ?? departments.find((d) => d.id === employee.departmentId);
  const manager = employees.find((e) => e.id === hierarchicalManagerId(employee.id, department, departments));

  return (
    <>
      <nav aria-label="Fil d'Ariane" className="breadcrumb">
        <a href="#employees">Employés</a>
        <svg width="16" height="16" viewBox="0 0 20 20" fill="none" stroke="#5B6B82" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
          <path d="M7.5 4.5L13 10l-5.5 5.5" />
        </svg>
        <span>{employee.name}</span>
      </nav>

      <section className="panel profile-header">
        <div className="profile-avatar-wrap">
          <div className="profile-avatar">
            {avatar ? <img className="avatar-img" src={`data:image/png;base64,${avatar}`} alt="" /> : initials(employee.name)}
          </div>
          {editing && userId && (
            <button type="button" className="avatar-edit" aria-label="Modifier la photo" onClick={() => setEditingAvatar(true)}>
              <svg width="16" height="16" viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                <path d="M13.5 3.5l3 3L7 16H4v-3z" />
                <path d="M12 5l3 3" />
              </svg>
            </button>
          )}
        </div>
        <div className="profile-main">
          <div className="profile-title">
            <h1>{employee.name}</h1>
            <span className={`badge ${STATUS_CLASS[employee.status]}`}>{employee.status}</span>
          </div>
          <div className="profile-subtitle">
            {employee.position} · {employee.department}
          </div>
          <div className="profile-meta">
            <span>
              Entrée le <span className="mono dark">{formatDate(employee.hireDate)}</span>
            </span>
          </div>
        </div>
        <button type="button" className="btn-primary" disabled={editing} onClick={() => setEditing(true)}>
          Modifier la fiche
        </button>
      </section>

      {editingAvatar && userId && (
        <AvatarEditor
          userId={userId}
          initials={initials(employee.name)}
          avatar={avatar}
          onSaved={setAvatar}
          onClose={() => setEditingAvatar(false)}
        />
      )}
      {editing && (
        <EmployeeForm
          title="Modifier la fiche"
          submitLabel="Enregistrer"
          forbiddenMessage="Vous n'avez pas la permission de modifier cet employé."
          initial={{
            firstName: employee.firstName,
            lastName: employee.lastName,
            email: employee.email,
            jobTitle: employee.jobTitle,
            status: employee.status,
            departmentId: employee.departmentId,
          }}
          departments={departments}
          onSubmit={async (changes) => {
            await onUpdate(employee.id, changes);
            setEditing(false);
          }}
          onCancel={() => setEditing(false)}
        />
      )}

      <div className="tabs tabs-flush">
        <button type="button" aria-current="page" className="tab tab-active tab-short">
          Profil
        </button>
      </div>

      {userId && <EmployeeRolesPanel userId={userId} />}

      <div className="detail-columns">
        <section aria-labelledby="t-info" className="panel panel-stack">
          <h2 id="t-info">Informations professionnelles</h2>
          <div className="field-grid">
            <Field label="Poste">{employee.position}</Field>
            <Field label="Département">
              {department ? <a href={`#department-${department.id}`}>{department.name}</a> : '—'}
            </Field>
            <Field label="Responsable hiérarchique">{manager ? `${manager.name} · ${manager.position}` : '—'}</Field>
            <Field label="Ancienneté" mono>
              {seniority(employee.hireDate)}
            </Field>
            <Field label="Courriel">{employee.email}</Field>
          </div>
        </section>
      </div>
    </>
  );
}
