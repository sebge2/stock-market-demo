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

## 🔐 Sécurité

Resource server OAuth2 : les JWT sont émis par `bank-user-service` (`bank.user-service.base-url`).
Permissions requises : `CLIENT_LIST`, `CLIENT_CREATE`, `CLIENT_UPDATE`, `CLIENT_DELETE`.
`POST /clients` crée automatiquement l'utilisateur dans le user-service (le token de l'appelant est
transmis, `USER_CREATE` est donc aussi requis). Swagger UI : `/api/client/swagger-ui.html`
(client OAuth2 `client-swagger-ui`, PKCE).

## 🏗️ Structure

```
client-service/src/main/java/be/sgerard/demo/bank/client/
├── config/       SecurityConfiguration, OpenApiConfiguration, UserServiceConfiguration
├── controller/   ClientController
├── mapper/       ClientMapper
├── model/        Client, dto/ (ClientDto, ClientRequestDto, ...)
├── repository/   ClientRepository
├── service/      ClientService
└── userservice/  UserServiceClient (+ retry)
```

## 📦 Dépendances

- Spring Boot Web, Data JPA, Validation, OAuth2 Resource Server
- springdoc-openapi, spring-retry
- H2 Database
