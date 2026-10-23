# Monorepo Structure avec Nx 📦

Documentation de la structure et de l'organisation du monorepo Stock Market Demo utilisant **Nx** pour gérer frontend ET backend avec Maven.

## 📁 Structure du Projet

```
stock-market-demo/
│
├── bank/                         Service Bank complet
│   ├── front/                    React Frontend (Nx-managed)
│   │   ├── src/
│   │   │   ├── App.tsx
│   │   │   ├── main.tsx
│   │   │   └── style.css
│   │   ├── vite.config.ts        Vite configuration
│   │   ├── tsconfig.json
│   │   ├── project.json          Nx configuration
│   │   ├── index.html
│   │   └── README.md
│   │
│   ├── backend/                  Java Spring Boot Backend (Nx-managed via Maven)
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/stockmarket/bank/
│   │   │   │   │   └── BankApplication.java
│   │   │   │   └── resources/
│   │   │   │       └── application.yml
│   │   │   └── test/
│   │   ├── project.json          Nx configuration (Maven integration)
│   │   └── README.md
│   │
│   ├── pom.xml                   Maven POM for backend
│   └── README.md
│
│
├── stock-exchange/               Service Stock Exchange complet
│   ├── front/                    React Frontend (Nx-managed)
│   │   ├── src/
│   │   │   ├── App.tsx
│   │   │   ├── main.tsx
│   │   │   └── style.css
│   │   ├── vite.config.ts        Vite configuration
│   │   ├── tsconfig.json
│   │   ├── project.json          Nx configuration
│   │   ├── index.html
│   │   └── README.md
│   │
│   ├── backend/                  Java Spring Boot Backend (Nx-managed via Maven)
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/stockmarket/exchange/
│   │   │   │   │   └── StockExchangeApplication.java
│   │   │   │   └── resources/
│   │   │   │       └── application.yml
│   │   │   └── test/
│   │   ├── project.json          Nx configuration (Maven integration)
│   │   └── README.md
│   │
│   ├── pom.xml                   Maven POM for backend
│   └── README.md
│
│
└── Configuration & Documentation racine
    ├── nx.json                   Nx workspace config (with Maven plugin)
    ├── package.json              NPM dependencies + scripts
    ├── tsconfig.base.json        Base TypeScript config
    ├── .npmrc                    NPM configuration
    ├── README.md                 Project overview
    ├── AGENTS.md                 Architecture & roles
    ├── MONOREPO.md               This file
    ├── INTELLIJ_SETUP.md         IntelliJ configuration
    └── .gitignore
```

## 🎯 Concepts Clés

### Frontend (Géré par Nx)
- **Locations**: `bank/front/`, `stock-exchange/front/`
- **Technology**: React 18+ + TypeScript + Vite
- **Build Tool**: Nx + Vite
- **Package Manager**: npm
- **Projects**: `frontend-bank`, `frontend-exchange`

### Backend (Géré par Nx via Maven)
- **Locations**: `bank/backend/`, `stock-exchange/backend/`
- **Technology**: Java 21 + Spring Boot 3.2 + Maven 3.9+
- **Build Tool**: Maven (via Nx integration)
- **Package Manager**: Maven Central
- **Projects**: `backend-bank`, `backend-exchange`

## 🚀 Installation et Démarrage

### Installation des Dépendances

```bash
# Installer toutes les dépendances (npm + Maven via Nx)
npm install
```

### Lancer les Applications

#### Frontend Uniquement

```bash
# Bank Frontend seul
npm run dev:bank
# ou
nx serve frontend-bank

# Exchange Frontend seul
npm run dev:exchange
# ou
nx serve frontend-exchange

# Les deux frontends en parallèle
npm run dev:all-frontends
```

#### Backend Uniquement

```bash
# Bank Backend seul
npm run dev:backend:bank
# ou
nx serve backend-bank

# Exchange Backend seul
npm run dev:backend:exchange
# ou
nx serve backend-exchange

# Les deux backends en parallèle
npm run dev:all-backends
```

#### Tous les Services

```bash
# Lance tous les services (4 au total)
npm run dev:all
# ou
nx run-many --target=serve --all --parallel
```

### Build

```bash
# Build tous les projets
npm run build

# Build frontend uniquement
npm run build:bank
npm run build:exchange

# Build backend uniquement
npm run build:backend:bank
npm run build:backend:exchange
```

### Tests

```bash
# Test tous les projets
npm run test

# Test frontend
npm run test:bank
npm run test:exchange

# Test backend
npm run test:backend:bank
npm run test:backend:exchange
```

### Lint

```bash
# Lint tous les projets
npm run lint

# Lint frontend
npm run lint:bank
npm run lint:exchange

# Lint backend
npm run lint:backend:bank
npm run lint:backend:exchange
```

## 📊 Projets Nx

### Reconnaissance par Nx

```bash
nx show projects
# Output:
# ["backend-bank", "backend-exchange", "frontend-bank", "frontend-exchange"]
```

### Voir le Graph de Dépendances

```bash
nx graph
# Ouvre un navigateur avec le graphique complet
```

## 🔗 Dépendances Entre Services

### Architecture

```
Frontend Bank ──→ Bank API (port 8081)
Frontend Exchange ──→ Exchange API (port 8082)

Bank API ←────────────────────────→ Exchange API (optionnel)
```

### API Communication

**Frontend vers Backend**:
```typescript
// Frontend Bank calls Bank Service
const response = await fetch('http://localhost:8081/api/bank/accounts');

// Frontend Exchange calls Exchange Service
const response = await fetch('http://localhost:8082/api/exchange/stocks');
```

## 🛠️ Ajouter un Nouveau Service

### Créer un nouveau service complet

1. **Créer la structure**:
```bash
mkdir -p new-service/front/src
mkdir -p new-service/backend/src/{main,test}
```

2. **Copier les configurations**:
```bash
cp bank/front/project.json new-service/front/
cp bank/front/vite.config.ts new-service/front/
cp bank/front/tsconfig.json new-service/front/
cp bank/backend/project.json new-service/backend/
cp bank/pom.xml new-service/
```

3. **Mettre à jour les paths**:
   - Dans `project.json` files
   - Dans `pom.xml`
   - Dans `vite.config.ts`

4. **Nx reconnaît automatiquement le nouveau service**

## 🎯 Commandes Utiles

### Commandes Affected (Builds Optimisés)

```bash
# Build uniquement ce qui a changé depuis main
npm run affected:build

# Test uniquement ce qui a changé
npm run affected:test

# Lint uniquement ce qui a changé
npm run affected:lint
```

### Inspections

```bash
# Voir tous les projets
nx show projects

# Voir les configurations d'un projet
nx show project frontend-bank

# Voir les cibles disponibles
nx list

# Vérifier les dépendances
nx dep-graph
```

## 🧠 Concepts Nx

### Caching
- Nx cache les builds et tests
- Les tâches identiques ne sont pas relancées
- Cache invalidé si le code source change

### Parallelization
- Nx exécute les tâches en parallèle quand possible
- `nx run-many` peut lancer plusieurs tâches sur plusieurs projets
- `npm run dev:all` lance 4 services simultanément

### Dependency Graph
- Nx calcule automatiquement les dépendances
- `nx graph` visualise les relations
- Utilisé pour les commandes `affected`

## 📦 Gestion des Dépendances

### Frontend (npm)
```bash
npm install @package/name
npm install -D @package/dev-name
```

### Backend (Maven)
```bash
# Éditer pom.xml et ajouter la dépendance directement
# Puis:
mvn dependency:resolve
```

## 🚢 Déploiement

### Build Production

```bash
# Frontend
npm run build:bank
npm run build:exchange
# Output: dist/bank/front/, dist/stock-exchange/front/

# Backend
npm run build:backend:bank
npm run build:backend:exchange
# Output: dist/bank/backend/, dist/stock-exchange/backend/
```

### Docker (optionnel)

Chaque service peut avoir son Dockerfile:
```dockerfile
# bank/Dockerfile
FROM node:20 AS builder
WORKDIR /app
COPY . .
RUN npm install && npm run build:bank

FROM node:20
COPY --from=builder /app/dist/bank/front /app
CMD ["npm", "serve"]
```

## 📚 Ressources

- [Nx Documentation](https://nx.dev)
- [Maven Documentation](https://maven.apache.org)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev)
- [Vite Documentation](https://vitejs.dev)

## 🆘 Troubleshooting

### Nx ne reconnaît pas un service

```bash
# Régénérer le cache Nx
nx reset

# Vérifier les projets
nx show projects
```

### Build Maven échoue dans Nx

```bash
# Vérifier que Maven est installé
mvn --version

# Build manual du service
cd bank && mvn clean package
```

### Port déjà en utilisation

```bash
# Modifier le port dans vite.config.ts (frontend)
# ou application.yml (backend)

# ou : tuer le processus
lsof -i :8081
kill -9 <PID>
```

---

**Dernière mise à jour**: 2026-10-20
**Status**: Production-ready
