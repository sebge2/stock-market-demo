# Stock Market Demo 📈

Une application de démonstration pour tester les technologies modernes en créant un écosystème financier complet avec **architecture microservices Java**, **React frontend**, et **Nx monorepo**.

## 🎯 Objectif

Créer un environnement sandbox pour explorer et expérimenter les meilleures pratiques en matière de :
- Architecture microservices (Java 21 + Spring Boot 3.2)
- Frontend moderne (React 18 + TypeScript + Vite)
- Intégration backend/frontend
- Gestion des données financières
- Monorepo avec Nx

## 🏗️ Architecture Globale

```
Frontend (React)
├── bank/front              (Port 3000)
└── stock-exchange/front    (Port 3001)

Backend Microservices
├── bank/backend/
│   ├── bank-employee-service (Port 8083)
│   ├── client-service      (Port 8084)
│   └── portfolio-service   (Port 8085)
└── stock-exchange/backend/
    ├── exchange-staff-service (Port 8086)
    └── company-service     (Port 8087)
```

## 🚀 Stack Technologique

| Couche | Technologie | Version |
|--------|-------------|---------|
| **Frontend** | React + TypeScript | 18.2+, 5.3+ |
| **Frontend Build** | Vite | 5.0+ |
| **Monorepo** | Nx | 19.0+ |
| **Backend Services** | Java + Spring Boot | 21, 3.2.0 |
| **Build Backend** | Maven | 3.9+ |
| **Database** | H2 | Embedded |

## 📂 Projets Nx (7 Total)

### Frontends (2)
- **frontend-bank** - Bank UI (port 3000)
- **frontend-exchange** - Exchange UI (port 3001)

### Microservices Bank (3)
- **bank-employee-service** - Bank employee management (port 8083)
- **client-service** - Client accounts (port 8084)
- **portfolio-service** - Portfolio management (port 8085)

### Microservices Exchange (2)
- **exchange-staff-service** - Staff management (port 8086)
- **exchange-company-service** - Company listings (port 8087)

## 🚀 Lancer l'Application

### Option 1: Frontends Séparément
```bash
npm run dev:bank               # Frontend Bank (3000)
npm run dev:exchange           # Frontend Exchange (3001)
```

### Option 2: Microservices Séparément
```bash
npm run dev:microservices           # Bank microservices (8083-8085)
npm run dev:exchange-microservices  # Exchange microservices (8086-8087)
```

### Option 3: Tout en Parallèle
```bash
npm run dev:all                # Tous les services (7)
```

### Option 4: Depuis chaque dossier
```bash
cd bank/backend/bank-employee-service && mvn spring-boot:run            # Port 8083
cd bank/backend/client-service && mvn spring-boot:run          # Port 8084
cd bank/backend/portfolio-service && mvn spring-boot:run       # Port 8085
cd stock-exchange/backend/exchange-staff-service && mvn spring-boot:run  # Port 8086
cd stock-exchange/backend/company-service && mvn spring-boot:run # Port 8087
```

## 🎮 IntelliJ Launchers

### Frontends
- **Frontend Bank** → Port 3000
- **Frontend Exchange** → Port 3001


### Bank Microservices
- **Bank Employee Service** → Port 8083
- **Client Service** → Port 8084
- **Portfolio Service** → Port 8085
- **All Microservices** → Tous 3 en parallèle

### Exchange Microservices
- **Exchange Staff Service** → Port 8086
- **Exchange Company Service** → Port 8087
- **Exchange Microservices** → Tous 2 en parallèle

### Groupés
- **All Backends** → Tous microservices (5 services)
- **All Services** → Tous les 7 services

## 📍 Endpoints

| Service | Port | Endpoint | H2 Console |
|---------|------|----------|-----------|
| Frontend Bank | 3000 | http://localhost:3000 | - |
| Frontend Exchange | 3001 | http://localhost:3001 | - |
| Bank Employee Service | 8083 | /api/employee | /api/employee/h2-console |
| Bank Client Service | 8084 | /api/client | /api/client/h2-console |
| Bank Portfolio Service | 8085 | /api/portfolio | /api/portfolio/h2-console |
| Exchange Staff Service | 8086 | /api/staff | /api/staff/h2-console |
| Exchange Company Service | 8087 | /api/company | /api/company/h2-console |

## 📦 Commandes Nx Utiles

```bash
# Voir tous les projets
nx show projects

# Voir le graphique des dépendances
nx graph

# Lancer un projet spécifique
nx serve frontend-bank
nx serve bank-employee-service

# Lancer plusieurs projets
nx run-many --target=serve --projects=bank-employee-service,client-service,portfolio-service

# Build tous les projets
nx run-many --target=build --all
```

## 📚 Documentation Supplémentaire

- [MONOREPO.md](MONOREPO.md) - Guide complet du monorepo Nx
- [INTELLIJ_SETUP.md](INTELLIJ_SETUP.md) - Configuration IntelliJ
- [AGENTS.md](AGENTS.md) - Architecture métier et rôles
- Voir les README dans chaque répertoire pour des détails spécifiques

## 👤 Auteur

Sébastien Gérard

## 📄 Licence

MIT
