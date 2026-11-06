# Bank Backend 🏦

Backend du service Bank avec architecture microservices.

## 📁 Structure

```
bank/backend/
├── bank-employee-service/      Employee management microservice
│   ├── src/
│   └── pom.xml
├── bank-user-service/          User profile management microservice
│   ├── src/
│   └── pom.xml
├── bank-client-service/             Client account management microservice
│   ├── src/
│   └── pom.xml
├── bank-portfolio-service/     Portfolio management microservice
│   ├── src/
│   └── pom.xml
├── bank-stock-service/         Portfolio valuation and quotation microservice
│   ├── src/
│   └── pom.xml
├── src/                        Legacy monolithic backend
├── pom.xml                     Root pom for monolithic app
└── README.md
```

## 🚀 Lancer les Microservices

### Individuellement

```bash
# Bank Employee Service
cd bank-employee-service && mvn spring-boot:run

# Client Service
cd bank-client-service && mvn spring-boot:run

# Bank User Service
cd bank-user-service && mvn spring-boot:run

# Bank Portfolio Service
cd bank-portfolio-service && mvn spring-boot:run
```

### Tous ensemble (dans différents terminaux)

```bash
# Terminal 1
cd bank-employee-service && mvn spring-boot:run

# Terminal 2
cd bank-client-service && mvn spring-boot:run

# Terminal 3
cd bank-portfolio-service && mvn spring-boot:run
```

## 📊 Ports et Endpoints

| Service | Port | Endpoint | H2 Console |
|---------|------|----------|-----------|
| Bank Employee Service | 8083 | /api/employee | /api/employee/h2-console |
| Bank User Service | 9000 | / | /h2-console |
| Client Service | 8084 | /api/client | /api/client/h2-console |
| Bank Portfolio Service | 8085 | /api/portfolio | /api/portfolio/h2-console |
| Bank Stock Service | 8088 | /api/valuation | - |

## 🏗️ Microservices

### Bank Employee Service (8083)
- Employee management
- Departments
- Roles and permissions

### Client Service (8084)
- Account management
- Balance tracking
- Deposits/Withdrawals

### Bank Stock Service (8088)
- Portfolio valuation from the latest quotations
- Stock quotations

### Bank Portfolio Service (8085)
- Portfolio management
- Holdings management
- Value tracking

## 📦 Build

```bash
# Build monolithic app
mvn clean package

# Build individual microservices
cd bank-employee-service && mvn clean package
cd ../bank-client-service && mvn clean package
cd ../bank-portfolio-service && mvn clean package
```

## 🔗 Communication

Les microservices communiquent via:
- REST API calls
- Service Discovery (optionnel)
- Message Queue (optionnel)

## 📚 Documentation

Voir les README individuels de chaque service pour plus de détails.
