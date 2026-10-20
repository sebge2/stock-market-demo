# Stock Market Demo 📈

Une application de démonstration pour tester les technologies modernes en créant un écosystème financier complet avec **Java backend** et **React frontend**.

## 🎯 Objectif

Créer un environnement sandbox pour explorer et expérimenter les meilleures pratiques en matière de :
- Architecture microservices (Java Spring Boot)
- Frontend moderne (React + TypeScript)
- Intégration backend/frontend
- Gestion des données financières
- Monorepo avec Nx

## 🏗️ Architecture

### Backend (Java 21 + Spring Boot 3.2)
Deux services microservices indépendants :

#### 1. **Bank Service** 🏦
- Gestion des comptes clients
- Soldes et portefeuilles
- Validation des opérations
- Historique des transactions
- **Port :** 8081
- **Context Path :** `/api/bank`

#### 2. **Stock Exchange Service** 📊
- Gestion des listes de valeurs
- Cotation des actions
- Exécution des ordres
- Historique des prix
- **Port :** 8082
- **Context Path :** `/api/exchange`

### Frontend (React 18 + TypeScript + Vite)
Application React moderne qui communique avec les services backend :
- Dashboard centralisé
- Gestion de portefeuille
- Affichage des cotations
- Historique des transactions
- **Port :** 3000

## 📊 Architecture Générale

```
┌─────────────────────────────────────────────────────────────┐
│                     Frontend (React)                        │
│                      Port 3000                              │
└──────────────────┬────────────────────────────────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
┌───────▼────────┐  ┌─────────▼──────────┐
│  Bank Service  │  │ Stock Exchange     │
│  (Port 8081)   │  │ (Port 8082)        │
│  Spring Boot   │  │ Spring Boot        │
│  Java 21       │  │ Java 21            │
└────────────────┘  └────────────────────┘
```

## 🚀 Stack Technologique

| Couche | Technologie | Version |
|--------|-------------|---------|
| **Frontend** | React | 18.2+ |
| **Frontend Build** | Vite | 5.0+ |
| **Frontend Lang** | TypeScript | 5.3+ |
| **Monorepo** | Nx | 18.0+ |
| **Backend** | Java | 21 (LTS) |
| **Framework Backend** | Spring Boot | 3.2.0 |
| **Build Backend** | Maven | 3.9+ |
| **DB Demo** | H2 | Embedded |

## 📋 Fonctionnalités Clés

### Phase 1 (Setup) ✅
- [x] Structure monorepo avec Nx
- [x] Services Java avec Spring Boot
- [x] Frontend React de base
- [ ] Connexion Frontend ↔ Backend

### Phase 2 (Développement)
- [ ] API Bank (gestion de comptes)
- [ ] API Stock Exchange (gestion de titres)
- [ ] UI Dashboard
- [ ] Authentification

### Phase 3 (Intégration)
- [ ] Communication Frontend-Backend
- [ ] WebSocket pour les cotations en temps réel
- [ ] Cache et optimisations
- [ ] Tests intégrés

## 📂 Structure du Projet

```
stock-market-demo/
├── apps/
│   └── frontend/                 # React application
│       ├── src/
│       ├── index.html
│       └── project.json
│
├── packages/
│   ├── bank/                     # Bank microservice (Java/Maven)
│   │   ├── src/
│   │   └── pom.xml
│   │
│   └── stock-exchange/           # Stock Exchange microservice (Java/Maven)
│       ├── src/
│       └── pom.xml
│
├── nx.json
├── package.json
├── tsconfig.base.json
├── README.md
├── AGENTS.md
└── MONOREPO.md
```

## 🛠️ Installation

### Prérequis
- **Node.js** 18+ (pour npm et le frontend)
- **Java 21** (LTS)
- **Maven 3.9+**
- **Git**

### Setup

```bash
# Installer les dépendances frontend/Nx
npm install
```

## 🚀 Lancer l'Application

### Frontend
```bash
npm run dev:frontend
# Accès : http://localhost:3000
```

### Backend Services
```bash
# Bank Service
cd packages/bank
mvn spring-boot:run
# API : http://localhost:8081/api/bank

# Stock Exchange Service (autre terminal)
cd packages/stock-exchange
mvn spring-boot:run
# API : http://localhost:8082/api/exchange
```

## 📚 Documentation Supplémentaire

- [AGENTS.md](./AGENTS.md) - Description des agents et architecture métier
- [MONOREPO.md](./MONOREPO.md) - Guide complet du monorepo Nx

## 👤 Auteur

Sébastien Gérard

## 📄 Licence

MIT