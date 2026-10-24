# Agents du Système Stock Market Demo 🤖

Description des agents (rôles/acteurs) qui composent l'écosystème financier.

## 📍 Agents Principaux

### 1. **Client** 👤
**Rôle** : Utilisateur final du système

**Responsabilités** :
- S'authentifier dans le système
- Consulter son solde bancaire
- Visualiser son portefeuille d'actions
- Initier des opérations d'achat/vente
- Suivre l'historique de ses transactions

**Permissions** :
- Lecture : Portefeuille personnel, prix du marché, historique
- Écriture : Ordres d'achat/vente personnels

---

### 2. **Banque (Bank Agent)** 🏦
**Rôle** : Gestionnaire des ressources financières et validateur

**Responsabilités** :
- Gérer les comptes clients
- Valider les demandes de transaction (solde, limites)
- Débiter/créditer les comptes
- Enregistrer les transactions
- Générer les rapports de mouvements

**Règles de Validation** :
- Vérifier que le solde est suffisant
- Respecter les plafonds de transaction
- Vérifier l'identité et les droits du client
- Enregistrer une piste d'audit (logs)

**Interfaces** :
- Reçoit des ordres de transaction de Client
- Communique avec Bourse pour exécution
- Persiste les données de compte/transaction

---

### 3. **Bourse (Stock Exchange Agent)** 📊
**Rôle** : Gestionnaire du marché et exécuteur des ordres

**Responsabilités** :
- Maintenir le catalogue des titres disponibles
- Gérer les prix et cotations
- Exécuter les ordres d'achat/vente
- Assurer l'appairage demande/offre
- Générer les historiques de prix
- Simuler les variations de marché

**Fonctionnalités** :
- Livre d'ordres (order book)
- Prix de marché en temps réel
- Historique des prix (OHLC)
- Tendances et statistiques
- Notifications de changement de prix

**Interfaces** :
- Reçoit les ordres validés de la Banque
- Confirme l'exécution ou le rejet
- Transmet les mises à jour de portefeuille
- Expose les données de marché au Client

---

### 4. **Administrateur (Admin Agent)** ⚙️
**Rôle** : Gestionnaire du système

**Responsabilités** :
- Ajouter/modifier les titres disponibles
- Définir les paramètres de marché
- Créer des campagnes de simulation
- Générer des rapports
- Gérer les incidents

**Permissions** :
- Lecture complète : Tous les données
- Écriture : Configuration et paramètres globaux

---

## 🔄 Interactions Entre Agents

### Scénario : Achat d'Actions

```
1. Client → Banque : "Acheter 10 actions APPLE"
2. Banque → Banque : Valider solde + limites
3. Banque → Bourse : "Ordre d'achat : 10 APPLE"
4. Bourse → Bourse : Exécuter + mettre à jour prix
5. Bourse → Banque : "Exécution confirmée - Montant: 1500€"
6. Banque → Banque : Débiter compte + Enregistrer transaction
7. Banque → Client : "Achat complété + Mise à jour portefeuille"
```

### Scénario : Consultation du Marché

```
1. Client → Bourse : "Afficher prix APPLE"
2. Bourse → Bourse : Récupérer prix actuel
3. Bourse → Client : "APPLE: 150€ (↑2%) - Volume: 1M actions"
```

---

## 🛡️ Niveaux d'Accès

| Agent | Comptes | Portefeuille | Marché | Admin |
|-------|---------|--------------|--------|-------|
| Client | Propre | Propre | Lecture | ✗ |
| Banque | Tous | Tous | Lecture | Partiel |
| Bourse | ✗ | Tous | Tous | Partiel |
| Admin | Tous | Tous | Tous | ✓ |

---

## 📐 Conventions

1. **Base package** : le base package Java doit être `be.sgerard.demo`
2. La parsistence est faites avec JPA
3. Les IDs des entités sont des UUID auto générés
4. Le code et complémentaire sont en anglais.

---

## 📝 Notes

- Les agents peuvent être implémentés comme des microservices, des modules ou des classes selon l'architecture choisie
- Chaque agent doit maintenir sa propre logique métier et ne pas dépendre d'autres agents
- Les communications doivent être asynchrones quand possible (events, queues, etc.)