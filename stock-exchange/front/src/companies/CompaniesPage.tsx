import { type FormEvent, useEffect, useMemo, useState } from 'react';
import { useCan } from '../auth/permissions';
import { ApiError } from '../roles/roles.api';
import {
  type Company,
  type CompanyChanges,
  type CompanyStatus,
  type SectorType,
  SECTOR_LABELS,
  STATUS_LABELS,
  createCompany,
  deleteCompany,
  describeCompanyError,
  fetchCompanies,
  updateCompany,
} from './companies.api';

/** Props of the companies screen. */
interface CompaniesPageProps {
  /** Called when the services reject the access token. */
  onUnauthorized: () => void;
}

/** Tabs filtering the list by status; `null` means no filter. */
const TABS: { label: string; status: CompanyStatus | null }[] = [
  { label: 'Toutes', status: null },
  { label: 'Cotées', status: 'LISTED' },
  { label: 'Suspendues', status: 'SUSPENDED' },
  { label: 'Admissions', status: 'ADMISSION' },
  { label: 'Radiées', status: 'DELISTED' },
];

/** Colour class of the dot of each status. */
const DOT_CLASS: Record<CompanyStatus, string> = {
  LISTED: '',
  SUSPENDED: ' sx-dot-suspended',
  ADMISSION: ' sx-dot-admission',
  DELISTED: ' sx-dot-off',
};

/** Colour of the figure of each indicator. */
const KPI_COLOR: Record<CompanyStatus, string> = {
  LISTED: '#EAF0FA',
  SUSPENDED: '#FF8A5C',
  ADMISSION: '#F5C451',
  DELISTED: '#9AA9C4',
};

/** Placeholder of the market figures that are not available yet. */
const NO_VALUE = '-';

/** Formats a timestamp as a French date. */
const date = (timestamp: string) => new Date(timestamp).toLocaleDateString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric' });

/** Edited company: `null` creates a new one. */
type EditedCompany = { company: Company | null };

/** Icon wrapper shared by the screen buttons. */
function Icon({ size = 18, children }: { size?: number; children: React.ReactNode }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      {children}
    </svg>
  );
}

/** Form used to create or edit a company. */
function CompanyForm({ edited, onSubmit, onCancel }: { edited: EditedCompany; onSubmit: (c: CompanyChanges) => Promise<void>; onCancel: () => void }) {
  const initial = edited.company;
  const [name, setName] = useState(initial?.name ?? '');
  const [countryCode, setCountryCode] = useState(initial?.countryCode ?? 'FR');
  const [sectorType, setSectorType] = useState<SectorType>(initial?.sectorType ?? 'FINANCIALS');
  const [status, setStatus] = useState<CompanyStatus>(initial?.status ?? 'ADMISSION');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const title = initial ? 'Modifier la compagnie' : 'Ajouter une compagnie';

  const submit = (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    onSubmit({ name: name.trim(), countryCode: countryCode.trim().toUpperCase(), sectorType, status }).catch((e: Error) => {
      setError(describeCompanyError(e, 'enregistrer cette compagnie'));
      setSaving(false);
    });
  };

  return (
    <form aria-label={title} className="sx-card" onSubmit={submit}>
      <h2 className="sx-card-title">{title}</h2>
      {error && <div role="alert" className="sx-alert">{error}</div>}
      <div className="sx-form-grid">
        <div className="sx-field">
          <label htmlFor="c-name">Nom</label>
          <input id="c-name" className="sx-input" required maxLength={255} value={name} onChange={(e) => setName(e.target.value)} />
        </div>
        <div className="sx-field">
          <label htmlFor="c-country">Pays (code ISO)</label>
          <input id="c-country" className="sx-input" required maxLength={2} pattern="[A-Za-z]{2}" placeholder="FR" value={countryCode} onChange={(e) => setCountryCode(e.target.value)} />
        </div>
        <div className="sx-field">
          <label htmlFor="c-sector">Secteur</label>
          <div className="sx-select sx-form-select">
            <select id="c-sector" value={sectorType} onChange={(e) => setSectorType(e.target.value as SectorType)}>
              {(Object.keys(SECTOR_LABELS) as SectorType[]).map((s) => (
                <option key={s} value={s}>{SECTOR_LABELS[s]}</option>
              ))}
            </select>
          </div>
        </div>
        <div className="sx-field">
          <label htmlFor="c-status">Statut</label>
          <div className="sx-select sx-form-select">
            <select id="c-status" value={status} onChange={(e) => setStatus(e.target.value as CompanyStatus)}>
              {(Object.keys(STATUS_LABELS) as CompanyStatus[]).map((s) => (
                <option key={s} value={s}>{STATUS_LABELS[s]}</option>
              ))}
            </select>
          </div>
        </div>
      </div>
      <div className="sx-card-foot">
        <button type="button" className="sx-btn sx-btn-ghost" onClick={onCancel} disabled={saving}>Annuler</button>
        <button type="submit" className="sx-btn sx-btn-primary" disabled={saving || name.trim() === '' || countryCode.trim().length !== 2}>
          {saving ? 'Enregistrement…' : 'Enregistrer'}
        </button>
      </div>
    </form>
  );
}

/** Companies screen: indicators by status and filterable list of the companies of the exchange. */
export function CompaniesPage({ onUnauthorized }: CompaniesPageProps) {
  const can = useCan();
  const [companies, setCompanies] = useState<Company[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [tab, setTab] = useState<CompanyStatus | null>(null);
  const [query, setQuery] = useState('');
  const [sector, setSector] = useState<SectorType | ''>('');
  const [edited, setEdited] = useState<EditedCompany | null>(null);

  useEffect(() => {
    fetchCompanies()
      .then(setCompanies)
      .catch((e: Error) => {
        if (e instanceof ApiError && e.status === 401) {
          onUnauthorized();
        }
        setError(describeCompanyError(e, 'consulter les compagnies'));
      })
      .finally(() => setLoading(false));
  }, [onUnauthorized]);

  const count = (status: CompanyStatus) => companies.filter((c) => c.status === status).length;
  const year = new Date().getFullYear();
  const delistedThisYear = companies.filter((c) => c.status === 'DELISTED' && new Date(c.updatedAt).getFullYear() === year).length;

  const sectors = useMemo(() => Array.from(new Set(companies.map((c) => c.sectorType))).sort((a, b) => SECTOR_LABELS[a].localeCompare(SECTOR_LABELS[b], 'fr')), [companies]);

  const rows = useMemo(() => {
    const needle = query.trim().toLowerCase();
    return companies
      .filter((c) => (tab === null || c.status === tab) && (sector === '' || c.sectorType === sector) && (needle === '' || c.name.toLowerCase().includes(needle)))
      .sort((a, b) => a.name.localeCompare(b.name, 'fr'));
  }, [companies, tab, query, sector]);

  return (
    <>
      <header className="sx-page-header">
        <div className="sx-page-title">
          <div className="sx-crumb">Référentiel · Place d’échange</div>
          <h1>Compagnies cotées</h1>
        </div>
        <div className="sx-actions">
          {can('COMPANY_CREATE') && (
            <button type="button" className="sx-btn sx-btn-primary" disabled={edited !== null} onClick={() => setEdited({ company: null })}>
              <Icon><path d="M12 5v14 M5 12h14" /></Icon>
              Ajouter une compagnie
            </button>
          )}
        </div>
      </header>

      {error && <div role="alert" className="sx-alert">{error}</div>}

      <div className="sx-kpis">
        <div className="sx-kpi">
          <p className="sx-card-title">Cotées</p>
          <span className="sx-kpi-value" style={{ color: KPI_COLOR.LISTED }}>{count('LISTED')}</span>
        </div>
        <div className="sx-kpi">
          <p className="sx-card-title">Suspendues</p>
          <span className="sx-kpi-value" style={{ color: KPI_COLOR.SUSPENDED }}>{count('SUSPENDED')}</span>
        </div>
        <div className="sx-kpi">
          <p className="sx-card-title">Admissions en cours</p>
          <span className="sx-kpi-value" style={{ color: KPI_COLOR.ADMISSION }}>{count('ADMISSION')}</span>
        </div>
        <div className="sx-kpi">
          <p className="sx-card-title">Radiées en {year}</p>
          <span className="sx-kpi-value" style={{ color: KPI_COLOR.DELISTED }}>{delistedThisYear}</span>
        </div>
      </div>

      {edited && (
        <CompanyForm
          key={edited.company?.id ?? 'new'}
          edited={edited}
          onCancel={() => setEdited(null)}
          onSubmit={async (changes) => {
            if (edited.company) {
              const updated = await updateCompany(edited.company, changes);
              setCompanies((current) => current.map((c) => (c.id === updated.id ? updated : c)));
            } else {
              const created = await createCompany(changes);
              setCompanies((current) => [...current, created]);
            }
            setEdited(null);
          }}
        />
      )}

      <section aria-label="Liste des compagnies" className="sx-card" aria-busy={loading}>
        <div role="tablist" aria-label="Filtrer par statut" className="sx-tabs">
          {TABS.map(({ label, status }) => (
            <button key={label} type="button" role="tab" className="sx-tab" aria-selected={tab === status} onClick={() => setTab(status)}>
              {label}
              <span className="sx-count">{status === null ? companies.length : count(status)}</span>
            </button>
          ))}
        </div>

        <div className="sx-filters">
          <div className="sx-search">
            <label htmlFor="q-compagnies" className="sx-sr-only">Rechercher une compagnie</label>
            <span className="sx-search-icon"><Icon size={20}><circle cx="11" cy="11" r="6.5" /><path d="M20 20l-4.2-4.2" /></Icon></span>
            <input id="q-compagnies" className="sx-input" type="search" placeholder="Rechercher une compagnie" value={query} onChange={(e) => setQuery(e.target.value)} />
          </div>
          <label className="sx-select">
            Secteur&nbsp;:
            <select value={sector} onChange={(e) => setSector(e.target.value as SectorType | '')}>
              <option value="">Tous</option>
              {sectors.map((s) => (
                <option key={s} value={s}>{SECTOR_LABELS[s]}</option>
              ))}
            </select>
          </label>
        </div>

        <div className="sx-table-wrap">
          <table className="sx-table sx-table-wide">
            <caption>Liste des compagnies de la place</caption>
            <thead>
              <tr>
                <th scope="col">Compagnie</th>
                <th scope="col">Secteur</th>
                <th scope="col">Admission</th>
                <th scope="col" className="sx-right">Capitalisation (EUR)</th>
                <th scope="col" className="sx-right">Flottant</th>
                <th scope="col" className="sx-right">Dernier cours</th>
                <th scope="col">Statut</th>
                <th scope="col" />
              </tr>
            </thead>
            <tbody>
              {rows.map((c) => (
                <tr key={c.id} className={c.status === 'LISTED' ? 'sx-row-on' : undefined}>
                  <th scope="row">
                    <span className="sx-company-name">{c.name}</span>
                    <div className="sx-company-sub">{c.countryCode}</div>
                  </th>
                  <td className="sx-muted">{SECTOR_LABELS[c.sectorType]}</td>
                  <td className="sx-muted">{date(c.createdAt)}</td>
                  <td className="sx-right"><span className="sx-num">{NO_VALUE}</span></td>
                  <td className="sx-right"><span className="sx-num">{NO_VALUE}</span></td>
                  <td className="sx-right"><span className="sx-num">{NO_VALUE}</span></td>
                  <td>
                    <span className="sx-pill">
                      <span className={`sx-dot${DOT_CLASS[c.status]}`} />
                      {STATUS_LABELS[c.status]}
                    </span>
                  </td>
                  <td>
                    <div className="sx-row-actions">
                      {can('COMPANY_UPDATE') && (
                        <button type="button" className="sx-icon-btn" aria-label={`Modifier ${c.name}`} disabled={edited !== null} onClick={() => setEdited({ company: c })}>
                          <Icon size={20}><path d="M4 20h4L19 9l-4-4L4 16z" /><path d="M13.5 6.5l4 4" /></Icon>
                        </button>
                      )}
                      {can('COMPANY_DELETE') && (
                        <button
                          type="button"
                          className="sx-icon-btn"
                          aria-label={`Supprimer ${c.name}`}
                          onClick={() => {
                            if (!window.confirm(`Supprimer la compagnie « ${c.name} » ?`)) {
                              return;
                            }
                            setError(null);
                            deleteCompany(c.id)
                              .then(() => setCompanies((current) => current.filter((x) => x.id !== c.id)))
                              .catch((e: Error) => setError(describeCompanyError(e, 'supprimer cette compagnie')));
                          }}
                        >
                          <Icon size={20}><path d="M4 7h16" /><path d="M9 7V4h6v3" /><path d="M6 7l1 13h10l1-13" /></Icon>
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {loading && <div className="sx-empty">Chargement…</div>}
          {!loading && rows.length === 0 && <div className="sx-empty">Aucune compagnie ne correspond à ces critères.</div>}
        </div>

        <div className="sx-table-foot">
          {rows.length} compagnie{rows.length > 1 ? 's' : ''}
        </div>
      </section>
    </>
  );
}
