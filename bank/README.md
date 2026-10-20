# Bank Service 🏦

Service de gestion bancaire pour l'application Stock Market Demo.

## 📋 Description

Le service Bank gère :
- Les comptes clients
- Les soldes et portefeuilles
- Les transactions financières
- La validation des opérations

## 🚀 Installation

### Prérequis
- Java 21
- Maven 3.9+

### Dépendances
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Validation
- H2 Database (pour la démo)

## 🏃 Développement

```bash
# Lancer le service en mode développement
mvn spring-boot:run

# Le service écoute sur http://localhost:8081/api/bank
```

## 🧪 Tests

```bash
# Exécuter tous les tests
mvn test

# Exécuter un test spécifique
mvn test -Dtest=YourTestClass
```

## 📦 Build

```bash
# Compiler et créer le JAR
mvn clean package

# JAR généré : target/bank-0.1.0.jar

# Lancer le JAR
java -jar target/bank-0.1.0.jar
```

## 🏗️ Structure du Projet

```
bank/
├── src/
│   ├── main/
│   │   ├── java/com/stockmarket/bank/
│   │   │   ├── BankApplication.java
│   │   │   ├── controller/               # REST Controllers
│   │   │   ├── service/                  # Business Logic
│   │   │   ├── repository/               # Data Access
│   │   │   ├── model/                    # Entities/DTOs
│   │   │   └── exception/                # Custom Exceptions
│   │   └── resources/
│   │       └── application.yml           # Configuration
│   └── test/java/...
├── pom.xml
└── README.md
```

## 📝 API Endpoints

À documenter une fois développés.

### Exemples

```
GET  /api/bank/accounts              # Lister les comptes
POST /api/bank/accounts              # Créer un compte
GET  /api/bank/accounts/{id}         # Détails d'un compte
GET  /api/bank/accounts/{id}/balance # Solde d'un compte
POST /api/bank/transactions          # Effectuer une transaction
```

## ⚙️ Configuration

### application.yml

```yaml
spring:
  application:
    name: bank-service
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver

server:
  port: 8081
  servlet:
    context-path: /api/bank
```

### Profils

```bash
# Production
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"

# Development (par défaut)
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

## 🔗 Dépendances

- **Spring Boot Web** : REST API
- **Spring Data JPA** : Accès base de données
- **Spring Validation** : Validation des données
- **H2 Database** : Base de données en mémoire (démo)

## 🔄 Communication

### Avec le Frontend
- REST API sur le port 8081
- Context path : `/api/bank`

### Avec Stock Exchange Service
- Utiliser `RestTemplate` ou `WebClient`
- URL : `http://localhost:8082/api/exchange`

## 📚 Ressources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring REST](https://spring.io/guides/gs/rest-service/)

## 👤 Auteur

Sébastien Gérard

## 📄 Licence

MIT
