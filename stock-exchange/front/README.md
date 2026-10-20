# Frontend Stock Exchange 📊

Application React pour la gestion de la bourse et du trading.

## 📋 Description

Frontend dédié au service de bourse permettant:
- Consultation des prix en temps réel
- Gestion du portefeuille
- Trading (achat/vente d'actions)
- Analytics et historique

## 🚀 Développement

### Lancer le serveur

```bash
npm run dev:exchange
# Serveur sur http://localhost:3001 (ou 3000 si indisponible)
```

### Build

```bash
npm run build:exchange
# Output: dist/apps/frontend-exchange/
```

### Tests

```bash
npm run test:exchange
```

### Lint

```bash
npm run lint:exchange
```

## 🏗️ Structure

```
frontend-exchange/
├── src/
│   ├── App.tsx              # Composant principal
│   ├── App.css              # Styles
│   ├── main.tsx             # Point d'entrée
│   └── style.css            # Styles globaux
├── index.html               # Template HTML
├── project.json             # Config Nx
├── tsconfig.json            # Config TypeScript
└── README.md                # Ce fichier
```

## 🔗 API Integration

Se connecte au service Stock Exchange sur le port 8082 :

```typescript
const apiUrl = 'http://localhost:8082/api/exchange';
```

## 📚 Documentation

- [README principal](../../README.md)
- [MONOREPO.md](../../MONOREPO.md)
- [AGENTS.md](../../AGENTS.md)
