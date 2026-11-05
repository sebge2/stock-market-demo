import { useMemo, useState } from 'react';
import type { Department, NewDepartment } from '../employees/employees.api';
import type { Employee } from '../employees/employees.data';
import { DepartmentForm } from './DepartmentForm';

/** Sort options of the list. */
const SORTS = ['Effectif', 'Nom'] as const;

/** Status filter options of the list. */
const STATUSES = ['Tous', 'Actif', 'Inactif'] as const;

/** Props of the departments screen. */
interface DepartmentsPageProps {
  /** All departments. */
  departments: Department[];
  /** All employees, used for headcount and manager names. */
  employees: Employee[];
  /** Whether data is being loaded. */
  loading: boolean;
  /** Creates a department; rejects when the creation fails. */
  onCreate: (department: NewDepartment) => Promise<void>;
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

/** Departments screen: summary figures and list of departments with manager and headcount. */
export function DepartmentsPage({ departments, employees, loading, onCreate }: DepartmentsPageProps) {
  const [query, setQuery] = useState('');
  const [sort, setSort] = useState<(typeof SORTS)[number]>('Effectif');
  const [status, setStatus] = useState<(typeof STATUSES)[number]>('Tous');
  const [creating, setCreating] = useState(false);
  const headcount = useMemo(() => {
    const counts = new Map<string, number>();
    employees.forEach((e) => e.departmentId && counts.set(e.departmentId, (counts.get(e.departmentId) ?? 0) + 1));
    return counts;
  }, [employees]);

  const managers = useMemo(() => new Map(employees.map((e) => [e.id, e.name])), [employees]);

  const rows = useMemo(() => {
    const needle = query.trim().toLowerCase();
    return departments
      .filter(
        (d) =>
          (status === 'Tous' || d.active === (status === 'Actif')) &&
          (needle === '' || d.name.toLowerCase().includes(needle)),
      )
      .sort((a, b) =>
        sort === 'Effectif' ? (headcount.get(b.id) ?? 0) - (headcount.get(a.id) ?? 0) : a.name.localeCompare(b.name, 'fr'),
      );
  }, [departments, query, sort, status, headcount]);

  const activeCount = departments.filter((d) => d.active).length;

  return (
    <>
      <header className="page-header">
        <div className="page-title">
          <div className="eyebrow">Organisation</div>
          <h1>Départements</h1>
        </div>
        <button type="button" className="btn-primary" disabled={creating} onClick={() => setCreating(true)}>
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" aria-hidden="true">
            <path d="M10 4v12M4 10h12" />
          </svg>
          Nouveau département
        </button>
      </header>

      {creating && (
        <DepartmentForm
          title="Nouveau département"
          submitLabel="Créer"
          forbiddenMessage="Vous n'avez pas la permission de créer un département."
          initial={{ name: '', description: '', active: true, managerId: null }}
          employees={employees}
          onSubmit={onCreate}
          onCancel={() => setCreating(false)}
        />
      )}

      <section aria-label="Synthèse" className="kpis">
        <div className="kpi">
          <div className="field-label">Départements</div>
          <div className="kpi-value">{fmt(departments.length)}</div>
        </div>
        <div className="kpi">
          <div className="field-label">Effectif total</div>
          <div className="kpi-value">{fmt(employees.length)}</div>
        </div>
        <div className="kpi">
          <div className="field-label">Départements actifs</div>
          <div className="kpi-value">
            {fmt(activeCount)} / {fmt(departments.length)}
          </div>
        </div>
      </section>

      <section aria-label="Liste des départements" className="card" aria-busy={loading}>
        <div className="filters">
          <div className="search">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="#5B6B82" strokeWidth="1.75" strokeLinecap="round" aria-hidden="true" className="search-icon">
              <circle cx="9" cy="9" r="5.5" />
              <path d="M13.25 13.25L17 17" />
            </svg>
            <input
              type="search"
              aria-label="Rechercher un département"
              placeholder="Rechercher un département"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
          </div>
          <label className="select">
            Trier par :
            <select value={sort} onChange={(e) => setSort(e.target.value as (typeof SORTS)[number])}>
              {SORTS.map((s) => (
                <option key={s}>{s}</option>
              ))}
            </select>
            <Chevron />
          </label>
          <label className="select">
            Statut :
            <select value={status} onChange={(e) => setStatus(e.target.value as (typeof STATUSES)[number])}>
              {STATUSES.map((s) => (
                <option key={s}>{s}</option>
              ))}
            </select>
            <Chevron />
          </label>
        </div>

        <div className="table-scroll">
          <div className="table table-dept">
            <div className="row row-dept row-head">
              <div>Département</div>
              <div>Responsable</div>
              <div className="cell-right">Effectif</div>
              <div>Statut</div>
              <div />
            </div>

            {rows.map((d) => (
              <div key={d.id} className="row row-dept row-body">
                <div>
                  <a href={`#department-${d.id}`} className="person-name">{d.name}</a>
                </div>
                <div>{(d.managerId && managers.get(d.managerId)) || '—'}</div>
                <div className="cell-right mono">{fmt(headcount.get(d.id) ?? 0)}</div>
                <div>
                  <span className={`badge ${d.active ? 'badge-active' : 'badge-inactive'}`}>{d.active ? 'Actif' : 'Inactif'}</span>
                </div>
                <a href={`#department-${d.id}`} aria-label={`Ouvrir le département ${d.name}`} className="row-link">
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="#5B6B82" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                    <path d="M7.5 4.5L13 10l-5.5 5.5" />
                  </svg>
                </a>
              </div>
            ))}

            {loading && <div className="empty">Chargement…</div>}
            {!loading && rows.length === 0 && <div className="empty">Aucun département ne correspond à ces critères.</div>}
          </div>
        </div>
      </section>
    </>
  );
}
