import { useEffect, useState } from 'react';
import { formatDate, initials } from '../employees/employees.data';
import { AvatarEditor } from '../users/AvatarEditor';
import { fetchAvatar } from '../users/users.api';
import { ClientForm } from './ClientForm';
import type { Client, ClientChanges } from './clients.api';

/** Tabs of the client sheet. */
export type ClientTab = 'info' | 'portfolio';

/** Props of the client sheet. */
interface ClientDetailPageProps {
  /** The displayed client, or undefined when not found. */
  client: Client | undefined;
  /** The selected tab. */
  tab: ClientTab;
  /** Whether data is being loaded. */
  loading: boolean;
  /** Saves the changes of the client; rejects when the update fails. */
  onUpdate: (id: string, changes: ClientChanges) => Promise<void>;
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

/** Client sheet: identity header, information tab and (empty for now) portfolio tab. */
export function ClientDetailPage({ client, tab, loading, onUpdate }: ClientDetailPageProps) {
  const [editing, setEditing] = useState(false);
  const [avatar, setAvatar] = useState<string | null>(null);
  const [editingAvatar, setEditingAvatar] = useState(false);
  const userId = client?.userId ?? null;

  useEffect(() => {
    setAvatar(null);
    if (userId) {
      fetchAvatar(userId).then(setAvatar).catch(() => setAvatar(null));
    }
  }, [userId]);

  useEffect(() => setEditing(false), [client?.id]);

  if (!client) {
    return (
      <>
        <nav aria-label="Fil d'Ariane" className="breadcrumb">
          <a href="#clients">Clients</a>
        </nav>
        <div className="panel">{loading ? 'Chargement…' : 'Client introuvable.'}</div>
      </>
    );
  }

  const company = client.type === 'Entreprise';
  const base = `#client-${client.id}`;

  return (
    <>
      <nav aria-label="Fil d'Ariane" className="breadcrumb">
        <a href="#clients">Clients</a>
        <svg width="16" height="16" viewBox="0 0 20 20" fill="none" stroke="#5B6B82" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
          <path d="M7.5 4.5L13 10l-5.5 5.5" />
        </svg>
        <span>{client.name}</span>
      </nav>

      <section className="panel profile-header">
        <div className="profile-avatar-wrap">
          <div className={`profile-avatar${company ? ' profile-avatar-square' : ''}`}>
            {avatar ? <img className="avatar-img" src={`data:image/png;base64,${avatar}`} alt="" /> : initials(client.name)}
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
            <h1>{client.name}</h1>
            <span className={`badge ${client.status === 'Actif' ? 'badge-active' : 'badge-inactive'}`}>{client.status}</span>
          </div>
          <div className="profile-meta">
            <span>
              Client depuis le <span className="mono dark">{formatDate(client.since)}</span>
            </span>
          </div>
        </div>
        <button type="button" className="btn-primary" disabled={editing} onClick={() => setEditing(true)}>
          Modifier la fiche
        </button>
      </section>

      {editing && (
        <ClientForm
          initial={{ type: client.type, status: client.status, firstName: client.firstName, lastName: client.lastName }}
          onSubmit={async (changes) => {
            await onUpdate(client.id, changes);
            setEditing(false);
          }}
          onCancel={() => setEditing(false)}
        />
      )}

      {editingAvatar && userId && (
        <AvatarEditor
          userId={userId}
          initials={initials(client.name)}
          avatar={avatar}
          onSaved={setAvatar}
          onClose={() => setEditingAvatar(false)}
        />
      )}

      <div className="tabs tabs-flush">
        <a href={base} aria-current={tab === 'info' ? 'page' : undefined} className={`tab tab-short${tab === 'info' ? ' tab-active' : ''}`}>
          Informations
        </a>
        <a href={`${base}/portfolio`} aria-current={tab === 'portfolio' ? 'page' : undefined} className={`tab tab-short${tab === 'portfolio' ? ' tab-active' : ''}`}>
          Portefeuille
        </a>
      </div>

      {tab === 'info' && (
        <div className="detail-columns">
          <section aria-labelledby="t-info" className="panel panel-stack">
            <h2 id="t-info">Informations du client</h2>
            <div className="field-grid">
              <Field label="Type">{client.type}</Field>
              <Field label="Statut">{client.status}</Field>
              <Field label="Prénom">{client.firstName || '—'}</Field>
              <Field label="Nom">{client.lastName || '—'}</Field>
              <Field label="Client depuis" mono>
                {formatDate(client.since)}
              </Field>
            </div>
          </section>
        </div>
      )}

      {tab === 'portfolio' && <section aria-label="Portefeuille" />}
    </>
  );
}
