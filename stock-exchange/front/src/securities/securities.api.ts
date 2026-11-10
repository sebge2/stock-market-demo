import { getAccessToken } from '../auth/auth';
import { COMPANY_API_URL } from '../companies/companies.api';
import { ApiError } from '../roles/roles.api';

/** Trading status of a security. */
export type SecurityStatus = 'LISTED' | 'SUSPENDED' | 'ADMISSION' | 'DELISTED';

/** Type of financial instrument. */
export type SecurityType = 'STOCK' | 'PREFERRED_STOCK' | 'ETF' | 'BOND';

/** Security as returned by the company service. */
export interface Security {
  /** Security identifier. */
  id: string;
  /** Identifier of the issuing company. */
  companyId: string;
  /** Name of the issuing company. */
  companyName: string;
  /** Name of the security. */
  name: string;
  /** ISIN code. */
  isin: string;
  /** Type of instrument. */
  type: SecurityType;
  /** Currency of the security (ISO 4217). */
  currency: string;
  /** Ticker symbol. */
  ticker: string;
  /** Trading currency (ISO 4217). */
  tradingCurrency: string;
  /** Date of first listing (ISO format). */
  listingDate: string;
  /** Date of delisting (ISO format), if any. */
  delistingDate: string | null;
  /** Number of issued shares, if applicable. */
  issuedShares: number | null;
  /** Number of outstanding shares, if applicable. */
  outstandingShares: number | null;
  /** Number of outstanding shares freely available for trading, if applicable. */
  freeFloatShares: number | null;
  /** Price of the most recent trade, in the trading currency. */
  lastPrice: number | null;
  /** Date and time of the most recent trade (ISO format). */
  lastTradeAt: string | null;
  /** Trading status. */
  status: SecurityStatus;
  /** Creation timestamp (ISO format). */
  createdAt: string;
  /** Last update timestamp (ISO format). */
  updatedAt: string;
}

/** Data entered to create or edit a security. */
export type SecurityChanges = Omit<Security, 'id' | 'companyName' | 'lastPrice' | 'lastTradeAt' | 'createdAt' | 'updatedAt'>;

/** French labels of the statuses. */
export const SECURITY_STATUS_LABELS: Record<SecurityStatus, string> = {
  LISTED: 'Coté',
  SUSPENDED: 'Suspendu',
  ADMISSION: 'Admission en cours',
  DELISTED: 'Radié',
};

/** French labels of the types. */
export const SECURITY_TYPE_LABELS: Record<SecurityType, string> = {
  STOCK: 'Action',
  PREFERRED_STOCK: 'Action de préférence',
  ETF: 'ETF',
  BOND: 'Obligation',
};

/** Calls the securities endpoints of the company service with the current access token. */
async function call<T>(method: 'GET' | 'POST' | 'PUT' | 'DELETE', path: string, body?: unknown): Promise<T> {
  const response = await fetch(`${COMPANY_API_URL}/securities${path}`, {
    method,
    headers: {
      Authorization: `Bearer ${getAccessToken() ?? ''}`,
      ...(body === undefined ? {} : { 'Content-Type': 'application/json' }),
    },
    body: body === undefined ? undefined : JSON.stringify(body),
  });
  if (!response.ok) {
    throw new ApiError(response.status, `Erreur ${response.status} lors de l'appel à /securities${path}`);
  }
  return (response.status === 204 ? undefined : await response.json()) as T;
}

/** Loads all the securities. */
export function fetchSecurities(): Promise<Security[]> {
  return call<Security[]>('GET', '');
}

/** Loads a security. */
export function fetchSecurity(id: string): Promise<Security> {
  return call<Security>('GET', `/${id}`);
}

/** Creates a security. */
export function createSecurity(changes: SecurityChanges): Promise<Security> {
  return call<Security>('POST', '', changes);
}

/** Updates a security. */
export function updateSecurity(id: string, changes: SecurityChanges): Promise<Security> {
  return call<Security>('PUT', `/${id}`, changes);
}

/** Deletes a security. */
export function deleteSecurity(id: string): Promise<void> {
  return call<void>('DELETE', `/${id}`);
}

/** Message displayed for a failed call of the securities endpoints. */
export function describeSecurityError(e: Error, action: string): string {
  if (e instanceof ApiError) {
    if (e.status === 403) {
      return `Vous n'avez pas la permission de ${action}.`;
    }
    if (e.status === 400) {
      return 'Les informations saisies ne sont pas valides.';
    }
    if (e.status === 409) {
      return "Cet ISIN ou ce symbole est déjà utilisé, ou le titre est encore référencé (cours).";
    }
  }
  return e.message;
}
