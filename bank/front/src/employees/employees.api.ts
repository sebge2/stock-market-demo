import { getAccessToken } from '../auth/auth';
import type { Employee, EmployeeStatus } from './employees.data';

/** Base URL of the employee service. */
const EMPLOYEE_API_URL = import.meta.env.VITE_EMPLOYEE_API_URL ?? 'http://localhost:8083/api/employee';
/** Page size used when loading all employees. */
const PAGE_SIZE = 200;

/** Error raised when the API call is rejected. */
export class ApiError extends Error {
  /** Creates the error with the HTTP status. */
  constructor(public readonly status: number, message: string) {
    super(message);
  }
}

/** Employee as returned by the API. */
interface EmployeeApi {
  firstName: string;
  lastName: string;
  email: string;
  jobTitle: string | null;
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED' | 'ON_LEAVE';
  departmentId: string | null;
  createdAt: string;
  id: string;
}

/** Department as returned by the API. */
interface DepartmentApi {
  id: string;
  name: string;
}

/** Page of results as returned by the API. */
interface PageApi<T> {
  content: T[];
  last: boolean;
}

const STATUS_LABELS: Record<EmployeeApi['status'], EmployeeStatus> = {
  ACTIVE: 'Actif',
  INACTIVE: 'Inactif',
  SUSPENDED: 'Suspendu',
  ON_LEAVE: 'En congé',
};

/** Calls the employee service with the current access token. */
async function get<T>(path: string): Promise<T> {
  const response = await fetch(`${EMPLOYEE_API_URL}${path}`, {
    headers: { Authorization: `Bearer ${getAccessToken() ?? ''}` },
  });
  if (!response.ok) {
    throw new ApiError(response.status, `Erreur ${response.status} lors de l'appel à ${path}`);
  }
  return (await response.json()) as T;
}

/** Loads all employees, with their department name resolved. */
export async function fetchEmployees(): Promise<Employee[]> {
  const departments = await get<DepartmentApi[]>('/departments');
  const names = new Map(departments.map((d) => [d.id, d.name]));
  const employees: EmployeeApi[] = [];
  for (let page = 0; ; page++) {
    const result = await get<PageApi<EmployeeApi>>(`/employees?page=${page}&size=${PAGE_SIZE}`);
    employees.push(...result.content);
    if (result.last) {
      break;
    }
  }
  return employees.map((e) => ({
    name: `${e.firstName} ${e.lastName}`,
    matricule: e.id.slice(0, 8).toUpperCase(),
    position: e.jobTitle ?? '—',
    department: (e.departmentId && names.get(e.departmentId)) || '—',
    hireDate: e.createdAt.slice(0, 10),
    status: STATUS_LABELS[e.status],
  }));
}
