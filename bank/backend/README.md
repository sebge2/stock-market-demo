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
├── client-service/             Client account management microservice
│   ├── src/
│   └── pom.xml
├── portfolio-service/          Portfolio management microservice
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
cd client-service && mvn spring-boot:run

# Bank User Service
cd bank-user-service && mvn spring-boot:run

# Portfolio Service
cd portfolio-service && mvn spring-boot:run
```

### Tous ensemble (dans différents terminaux)

```bash
# Terminal 1
cd bank-employee-service && mvn spring-boot:run

# Terminal 2
cd client-service && mvn spring-boot:run

# Terminal 3
cd portfolio-service && mvn spring-boot:run
```

## 📊 Ports et Endpoints

| Service | Port | Endpoint | H2 Console |
|---------|------|----------|-----------|
| Bank Employee Service | 8083 | /api/employee | /api/employee/h2-console |
| Bank User Service | 8086 | /api/user | /api/user/h2-console |
| Client Service | 8084 | /api/client | /api/client/h2-console |
| Portfolio Service | 8085 | /api/portfolio | /api/portfolio/h2-console |

## 🏗️ Microservices

### Bank Employee Service (8083)
- Employee management
- Departments
- Roles and permissions

### Client Service (8084)
- Account management
- Balance tracking
- Deposits/Withdrawals

### Portfolio Service (8085)
- Portfolio management
- Holdings management
- Value tracking

## 📦 Build

```bash
# Build monolithic app
mvn clean package

# Build individual microservices
cd bank-employee-service && mvn clean package
cd ../client-service && mvn clean package
cd ../portfolio-service && mvn clean package
```

## 🔗 Communication

Les microservices communiquent via:
- REST API calls
- Service Discovery (optionnel)
- Message Queue (optionnel)

## 📚 Documentation

Voir les README individuels de chaque service pour plus de détails.
