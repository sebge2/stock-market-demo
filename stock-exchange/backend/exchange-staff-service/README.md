# Exchange Staff Service

Microservice managing the stock exchange staff (persistence layer).

## Description

- Staff members and staff roles (JPA entities in `be.sgerard.demo.exchange.staff.model`)
- Spring Data repositories in `be.sgerard.demo.exchange.staff.repository`

## Run

```bash
mvn spring-boot:run
# Port: 8086
# API: http://localhost:8086/api/staff
```

## Structure

```
exchange-staff-service/
├── src/main/java/be/sgerard/demo/exchange/staff/
│   ├── ExchangeStaffServiceApplication.java
│   ├── model/        (StaffMember, StaffRole + enums)
│   └── repository/
└── pom.xml
```
