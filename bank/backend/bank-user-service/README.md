# Bank User Service

Microservice for bank user profiles, authorization data, and OAuth2/OIDC authorization.

## Run

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

- Authorization server: <http://localhost:9000>
- OIDC metadata: <http://localhost:9000/.well-known/openid-configuration>
- Actuator: <http://localhost:9000/actuator>
- Roles: <http://localhost:9000/roles>
- Permissions: <http://localhost:9000/permissions>

The service registers with Eureka at `http://localhost:8761/eureka/` and is automatically discovered by the Bank Service Console.

With the `local` profile, the authorization server seeds `sebastien.gerard` with the temporary password `changeit` and the `ADMIN` role.
