import { type FormEvent, useState } from 'react';
import { ApiError } from '../employees/employees.api';
import type { ClientChanges, ClientStatus, ClientType } from './clients.api';

/** Selectable client types. */
const TYPES: ClientType[] = ['Particulier', 'Entreprise'];

/** Selectable client statuses. */
const STATUSES: ClientStatus[] = ['Actif', 'Inactif'];

/** Props of the client form. */
interface ClientFormProps {
  /** Form title. */
  title?: string;
  /** Label of the submit button. */
  submitLabel?: string;
  /** Whether the email field is shown and required (client creation). */
  withEmail?: boolean;
  /** Initial values of the fields. */
  initial: ClientChanges;
  /** Saves the changes; rejects when the call fails. */
  onSubmit: (changes: ClientChanges) => Promise<void>;
  /** Closes the form without saving. */
  onCancel: () => void;
}

/** Form used to edit all the information of a client. */
export function ClientForm({ title = 'Modifier la fiche', submitLabel = 'Enregistrer', withEmail = false, initial, onSubmit, onCancel }: ClientFormProps) {
  const [type, setType] = useState(initial.type);
  const [status, setStatus] = useState(initial.status);
  const [firstName, setFirstName] = useState(initial.firstName);
  const [lastName, setLastName] = useState(initial.lastName);
  const [email, setEmail] = useState(initial.email ?? '');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const submit = (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    onSubmit({ type, status, firstName: firstName.trim(), lastName: lastName.trim(), ...(withEmail ? { email: email.trim() } : {}) }).catch((e: Error) => {
      setError(
        e instanceof ApiError && e.status === 403
          ? `Vous n'avez pas la permission de ${withEmail ? 'créer' : 'modifier'} ce client.`
          : e instanceof ApiError && e.status === 400
            ? 'Les informations saisies ne sont pas valides.'
            : e instanceof ApiError && e.status === 409
              ? 'Un client ou un utilisateur avec ce courriel existe déjà.'
              : e instanceof ApiError && (e.status === 502 || e.status === 503)
                ? 'Le service des utilisateurs est indisponible, réessayez plus tard.'
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
        {withEmail && (
          <label className="form-field">
            <span className="field-label">Courriel</span>
            <input required type="email" maxLength={255} value={email} onChange={(e) => setEmail(e.target.value)} />
          </label>
        )}
        <label className="form-field">
          <span className="field-label">Type</span>
          <select value={type} onChange={(e) => setType(e.target.value as ClientType)}>
            {TYPES.map((t) => (
              <option key={t}>{t}</option>
            ))}
          </select>
        </label>
        <label className="form-field">
          <span className="field-label">Statut</span>
          <select value={status} onChange={(e) => setStatus(e.target.value as ClientStatus)}>
            {STATUSES.map((s) => (
              <option key={s}>{s}</option>
            ))}
          </select>
        </label>
      </div>
      <div className="form-actions">
        <button type="button" className="btn-secondary" onClick={onCancel} disabled={saving}>Annuler</button>
        <button type="submit" className="btn-primary" disabled={saving || firstName.trim() === '' || lastName.trim() === '' || (withEmail && email.trim() === '')}>
          {saving ? 'Enregistrement…' : submitLabel}
        </button>
      </div>
    </form>
  );
}
