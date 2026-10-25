# Bank User Service

Microservice for bank user profiles and authorization data. Credentials and authentication remain managed by Keycloak; this service owns roles and permissions.

## Run

```bash
mvn spring-boot:run
```

- API base URL: <http://localhost:8086/api/user>
- Actuator: <http://localhost:8086/api/user/actuator>
- Roles: <http://localhost:8086/api/user/roles>
- Permissions: <http://localhost:8086/api/user/permissions>

The service registers with Eureka at `http://localhost:8761/eureka/` and is automatically discovered by the Bank Service Console.
