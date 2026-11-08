import { useEffect, useState } from 'react';
import { useCan } from '../auth/permissions';
import { type Company, fetchCompanies } from '../companies/companies.api';
import { ApiError } from '../roles/roles.api';
import { DOT_CLASS, Icon, STATUS_HINT, changesOf, date, draftOf, type Draft } from './common';
import {
  type Security,
  type SecurityStatus,
  type SecurityType,
  SECURITY_STATUS_LABELS,
  SECURITY_TYPE_LABELS,
  createSecurity,
  describeSecurityError,
  fetchSecurity,
  updateSecurity,
} from './securities.api';

/** Props of the security creation and edition screen. */
interface SecurityEditPageProps {
  /** Identifier of the edited security, or `null` to create one. */
  id: string | null;
  /** Called when the services reject the access token. */
  onUnauthorized: () => void;
}

/** Required field marker. */
function Required() {
  return <span aria-hidden="true" style={{ color: '#FF8A5C' }}> *</span>;
}

/** Description of the status of a security, displayed in the side card. */
function statusSince(security: Security): string {
  switch (security.status) {
    case 'LISTED':
      return `depuis le ${date(security.listingDate)}`;
    case 'ADMISSION':
      return `admission prévue le ${date(security.listingDate)}`;
    case 'DELISTED':
      return `depuis le ${date(security.delistingDate ?? security.updatedAt)}`;
    default:
      return `depuis le ${date(security.updatedAt)}`;
  }
}

/** Security creation and edition screen: issuer and instrument, listing and capital, with the status actions. */
export function SecurityEditPage({ id, onUnauthorized }: SecurityEditPageProps) {
  const can = useCan();
  const creating = id === null;
  const [security, setSecurity] = useState<Security | undefined>(undefined);
  const [companies, setCompanies] = useState<Company[]>([]);
  const [draft, setDraft] = useState<Draft>(draftOf(undefined, ''));
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const canEdit = creating ? can('SECURITY_CREATE') : can('SECURITY_UPDATE');

  useEffect(() => {
    setLoading(true);
    setError(null);
    const companiesRequest = creating && can('COMPANY_LIST') ? fetchCompanies() : Promise.resolve([] as Company[]);
    Promise.all([creating ? Promise.resolve(undefined) : fetchSecurity(id), companiesRequest])
      .then(([loaded, loadedCompanies]) => {
        setSecurity(loaded);
        setCompanies(loadedCompanies);
        setDraft(draftOf(loaded, loadedCompanies[0]?.id ?? ''));
      })
      .catch((e: Error) => {
        if (e instanceof ApiError && e.status === 401) {
          onUnauthorized();
        }
        setError(e instanceof ApiError && e.status === 404 ? 'Titre introuvable.' : describeSecurityError(e, 'consulter ce titre'));
      })
      .finally(() => setLoading(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id, onUnauthorized]);

  const issued = draft.issuedShares.trim() === '' ? null : Number(draft.issuedShares);
  const outstanding = draft.outstandingShares.trim() === '' ? null : Number(draft.outstandingShares);
  const sharesInvalid = issued !== null && outstanding !== null && outstanding > issued;
  const valid =
    draft.companyId !== '' && draft.name.trim() !== '' && draft.ticker.trim() !== '' && draft.isin.trim() !== '' &&
    draft.currency.trim().length === 3 && draft.tradingCurrency.trim().length === 3 && draft.listingDate !== '' && !sharesInvalid;

  const save = () => {
    setSaving(true);
    setError(null);
    const task = creating ? createSecurity(changesOf(draft)) : updateSecurity(id, changesOf(draft));
    task
      .then(() => {
        window.location.hash = '#securities';
      })
      .catch((e: Error) => {
        setError(describeSecurityError(e, 'enregistrer ce titre'));
        setSaving(false);
      });
  };

  /** Changes the status of the saved security right away (suspension and resumption of the listing). */
  const changeStatus = (status: SecurityStatus) => {
    if (!security) {
      return;
    }
    setSaving(true);
    setError(null);
    updateSecurity(security.id, { ...changesOf(draftOf(security, '')), status })
      .then((saved) => {
        setSecurity(saved);
        setDraft(draftOf(saved, ''));
      })
      .catch((e: Error) => setError(describeSecurityError(e, 'modifier la cotation de ce titre')))
      .finally(() => setSaving(false));
  };

  /** Props shared by the text fields of the form. */
  const field = <K extends keyof Draft>(key: K) => ({
    value: draft[key] as string,
    readOnly: !canEdit,
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => setDraft({ ...draft, [key]: e.target.value }),
  });

  if (loading) {
    return <div className="sx-empty">Chargement…</div>;
  }
  if (!creating && !security) {
    return (
      <>
        <nav aria-label="Fil d’Ariane" className="sx-crumb"><a href="#securities">Titres</a></nav>
        {error && <div role="alert" className="sx-alert">{error}</div>}
      </>
    );
  }

  return (
    <>
      <header className="sx-page-header">
        <div className="sx-page-title">
          <nav aria-label="Fil d’Ariane" className="sx-crumb">
            <a href="#securities">
              <Icon size={16}><path d="M15 6l-6 6 6 6" /></Icon>
              Titres
            </a>
            {' / '}
            <span className="sx-crumb-current">{creating ? 'Nouveau titre' : security!.ticker}</span>
          </nav>
          <h1>{creating ? 'Nouveau titre' : `${security!.companyName} · ${security!.ticker}`}</h1>
        </div>
        <div className="sx-actions">
          <span className="sx-pill">
            <span className={`sx-dot${DOT_CLASS[draft.status]}`} />
            {SECURITY_STATUS_LABELS[draft.status]}
          </span>
          <a href="#securities" className="sx-btn sx-btn-ghost">Annuler</a>
          {canEdit && (
            <button type="button" className="sx-btn sx-btn-primary" disabled={saving || !valid} onClick={save}>
              <Icon><circle cx="12" cy="12" r="9" /><path d="M8 12.5l3 3 5-6" /></Icon>
              {saving ? 'Enregistrement…' : creating ? 'Créer le titre' : 'Enregistrer les modifications'}
            </button>
          )}
        </div>
      </header>

      {error && <div role="alert" className="sx-alert">{error}</div>}

      <div className="sx-columns">
        <div className="sx-detail-col">
          <section aria-label="Émetteur et instrument" className="sx-card">
            <p className="sx-card-title">Émetteur et instrument</p>
            <div className="sx-form-grid">
              <div className="sx-field">
                <label htmlFor="e-comp">Émetteur{creating && <Required />}</label>
                {creating ? (
                  <div className="sx-select">
                    <select id="e-comp" value={draft.companyId} onChange={(e) => setDraft({ ...draft, companyId: e.target.value })}>
                      {companies.map((c) => (
                        <option key={c.id} value={c.id}>{c.name}</option>
                      ))}
                    </select>
                  </div>
                ) : (
                  <>
                    <input id="e-comp" className="sx-input sx-input-readonly" readOnly value={security!.companyName} />
                    <span className="sx-field-hint">Non modifiable après la création.</span>
                  </>
                )}
              </div>
              <div className="sx-field">
                <label htmlFor="e-nom">Nom de l’instrument<Required /></label>
                <input id="e-nom" className="sx-input" maxLength={255} {...field('name')} />
              </div>
              <div className="sx-field">
                <label htmlFor="e-type">Type<Required /></label>
                <div className="sx-select">
                  <select id="e-type" disabled={!canEdit} value={draft.type} onChange={(e) => setDraft({ ...draft, type: e.target.value as SecurityType })}>
                    {(Object.keys(SECURITY_TYPE_LABELS) as SecurityType[]).map((t) => (
                      <option key={t} value={t}>{SECURITY_TYPE_LABELS[t]}</option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="sx-field">
                <label htmlFor="e-tick">Ticker<Required /></label>
                <input id="e-tick" className="sx-input sx-input-mono" maxLength={20} {...field('ticker')} />
                <span className="sx-field-hint">Unique sur la place, 20 caractères max.</span>
              </div>
              <div className="sx-field sx-form-wide">
                <label htmlFor="e-isin">Code ISIN<Required /></label>
                <input id="e-isin" className="sx-input sx-input-mono" maxLength={12} {...field('isin')} />
                <span className="sx-field-hint">2 lettres, 9 caractères alphanumériques, 1 chiffre.</span>
              </div>
            </div>
          </section>

          <section aria-label="Cotation" className="sx-card">
            <p className="sx-card-title">Cotation</p>
            <div className="sx-form-grid">
              <div className="sx-field">
                <label htmlFor="e-cur">Devise de référence<Required /></label>
                <input id="e-cur" className="sx-input sx-input-mono" maxLength={3} {...field('currency')} />
                <span className="sx-field-hint">Code ISO 4217, 3 lettres.</span>
              </div>
              <div className="sx-field">
                <label htmlFor="e-tcur">Devise de négociation<Required /></label>
                <input id="e-tcur" className="sx-input sx-input-mono" maxLength={3} {...field('tradingCurrency')} />
                <span className="sx-field-hint">Code ISO 4217, 3 lettres.</span>
              </div>
              <div className="sx-field">
                <label htmlFor="e-ld">Date d’admission<Required /></label>
                <input id="e-ld" className="sx-input" type="date" {...field('listingDate')} />
              </div>
              <div className="sx-field">
                <label htmlFor="e-dd">Date de radiation</label>
                <input id="e-dd" className="sx-input" type="date" placeholder="Aucune" {...field('delistingDate')} />
                <span className="sx-field-hint">Vide tant que le titre est coté.</span>
              </div>
              <div className="sx-field sx-form-wide">
                <span className="sx-field-label">Statut de cotation</span>
                <div role="group" aria-label="Statut de cotation" className="sx-toggle sx-toggle-wrap">
                  {(Object.keys(SECURITY_STATUS_LABELS) as SecurityStatus[]).map((s) => (
                    <button key={s} type="button" aria-pressed={draft.status === s} disabled={!canEdit} onClick={() => setDraft({ ...draft, status: s })}>
                      {s === 'ADMISSION' ? 'En admission' : SECURITY_STATUS_LABELS[s]}
                    </button>
                  ))}
                </div>
                <span className="sx-field-hint">{STATUS_HINT}</span>
              </div>
            </div>
          </section>

          <section aria-label="Capital" className="sx-card">
            <p className="sx-card-title">Capital</p>
            <div className="sx-form-grid">
              <div className="sx-field">
                <label htmlFor="e-iss">Titres émis</label>
                <input id="e-iss" className="sx-input" type="number" min={0} {...field('issuedShares')} />
              </div>
              <div className="sx-field">
                <label htmlFor="e-out">Titres en circulation</label>
                <input id="e-out" className="sx-input" type="number" min={0} aria-invalid={sharesInvalid} {...field('outstandingShares')} />
                <span className="sx-field-hint" style={sharesInvalid ? { color: '#FF9B9B' } : undefined}>Inférieur ou égal aux titres émis.</span>
              </div>
            </div>
          </section>
        </div>

        {security && (
          <div className="sx-aside-col">
            <section aria-label="Statut" className="sx-card">
              <p className="sx-card-title">Statut</p>
              <div className="sx-card-head">
                <span className="sx-pill">
                  <span className={`sx-dot${DOT_CLASS[security.status]}`} />
                  {SECURITY_STATUS_LABELS[security.status]}
                </span>
                <span className="sx-hint">{statusSince(security)}</span>
              </div>
              {can('SECURITY_UPDATE') && security.status === 'LISTED' && (
                <button type="button" className="sx-btn sx-btn-secondary" disabled={saving} onClick={() => changeStatus('SUSPENDED')}>
                  <Icon><rect x="6" y="5" width="4" height="14" rx="1" /><rect x="14" y="5" width="4" height="14" rx="1" /></Icon>
                  Suspendre la cotation
                </button>
              )}
              {can('SECURITY_UPDATE') && security.status === 'SUSPENDED' && (
                <button type="button" className="sx-btn sx-btn-secondary" disabled={saving} onClick={() => changeStatus('LISTED')}>
                  <Icon><path d="M7 5l12 7-12 7z" /></Icon>
                  Reprendre la cotation
                </button>
              )}
            </section>
          </div>
        )}
      </div>
    </>
  );
}
