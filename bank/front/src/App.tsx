import './App.css';

export function App() {
  return (
    <div className="app bank-app">
      <header className="header">
        <h1>🏦 Bank Service</h1>
        <p>Manage your accounts and transactions</p>
      </header>

      <main className="main">
        <div className="features">
          <div className="feature-card">
            <h2>💳 My Accounts</h2>
            <p>View and manage all your bank accounts</p>
            <button>View Accounts</button>
          </div>

          <div className="feature-card">
            <h2>💰 Transfers</h2>
            <p>Send money between accounts</p>
            <button>Make Transfer</button>
          </div>

          <div className="feature-card">
            <h2>📊 Transactions</h2>
            <p>View your transaction history</p>
            <button>View History</button>
          </div>

          <div className="feature-card">
            <h2>⚙️ Settings</h2>
            <p>Manage account settings and preferences</p>
            <button>Settings</button>
          </div>
        </div>
      </main>
    </div>
  );
}

export default App;
