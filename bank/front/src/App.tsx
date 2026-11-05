import { useEffect, useMemo, useState } from 'react';
import './App.css';
import { CALLBACK_PATH, completeLogin, getAccessToken, login, logout } from './auth/auth';
import { ApiError, fetchEmployees } from './employees/employees.api';
import {
  type Employee,
  type EmployeeStatus,
  SENIORITY_FILTERS,
  formatDate,
  initials,
  yearsSince,
} from './employees/employees.data';

/** Number of employees displayed per page. */
const PAGE_SIZE = 10;

/** Status tabs of the directory; `null` means no status filter. */
const TABS: { label: string; status: EmployeeStatus | null }[] = [
  { label: 'Tous', status: null },
  { label: 'Actifs', status: 'Actif' },
  { label: 'Inactifs', status: 'Inactif' },
  { label: 'Suspendus', status: 'Suspendu' },
  { label: 'En congé', status: 'En congé' },
];

/** CSS modifier of each status badge. */
const STATUS_CLASS: Record<EmployeeStatus, string> = {
  Actif: 'badge-active',
  Inactif: 'badge-inactive',
  Suspendu: 'badge-suspended',
  'En congé': 'badge-leave',
};

/** Icon used in the sidebar navigation. */
function NavIcon({ children }: { children: React.ReactNode }) {
  return (
    <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      {children}
    </svg>
  );
}

/** Chevron icon used in selects. */
function Chevron() {
  return (
    <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true" className="select-chevron">
      <path d="M5 7.5l5 5 5-5" />
    </svg>
  );
}

/** Formats a number with the French thousands separator. */
const fmt = (value: number) => value.toLocaleString('fr-FR');

/** Bank back-office: employee directory. */
export function App() {
  const [tab, setTab] = useState<EmployeeStatus | null>(null);
  const [query, setQuery] = useState('');
  const [department, setDepartment] = useState('Tous');
  const [seniority, setSeniority] = useState('Toutes');
  const [page, setPage] = useState(1);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [authenticated, setAuthenticated] = useState(getAccessToken() !== null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (window.location.pathname !== CALLBACK_PATH) {
      return;
    }
    completeLogin()
      .then(() => setAuthenticated(true))
      .catch((e: Error) => setError(e.message))
      .finally(() => window.history.replaceState(null, '', '/'));
  }, []);

  useEffect(() => {
    if (!authenticated) {
      return;
    }
    setLoading(true);
    setError(null);
    fetchEmployees()
      .then(setEmployees)
      .catch((e: Error) => {
        if (e instanceof ApiError && e.status === 401) {
          logout();
          setAuthenticated(false);
        }
        setError(e instanceof ApiError && e.status === 403 ? "Vous n'avez pas la permission de consulter les employés." : e.message);
      })
      .finally(() => setLoading(false));
  }, [authenticated]);

  const departments = useMemo(
    () => ['Tous', ...Array.from(new Set(employees.map((e) => e.department))).sort()],
    [employees],
  );

  const filtered = useMemo(() => {
    const needle = query.trim().toLowerCase();
    const matchesSeniority = SENIORITY_FILTERS[seniority];
    return employees.filter(
      (e) =>
        (tab === null || e.status === tab) &&
        (department === 'Tous' || e.department === department) &&
        matchesSeniority(yearsSince(e.hireDate)) &&
        (needle === '' ||
          [e.name, e.matricule, e.position].some((v) => v.toLowerCase().includes(needle))),
    );
  }, [employees, tab, query, department, seniority]);

  const pageCount = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  const currentPage = Math.min(page, pageCount);
  const start = (currentPage - 1) * PAGE_SIZE;
  const rows = filtered.slice(start, start + PAGE_SIZE);

  const resetPage = () => setPage(1);

  if (!authenticated) {
    return (
      <div className="login">
        <div className="login-card">
          <h1>Gerardson Capital</h1>
          <p>Connectez-vous pour accéder à l'annuaire des employés.</p>
          {error && <p role="alert" className="alert">{error}</p>}
          <button type="button" className="btn-primary" onClick={() => void login()}>
            Se connecter
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="layout">
      <aside className="sidebar">
        <div className="brand">
          <svg width="40" height="40" viewBox="0 0 64 64" fill="none" role="img" aria-label="Symbole Gerardson Capital" className="brand-logo">
            <path d="M44.9 16.7A20 20 0 1 0 52 32H34" stroke="#FFFFFF" strokeWidth="8" />
            <circle cx="53" cy="14" r="4.5" fill="#C99A2E" />
          </svg>
          <div className="brand-text">
            <div className="brand-name">Gerardson</div>
            <div className="brand-sub">CAPITAL</div>
          </div>
        </div>

        <nav aria-label="Navigation principale" className="nav">
          <a href="#dashboard" className="nav-link">
            <NavIcon>
              <rect x="3" y="3" width="6" height="6" rx="1.5" />
              <rect x="11" y="3" width="6" height="6" rx="1.5" />
              <rect x="3" y="11" width="6" height="6" rx="1.5" />
              <rect x="11" y="11" width="6" height="6" rx="1.5" />
            </NavIcon>
            Tableau de bord
          </a>
          <a href="#employees" aria-current="page" className="nav-link nav-link-active">
            <NavIcon>
              <circle cx="8" cy="7" r="3" />
              <path d="M2.5 16.5c.6-2.8 2.8-4.5 5.5-4.5s4.9 1.7 5.5 4.5" />
              <circle cx="14.5" cy="7.5" r="2.25" />
              <path d="M14.5 12c2 0 3.4 1.2 3.9 3.2" />
            </NavIcon>
            Employés
          </a>
          <a href="#departments" className="nav-link">
            <NavIcon>
              <path d="M4 17V4.5A1.5 1.5 0 0 1 5.5 3h6A1.5 1.5 0 0 1 13 4.5V17" />
              <path d="M13 8h1.5A1.5 1.5 0 0 1 16 9.5V17" />
              <path d="M2.5 17h15" />
              <path d="M7 6.5h.01M10 6.5h.01M7 9.5h.01M10 9.5h.01M7 12.5h.01M10 12.5h.01" />
            </NavIcon>
            Départements
          </a>
        </nav>

        <div className="user">
          <button
            type="button"
            className="logout"
            onClick={() => {
              logout();
              setEmployees([]);
              setAuthenticated(false);
            }}
          >
            Se déconnecter
          </button>
        </div>
      </aside>

      <main className="main">
        <header className="page-header">
          <div className="page-title">
            <div className="eyebrow">Ressources humaines</div>
            <h1>Annuaire des employés</h1>
          </div>
          <button type="button" className="btn-primary">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" aria-hidden="true">
              <path d="M10 4v12M4 10h12" />
            </svg>
            Nouvel employé
          </button>
        </header>

        {error && <div role="alert" className="alert">{error}</div>}

        <section aria-label="Liste des employés" className="card" aria-busy={loading}>
          <div className="tabs">
            {TABS.map(({ label, status }) => {
              const count = status === null ? employees.length : employees.filter((e) => e.status === status).length;
              const active = tab === status;
              return (
                <button
                  key={label}
                  type="button"
                  aria-current={active ? 'page' : undefined}
                  className={`tab${active ? ' tab-active' : ''}`}
                  onClick={() => {
                    setTab(status);
                    resetPage();
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
                aria-label="Rechercher par nom, matricule ou poste"
                placeholder="Nom, matricule ou poste"
                value={query}
                onChange={(e) => {
                  setQuery(e.target.value);
                  resetPage();
                }}
              />
            </div>
            <label className="select">
              Département :
              <select
                value={department}
                onChange={(e) => {
                  setDepartment(e.target.value);
                  resetPage();
                }}
              >
                {departments.map((d) => (
                  <option key={d}>{d}</option>
                ))}
              </select>
              <Chevron />
            </label>
            <label className="select">
              Ancienneté :
              <select
                value={seniority}
                onChange={(e) => {
                  setSeniority(e.target.value);
                  resetPage();
                }}
              >
                {Object.keys(SENIORITY_FILTERS).map((s) => (
                  <option key={s}>{s}</option>
                ))}
              </select>
              <Chevron />
            </label>
            <div className="total">
              {fmt(filtered.length)} employé{filtered.length > 1 ? 's' : ''}
            </div>
          </div>

          <div className="table-scroll">
            <div className="table">
              <div className="row row-head">
                <div>Employé</div>
                <div>Poste</div>
                <div>Département</div>
                <div className="cell-right">Date d'entrée</div>
                <div>Statut</div>
                <div />
              </div>

              {rows.map((e) => (
                <div key={e.matricule} className="row row-body">
                  <div className="person">
                    <div className="avatar">{initials(e.name)}</div>
                    <div>
                      <a href={`#employee-${e.matricule}`} className="person-name">{e.name}</a>
                      <div className="matricule">{e.matricule}</div>
                    </div>
                  </div>
                  <div>{e.position}</div>
                  <div>{e.department}</div>
                  <div className="cell-right mono">{formatDate(e.hireDate)}</div>
                  <div>
                    <span className={`badge ${STATUS_CLASS[e.status]}`}>{e.status}</span>
                  </div>
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="#5B6B82" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                    <path d="M7.5 4.5L13 10l-5.5 5.5" />
                  </svg>
                </div>
              ))}

              {loading && <div className="empty">Chargement…</div>}
              {!loading && rows.length === 0 && <div className="empty">Aucun employé ne correspond à ces critères.</div>}
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
      </main>
    </div>
  );
}

export default App;
