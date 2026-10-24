# Bank Employee Service

Microservice managing the bank employees (persistence layer).

## Description

- Employees, departments, roles and permissions (JPA entities in `be.sgerard.demo.employee.model`)
- Spring Data repositories in `be.sgerard.demo.employee.repository`

## Run

```bash
mvn spring-boot:run
# Port: 8083
# API: http://localhost:8083/api/employee
# OpenAPI: http://localhost:8083/api/employee/swagger-ui/index.html
```

## Structure

```
bank-employee-service/
├── src/main/java/be/sgerard/demo/employee/
│   ├── EmployeeServiceApplication.java
│   ├── model/        (Employee, Department, Role, Permission + enums)
│   └── repository/
└── pom.xml
```
