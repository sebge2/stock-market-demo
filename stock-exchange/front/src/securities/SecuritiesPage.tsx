import { useEffect, useMemo, useState } from 'react';
import { useCan } from '../auth/permissions';
import { ApiError } from '../roles/roles.api';
import { DOT_CLASS, Icon, date, dateTime } from './common';
import {
  type Security,
  type SecurityStatus,
  type SecurityType,
  SECURITY_STATUS_LABELS,
  SECURITY_TYPE_LABELS,
  deleteSecurity,
  describeSecurityError,
  fetchSecurities,
} from './securities.api';

/** Props of the securities screen. */
interface SecuritiesPageProps {
  /** Called when the services reject the access token. */
  onUnauthorized: () => void;
}

/** Tabs filtering the list by status; `null` means no filter. */
const TABS: { label: string; status: SecurityStatus | null }[] = [
  { label: 'Tous', status: null },
  { label: 'Cotés', status: 'LISTED' },
  { label: 'Suspendus', status: 'SUSPENDED' },
  { label: 'En admission', status: 'ADMISSION' },
  { label: 'Radiés', status: 'DELISTED' },
];

/** Labels of the indicators, by status. */
const KPI_LABELS: Record<SecurityStatus, string> = {
  LISTED: 'Cotés',
  SUSPENDED: 'Suspendus',
  ADMISSION: 'En admission',
  DELISTED: 'Radiés',
};

/** Colour of the figure of each indicator. */
const KPI_COLOR: Record<SecurityStatus, string> = {
  LISTED: '#EAF0FA',
  SUSPENDED: '#FF8A5C',
  ADMISSION: '#F5C451',
  DELISTED: '#9AA9C4',
};

/** Placeholder for market figures not yet exposed by the securities API. */
const NO_VALUE = '—';

/** Formats a trade price in the security trading currency. */
function price(value: number, currency: string): string {
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency, maximumFractionDigits: 4 }).format(value);
}

/** Calculates and formats market capitalization from the last price and shares outstanding. */
function marketCapitalization(security: Security): string {
  if (security.lastPrice === null || security.outstandingShares === null) {
    return NO_VALUE;
  }
  return price(security.lastPrice * security.outstandingShares, security.tradingCurrency);
}

/** Securities screen: indicators and filterable list of the securities of the exchange. */
export function SecuritiesPage({ onUnauthorized }: SecuritiesPageProps) {
  const can = useCan();
  const [securities, setSecurities] = useState<Security[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [tab, setTab] = useState<SecurityStatus | null>(null);
  const [query, setQuery] = useState('');
  const [issuer, setIssuer] = useState('');
  const [type, setType] = useState<SecurityType | ''>('');

  useEffect(() => {
    fetchSecurities()
      .then(setSecurities)
      .catch((e: Error) => {
        if (e instanceof ApiError && e.status === 401) {
          onUnauthorized();
        }
        setError(describeSecurityError(e, 'consulter les titres'));
      })
      .finally(() => setLoading(false));
  }, [onUnauthorized]);

  const count = (status: SecurityStatus) => securities.filter((s) => s.status === status).length;

  const issuers = useMemo(() => {
    const names = new Map(securities.map((s) => [s.companyId, s.companyName]));
    return Array.from(names, ([id, name]) => ({ id, name })).sort((a, b) => a.name.localeCompare(b.name, 'fr'));
  }, [securities]);

  const rows = useMemo(() => {
    const needle = query.trim().toLowerCase();
    return securities
      .filter(
        (s) =>
          (tab === null || s.status === tab) &&
          (issuer === '' || s.companyId === issuer) &&
          (type === '' || s.type === type) &&
          (needle === '' || [s.name, s.ticker, s.isin].some((v) => v.toLowerCase().includes(needle))),
      )
      .sort((a, b) => a.ticker.localeCompare(b.ticker, 'fr'));
  }, [securities, tab, issuer, type, query]);

  const issuerCount = new Set(rows.map((s) => s.companyId)).size;

  return (
    <>
      <header className="sx-page-header">
        <div className="sx-page-title">
          <nav aria-label="Fil d’Ariane" className="sx-crumb">
            {can('COMPANY_LIST') ? <a href="#companies">Compagnies cotées</a> : <span>Référentiel</span>}
            {' / '}
            <span className="sx-crumb-current">Titres</span>
          </nav>
          <h1>Titres de la place</h1>
        </div>
        <div className="sx-actions">
          {can('SECURITY_CREATE') && (
            <a href="#securities/new" className="sx-btn sx-btn-primary">
              <Icon><path d="M12 5v14 M5 12h14" /></Icon>
              Nouveau titre
            </a>
          )}
        </div>
      </header>

      {error && <div role="alert" className="sx-alert">{error}</div>}

      <div className="sx-kpis">
        {(Object.keys(KPI_LABELS) as SecurityStatus[]).map((status) => (
          <div key={status} className="sx-kpi">
            <p className="sx-card-title">{KPI_LABELS[status]}</p>
            <span className="sx-kpi-value" style={{ color: KPI_COLOR[status] }}>{count(status)}</span>
          </div>
        ))}
      </div>

      <section aria-label="Liste des titres" className="sx-card" aria-busy={loading}>
        <div role="tablist" aria-label="Filtrer par statut" className="sx-tabs">
          {TABS.map(({ label, status }) => (
            <button key={label} type="button" role="tab" className="sx-tab" aria-selected={tab === status} onClick={() => setTab(status)}>
              {label}
              <span className="sx-count">{status === null ? securities.length : count(status)}</span>
            </button>
          ))}
        </div>

        <div className="sx-filters">
          <div className="sx-search">
            <label htmlFor="q-titres" className="sx-sr-only">Rechercher par nom, ticker ou ISIN</label>
            <span className="sx-search-icon"><Icon size={20}><circle cx="11" cy="11" r="6.5" /><path d="M20 20l-4.2-4.2" /></Icon></span>
            <input id="q-titres" className="sx-input" type="search" placeholder="Rechercher par nom, ticker ou ISIN" value={query} onChange={(e) => setQuery(e.target.value)} />
          </div>
          <label className="sx-select">
            Émetteur&nbsp;:
            <select value={issuer} onChange={(e) => setIssuer(e.target.value)}>
              <option value="">Tous</option>
              {issuers.map((i) => (
                <option key={i.id} value={i.id}>{i.name}</option>
              ))}
            </select>
          </label>
          <label className="sx-select">
            Type&nbsp;:
            <select value={type} onChange={(e) => setType(e.target.value as SecurityType | '')}>
              <option value="">Tous</option>
              {(Object.keys(SECURITY_TYPE_LABELS) as SecurityType[]).map((t) => (
                <option key={t} value={t}>{SECURITY_TYPE_LABELS[t]}</option>
              ))}
            </select>
          </label>
        </div>

        <div className="sx-table-wrap">
          <table className="sx-table sx-table-wide">
            <caption>Liste des titres de la place</caption>
            <thead>
              <tr>
                <th scope="col">Titre</th>
                <th scope="col">Émetteur</th>
                <th scope="col">Type</th>
                <th scope="col">ISIN</th>
                <th scope="col">Devise réf. / négo.</th>
                <th scope="col" className="sx-right">Capitalisation</th>
                <th scope="col" className="sx-right">Flottant</th>
                <th scope="col" className="sx-right">Dernier cours / échange</th>
                <th scope="col">Admission</th>
                <th scope="col">Radiation</th>
                <th scope="col">Statut</th>
                <th scope="col" />
              </tr>
            </thead>
            <tbody>
              {rows.map((s) => (
                <tr key={s.id} className={s.status === 'DELISTED' ? 'sx-row-muted' : s.status === 'LISTED' ? 'sx-row-on' : undefined}>
                  <th scope="row">
                    <a href={`#securities/${s.id}`} className="sx-company-name sx-link">{s.ticker}</a>
                    <div className="sx-company-sub">{s.name}</div>
                  </th>
                  <td className="sx-muted">{s.companyName}</td>
                  <td><span className="sx-type-pill">{SECURITY_TYPE_LABELS[s.type]}</span></td>
                  <td><span className="sx-code">{s.isin}</span></td>
                  <td className="sx-muted">{s.currency} / {s.tradingCurrency}</td>
                  <td className="sx-right"><span className="sx-num">{marketCapitalization(s)}</span></td>
                  <td className="sx-right"><span className="sx-num">{s.freeFloatShares?.toLocaleString('fr-FR') ?? NO_VALUE}</span></td>
                  <td className="sx-right">
                    <span className="sx-num">{s.lastPrice === null ? NO_VALUE : price(s.lastPrice, s.tradingCurrency)}</span>
                    {s.lastTradeAt && <div className="sx-company-sub">{dateTime(s.lastTradeAt)}</div>}
                  </td>
                  <td className="sx-muted">{date(s.listingDate)}</td>
                  <td className="sx-muted">{s.delistingDate ? date(s.delistingDate) : '—'}</td>
                  <td>
                    <span className="sx-pill">
                      <span className={`sx-dot${DOT_CLASS[s.status]}`} />
                      {SECURITY_STATUS_LABELS[s.status]}
                    </span>
                  </td>
                  <td>
                    <div className="sx-row-actions">
                      <a href={`#securities/${s.id}`} className="sx-icon-btn" aria-label={`${can('SECURITY_UPDATE') ? 'Modifier' : 'Ouvrir'} ${s.ticker}`}>
                        <Icon size={20}><path d="M9 6l6 6-6 6" /></Icon>
                      </a>
                      {can('SECURITY_DELETE') && (
                        <button
                          type="button"
                          className="sx-icon-btn"
                          aria-label={`Supprimer ${s.ticker}`}
                          onClick={() => {
                            if (!window.confirm(`Supprimer le titre « ${s.ticker} » ?`)) {
                              return;
                            }
                            setError(null);
                            deleteSecurity(s.id)
                              .then(() => setSecurities((current) => current.filter((x) => x.id !== s.id)))
                              .catch((e: Error) => setError(describeSecurityError(e, 'supprimer ce titre')));
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
          {!loading && rows.length === 0 && <div className="sx-empty">Aucun titre ne correspond à ces critères.</div>}
        </div>

        <div className="sx-table-foot">
          {rows.length} titre{rows.length > 1 ? 's' : ''} · {issuerCount} émetteur{issuerCount > 1 ? 's' : ''}
        </div>
      </section>
    </>
  );
}
