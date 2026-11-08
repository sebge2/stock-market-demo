import { useCallback, useEffect, useMemo, useState } from 'react';
import './App.css';
import './theme.css';
import { CALLBACK_PATH, completeLogin, getAccessToken, login, logout } from './auth/auth';
import { PermissionsProvider } from './auth/permissions';
import { fetchProfile, initialsOf, type Profile } from './auth/profile';
import { CompaniesPage } from './companies/CompaniesPage';
import { RolesPage } from './roles/RolesPage';

/** Logo of SGXchange. */
function Logo({ size }: { size: number }) {
  return (
    <svg width={size} height={size} viewBox="0 0 64 64" role="img" aria-label="Symbole SGXchange" fill="none" strokeLinecap="round" strokeLinejoin="round" strokeWidth="6">
      <path d="M13 12 L51 50" stroke="#5B8CFF" />
      <path d="M37 50 H51 V36" stroke="#5B8CFF" />
      <path d="M13 52 L51 14" stroke="#070B14" strokeWidth="13" />
      <path d="M13 52 L51 14" stroke="#3DDC97" />
      <path d="M37 14 H51 V28" stroke="#3DDC97" />
    </svg>
  );
}

/** Icon used in the sidebar navigation. */
function NavIcon({ children }: { children: React.ReactNode }) {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      {children}
    </svg>
  );
}

/** Home screen of the exchange. */
function HomePage() {
  return (
    <div className="app exchange-app">
      <header className="header">
        <h1>📊 Stock Exchange</h1>
        <p>Trade stocks and manage your portfolio</p>
      </header>

      <div className="features">
        <div className="feature-card">
          <h2>📈 Market Data</h2>
          <p>View real-time stock prices and trends</p>
          <button>View Market</button>
        </div>

        <div className="feature-card">
          <h2>💼 Portfolio</h2>
          <p>Manage your stock portfolio</p>
          <button>My Portfolio</button>
        </div>

        <div className="feature-card">
          <h2>🛒 Trading</h2>
          <p>Buy and sell stocks</p>
          <button>Trade Now</button>
        </div>

        <div className="feature-card">
          <h2>📊 Analytics</h2>
          <p>Analyze your investments and performance</p>
          <button>View Analytics</button>
        </div>
      </div>
    </div>
  );
}

/** Application shell: login, sidebar navigation and screens, filtered by the permissions of the user. */
export function App() {
  const [route, setRoute] = useState(window.location.hash);
  const [authenticated, setAuthenticated] = useState(getAccessToken() !== null);
  const [profile, setProfile] = useState<Profile | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (window.location.pathname !== CALLBACK_PATH) {
      return;
    }
    completeLogin()
      .then(() => setAuthenticated(true))
      .catch((e: Error) => setError(e.message))
      .finally(() => window.history.replaceState(null, '', '/'));
  }, []);

  useEffect(() => {
    const onHashChange = () => setRoute(window.location.hash);
    window.addEventListener('hashchange', onHashChange);
    return () => window.removeEventListener('hashchange', onHashChange);
  }, []);

  useEffect(() => {
    if (!authenticated) {
      setProfile(null);
      return;
    }
    fetchProfile().then(setProfile).catch(() => setProfile(null));
  }, [authenticated]);

  const onUnauthorized = useCallback(() => {
    logout();
    setAuthenticated(false);
  }, []);

  const permissions = useMemo(() => new Set(profile?.permissions ?? []), [profile]);
  const canRoles = permissions.has('ROLE_LIST') || permissions.has('PERMISSION_LIST');
  const canCompanies = permissions.has('COMPANY_LIST');
  const onRoles = route === '#roles';
  const onCompanies = route === '#companies';
  const onHome = !onRoles && !onCompanies;

  if (!authenticated) {
    return (
      <div className="sx-login">
        <div className="sx-card">
          <Logo size={48} />
          <h1>Connexion</h1>
          <p>Connectez-vous pour accéder au back-office de la place d’échange.</p>
          {error && <div role="alert" className="sx-alert">{error}</div>}
          <button type="button" className="sx-btn sx-btn-primary" onClick={() => void login()}>
            Se connecter
          </button>
        </div>
      </div>
    );
  }

  return (
    <PermissionsProvider permissions={permissions}>
      <div className="sx-layout">
        <aside className="sx-sidebar">
          <div className="sx-brand">
            <Logo size={32} />
            <div className="sx-brand-text">
              <span className="sx-brand-name"><b>SGX</b>change</span>
              <span className="sx-mono-label">Back-office</span>
            </div>
          </div>

          <nav aria-label="Navigation principale" className="sx-nav">
            <span className="sx-mono-label">Exploitation</span>
            <a href="#home" aria-current={onHome ? 'page' : undefined} className={`sx-nav-link${onHome ? ' sx-nav-link-active' : ''}`}>
              <NavIcon>
                <path d="M3 17 L9 11 l4 4 L21 7" />
                <path d="M15 7 h6 v6" />
              </NavIcon>
              Accueil
            </a>
            {canCompanies && (
              <a href="#companies" aria-current={onCompanies ? 'page' : undefined} className={`sx-nav-link${onCompanies ? ' sx-nav-link-active' : ''}`}>
                <NavIcon>
                  <path d="M4 21V5a1 1 0 0 1 1-1h8a1 1 0 0 1 1 1v16" />
                  <path d="M14 10h5a1 1 0 0 1 1 1v10" />
                  <path d="M3 21h18" />
                  <path d="M8 8h2 M8 12h2 M8 16h2" />
                </NavIcon>
                Compagnies cotées
              </a>
            )}
            {canRoles && (
              <a href="#roles" aria-current={onRoles ? 'page' : undefined} className={`sx-nav-link${onRoles ? ' sx-nav-link-active' : ''}`}>
                <NavIcon>
                  <path d="M12 3l7 2.6v5.4c0 4.4-3 7.6-7 9-4-1.4-7-4.6-7-9V5.6z" />
                  <path d="M8.75 12l2.25 2.25 4-4.25" />
                </NavIcon>
                Rôles et permissions
              </a>
            )}
          </nav>

          <div className="sx-user">
            <div className="sx-user-card">
              <span className="sx-avatar">{profile ? initialsOf(profile.fullName) : ''}</span>
              <div className="sx-user-info">
                <span className="sx-user-name">{profile?.fullName ?? ''}</span>
                <span className="sx-user-sub">{profile?.email ?? ''}</span>
              </div>
            </div>
            <button
              type="button"
              className="sx-btn sx-btn-secondary"
              onClick={() => {
                logout();
                setAuthenticated(false);
              }}
            >
              Se déconnecter
            </button>
          </div>
        </aside>

        <main className="sx-main">
          {!profile ? (
            <div className="sx-empty">Chargement du profil…</div>
          ) : onCompanies ? (
            canCompanies ? (
              <CompaniesPage onUnauthorized={onUnauthorized} />
            ) : (
              <div role="alert" className="sx-alert">Vous n'avez pas la permission d'accéder à cet écran.</div>
            )
          ) : onRoles ? (
            canRoles ? (
              <RolesPage onUnauthorized={onUnauthorized} />
            ) : (
              <div role="alert" className="sx-alert">Vous n'avez pas la permission d'accéder à cet écran.</div>
            )
          ) : (
            <HomePage />
          )}
        </main>
      </div>
    </PermissionsProvider>
  );
}

export default App;
