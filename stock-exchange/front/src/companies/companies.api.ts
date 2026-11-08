import { getAccessToken } from '../auth/auth';
import { ApiError } from '../roles/roles.api';

/** Base URL of the company service. */
const COMPANY_API_URL = import.meta.env.VITE_COMPANY_API_URL ?? 'http://localhost:8087/api/company';

/** Status of a company on the exchange. */
export type CompanyStatus = 'LISTED' | 'SUSPENDED' | 'ADMISSION' | 'DELISTED';

/** Sector of a company (GICS classification). */
export type SectorType =
  | 'COMMUNICATION_SERVICES'
  | 'CONSUMER_DISCRETIONARY'
  | 'CONSUMER_STAPLES'
  | 'ENERGY'
  | 'FINANCIALS'
  | 'HEALTH_CARE'
  | 'INDUSTRIALS'
  | 'INFORMATION_TECHNOLOGY'
  | 'MATERIALS'
  | 'REAL_ESTATE'
  | 'UTILITIES';

/** Company as returned by the company service. */
export interface Company {
  /** Company identifier. */
  id: string;
  /** Company name. */
  name: string;
  /** ISO 3166-1 alpha-2 country code. */
  countryCode: string;
  /** Web site, if any. */
  website: string | null;
  /** Logo URL, if any. */
  logoUrl: string | null;
  /** Sector of the company. */
  sectorType: SectorType;
  /** Status on the exchange. */
  status: CompanyStatus;
  /** Creation timestamp (ISO format). */
  createdAt: string;
  /** Last update timestamp (ISO format). */
  updatedAt: string;
}

/** Data entered to create or edit a company. */
export interface CompanyChanges {
  /** Company name. */
  name: string;
  /** ISO 3166-1 alpha-2 country code. */
  countryCode: string;
  /** Sector of the company. */
  sectorType: SectorType;
  /** Status on the exchange. */
  status: CompanyStatus;
}

/** French labels of the statuses. */
export const STATUS_LABELS: Record<CompanyStatus, string> = {
  LISTED: 'Cotée',
  SUSPENDED: 'Suspendue',
  ADMISSION: 'Admission en cours',
  DELISTED: 'Radiée',
};

/** French labels of the sectors. */
export const SECTOR_LABELS: Record<SectorType, string> = {
  COMMUNICATION_SERVICES: 'Communication',
  CONSUMER_DISCRETIONARY: 'Consommation discrétionnaire',
  CONSUMER_STAPLES: 'Consommation de base',
  ENERGY: 'Énergie',
  FINANCIALS: 'Finance',
  HEALTH_CARE: 'Santé',
  INDUSTRIALS: 'Industrie',
  INFORMATION_TECHNOLOGY: 'Technologie',
  MATERIALS: 'Matériaux',
  REAL_ESTATE: 'Immobilier',
  UTILITIES: 'Services aux collectivités',
};

/** Calls the company service with the current access token. */
async function call<T>(method: 'GET' | 'POST' | 'PUT' | 'DELETE', path: string, body?: unknown): Promise<T> {
  const response = await fetch(`${COMPANY_API_URL}${path}`, {
    method,
    headers: {
      Authorization: `Bearer ${getAccessToken() ?? ''}`,
      ...(body === undefined ? {} : { 'Content-Type': 'application/json' }),
    },
    body: body === undefined ? undefined : JSON.stringify(body),
  });
  if (!response.ok) {
    throw new ApiError(response.status, `Erreur ${response.status} lors de l'appel à ${path}`);
  }
  return (response.status === 204 ? undefined : await response.json()) as T;
}

/** Loads all the companies. */
export function fetchCompanies(): Promise<Company[]> {
  return call<Company[]>('GET', '/companies');
}

/** Creates a company. */
export function createCompany(changes: CompanyChanges): Promise<Company> {
  return call<Company>('POST', '/companies', changes);
}

/** Updates a company, keeping its web site and logo. */
export function updateCompany(company: Company, changes: CompanyChanges): Promise<Company> {
  return call<Company>('PUT', `/companies/${company.id}`, { ...changes, website: company.website, logoUrl: company.logoUrl });
}

/** Deletes a company. */
export function deleteCompany(id: string): Promise<void> {
  return call<void>('DELETE', `/companies/${id}`);
}

/** Message displayed for a failed call of the company service. */
export function describeCompanyError(e: Error, action: string): string {
  if (e instanceof ApiError) {
    if (e.status === 403) {
      return `Vous n'avez pas la permission de ${action}.`;
    }
    if (e.status === 400) {
      return 'Les informations saisies ne sont pas valides.';
    }
    if (e.status === 409) {
      return 'Cette compagnie existe déjà ou est encore utilisée.';
    }
  }
  return e.message;
}
