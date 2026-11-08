import type { Security, SecurityChanges, SecurityStatus, SecurityType } from './securities.api';

/** Colour class of the dot of each status. */
export const DOT_CLASS: Record<SecurityStatus, string> = {
  LISTED: '',
  SUSPENDED: ' sx-dot-suspended',
  ADMISSION: ' sx-dot-admission',
  DELISTED: ' sx-dot-off',
};

/** Hint displayed under the status of the forms. */
export const STATUS_HINT = 'Une valeur suspendue n’accepte plus de nouveaux ordres jusqu’à sa reprise.';

/** Formats an ISO date as a French date. */
export const date = (isoDate: string) => new Date(isoDate).toLocaleDateString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric' });

/** Icon wrapper shared by the screen buttons. */
export function Icon({ size = 18, children }: { size?: number; children: React.ReactNode }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      {children}
    </svg>
  );
}

/** Values of the security form, as typed. */
export interface Draft {
  companyId: string;
  name: string;
  type: SecurityType;
  isin: string;
  ticker: string;
  currency: string;
  tradingCurrency: string;
  listingDate: string;
  delistingDate: string;
  issuedShares: string;
  outstandingShares: string;
  status: SecurityStatus;
}

/** Builds the draft of the form from a saved security, or an empty one. */
export function draftOf(security: Security | undefined, companyId: string): Draft {
  return {
    companyId: security?.companyId ?? companyId,
    name: security?.name ?? '',
    type: security?.type ?? 'STOCK',
    isin: security?.isin ?? '',
    ticker: security?.ticker ?? '',
    currency: security?.currency ?? 'EUR',
    tradingCurrency: security?.tradingCurrency ?? 'EUR',
    listingDate: security?.listingDate ?? new Date().toISOString().slice(0, 10),
    delistingDate: security?.delistingDate ?? '',
    issuedShares: security?.issuedShares?.toString() ?? '',
    outstandingShares: security?.outstandingShares?.toString() ?? '',
    status: security?.status ?? 'ADMISSION',
  };
}

/** Converts the draft of the form to the data sent to the service. */
export function changesOf(draft: Draft): SecurityChanges {
  const shares = (value: string) => (value.trim() === '' ? null : Number(value));
  return {
    companyId: draft.companyId,
    name: draft.name.trim(),
    type: draft.type,
    isin: draft.isin.trim().toUpperCase(),
    ticker: draft.ticker.trim().toUpperCase(),
    currency: draft.currency.trim().toUpperCase(),
    tradingCurrency: draft.tradingCurrency.trim().toUpperCase(),
    listingDate: draft.listingDate,
    delistingDate: draft.delistingDate === '' ? null : draft.delistingDate,
    issuedShares: shares(draft.issuedShares),
    outstandingShares: shares(draft.outstandingShares),
    status: draft.status,
  };
}
