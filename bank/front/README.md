# Frontend Bank 🏦

Application React pour la gestion des comptes bancaires.

## 📋 Description

Frontend dédié au service bancaire permettant:
- Gestion des comptes clients
- Consultation des soldes
- Historique des transactions
- Transfers entre comptes

## 🚀 Développement

### Lancer le serveur

```bash
npm run dev:bank
# Serveur sur http://localhost:3000
```

### Build

```bash
npm run build:bank
# Output: dist/apps/frontend-bank/
```

### Tests

```bash
npm run test:bank
```

### Lint

```bash
npm run lint:bank
```

## 🏗️ Structure

```
frontend-bank/
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

Se connecte au service Bank sur le port 8081 :

```typescript
const apiUrl = 'http://localhost:8081/api/bank';
```

## 📚 Documentation

- [README principal](../../README.md)
- [MONOREPO.md](../../MONOREPO.md)
- [AGENTS.md](../../AGENTS.md)
