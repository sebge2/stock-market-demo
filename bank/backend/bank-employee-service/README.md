# Bank Employee Service

Microservice managing the bank employees (persistence layer).

## Description

- Employees and departments (JPA entities in `be.sgerard.demo.employee.model`)
- Roles and permissions are managed by `bank-user-service`.
- Spring Data repositories in `be.sgerard.demo.employee.repository`

## Run

```bash
mvn spring-boot:run
# Port: 8083
# API: http://localhost:8083/api/employee
# OpenAPI: http://localhost:8083/api/employee/swagger-ui/index.html
```

## Configuration

Employee creation automatically creates the matching user in `bank-user-service`: the caller's JWT
is forwarded (the caller needs `EMPLOYEE_CREATE` and `USER_CREATE`). Calls time out after 5s and are
attempted at most 3 times with exponential backoff on 5xx responses and I/O errors; 4xx responses
are never retried. A 409 (username already used) is returned as is, no suffix is tried. If the
employee cannot be persisted afterwards, the created user is deleted (best effort).

| Variable | Description |
|----------|-------------|
| `BANK_BASE_DOMAIN` | Base domain of the bank services. The user service URL becomes `http://bank-user-service.<domain>[:<port>]`. Takes precedence over `BANK_USER_SERVICE_BASE_URL`. |
| `BANK_USER_SERVICE_PORT` | Port of the user service, only used with `BANK_BASE_DOMAIN` (optional). |
| `BANK_USER_SERVICE_BASE_URL` | Explicit base URL of the user service (fallback, also used as JWT issuer). |

## Structure

```
bank-employee-service/
├── src/main/java/be/sgerard/demo/employee/
│   ├── EmployeeServiceApplication.java
│   ├── model/        (Employee, Department, Role, Permission + enums)
│   └── repository/
└── pom.xml
```
