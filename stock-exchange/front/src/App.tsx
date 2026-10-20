import './App.css';

export function App() {
  return (
    <div className="app exchange-app">
      <header className="header">
        <h1>📊 Stock Exchange</h1>
        <p>Trade stocks and manage your portfolio</p>
      </header>

      <main className="main">
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
      </main>
    </div>
  );
}

export default App;
