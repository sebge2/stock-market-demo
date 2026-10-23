# Client Service 💳

Microservice de gestion des comptes clients pour le service Bank.

## 📋 Description

Service responsable de:
- Gestion des comptes clients
- Gestion des soldes
- Dépôts et retraits
- Suivi des transactions

## 🚀 Démarrage

```bash
mvn spring-boot:run
# Port: 8084
# API: http://localhost:8084/api/client
```

## 🏗️ Structure

```
client-service/
├── src/main/
│   ├── java/com/stockmarket/bank/client/
│   │   └── ClientServiceApplication.java
│   └── resources/
│       └── application.yml
└── pom.xml
```

## 📦 Dépendances

- Spring Boot Web
- Spring Data JPA
- Spring Validation
- H2 Database
