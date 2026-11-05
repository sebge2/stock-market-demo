import { useMemo } from 'react';
import type { Department } from '../employees/employees.api';
import { type Employee, type EmployeeStatus, STATUS_CLASS } from '../employees/employees.data';

/** Number of departments listed individually. */
const TOP_DEPARTMENTS = 8;

/** Statuses in display order, with the color of their share bar. */
const STATUS_BARS: { status: EmployeeStatus; color: string }[] = [
  { status: 'Actif', color: '#0B7A55' },
  { status: 'En congé', color: '#1A56B0' },
  { status: 'Inactif', color: '#5B6B82' },
  { status: 'Suspendu', color: '#8A5A00' },
];

/** Props of the dashboard. */
interface DashboardPageProps {
  /** All employees. */
  employees: Employee[];
  /** All departments. */
  departments: Department[];
}

/** Formats a number with the French thousands separator. */
const fmt = (value: number) => value.toLocaleString('fr-FR');

/** Formats today's date, e.g. "Jeudi 5 novembre 2026". */
function today(): string {
  const text = new Date().toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' });
  return text.charAt(0).toUpperCase() + text.slice(1);
}

/** A key figure card. */
function Kpi({ label, value, children }: { label: string; value: number; children: React.ReactNode }) {
  return (
    <div className="kpi">
      <div className="field-label">{label}</div>
      <div className="kpi-value">{fmt(value)}</div>
      <div className="kpi-foot">{children}</div>
    </div>
  );
}

/** Dashboard: key figures, headcount per department and status breakdown. */
export function DashboardPage({ employees, departments }: DashboardPageProps) {
  const count = (status: EmployeeStatus) => employees.filter((e) => e.status === status).length;

  const perDepartment = useMemo(() => {
    const counts = new Map<string, number>();
    employees.forEach((e) => e.departmentId && counts.set(e.departmentId, (counts.get(e.departmentId) ?? 0) + 1));
    return departments
      .map((d) => ({ department: d, count: counts.get(d.id) ?? 0 }))
      .sort((a, b) => b.count - a.count || a.department.name.localeCompare(b.department.name, 'fr'));
  }, [employees, departments]);

  const top = perDepartment.slice(0, TOP_DEPARTMENTS);
  const others = perDepartment.slice(TOP_DEPARTMENTS);
  const max = Math.max(1, ...top.map((d) => d.count));
  const total = employees.length;

  return (
    <>
      <header className="page-header">
        <div className="page-title">
          <div className="eyebrow">{today()}</div>
          <h1>Tableau de bord</h1>
        </div>
      </header>

      <section aria-label="Indicateurs clés" className="kpis">
        <Kpi label="Effectif total" value={total}>
          <span className="muted">dont {fmt(count('Inactif'))} inactif{count('Inactif') > 1 ? 's' : ''}</span>
        </Kpi>
        <Kpi label="Employés actifs" value={count('Actif')}>
          <span className={`badge ${STATUS_CLASS['Actif']}`}>Actif</span>
        </Kpi>
        <Kpi label="En congé" value={count('En congé')}>
          <span className={`badge ${STATUS_CLASS['En congé']}`}>En congé</span>
        </Kpi>
        <Kpi label="Suspendus" value={count('Suspendu')}>
          <span className={`badge ${STATUS_CLASS['Suspendu']}`}>Suspendu</span>
        </Kpi>
      </section>

      <div className="dashboard-columns">
        <section aria-labelledby="t-dep" className="panel panel-stack dashboard-wide">
          <div className="card-header card-header-flush">
            <h2 id="t-dep">Effectifs par département</h2>
            <a href="#departments" className="card-link">
              Voir tous les départements
            </a>
          </div>
          <div className="bars">
            {top.map(({ department, count: n }) => (
              <div key={department.id} className="bar-row">
                <a href={`#department-${department.id}`} className="bar-label">
                  {department.name}
                </a>
                <div aria-hidden="true" className="bar-track">
                  <div className="bar-fill" style={{ width: `${(n / max) * 100}%`, background: '#1A56B0' }} />
                </div>
                <div className="cell-right mono">{fmt(n)}</div>
              </div>
            ))}
            {top.length === 0 && <div className="muted">Aucun département.</div>}
          </div>
          {others.length > 0 && (
            <div className="panel-foot">
              {fmt(others.length)} autre{others.length > 1 ? 's' : ''} département{others.length > 1 ? 's' : ''} ·{' '}
              {fmt(others.reduce((sum, d) => sum + d.count, 0))} employé{others.reduce((sum, d) => sum + d.count, 0) > 1 ? 's' : ''}
            </div>
          )}
        </section>

        <section aria-labelledby="t-stat" className="panel panel-stack dashboard-side">
          <h2 id="t-stat">Répartition par statut</h2>
          <div className="bars bars-loose">
            {STATUS_BARS.map(({ status, color }) => {
              const n = count(status);
              const share = total === 0 ? 0 : (n / total) * 100;
              return (
                <div key={status} className="status-row">
                  <span className={`badge ${STATUS_CLASS[status]}`}>{status}</span>
                  <div aria-hidden="true" className="bar-track">
                    <div className="bar-fill" style={{ width: `${share}%`, minWidth: 4, background: color }} />
                  </div>
                  <div className="cell-right mono">{fmt(n)}</div>
                  <div className="cell-right mono muted small">{share.toLocaleString('fr-FR', { minimumFractionDigits: 1, maximumFractionDigits: 1 })} %</div>
                </div>
              );
            })}
          </div>
          <div className="panel-foot">
            {fmt(total)} employé{total > 1 ? 's' : ''} au total
          </div>
        </section>
      </div>
    </>
  );
}
