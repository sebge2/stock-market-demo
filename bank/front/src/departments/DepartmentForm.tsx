import { type FormEvent, useState } from 'react';
import { ApiError, type Department, type NewDepartment } from '../employees/employees.api';
import type { Employee } from '../employees/employees.data';

/** Props of the department form. */
interface DepartmentFormProps {
  /** Form title. */
  title: string;
  /** Label of the submit button. */
  submitLabel: string;
  /** Message displayed when the server refuses the action (403). */
  forbiddenMessage: string;
  /** Initial values of the fields. */
  initial: NewDepartment;
  /** All departments, from which the parent department is chosen. */
  departments: Department[];
  /** Identifier of the edited department, excluded (with its sub-departments) from the possible parents. */
  currentId?: string;
  /** Employees that can be chosen as manager. */
  employees: Employee[];
  /** Saves the department; rejects when the call fails. */
  onSubmit: (department: NewDepartment) => Promise<void>;
  /** Closes the form without saving. */
  onCancel: () => void;
}

/** Form used to create or edit a department. */
export function DepartmentForm({
  title,
  submitLabel,
  forbiddenMessage,
  initial,
  departments,
  currentId,
  employees,
  onSubmit,
  onCancel,
}: DepartmentFormProps) {
  const [name, setName] = useState(initial.name);
  const [description, setDescription] = useState(initial.description);
  const [active, setActive] = useState(initial.active);
  const [managerId, setManagerId] = useState(initial.managerId ?? '');
  const [parentId, setParentId] = useState(initial.parentId ?? '');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const descendants = new Set<string>();
  for (let grew = currentId !== undefined; grew; ) {
    grew = false;
    for (const d of departments) {
      if (d.parentId && (d.parentId === currentId || descendants.has(d.parentId)) && !descendants.has(d.id)) {
        descendants.add(d.id);
        grew = true;
      }
    }
  }

  const submit = (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    onSubmit({ name: name.trim(), description: description.trim(), active, managerId: managerId || null, parentId: parentId || null })
      .catch((e: Error) => {
        setError(
          e instanceof ApiError && e.status === 409
            ? 'Ce nom existe déjà, ou ce responsable dirige déjà un autre département.'
            : e instanceof ApiError && e.status === 403
              ? forbiddenMessage
              : e instanceof ApiError && e.status === 400
                ? 'Un département ne peut pas dépendre de lui-même ni de l’un de ses sous-départements.'
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
          <span className="field-label">Nom</span>
          <input required maxLength={100} value={name} onChange={(e) => setName(e.target.value)} />
        </label>
        <label className="form-field">
          <span className="field-label">Département parent</span>
          <select value={parentId} onChange={(e) => setParentId(e.target.value)}>
            <option value="">Aucun</option>
            {departments
              .filter((d) => d.id !== currentId && !descendants.has(d.id))
              .sort((a, b) => a.name.localeCompare(b.name, 'fr'))
              .map((d) => (
                <option key={d.id} value={d.id}>{d.name}</option>
              ))}
          </select>
        </label>
        <label className="form-field">
          <span className="field-label">Responsable</span>
          <select value={managerId} onChange={(e) => setManagerId(e.target.value)}>
            <option value="">Aucun</option>
            {[...employees]
              .sort((a, b) => a.name.localeCompare(b.name, 'fr'))
              .map((e) => (
                <option key={e.id} value={e.id}>{e.name}</option>
              ))}
          </select>
        </label>
        <label className="form-field form-wide">
          <span className="field-label">Description</span>
          <input maxLength={500} value={description} onChange={(e) => setDescription(e.target.value)} />
        </label>
        <label className="form-check">
          <input type="checkbox" checked={active} onChange={(e) => setActive(e.target.checked)} />
          Département actif
        </label>
      </div>
      <div className="form-actions">
        <button type="button" className="btn-secondary" onClick={onCancel} disabled={saving}>Annuler</button>
        <button type="submit" className="btn-primary" disabled={saving || name.trim() === ''}>
          {saving ? 'Enregistrement…' : submitLabel}
        </button>
      </div>
    </form>
  );
}
