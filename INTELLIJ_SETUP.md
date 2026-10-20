# IntelliJ IDEA Setup 🚀

Guide pour configurer et utiliser IntelliJ IDEA avec Stock Market Demo monorepo.

## 📋 Prérequis

- IntelliJ IDEA 2023.1+ (Community ou Ultimate)
- Java 21 SDK configuré dans IntelliJ
- Node.js 18+ (pour le frontend)
- Maven 3.9+ (pour les services Java)

## ⚙️ Configuration Initiale

### 1. Ouvrir le Projet

```
File → Open → Sélectionner le répertoire stock-market-demo
```

IntelliJ devrait détecter automatiquement :
- ✅ Le workspace Nx
- ✅ Les projets Maven (bank et stock-exchange)
- ✅ La configuration Node.js/React

### 2. Configurer le JDK

```
IntelliJ IDEA → Preferences → Project: stock-market-demo → Project Structure
→ Project SDK → Télécharger Java 21 (ou sélectionner si disponible)
```

### 3. Configurer Node.js (Optionnel mais Recommandé)

```
Preferences → Languages & Frameworks → Node.js
→ Node interpreter → Sélectionner votre Node.js
```

## 🎯 Launchers Disponibles

IntelliJ a détecté automatiquement les launchers suivants dans `.idea/runConfigurations/` :

### ▶️ Frontend
**Nom :** Frontend  
**Type :** npm run dev:frontend  
**Port :** 3000  
**Description :** Lance le serveur de développement React avec hot-reload

**Utilisation :**
```
Cliquer sur ▶️ Frontend dans le sélecteur de run configuration
Ou : Alt + Shift + F10 → sélectionner Frontend → Enter
```

### ▶️ Bank Service
**Nom :** Bank Service  
**Type :** mvn spring-boot:run  
**Port :** 8081  
**Context Path :** /api/bank  
**Description :** Lance le service Banking sur le port 8081

**Utilisation :**
```
Cliquer sur ▶️ Bank Service dans le sélecteur de run configuration
Ou : Alt + Shift + F10 → sélectionner Bank Service → Enter
```

### ▶️ Stock Exchange Service
**Nom :** Stock Exchange Service  
**Type :** mvn spring-boot:run  
**Port :** 8082  
**Context Path :** /api/exchange  
**Description :** Lance le service Stock Exchange sur le port 8082

**Utilisation :**
```
Cliquer sur ▶️ Stock Exchange Service dans le sélecteur de run configuration
Ou : Alt + Shift + F10 → sélectionner Stock Exchange Service → Enter
```

### ▶️ Backend Services (Compound)
**Nom :** Backend Services  
**Type :** Compound (lanceur multiple)  
**Description :** Lance Bank Service et Stock Exchange Service simultanément

**Utilisation :**
```
Cliquer sur ▶️ Backend Services dans le sélecteur de run configuration
Les deux services se lancent dans des onglets séparés du Run panel
```

### ▶️ All Services (Compound)
**Nom :** All Services  
**Type :** Compound (lanceur multiple)  
**Description :** Lance Frontend, Bank Service et Stock Exchange Service simultanément

**Utilisation :**
```
Cliquer sur ▶️ All Services dans le sélecteur de run configuration
Tous les services se lancent dans des onglets séparés du Run panel
```

## 🎮 Comment Utiliser les Launchers

### Méthode 1 : Bouton Run/Debug dans la Toolbar

```
┌─────────────────────────────────────────────────┐
│ ▶️ [Frontend▼] | 🐛 ⏸️ ⏹️ |                    │
└─────────────────────────────────────────────────┘
    Cliquer sur ▼ pour sélectionner une config
```

### Méthode 2 : Menu Run

```
Run → Run... → Sélectionner la configuration
```

### Méthode 3 : Raccourci Clavier

```
Alt + Shift + F10    (Ouvrir le sélecteur de run configuration)
Alt + Shift + F9     (Ouvrir le sélecteur de debug configuration)
Shift + F10          (Lancer la configuration sélectionnée)
```

## 📍 Localiser les Services en Développement

Une fois les services lancés, vous verrez :

```
Run Tab → Onglets des services
├── Frontend         → http://localhost:3000
├── Bank Service     → http://localhost:8081/api/bank
└── Exchange Service → http://localhost:8082/api/exchange
```

## 🐛 Debugging

### Debugger le Frontend

```
1. Cliquer sur 🐛 (au lieu de ▶️) pour Backend Services
2. Dans Chrome: F12 → Sources
3. Les breakpoints IntelliJ seront synchronisés
```

### Debugger les Services Java

```
1. Cliquer sur 🐛 (au lieu de ▶️) pour Bank Service
2. IntelliJ s'arrête aux breakpoints
3. Utiliser la Debugger toolbar pour step/continue
```

## 🔍 Logs et Console

### Voir les logs

```
View → Tool Windows → Run (Alt + 4)
Chaque service a son onglet avec les logs en direct
```

### Filtrer les logs

```
Dans l'onglet Run:
- Icône en bas pour filter par log level
- Search box pour chercher dans les logs
```

## ⚡ Raccourcis Clavier Utiles

| Action | Raccourci (Mac) | Raccourci (Linux/Windows) |
|--------|-----------------|---------------------------|
| Run configuration | ⌃R | Alt + Shift + F10 |
| Debug configuration | ⌃D | Alt + Shift + F9 |
| Stop | ⌘F2 | Ctrl + F2 |
| Rerun | ⌘R | Ctrl + F5 |
| Step Over | F6 | F6 |
| Step Into | F7 | F7 |
| Step Out | ⇧F8 | Shift + F8 |

## 🛠️ Configuration Personnalisée

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
→ Ajouter : VAR_NAME=value
```

## 🔧 Configuration IDE Recommandée

### Plugins Recommandés

```
Preferences → Plugins
Installer:
- Nx Console (pour les commandes Nx)
- Spring Boot (pour le dev Java)
- JavaScript Debugger (pour le frontend)
```

### Formatage du Code

```
Preferences → Editor → Code Style
→ Sélectionner "Project" si disponible
→ Appliquer les règles du projet
```

## 📚 Fichiers Importants

```
.idea/
├── runConfigurations/
│   ├── Frontend.xml                    (npm dev:frontend)
│   ├── Bank_Service.xml                (mvn spring-boot:run)
│   ├── Stock_Exchange_Service.xml      (mvn spring-boot:run)
│   ├── Backend_Services.xml            (Compound)
│   └── All_Services.xml                (Compound)
└── .gitignore                          (Ignore les fichiers IDE)
```

## ❓ Troubleshooting

### Les launchers n'apparaissent pas

```
1. Recharger le projet : File → Invalidate Caches → OK
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
Bank Service (8081):    lsof -i :8081 | grep LISTEN
Exchange (8082):        lsof -i :8082 | grep LISTEN
Frontend (3000):        lsof -i :3000 | grep LISTEN

Tuer le processus :     kill -9 <PID>
```

## 📝 Notes

- Les launchers sont définis dans `.idea/runConfigurations/` en XML
- Ils sont ignorés par Git (voir `.idea/.gitignore`)
- Ils sont automatiquement rechargés quand modifiés
- Les configurations peuvent être partagées en supprimant `.gitignore` (si souhaité)

## 🎯 Workflow Recommandé

1. **Ouvrir le projet** dans IntelliJ
2. **Configurer le JDK** si nécessaire
3. **Lancer "All Services"** pour démarrer tout
4. **Ouvrir http://localhost:3000** dans le navigateur
5. **Utiliser les tabs Run** pour voir les logs de chaque service
6. **Debugger** en mettant des breakpoints

---

Pour plus d'aide : Voir [README.md](README.md) et [MONOREPO.md](MONOREPO.md)
