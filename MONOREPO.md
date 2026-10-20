# Monorepo Structure avec Nx 📦

Documentation de la structure et de l'organisation du monorepo Stock Market Demo utilisant **Nx** pour le frontend et **Maven** pour les services Java.

## 📁 Structure du Projet

```
stock-market-demo/
├── apps/
│   └── frontend/                  # React + TypeScript application
│       ├── src/
│       │   ├── App.tsx           # Composant principal
│       │   ├── App.css
│       │   ├── main.tsx          # Point d'entrée
│       │   └── style.css
│       ├── index.html            # HTML racine
│       ├── project.json          # Config Nx du projet
│       ├── package.json
│       ├── tsconfig.json
│       ├── tsconfig.app.json
│       └── tsconfig.spec.json
│
├── packages/
│   ├── bank/                     # Service Banque (Java/Spring Boot)
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/stockmarket/bank/
│   │   │   │   │   └── BankApplication.java
│   │   │   │   └── resources/
│   │   │   │       └── application.yml
│   │   │   └── test/java/...
│   │   ├── pom.xml               # Maven POM
│   │   └── README.md
│   │
│   └── stock-exchange/           # Service Bourse (Java/Spring Boot)
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/com/stockmarket/exchange/
│       │   │   │   └── StockExchangeApplication.java
│       │   │   └── resources/
│       │   │       └── application.yml
│       │   └── test/java/...
│       ├── pom.xml               # Maven POM
│       └── README.md
│
├── nx.json                       # Configuration Nx workspace
├── package.json                  # Dependencies npm (Frontend)
├── tsconfig.base.json            # Config TypeScript partagée
├── vite.config.ts               # Config Vite
├── jest.preset.js               # Config Jest
├── README.md                     # Documentation principale
├── AGENTS.md                     # Architecture métier
├── MONOREPO.md                   # Ce fichier
└── .gitignore
```

## 🎯 Concepts Clés

### Frontend (Géré par Nx)
- **Location** : `apps/frontend/`
- **Technology** : React + TypeScript + Vite
- **Build Tool** : Nx + Vite
- **Package Manager** : npm/yarn

### Backend Services (Indépendants)
- **Location** : `packages/bank/` et `packages/stock-exchange/`
- **Technology** : Java 21 + Spring Boot 3.2
- **Build Tool** : Maven
- **Package Manager** : Maven Central

## 🚀 Installation et Démarrage

### Installation des dépendances

```bash
# Installer les dépendances frontend/Nx
npm install

# Les services Java utilisent Maven, aucune installation npm nécessaire
```

### Développement

#### Frontend

```bash
# Dev mode avec hot reload
npm run dev:frontend

# Build production
npm run build:frontend

# Lint
npm run lint:frontend

# Tests
npm run test:frontend
```

#### Backend Services

```bash
# Bank Service
cd packages/bank
mvn spring-boot:run          # Port 8081

# Stock Exchange Service
cd packages/stock-exchange
mvn spring-boot:run          # Port 8082
```

### Build Complet

```bash
# Build frontend uniquement
npm run build:frontend

# Pour les services Java, se placer dans chaque dossier
cd packages/bank
mvn clean package
# JAR généré : target/bank-0.1.0.jar

cd packages/stock-exchange
mvn clean package
# JAR généré : target/stock-exchange-0.1.0.jar
```

## 📋 Commandes Nx Utiles

### Serve et Build

```bash
# Serveur de développement frontend
nx serve frontend

# Build frontend
nx build frontend

# Visualiser le graphique des dépendances
nx graph

# Voir l'état du cache
nx show projects
```

### Affected Commands

```bash
# Build uniquement les projets modifiés
npm run affected:build

# Test uniquement les projets modifiés
npm run affected:test

# Lint uniquement les projets modifiés
npm run affected:lint
```

## 🏗️ Configuration

### nx.json
Configuration du workspace Nx :
- `npmScope` : "@stock-market"
- `affected.defaultBase` : "main"
- `tasksRunnerOptions` : Cache et parallelization

### Frontend (apps/frontend/)

**project.json** - Définit les targets :
- `serve` : Lancer le dev server (port 3000)
- `build` : Build production
- `test` : Jest tests
- `lint` : ESLint

**tsconfig.json** - Configuration TypeScript

**vite.config.ts** - Configuration Vite

### Backend (packages/bank/, packages/stock-exchange/)

**pom.xml** - Configuration Maven :
- Parent : spring-boot-starter-parent 3.2.0
- Dependencies : Spring Web, Data JPA, etc.
- Plugins : spring-boot-maven-plugin
- Java Version : 21

**application.yml** - Configuration Spring Boot :
- Port (8081/8082)
- Database (H2 in-memory)
- Logging level

## 📦 Ajouter un Nouveau Package Frontend

```bash
# Créer la structure avec Nx
npx nx generate @nx/react:app my-new-app

# Ou manuellement :
mkdir -p apps/my-new-app/src
cp apps/frontend/project.json apps/my-new-app/
cp apps/frontend/package.json apps/my-new-app/
cp apps/frontend/tsconfig*.json apps/my-new-app/
```

## 📦 Ajouter un Nouveau Service Java

```bash
# Créer la structure
mkdir -p packages/my-service/src/main/java/com/stockmarket/myservice
mkdir -p packages/my-service/src/main/resources

# Copier les fichiers de configuration
cp packages/bank/pom.xml packages/my-service/
cp packages/bank/src/main/resources/application.yml packages/my-service/src/main/resources/

# Éditer le pom.xml : changer l'artifactId et la description
# Créer MyServiceApplication.java en suivant BankApplication.java
```

## 🔗 Communication Entre Services

### Frontend ↔ Backend

```typescript
// Exemple en React
const response = await fetch('http://localhost:8081/api/bank/accounts');
const data = await response.json();
```

### Backend ↔ Backend (Optional)

Utiliser RestTemplate ou WebClient en Spring pour communiquer entre services :

```java
// Dans BankApplication.java ou Stock Exchange
RestTemplate restTemplate = new RestTemplate();
String response = restTemplate.getForObject(
  "http://localhost:8082/api/exchange/prices",
  String.class
);
```

## 🎯 Avantages de cette Architecture

### Nx (Frontend)
- ✅ Build cache
- ✅ Affected commands
- ✅ Dependency graph
- ✅ Scalable monorepo

### Maven (Backend)
- ✅ Dépendance management robuste
- ✅ Écosystème Java riche
- ✅ Build reproductible
- ✅ Plugins nombreux

### Séparation
- ✅ Frontend et Backend indépendants
- ✅ Stack technologies différentes
- ✅ Déploiement flexible
- ✅ Scalabilité per-service

## 🔄 Workflow de Développement

1. **Créer une branche**
   ```bash
   git checkout -b feature/ma-feature
   ```

2. **Développer**
   - Frontend : modifier les fichiers dans `apps/frontend/`
   - Backend : modifier les fichiers dans `packages/*/src/`

3. **Tester**
   ```bash
   # Frontend
   npm run test:frontend
   npm run lint:frontend

   # Backend (dans le dossier du service)
   mvn test
   ```

4. **Build**
   ```bash
   # Frontend
   npm run build:frontend

   # Backend
   mvn clean package
   ```

5. **Commit**
   ```bash
   git commit -m "feat: description de la feature"
   ```

6. **Push & PR**
   ```bash
   git push origin feature/ma-feature
   ```

## 🔍 Visualiser l'Architecture

```bash
# Affiche le graphe des dépendances
nx graph
```

## 📚 Documentation Supplémentaire

- [Nx Docs](https://nx.dev)
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [React Docs](https://react.dev)
- [Vite Docs](https://vitejs.dev)
- [Maven Docs](https://maven.apache.org)
- [AGENTS.md](./AGENTS.md) - Architecture métier
- [README.md](./README.md) - Overview du projet
