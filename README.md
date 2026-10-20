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
├── bank/                         # Service Bank complet
│   ├── front/                    # React Frontend
│   │   ├── src/
│   │   ├── index.html
│   │   └── project.json
│   ├── backend/                  # Backend Java
│   │   ├── src/
│   │   │   ├── main/
│   │   │   └── test/
│   │   └── README.md
│   ├── pom.xml
│   └── README.md
│
├── stock-exchange/               # Service Stock Exchange complet
│   ├── front/                    # React Frontend
│   │   ├── src/
│   │   ├── index.html
│   │   └── project.json
│   ├── backend/                  # Backend Java
│   │   ├── src/
│   │   │   ├── main/
│   │   │   └── test/
│   │   └── README.md
│   ├── pom.xml
│   └── README.md
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

### Option 1 : Chaque application séparément

**Frontend Bank**
```bash
npm run dev:bank
# Accès : http://localhost:3000
```

**Frontend Stock Exchange**
```bash
npm run dev:exchange
# Accès : http://localhost:3001 (ou 3000 si 3001 occupé)
```

**Bank Service (Java)**
```bash
cd bank
mvn spring-boot:run
# API : http://localhost:8081/api/bank
```

**Stock Exchange Service (Java)**
```bash
cd stock-exchange
mvn spring-boot:run
# API : http://localhost:8082/api/exchange
```

### Option 2 : Avec npm (frontends uniquement)
```bash
npm run dev:all-frontends
# Lance Bank et Exchange frontends en parallèle
```

### Option 3 : IntelliJ IDEA (Recommandé)
Utiliser les launchers IntelliJ pour lancer les services :
- `Frontend Bank` - React sur port 3000
- `Frontend Exchange` - React sur port 3001
- `Bank Service` - Spring Boot sur port 8081
- `Stock Exchange Service` - Spring Boot sur port 8082
- Launchers composites pour lancer plusieurs services ensemble

## 📚 Documentation Supplémentaire

- [AGENTS.md](./AGENTS.md) - Description des agents et architecture métier
- [MONOREPO.md](./MONOREPO.md) - Guide complet du monorepo Nx

## 👤 Auteur

Sébastien Gérard

## 📄 Licence

MIT