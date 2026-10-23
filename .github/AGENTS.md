# Agents Automatisés - Configuration

## 🚀 Configuration Requise

### 1. Labels GitHub

Créer les labels suivants dans le repository:

```bash
# Status labels
gh label create "status:ready" -d "Ticket prêt à être implémenté" -c "0366d6"
gh label create "status:in-progress" -d "Ticket en cours d'implémentation" -c "fbca04"
gh label create "status:in-review" -d "PR en review" -c "ffc200"
gh label create "status:done" -d "Ticket complété" -c "85e89d"
gh label create "status:blocked" -d "Ticket bloqué" -c "d73a49"

# Review cycle labels
gh label create "review-cycle:1" -d "Cycle 1 de review" -c "c2e0c6"
gh label create "review-cycle:2" -d "Cycle 2 de review" -c "a3d9a3"
gh label create "review-cycle:3" -d "Cycle 3 de review (dernier)" -c "7db380"
```

### 2. Secrets GitHub

Ajouter les secrets suivants dans Settings > Secrets:

- `CLAUDE_API_KEY`: Votre clé API Anthropic
- `GITHUB_TOKEN`: Auto-généré (permissions par défaut suffisent)

### 3. Variables d'Environnement

Optionnel, pour personnaliser le workflow:

```yaml
REVIEW_MAX_CYCLES: 3
IMPLEMENTATION_TIMEOUT: 3600
REVIEW_TIMEOUT: 1800
```

## 📋 Utilisation

### Créer un Ticket Ready

```bash
gh issue create \
  --title "Implémenter la fonctionnalité X" \
  --body "Description détaillée de ce qu'il faut faire" \
  --label "status:ready"
```

### Lancer le Workflow Manuellement

```bash
gh workflow run ticket-workflow.yml
```

### Suivre l'Exécution

```bash
# Voir les exécutions
gh workflow view ticket-workflow.yml

# Voir les logs d'une exécution
gh run view <run-id> --log
```

### Checker le Status d'un Ticket

```bash
gh issue view <issue-number>
```

## 🔄 Flux Automatique

1. **Ticket créé avec label `status:ready`**
   - Workflow démarre automatiquement (toutes les heures ou sur trigger)
   - Agent Implementation le récupère

2. **Agent Implementation**
   - ✅ Crée branche `feature/#XXXX-nom`
   - ✅ Effectue les changements requis
   - ✅ Crée commit
   - ✅ Pousse la branche
   - ✅ Crée PR
   - ✅ Change le status en `status:in-review`
   - ✅ Ajoute label `review-cycle:1`

3. **Agent Review (Cycle 1-2)**
   - 📊 Analyse le code et la description
   - ✅ Si acceptée:
     - Merge la PR
     - Change status en `status:done`
   - ❌ Si rejetée:
     - Commente les problèmes
     - Remet en `status:ready`
     - Incrémente cycle (1→2 ou 2→3)

4. **Agent Review (Cycle 3)**
   - ❌ Si rejetée après 3 tentatives:
     - Change status en `status:blocked`
     - Intervention manuelle requise

## 🛑 Limitations & Sécurité

✅ **Max 3 cycles** pour éviter boucles infinies
✅ **État Blocked** pour tickets non-résolubles
✅ **Branches séparées** par ticket
✅ **Logs complets** pour audit
✅ **Review manuelle** possible avant merge

## 🐛 Dépannage

### Workflow ne démarre pas
```bash
# Vérifier que la GitHub Actions est activée
gh api /repos/{owner}/{repo}/actions --method GET

# Vérifier les secrets
gh secret list
```

### Les agents ne trouvent pas les tickets
- Vérifier que le label `status:ready` existe
- Vérifier que le ticket n'a pas d'autres labels conflictuels
- Checker les logs du workflow

### PR ne se crée pas
- Vérifier que `GITHUB_TOKEN` a les bonnes permissions
- Vérifier que la branche peut être pushée
- Vérifier que le repo a les droits d'auto-merge si activé

## 📊 Monitoring

### Dashboard Status

Voir le nombre de tickets par état:

```bash
# Ready
gh issue list --label "status:ready" | wc -l

# In Progress
gh issue list --label "status:in-progress" | wc -l

# In Review
gh issue list --label "status:in-review" | wc -l

# Done
gh issue list --label "status:done" | wc -l

# Blocked
gh issue list --label "status:blocked" | wc -l
```

### Historique des Runs

```bash
gh run list --workflow ticket-workflow.yml --limit 10
```

## 🔧 Customisation

### Ajouter une étape de validation

Modifier `scripts/agent-implementation.sh`:
```bash
# Ajouter avant le commit
npm run test || exit 1
npm run lint || exit 1
```

### Changer la fréquence du workflow

Dans `.github/workflows/ticket-workflow.yml`:
```yaml
schedule:
  - cron: '*/30 * * * *'  # Toutes les 30 minutes
```

### Configurer la review avec Claude

Remplacer dans `scripts/agent-review.sh`:
```bash
# Faire un vrai appel à Claude API
REVIEW_RESULT=$(curl -s -X POST "https://api.anthropic.com/v1/messages" \
  -H "x-api-key: $CLAUDE_API_KEY" \
  -d "{\"model\": \"claude-opus-5-5\", \"messages\": [{\"role\": \"user\", \"content\": \"Review this code: $DIFF\"}]}")
```

## 📚 Fichiers

- `WORKFLOW.md` - Architecture globale
- `scripts/agent-implementation.sh` - Agent implémentation
- `scripts/agent-review.sh` - Agent review
- `scripts/workflow-orchestrator.sh` - Orchestration
- `.github/workflows/ticket-workflow.yml` - GitHub Actions
