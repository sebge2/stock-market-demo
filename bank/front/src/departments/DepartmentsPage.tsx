import { useMemo, useState } from 'react';
import type { Department } from '../employees/employees.api';
import { type Employee, initials } from '../employees/employees.data';

/** Status tabs; `null` means no filter. */
const TABS: { label: string; active: boolean | null }[] = [
  { label: 'Tous', active: null },
  { label: 'Actifs', active: true },
  { label: 'Inactifs', active: false },
];

/** Props of the departments screen. */
interface DepartmentsPageProps {
  /** All departments. */
  departments: Department[];
  /** All employees, used for headcount and manager names. */
  employees: Employee[];
  /** Whether data is being loaded. */
  loading: boolean;
}

/** Departments screen: list of departments with manager and headcount. */
export function DepartmentsPage({ departments, employees, loading }: DepartmentsPageProps) {
  const [tab, setTab] = useState<boolean | null>(null);
  const [query, setQuery] = useState('');

  const headcount = useMemo(() => {
    const counts = new Map<string, number>();
    employees.forEach((e) => e.departmentId && counts.set(e.departmentId, (counts.get(e.departmentId) ?? 0) + 1));
    return counts;
  }, [employees]);

  const managers = useMemo(() => new Map(employees.map((e) => [e.id, e.name])), [employees]);

  const rows = useMemo(() => {
    const needle = query.trim().toLowerCase();
    return departments.filter(
      (d) =>
        (tab === null || d.active === tab) &&
        (needle === '' || [d.name, d.description].some((v) => v.toLowerCase().includes(needle))),
    );
  }, [departments, tab, query]);

  return (
    <>
      <header className="page-header">
        <div className="page-title">
          <div className="eyebrow">Ressources humaines</div>
          <h1>Départements</h1>
        </div>
      </header>

      <section aria-label="Liste des départements" className="card" aria-busy={loading}>
        <div className="tabs">
          {TABS.map(({ label, active }) => {
            const count = active === null ? departments.length : departments.filter((d) => d.active === active).length;
            const current = tab === active;
            return (
              <button
                key={label}
                type="button"
                aria-current={current ? 'page' : undefined}
                className={`tab${current ? ' tab-active' : ''}`}
                onClick={() => setTab(active)}
              >
                {label}
                <span className="tab-count">{count.toLocaleString('fr-FR')}</span>
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
              aria-label="Rechercher par nom ou description"
              placeholder="Nom ou description"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
          </div>
          <div className="total">
            {rows.length.toLocaleString('fr-FR')} département{rows.length > 1 ? 's' : ''}
          </div>
        </div>

        <div className="table-scroll">
          <div className="table">
            <div className="row row-dept row-head">
              <div>Département</div>
              <div>Responsable</div>
              <div className="cell-right">Effectif</div>
              <div>Statut</div>
            </div>

            {rows.map((d) => {
              const manager = d.managerId ? managers.get(d.managerId) : undefined;
              return (
                <div key={d.id} className="row row-dept row-body">
                  <div>
                    <div className="person-name">{d.name}</div>
                    <div className="matricule dept-description">{d.description || '—'}</div>
                  </div>
                  <div className="person">
                    {manager ? (
                      <>
                        <div className="avatar">{initials(manager)}</div>
                        <div className="person-name">{manager}</div>
                      </>
                    ) : (
                      '—'
                    )}
                  </div>
                  <div className="cell-right mono">{(headcount.get(d.id) ?? 0).toLocaleString('fr-FR')}</div>
                  <div>
                    <span className={`badge ${d.active ? 'badge-active' : 'badge-inactive'}`}>{d.active ? 'Actif' : 'Inactif'}</span>
                  </div>
                </div>
              );
            })}

            {loading && <div className="empty">Chargement…</div>}
            {!loading && rows.length === 0 && <div className="empty">Aucun département ne correspond à ces critères.</div>}
          </div>
        </div>
      </section>
    </>
  );
}
