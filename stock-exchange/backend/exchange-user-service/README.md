# Exchange User Service

Microservice for stock exchange users, authorization data, and the OAuth2/OIDC authorization server.
It is the OAuth2 issuer of the exchange backend services. It follows the same design as `bank-user-service`.

## Run

```bash
npm run dev:exchange-user-service
# or
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

- Authorization server: <http://localhost:8091>
- Swagger: <http://localhost:8091/swagger-ui.html>
- OIDC metadata: <http://localhost:8091/.well-known/openid-configuration>
- Token endpoint: `POST /oauth2/token`, JWKS: `GET /oauth2/jwks`
- Actuator: <http://localhost:8091/actuator>
- Profile: `GET /profile`; users: `/users`; roles: `/roles`; permissions: `/permissions`

The service registers with Eureka at `http://localhost:8761/eureka/` as `exchange-user-service`.

## Local data

With the `local` profile (H2 in memory, seeded from `db/local/data.sql`), the password of every seeded user is `changeit`:

| User    | Role  | Permissions                                   |
|---------|-------|-----------------------------------------------|
| `admin` | ADMIN | all                                           |
| `staff` | STAFF | `USER_LIST`, `ROLE_LIST`                      |

A `GUEST` role (`USER_LIST` only) is also seeded.

OAuth2 public PKCE clients: `exchange-frontend`, `exchange-swagger-ui`, `company-swagger-ui`, `staff-swagger-ui`.

The signing key of the `local` profile is a development-only key pair, committed on purpose. Outside of it, provide
`EXCHANGE_USER_SERVICE_JWT_KEY_ID`, `EXCHANGE_USER_SERVICE_JWT_PRIVATE_KEY` and `EXCHANGE_USER_SERVICE_JWT_PUBLIC_KEY`.
