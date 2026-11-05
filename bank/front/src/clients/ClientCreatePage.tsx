import { ClientForm } from './ClientForm';
import type { ClientChanges } from './clients.api';

/** Props of the client creation page. */
interface ClientCreatePageProps {
  /** Creates the client; rejects when the creation fails. */
  onCreate: (changes: ClientChanges) => Promise<void>;
}

/** Page used to add a new client. */
export function ClientCreatePage({ onCreate }: ClientCreatePageProps) {
  return (
    <>
      <nav aria-label="Fil d'Ariane" className="breadcrumb">
        <a href="#clients">Clients</a>
        <svg width="16" height="16" viewBox="0 0 20 20" fill="none" stroke="#5B6B82" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
          <path d="M7.5 4.5L13 10l-5.5 5.5" />
        </svg>
        <span>Nouveau client</span>
      </nav>

      <header className="page-header">
        <div className="page-title">
          <div className="eyebrow">Clientèle</div>
          <h1>Nouveau client</h1>
        </div>
      </header>

      <div className="detail-columns">
        <ClientForm
          title="Informations du client"
          submitLabel="Créer le client"
          withEmail
          initial={{ type: 'Particulier', status: 'Actif', firstName: '', lastName: '', email: '' }}
          onSubmit={onCreate}
          onCancel={() => {
            window.location.hash = '#clients';
          }}
        />
      </div>
    </>
  );
}
