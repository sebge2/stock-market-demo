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
  userId: string | null;
}

/** Department as returned by the API. */
interface DepartmentApi {
  id: string;
  name: string;
  description: string | null;
  status: 'ACTIVE' | 'INACTIVE';
  managerId: string | null;
  updatedAt: string;
}

/** Department displayed in the directory. */
export interface Department {
  /** Department identifier. */
  id: string;
  /** Department name. */
  name: string;
  /** Short description. */
  description: string;
  /** Whether the department is active. */
  active: boolean;
  /** Identifier of the managing employee, if any. */
  managerId: string | null;
  /** Last update date (ISO format). */
  updatedAt: string;
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

/** Sends a JSON body to the employee service with the current access token. */
async function send<T>(method: 'POST' | 'PUT', path: string, body: unknown): Promise<T> {
  const response = await fetch(`${EMPLOYEE_API_URL}${path}`, {
    method,
    headers: { Authorization: `Bearer ${getAccessToken() ?? ''}`, 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (!response.ok) {
    throw new ApiError(response.status, `Erreur ${response.status} lors de l'appel à ${path}`);
  }
  return (await response.json()) as T;
}

/** Maps a department returned by the API to the displayed model. */
function toDepartment(d: DepartmentApi): Department {
  return {
    id: d.id,
    name: d.name,
    description: d.description ?? '',
    active: d.status === 'ACTIVE',
    managerId: d.managerId,
    updatedAt: d.updatedAt.slice(0, 10),
  };
}

/** Data entered to create a department. */
export interface NewDepartment {
  /** Department name. */
  name: string;
  /** Short description. */
  description: string;
  /** Whether the department is active. */
  active: boolean;
  /** Identifier of the managing employee, if any. */
  managerId: string | null;
}

/** Converts the entered department data to the API payload. */
function toDepartmentRequest(department: NewDepartment) {
  return {
    name: department.name,
    description: department.description || null,
    status: department.active ? 'ACTIVE' : 'INACTIVE',
    managerId: department.managerId,
  };
}

/** Creates a department and returns it. */
export async function createDepartment(department: NewDepartment): Promise<Department> {
  return toDepartment(await send<DepartmentApi>('POST', '/departments', toDepartmentRequest(department)));
}

/** Updates a department and returns it. */
export async function updateDepartment(id: string, department: NewDepartment): Promise<Department> {
  return toDepartment(await send<DepartmentApi>('PUT', `/departments/${id}`, toDepartmentRequest(department)));
}

/** Loads all departments. */
export async function fetchDepartments(): Promise<Department[]> {
  const departments = await get<DepartmentApi[]>('/departments');
  return departments.map(toDepartment);
}

/** Maps an employee returned by the API to the displayed model. */
function toEmployee(e: EmployeeApi, departmentNames: Map<string, string>): Employee {
  return {
    firstName: e.firstName,
    lastName: e.lastName,
    name: `${e.firstName} ${e.lastName}`,
    id: e.id,
    userId: e.userId,
    departmentId: e.departmentId,
    email: e.email,
    position: e.jobTitle ?? '—',
    jobTitle: e.jobTitle ?? '',
    department: (e.departmentId && departmentNames.get(e.departmentId)) || '—',
    hireDate: e.createdAt.slice(0, 10),
    status: STATUS_LABELS[e.status],
  };
}

/** Data entered to edit an employee. */
export interface EmployeeChanges {
  /** First name. */
  firstName: string;
  /** Last name. */
  lastName: string;
  /** Email address. */
  email: string;
  /** Job title (may be empty). */
  jobTitle: string;
  /** Employment status. */
  status: EmployeeStatus;
  /** Identifier of the department, if any. */
  departmentId: string | null;
}

/** Converts the entered employee data to the API payload. */
function toEmployeeRequest(changes: EmployeeChanges) {
  return {
    firstName: changes.firstName,
    lastName: changes.lastName,
    email: changes.email,
    jobTitle: changes.jobTitle || null,
    status: (Object.keys(STATUS_LABELS) as EmployeeApi['status'][]).find((k) => STATUS_LABELS[k] === changes.status),
    departmentId: changes.departmentId,
  };
}

/** Creates an employee (and its user account in the user service) and returns it. */
export async function createEmployee(changes: EmployeeChanges, departments: Department[]): Promise<Employee> {
  const created = await send<EmployeeApi>('POST', '/employees', toEmployeeRequest(changes));
  return toEmployee(created, new Map(departments.map((d) => [d.id, d.name])));
}

/** Updates an employee and returns it, with its department name resolved from the given departments. */
export async function updateEmployee(id: string, changes: EmployeeChanges, departments: Department[]): Promise<Employee> {
  const updated = await send<EmployeeApi>('PUT', `/employees/${id}`, toEmployeeRequest(changes));
  return toEmployee(updated, new Map(departments.map((d) => [d.id, d.name])));
}

/** Loads all employees, with their department name resolved from the given departments. */
export async function fetchEmployees(departments: Department[]): Promise<Employee[]> {
  const names = new Map(departments.map((d) => [d.id, d.name]));
  const employees: EmployeeApi[] = [];
  for (let page = 0; ; page++) {
    const result = await get<PageApi<EmployeeApi>>(`/employees?page=${page}&size=${PAGE_SIZE}`);
    employees.push(...result.content);
    if (result.last) {
      break;
    }
  }
  return employees.map((e) => toEmployee(e, names));
}
