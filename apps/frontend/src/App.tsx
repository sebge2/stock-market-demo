import './App.css';

export function App() {
  return (
    <div className="app">
      <header className="header">
        <h1>📈 Stock Market Demo</h1>
        <p>Banking and Trading Platform</p>
      </header>

      <main className="main">
        <div className="services">
          <div className="service-card">
            <h2>🏦 Bank Service</h2>
            <p>Manage your accounts and transactions</p>
            <button>Open Bank</button>
          </div>

          <div className="service-card">
            <h2>📊 Stock Exchange</h2>
            <p>Trade stocks and manage your portfolio</p>
            <button>Open Exchange</button>
          </div>
        </div>
      </main>
    </div>
  );
}

export default App;
