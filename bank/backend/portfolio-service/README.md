# Portfolio Service 📊

Microservice de gestion des portefeuilles pour le service Bank.

## 📋 Description

Service responsable de:
- Création et gestion des portefeuilles
- Gestion des actions détenues
- Suivi de la valeur du portefeuille
- Achat/vente d'actions

## 🚀 Démarrage

```bash
mvn spring-boot:run
# Port: 8085
# API: http://localhost:8085/api/portfolio
```

## 🏗️ Structure

```
portfolio-service/
├── src/main/
│   ├── java/com/stockmarket/bank/portfolio/
│   │   └── PortfolioServiceApplication.java
│   └── resources/
│       └── application.yml
└── pom.xml
```

## 📦 Dépendances

- Spring Boot Web
- Spring Data JPA
- Spring Validation
- H2 Database
