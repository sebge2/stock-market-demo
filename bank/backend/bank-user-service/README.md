# Bank User Service

Microservice for bank user profiles. Credentials and authentication remain managed by Keycloak; this service will store only business-profile data and the corresponding Keycloak user ID.

## Run

```bash
mvn spring-boot:run
```

- API base URL: <http://localhost:8086/api/user>
- Actuator: <http://localhost:8086/api/user/actuator>

The service registers with Eureka at `http://localhost:8761/eureka/` and is automatically discovered by the Bank Service Console.
