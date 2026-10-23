# Custom Agents - Utilisation

## 🤖 Agents Disponibles

### Issue Developer
**Fichier**: `.claude/agents/issue-developer.md`

Développe complètement une issue GitHub:
- Récupère l'issue depuis GitHub
- Crée une branche feature
- Implémente la solution
- Crée une PR
- Met à jour le statut en `in-review`

## 📖 Installation

```bash
# Cloner le repo
cd /Users/sebastiengerard/sandboxes/stock-market-demo

# Installer les dépendances
npm install

# Vérifier l'installation
ls -la .claude/agents/
```

## 🚀 Utilisation

### 1. Authentification GitHub

```bash
# Option A: CLI GitHub
gh auth login

# Option B: Token d'environnement
export GITHUB_TOKEN=ghp_xxxxxxxxxxxx
export GITHUB_REPO=sebge2/stock-market-demo
```

### 2. Lancer l'Agent Issue Developer

```bash
# Développer issue #1
./.claude/agents/issue-developer.sh 1

# Ou avec le numéro préfixé par #
./.claude/agents/issue-developer.sh "#5"

# Ou avec npx ts-node directement
npx ts-node ./.claude/agents/issue-developer-impl.ts 3
```

### 3. Résultat Attendu

L'agent va:

```
✅ Branche créée: feature/1-implémenter-authentification
✅ Code implémenté et commité
✅ Branche pushée vers origin
✅ PR créée: #42 lié à #1
✅ Issue #1 → status:in-review
```

## 📋 Exemple Complet

### Étape 1: Créer une issue

```bash
gh issue create \
  --title "Ajouter authentification JWT" \
  --body "Implémenter JWT auth pour l'API
  
## Requirements
- [ ] Générer tokens JWT
- [ ] Middleware d'authentification
- [ ] Routes protégées
- [ ] Tests unitaires" \
  --label "status:ready"
```

### Étape 2: Lancer l'agent

```bash
export GITHUB_TOKEN=ghp_xxxx
./.claude/agents/issue-developer.sh 1
```

### Étape 3: Vérifier le résultat

```bash
# Voir la PR créée
gh pr view 42 --web

# Voir l'issue mise à jour
gh issue view 1

# Voir la branche
git branch -a | grep feature/1
```

## 🔧 Configuration

### Variables d'Environnement

```bash
# Requis
GITHUB_TOKEN=ghp_xxxxxxxxxxxx

# Optionnel
GITHUB_REPO=sebge2/stock-market-demo  # Auto-détecté sinon
```

### Configuration Git

```bash
# Vérifier la configuration
git config user.name
git config user.email

# Configurer si nécessaire
git config --global user.name "Your Name"
git config --global user.email "your@email.com"
```

## 📊 Workflow Détaillé

```
1. fetchIssue()
   └─ Récupère issue #N depuis GitHub API
   └─ Valide que l'issue existe
   └─ Affiche title + description

2. createBranch()
   └─ Génère slug from title
   └─ Crée branche: feature/{N}-{slug}
   └─ Checkout la branche

3. implementCode()
   └─ Crée ISSUE_{N}_DEVELOPMENT.md
   └─ Commit initial
   └─ Prêt pour l'implémentation

4. pushBranch()
   └─ git push -u origin {branch}
   └─ Valide la push

5. createPR()
   └─ POST /repos/{repo}/pulls
   └─ Title: "[#{N}] {issue-title}"
   └─ Body: Description + changements
   └─ Retourne PR #M

6. updateIssueStatus()
   └─ PATCH /issues/{N}
   └─ Labels: ["status:in-review"]
   └─ Issue prête pour review

7. printSummary()
   └─ Affiche résumé final
   └─ Commandes utiles
```

## 🛠️ Dépannage

### "GITHUB_TOKEN is required"

```bash
# Solution 1: Utiliser gh CLI
gh auth login
# Accepter les prompts

# Solution 2: Exporter le token
export GITHUB_TOKEN=ghp_xxxxxxxxxxxx
```

### "Issue not found"

```bash
# Vérifier que l'issue existe
gh issue view 999

# Vérifier le repo
echo $GITHUB_REPO

# Lancer avec le bon repo
GITHUB_REPO=owner/repo ./.claude/agents/issue-developer.sh 1
```

### "Branch already exists"

```bash
# Supprimer la branche locale
git branch -D feature/1-slug

# Supprimer la branche distante
git push origin --delete feature/1-slug

# Relancer l'agent
./.claude/agents/issue-developer.sh 1
```

### "PR creation failed"

```bash
# Vérifier les permissions du token
gh auth status

# Vérifier que la branche est pushée
git branch -r | grep feature/1

# Vérifier le repo et les droits
gh repo view
```

## 📚 Fichiers de l'Agent

```
.claude/agents/
├── issue-developer.md           # Définition de l'agent
├── issue-developer-impl.ts      # Implémentation TypeScript
├── issue-developer.sh           # Script wrapper bash
└── AGENTS_USAGE.md             # Ce fichier
```

## 🔄 Extension de l'Agent

Pour ajouter plus de fonctionnalités:

### 1. Ajouter une étape de validation

Dans `issue-developer-impl.ts`:

```typescript
async validateImplementation(): Promise<void> {
  console.log(`\n✅ Validation...`);
  this.exec(`npm run lint`);
  this.exec(`npm run test`);
}
```

### 2. Ajouter des labels automatiques

```typescript
async updateIssueStatus(): Promise<void> {
  await this.githubRequest("PATCH", `/issues/${this.issueNumber}`, {
    labels: [
      "status:in-review",
      "priority:high",
      "type:feature"
    ],
  });
}
```

### 3. Poster un commentaire sur la PR

```typescript
async postPRComment(prNumber: number, comment: string): Promise<void> {
  await this.githubRequest(
    "POST", 
    `/issues/${prNumber}/comments`,
    { body: comment }
  );
}
```

## 📝 Notes

- L'agent crée des commits avec messages descriptifs
- Les branches suivent le format: `feature/{number}-{slug}`
- Les PRs sont liées automatiquement aux issues
- Le statut de l'issue passe à `in-review` automatiquement
- L'agent supporte les issues avec jusqu'à 5000 caractères de description

## 🎯 Cas d'Usage

### Cas 1: Issue Simple

```bash
# Issue avec description courte
./.claude/agents/issue-developer.sh 1
# → Tout est automatique
```

### Cas 2: Issue Complexe

```bash
# Issue avec beaucoup de requirements
./.claude/agents/issue-developer.sh 10
# → Crée la structure, prêt à implémenter
```

### Cas 3: Batch

```bash
# Développer plusieurs issues
for i in 1 2 3; do
  ./.claude/agents/issue-developer.sh $i
  sleep 5  # Éviter rate limit
done
```