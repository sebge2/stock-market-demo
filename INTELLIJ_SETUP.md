# IntelliJ IDEA Setup 🚀

Guide pour configurer et utiliser IntelliJ IDEA avec le monorepo Stock Market Demo géré par Nx.

## 📋 Prérequis

- IntelliJ IDEA 2023.1+ (Community ou Ultimate)
- Java 21 SDK configuré dans IntelliJ
- Node.js 18+ (pour npm et Nx)
- Maven 3.9+ (pour les services Java)

## ⚙️ Configuration Initiale

### 1. Ouvrir le Projet

```
File → Open → Sélectionner le répertoire stock-market-demo
```

IntelliJ devrait détecter automatiquement:
- ✅ Le workspace Nx
- ✅ Les frontends React (frontend-bank, frontend-exchange)
- ✅ Les backends Java (backend-bank, backend-exchange)
- ✅ La configuration Node.js/npm

### 2. Configurer le JDK

```
IntelliJ IDEA → Preferences → Project: stock-market-demo → Project Structure
→ Project SDK → Télécharger Java 21 (ou sélectionner si disponible)
```

### 3. Configurer Node.js

```
Preferences → Languages & Frameworks → Node.js
→ Node interpreter → Sélectionner votre Node.js
```

## 🎯 Launchers Disponibles

IntelliJ a détecté automatiquement les launchers suivants dans `.idea/runConfigurations/`:

### Frontend Launchers

#### ▶️ Frontend Bank
- **Type**: npm run dev:bank
- **Port**: 3000
- **Description**: React dev server pour Bank (port fallback: 3001)
- **Utilisation**: Cliquer sur `▶️ Frontend Bank` dans la toolbar

#### ▶️ Frontend Exchange
- **Type**: npm run dev:exchange
- **Port**: 3001
- **Description**: React dev server pour Exchange (port fallback: 3000)
- **Utilisation**: Cliquer sur `▶️ Frontend Exchange` dans la toolbar

### Backend Launchers

#### ▶️ Backend Bank
- **Type**: npm run dev:backend:bank (via Nx)
- **Port**: 8081
- **Path**: /api/bank
- **Description**: Spring Boot service via Nx
- **Utilisation**: Cliquer sur `▶️ Backend Bank` dans la toolbar

#### ▶️ Backend Exchange
- **Type**: npm run dev:backend:exchange (via Nx)
- **Port**: 8082
- **Path**: /api/exchange
- **Description**: Spring Boot service via Nx
- **Utilisation**: Cliquer sur `▶️ Backend Exchange` dans la toolbar

### Compound Launchers (Multiple Services)

#### ▶️ Frontend + Bank Service
- **Lance**: Frontend Bank + Backend Bank
- **Utilisation**: Cliquer sur `▶️ Frontend + Bank Service`

#### ▶️ Frontend + Exchange Service
- **Lance**: Frontend Exchange + Backend Exchange
- **Utilisation**: Cliquer sur `▶️ Frontend + Exchange Service`

#### ▶️ All Backends
- **Lance**: Backend Bank + Backend Exchange
- **Utilisation**: Cliquer sur `▶️ All Backends`
- **Logs**: 2 onglets séparés dans Run panel

#### ▶️ All Services
- **Lance**: Frontend Bank + Frontend Exchange + Backend Bank + Backend Exchange
- **Utilisation**: Cliquer sur `▶️ All Services`
- **Logs**: 4 onglets séparés dans Run panel

## 🎮 Utiliser les Launchers

### Méthode 1: Toolbar
```
┌──────────────────────────────────────────┐
│ ▶️ [All Services ▼] | 🐛 ⏸️ ⏹️ |      │
└──────────────────────────────────────────┘
        ↑ Cliquer sur ▼ pour choisir
```

### Méthode 2: Menu
```
Run → Run... → Sélectionner le launcher
```

### Méthode 3: Raccourcis Clavier
```
Alt + Shift + F10    Ouvrir le sélecteur de configuration
Alt + Shift + F9     Ouvrir le sélecteur de debug
Shift + F10          Lancer la configuration sélectionnée
Ctrl + F2            Arrêter le service
```

## 📍 Accès aux Services

Une fois les services lancés:

```
Frontend Bank:       http://localhost:3000
Frontend Exchange:   http://localhost:3001
Bank API:            http://localhost:8081/api/bank
Exchange API:        http://localhost:8082/api/exchange
Bank H2 Console:     http://localhost:8081/api/bank/h2-console
Exchange H2 Console: http://localhost:8082/api/exchange/h2-console
```

## 🐛 Debugging

### Debugger le Frontend
```
1. Cliquer sur 🐛 (au lieu de ▶️) pour le frontend
2. Ouvrir Chrome DevTools (F12)
3. Sources → définir les breakpoints
4. IntelliJ se synchronise automatiquement
```

### Debugger le Backend
```
1. Cliquer sur 🐛 (au lieu de ▶️) pour le backend
2. IntelliJ s'arrête aux breakpoints
3. Utiliser la toolbar Debugger pour step/continue
```

## 📋 Logs et Console

### Voir les Logs
```
View → Tool Windows → Run (Alt + 4)
Chaque service a son onglet avec les logs en direct
```

### Filtrer les Logs
```
Dans l'onglet Run:
- Icône pour filter par log level
- Search box pour chercher dans les logs
```

## ⚡ Raccourcis Clavier Utiles

| Action | Mac | Linux/Windows |
|--------|-----|---------------|
| Run configuration | ⌃R | Alt + Shift + F10 |
| Debug configuration | ⌃D | Alt + Shift + F9 |
| Stop | ⌘F2 | Ctrl + F2 |
| Rerun | ⌘R | Ctrl + F5 |
| Step Over | F6 | F6 |
| Step Into | F7 | F7 |
| Step Out | ⇧F8 | Shift + F8 |

## 🔧 Configuration Personnalisée

### Modifier un Launcher
```
Run → Edit Configurations...
→ Sélectionner le launcher
→ Modifier les paramètres
→ Apply → OK
```

### Ajouter une Variable d'Environnement
```
Run → Edit Configurations...
→ Sélectionner le service
→ Environment variables
→ Ajouter: VAR_NAME=value
```

## 🛠️ Plugins Recommandés

```
Preferences → Plugins
Installer:
- Nx Console (pour les commandes Nx)
- Spring Boot (pour le dev Java)
- JavaScript Debugger (pour le frontend)
- Maven (usually pre-installed)
```

## 📁 Fichiers de Configuration

```
.idea/runConfigurations/
├── Frontend_Bank.xml
├── Frontend_Exchange.xml
├── Backend_Bank.xml
├── Backend_Exchange.xml
├── Frontend_Bank_Service.xml
├── Frontend_Exchange_Service.xml
├── All_Backends.xml
└── All_Services.xml
```

## 🎯 Workflow Recommandé

1. **Lancer le projet** dans IntelliJ
2. **Configurer le JDK** si nécessaire
3. **Choisir un launcher** (ou `All Services` pour tout)
4. **Cliquer sur ▶️** pour lancer
5. **Ouvrir les applications**:
   - Frontend: http://localhost:3000
   - Backend: http://localhost:8081 ou 8082
6. **Debugger** en mettant des breakpoints et utilisant 🐛

## ❓ Troubleshooting

### Les launchers n'apparaissent pas
```
1. File → Invalidate Caches → OK
2. Redémarrer IntelliJ
3. Vérifier que .idea/runConfigurations/ existe
```

### Maven ne trouve pas le JDK
```
File → Project Structure → Project
→ Project SDK → Sélectionner Java 21
```

### Le frontend ne se lance pas
```
Terminal → npm install
Puis relancer le launcher Frontend
```

### Port déjà en utilisation
```
Vérifier les processus:
lsof -i :3000     (Frontend Bank)
lsof -i :3001     (Frontend Exchange)
lsof -i :8081     (Backend Bank)
lsof -i :8082     (Backend Exchange)

Tuer le processus:
kill -9 <PID>
```

## 📚 Ressources

- [Nx Documentation](https://nx.dev)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev)
- [IntelliJ Documentation](https://www.jetbrains.com/help/idea/)
- Voir aussi: [README.md](README.md) et [MONOREPO.md](MONOREPO.md)

---

**Note**: Les launchers sont mis à jour automatiquement. Si vous en créez de nouveaux dans IntelliJ, ils seront sauvegardés dans `.idea/runConfigurations/`.
