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
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
# Port: 8085
# API: http://localhost:8085/api/portfolio
# Swagger UI: http://localhost:8085/api/portfolio/swagger-ui.html
# OpenAPI JSON: http://localhost:8085/api/portfolio/v3/api-docs
```

Le profil `local` charge des données de test (`db/local/data.sql`) cohérentes avec celles du bank-client-service.
Le `bank-user-service` doit tourner sur `bank.user-service.base-url` (`http://localhost:9000` en local).

## 🔐 Sécurité

Resource server OAuth2 : les JWT sont émis par `bank-user-service` (`bank.user-service.base-url`) et les
permissions sont lues dans le claim `authorities`. Permissions requises :

| Endpoint | Permission |
|----------|-----------|
| `GET /portfolios`, `GET /portfolios/{id}` | `PORTFOLIO_LIST` |
| `POST /portfolios` | `PORTFOLIO_CREATE` |
| `PUT /portfolios/{id}` | `PORTFOLIO_UPDATE` |
| `DELETE /portfolios/{id}` | `PORTFOLIO_DELETE` |

Un client possède au plus un portefeuille (409 sinon) ; un portefeuille contenant des positions ne peut
pas être supprimé (409). Swagger UI utilise le client OAuth2 public `portfolio-swagger-ui` (PKCE, scope
`portfolio.read`).

Exemple :

```bash
curl -H "Authorization: Bearer $TOKEN" http://localhost:8085/api/portfolio/portfolios?page=0&size=20
curl -X POST -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"clientId":"00000000-0000-4000-d000-000000000003"}' http://localhost:8085/api/portfolio/portfolios
```

## 🏗️ Structure

```
portfolio-service/src/main/java/be/sgerard/demo/portfolio/
├── config/      SecurityConfiguration, OpenApiConfiguration
├── controller/  PortfolioController, RestExceptionHandler
├── mapper/      PortfolioMapper
├── model/       Portfolio, Position, Purchase, PurchaseReceipt, dto/
├── repository/  *Repository
└── service/     PortfolioService
```

## 📦 Dépendances

- Spring Boot Web
- Spring Data JPA
- Spring Validation
- OAuth2 Resource Server, springdoc-openapi
- H2 Database
