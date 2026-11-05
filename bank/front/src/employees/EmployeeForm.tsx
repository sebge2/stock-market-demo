import { type FormEvent, useState } from 'react';
import { ApiError, type Department, type EmployeeChanges } from './employees.api';
import { type EmployeeStatus } from './employees.data';

/** Selectable employment statuses. */
const STATUSES: EmployeeStatus[] = ['Actif', 'Inactif', 'Suspendu', 'En congé'];

/** Props of the employee form. */
interface EmployeeFormProps {
  /** Form title. */
  title: string;
  /** Label of the submit button. */
  submitLabel: string;
  /** Message displayed when the server refuses the action (403). */
  forbiddenMessage: string;
  /** Initial values of the fields. */
  initial: EmployeeChanges;
  /** Departments that can be chosen. */
  departments: Department[];
  /** Saves the changes; rejects when the call fails. */
  onSubmit: (changes: EmployeeChanges) => Promise<void>;
  /** Closes the form without saving. */
  onCancel: () => void;
}

/** Form used to create or edit an employee. */
export function EmployeeForm({ title, submitLabel, forbiddenMessage, initial, departments, onSubmit, onCancel }: EmployeeFormProps) {
  const [firstName, setFirstName] = useState(initial.firstName);
  const [lastName, setLastName] = useState(initial.lastName);
  const [email, setEmail] = useState(initial.email);
  const [jobTitle, setJobTitle] = useState(initial.jobTitle);
  const [status, setStatus] = useState<EmployeeStatus>(initial.status);
  const [departmentId, setDepartmentId] = useState(initial.departmentId ?? '');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const submit = (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    onSubmit({
      firstName: firstName.trim(),
      lastName: lastName.trim(),
      email: email.trim(),
      jobTitle: jobTitle.trim(),
      status,
      departmentId: departmentId || null,
    }).catch((e: Error) => {
      setError(
        e instanceof ApiError && e.status === 409
          ? 'Un employé ou un utilisateur avec ce courriel existe déjà.'
          : e instanceof ApiError && e.status === 403
            ? forbiddenMessage
            : e instanceof ApiError && (e.status === 502 || e.status === 503)
              ? "Le service des utilisateurs est indisponible, réessayez plus tard."
              : e.message,
      );
      setSaving(false);
    });
  };

  return (
    <form aria-label={title} className="panel form" onSubmit={submit}>
      <h2>{title}</h2>
      {error && <div role="alert" className="alert">{error}</div>}
      <div className="form-grid">
        <label className="form-field">
          <span className="field-label">Prénom</span>
          <input required maxLength={100} value={firstName} onChange={(e) => setFirstName(e.target.value)} />
        </label>
        <label className="form-field">
          <span className="field-label">Nom</span>
          <input required maxLength={100} value={lastName} onChange={(e) => setLastName(e.target.value)} />
        </label>
        <label className="form-field">
          <span className="field-label">Courriel</span>
          <input required type="email" maxLength={255} value={email} onChange={(e) => setEmail(e.target.value)} />
        </label>
        <label className="form-field">
          <span className="field-label">Poste</span>
          <input maxLength={100} value={jobTitle} onChange={(e) => setJobTitle(e.target.value)} />
        </label>
        <label className="form-field">
          <span className="field-label">Département</span>
          <select value={departmentId} onChange={(e) => setDepartmentId(e.target.value)}>
            <option value="">Aucun</option>
            {[...departments]
              .sort((a, b) => a.name.localeCompare(b.name, 'fr'))
              .map((d) => (
                <option key={d.id} value={d.id}>{d.name}</option>
              ))}
          </select>
        </label>
        <label className="form-field">
          <span className="field-label">Statut</span>
          <select value={status} onChange={(e) => setStatus(e.target.value as EmployeeStatus)}>
            {STATUSES.map((s) => (
              <option key={s}>{s}</option>
            ))}
          </select>
        </label>
      </div>
      <div className="form-actions">
        <button type="button" className="btn-secondary" onClick={onCancel} disabled={saving}>Annuler</button>
        <button
          type="submit"
          className="btn-primary"
          disabled={saving || firstName.trim() === '' || lastName.trim() === '' || email.trim() === ''}
        >
          {saving ? 'Enregistrement…' : submitLabel}
        </button>
      </div>
    </form>
  );
}
