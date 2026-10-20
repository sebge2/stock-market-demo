# Frontend - Stock Market Demo 💻

Application React pour l'application Stock Market Demo.

## 📋 Description

Frontend moderne (React 18 + TypeScript + Vite) qui fournit :
- Dashboard centralisé
- Gestion de portefeuille
- Affichage des cotations en temps réel
- Historique des transactions
- Interface responsive

## 🚀 Installation

### Prérequis
- Node.js 18+
- npm ou yarn

### Dépendances

Les dépendances sont installées via le package.json root.

```bash
npm install
```

## 🏃 Développement

```bash
# Lancer le dev server avec hot reload
npm run dev:frontend

# Le serveur écoute sur http://localhost:3000
```

## 🏗️ Build

```bash
# Build pour la production
npm run build:frontend

# Les fichiers générés sont dans dist/apps/frontend/
```

## 🔍 Linting

```bash
# Vérifier le code
npm run lint:frontend
```

## 🧪 Tests

```bash
# Exécuter les tests
npm run test:frontend
```

## 📁 Structure du Projet

```
frontend/
├── src/
│   ├── App.tsx              # Composant principal
│   ├── App.css              # Styles du composant
│   ├── main.tsx             # Point d'entrée
│   ├── style.css            # Styles globaux
│   ├── components/          # Composants réutilisables
│   ├── pages/               # Pages de l'application
│   ├── services/            # Services API
│   ├── hooks/               # Custom hooks
│   └── types/               # Type definitions
├── index.html               # HTML racine
├── tsconfig.json            # Config TypeScript
├── project.json             # Config Nx
├── package.json
└── README.md
```

## 🎯 Composants Principaux

### App
Composant racine affichant les services disponibles :
- Bank Service
- Stock Exchange

## 🔗 API Integration

À implémenter :

```typescript
// Exemple : appel au service Bank
const response = await fetch('http://localhost:8081/api/bank/accounts');
const data = await response.json();
```

## 🎨 Styling

- CSS pur (pas de framework CSS)
- Responsive design
- Dark/Light theme support (À implémenter)

## 🔄 Communication avec les Services

### Bank Service
```typescript
// Exemples
GET  http://localhost:8081/api/bank/accounts
POST http://localhost:8081/api/bank/accounts
GET  http://localhost:8081/api/bank/accounts/{id}/balance
```

### Stock Exchange Service
```typescript
// Exemples
GET  http://localhost:8082/api/exchange/stocks
GET  http://localhost:8082/api/exchange/prices/{symbol}
POST http://localhost:8082/api/exchange/orders
```

## 📚 Ressources

- [React Documentation](https://react.dev)
- [TypeScript Documentation](https://www.typescriptlang.org)
- [Vite Documentation](https://vitejs.dev)
- [Nx React Guide](https://nx.dev/packages/react)

## 🎯 À Implémenter

- [ ] Pages pour chaque service
- [ ] Composants réutilisables
- [ ] Services API clients
- [ ] State management (Context, Redux, etc.)
- [ ] Authentification
- [ ] Gestion des erreurs
- [ ] Loading states
- [ ] Theme switcher

## 👤 Auteur

Sébastien Gérard

## 📄 Licence

MIT
