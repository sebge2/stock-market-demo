import { useCan } from '../auth/permissions';
import { useMemo, useState } from 'react';
import { formatDate, initials } from '../employees/employees.data';
import type { Client, ClientStatus, ClientType } from './clients.api';

/** Number of clients displayed per page. */
const PAGE_SIZE = 10;

/** Type tabs; `null` means no filter. */
const TABS: { label: string; type: ClientType | null }[] = [
  { label: 'Tous', type: null },
  { label: 'Particuliers', type: 'Particulier' },
  { label: 'Entreprises', type: 'Entreprise' },
];

/** Status filter options. */
const STATUSES = ['Tous', 'Actif', 'Inactif'] as const;

/** Props of the clients screen. */
interface ClientsPageProps {
  /** All clients. */
  clients: Client[];
  /** Whether data is being loaded. */
  loading: boolean;
}

/** Formats a number with the French thousands separator. */
const fmt = (value: number) => value.toLocaleString('fr-FR');

/** Chevron icon used in selects. */
function Chevron() {
  return (
    <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true" className="select-chevron">
      <path d="M5 7.5l5 5 5-5" />
    </svg>
  );
}

/** Clients screen: filterable and paginated list of clients. */
export function ClientsPage({ clients, loading }: ClientsPageProps) {
  const can = useCan();
  const [tab, setTab] = useState<ClientType | null>(null);
  const [query, setQuery] = useState('');
  const [status, setStatus] = useState<(typeof STATUSES)[number]>('Tous');
  const [page, setPage] = useState(1);

  const filtered = useMemo(() => {
    const needle = query.trim().toLowerCase();
    return clients.filter(
      (c) =>
        (tab === null || c.type === tab) &&
        (status === 'Tous' || c.status === (status as ClientStatus)) &&
        (needle === '' || c.name.toLowerCase().includes(needle)),
    );
  }, [clients, tab, query, status]);

  const pageCount = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  const currentPage = Math.min(page, pageCount);
  const start = (currentPage - 1) * PAGE_SIZE;
  const rows = filtered.slice(start, start + PAGE_SIZE);

  return (
    <>
      <header className="page-header">
        <div className="page-title">
          <div className="eyebrow">Clientèle</div>
          <h1>Clients</h1>
        </div>
        {can('CLIENT_CREATE') && (
          <a href="#client-new" className="btn-primary">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" aria-hidden="true">
              <path d="M10 4v12M4 10h12" />
            </svg>
            Nouveau client
          </a>
        )}
      </header>

      <section aria-label="Liste des clients" className="card" aria-busy={loading}>
        <div className="tabs">
          {TABS.map(({ label, type }) => {
            const count = type === null ? clients.length : clients.filter((c) => c.type === type).length;
            const active = tab === type;
            return (
              <button
                key={label}
                type="button"
                aria-current={active ? 'page' : undefined}
                className={`tab${active ? ' tab-active' : ''}`}
                onClick={() => {
                  setTab(type);
                  setPage(1);
                }}
              >
                {label}
                <span className="tab-count">{fmt(count)}</span>
              </button>
            );
          })}
        </div>

        <div className="filters">
          <div className="search">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="#5B6B82" strokeWidth="1.75" strokeLinecap="round" aria-hidden="true" className="search-icon">
              <circle cx="9" cy="9" r="5.5" />
              <path d="M13.25 13.25L17 17" />
            </svg>
            <input
              type="search"
              aria-label="Rechercher un client par nom"
              placeholder="Nom du client"
              value={query}
              onChange={(e) => {
                setQuery(e.target.value);
                setPage(1);
              }}
            />
          </div>
          <label className="select">
            Statut :
            <select
              value={status}
              onChange={(e) => {
                setStatus(e.target.value as (typeof STATUSES)[number]);
                setPage(1);
              }}
            >
              {STATUSES.map((s) => (
                <option key={s}>{s}</option>
              ))}
            </select>
            <Chevron />
          </label>
          <div className="total">
            {fmt(filtered.length)} client{filtered.length > 1 ? 's' : ''}
          </div>
        </div>

        <div className="table-scroll">
          <div className="table table-clients">
            <div className="row row-clients row-head">
              <div>Client</div>
              <div>Type</div>
              <div className="cell-right">Client depuis</div>
              <div>Statut</div>
              <div />
            </div>

            {rows.map((c) => (
              <div key={c.id} className="row row-clients row-body">
                <div className="person">
                  <div className={`avatar${c.type === 'Entreprise' ? ' avatar-square' : ''}`}>{initials(c.name)}</div>
                  <div>
                    <a href={`#client-${c.id}`} className="person-name">{c.name}</a>
                  </div>
                </div>
                <div>
                  <span className={`badge ${c.type === 'Entreprise' ? 'badge-leave' : 'badge-inactive'}`}>{c.type}</span>
                </div>
                <div className="cell-right mono">{formatDate(c.since)}</div>
                <div>
                  <span className={`badge ${c.status === 'Actif' ? 'badge-active' : 'badge-inactive'}`}>{c.status}</span>
                </div>
                <a href={`#client-${c.id}`} className="row-chevron" aria-label={`Ouvrir la fiche de ${c.name}`}>
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                    <path d="M7.5 4.5L13 10l-5.5 5.5" />
                  </svg>
                </a>
              </div>
            ))}

            {loading && <div className="empty">Chargement…</div>}
            {!loading && rows.length === 0 && <div className="empty">Aucun client ne correspond à ces critères.</div>}
          </div>
        </div>

        <div className="pagination">
          <div className="pagination-info">
            {filtered.length === 0 ? '0' : `${start + 1}–${start + rows.length}`} sur {fmt(filtered.length)}
          </div>
          <div className="pagination-controls">
            <button type="button" className="btn-secondary" disabled={currentPage === 1} onClick={() => setPage(currentPage - 1)}>
              Précédent
            </button>
            {Array.from({ length: pageCount }, (_, i) => i + 1).map((n) => (
              <button
                key={n}
                type="button"
                aria-current={n === currentPage ? 'page' : undefined}
                className={`page-number${n === currentPage ? ' page-number-active' : ''}`}
                onClick={() => setPage(n)}
              >
                {n}
              </button>
            ))}
            <button type="button" className="btn-outline" disabled={currentPage === pageCount} onClick={() => setPage(currentPage + 1)}>
              Suivant
            </button>
          </div>
        </div>
      </section>
    </>
  );
}
