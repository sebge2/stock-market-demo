/** Employment status of an employee. */
export type EmployeeStatus = 'Actif' | 'Inactif' | 'Suspendu' | 'En congé';

/** Employee row displayed in the directory. */
export interface Employee {
  /** Employee identifier. */
  id: string;
  /** Identifier of the department, if any. */
  departmentId: string | null;
  /** Employee first name. */
  firstName: string;
  /** Employee last name. */
  lastName: string;
  /** Employee display name. */
  name: string;
  /** Email address. */
  email: string;
  /** Employee registration number. */
  matricule: string;
  /** Job title ('—' when none). */
  position: string;
  /** Job title as stored (empty when none). */
  jobTitle: string;
  /** Department name. */
  department: string;
  /** Hiring date (ISO format). */
  hireDate: string;
  /** Employment status. */
  status: EmployeeStatus;
}

/** CSS modifier of each status badge. */
export const STATUS_CLASS: Record<EmployeeStatus, string> = {
  Actif: 'badge-active',
  Inactif: 'badge-inactive',
  Suspendu: 'badge-suspended',
  'En congé': 'badge-leave',
};

/** Seniority filter options, keyed by label with the matching predicate on years of service. */
export const SENIORITY_FILTERS: Record<string, (years: number) => boolean> = {
  Toutes: () => true,
  "Moins d'un an": (years) => years < 1,
  '1 à 5 ans': (years) => years >= 1 && years < 5,
  'Plus de 5 ans': (years) => years >= 5,
};

/** Returns the number of full years elapsed since the given ISO date. */
export function yearsSince(isoDate: string, now: Date = new Date()): number {
  const hired = new Date(isoDate);
  return (now.getTime() - hired.getTime()) / (365.25 * 24 * 3600 * 1000);
}

/** Formats an ISO date as dd/MM/yyyy. */
export function formatDate(isoDate: string): string {
  const [year, month, day] = isoDate.split('-');
  return `${day}/${month}/${year}`;
}

/** Returns the initials of a name (first letters of the first two words). */
export function initials(name: string): string {
  return name
    .split(' ')
    .slice(0, 2)
    .map((part) => part[0])
    .join('')
    .toUpperCase();
}
