# User Service 👤

Microservice de gestion des utilisateurs pour le service Bank.

## 📋 Description

Service responsable de:
- Registration des utilisateurs
- Authentification
- Gestion des profils utilisateur

## 🚀 Démarrage

```bash
mvn spring-boot:run
# Port: 8083
# API: http://localhost:8083/api/user
```

## 🏗️ Structure

```
user-service/
├── src/main/
│   ├── java/com/stockmarket/bank/user/
│   │   └── UserServiceApplication.java
│   └── resources/
│       └── application.yml
└── pom.xml
```

## 📦 Dépendances

- Spring Boot Web
- Spring Data JPA
- Spring Validation
- H2 Database
