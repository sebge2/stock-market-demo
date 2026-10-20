# Stock Market Demo 📈

Une application de démonstration pour tester les technologies modernes en créant un écosystème financier complet.

## 🎯 Objectif

Créer un environnement sandbox pour explorer et expérimenter les meilleures pratiques en matière de :
- Architecture et design patterns
- Technologies frontend et backend
- Gestion des données financières
- Intégration et orchestration

## 🏗️ Architecture

L'application simule un écosystème financier avec trois entités principales :

### 1. **Bourse (Stock Exchange)**
- Gestion des listes de valeurs
- Cotation des actions
- Historique des prix et tendances
- Actualisation en temps réel (simulée)

### 2. **Banque (Bank)**
- Gestion des comptes clients
- Soldes et portefeuilles
- Validation des opérations
- Historique des transactions

### 3. **Clients (Customers)**
- Inscription et authentification
- Consultation du portefeuille
- Achat/vente d'actions
- Suivi des positions et performances

## 📊 Flux Principal

```
Client → Banque (Validation) → Bourse (Exécution)
                    ↓
            Enregistrement Transaction
                    ↓
            Mise à Jour Portefeuille
```

## 🚀 Stack Technologique (À définir)

- **Frontend** : [À spécifier]
- **Backend** : [À spécifier]
- **Base de Données** : [À spécifier]
- **Communication** : [À spécifier]

## 📋 Fonctionnalités Clés

- [ ] Création de comptes clients
- [ ] Gestion de portefeuille d'actions
- [ ] Achat/vente d'actions
- [ ] Consultation des prix en temps réel
- [ ] Historique des transactions
- [ ] Dashboard de performance
- [ ] Validation des opérations (solde, plafonds, etc.)

## 🔧 Configuration

```bash
# À définir selon le stack choisi
```

## 📚 Documentation Supplémentaire

- [AGENTS.md](./AGENTS.md) - Description des agents et acteurs du système

## 👤 Auteur

Sébastien Gérard

## 📄 Licence

Voir [LICENSE](./LICENSE)