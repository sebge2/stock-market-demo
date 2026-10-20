# Stock Exchange Service 📊

Service de gestion de la bourse pour l'application Stock Market Demo.

## 📋 Description

Le service Stock Exchange gère :
- Le catalogue des titres
- Les cotations et prix du marché
- L'exécution des ordres d'achat/vente
- L'historique des transactions de marché

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

# Le service écoute sur http://localhost:8082/api/exchange
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

# JAR généré : target/stock-exchange-0.1.0.jar

# Lancer le JAR
java -jar target/stock-exchange-0.1.0.jar
```

## 🏗️ Structure du Projet

```
stock-exchange/
├── src/
│   ├── main/
│   │   ├── java/com/stockmarket/exchange/
│   │   │   ├── StockExchangeApplication.java
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
GET  /api/exchange/stocks            # Lister les actions
GET  /api/exchange/stocks/{symbol}   # Détails d'une action
GET  /api/exchange/prices/{symbol}   # Prix actuels
GET  /api/exchange/prices/{symbol}/history  # Historique des prix
POST /api/exchange/orders            # Créer un ordre
GET  /api/exchange/orders            # Lister les ordres
```

## ⚙️ Configuration

### application.yml

```yaml
spring:
  application:
    name: stock-exchange-service
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver

server:
  port: 8082
  servlet:
    context-path: /api/exchange
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
- REST API sur le port 8082
- Context path : `/api/exchange`

### Avec Bank Service
- Utiliser `RestTemplate` ou `WebClient`
- URL : `http://localhost:8081/api/bank`

## 📊 Simulation du Marché

À implémenter :
- Générateur de prix aléatoires
- Scheduler pour mettre à jour les prix régulièrement
- WebSocket pour les mises à jour en temps réel

## 📚 Ressources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring REST](https://spring.io/guides/gs/rest-service/)

## 👤 Auteur

Sébastien Gérard

## 📄 Licence

MIT
