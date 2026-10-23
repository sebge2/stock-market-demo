# Workflow Automatisé - Tickets GitHub

## Architecture

### États des Tickets
- **Ready** → Ticket à implémenter
- **In Progress** → Implémentation en cours
- **In Review** → PR créée, en attente de review
- **Done** → Implémentation acceptée
- **Blocked** → Bloqué après 3 cycles de review échoués

### Cycle de Vie

```
Ready
  ↓ [Agent 1: Implementation]
  → In Progress
  → Crée branche + implémente
  → Crée PR
  → In Review
  ↓ [Agent 2: Review]
  ├→ ✓ Review Passée → Done
  └→ ✗ Review Échouée → Ready (max 3 fois)
            Après 3 cycles → Blocked
```

### Cycle de Review

Chaque ticket en "In Review" compte ses tentatives:
- **Cycle 1-2**: Review échouée → Remet en "Ready"
- **Cycle 3**: Review échouée → Passe en "Blocked"

Le compteur est stocké dans un label GitHub: `review-cycle:X`

## Agents

### Agent 1: Implementation
**Trigger**: Ticket avec label `status:ready`

**Actions**:
1. Récupère le ticket
2. Ajoute label `status:in-progress`
3. Crée branche: `feature/#{issue-number}-{slug}`
4. Applique la description (code/changes)
5. Commit et pousse
6. Crée PR avec description du ticket
7. Ajoute label `status:in-review`
8. Ajoute label `review-cycle:1`

### Agent 2: Review
**Trigger**: PR avec label `status:in-review`

**Actions**:
1. Récupère le ticket/PR
2. Lit le code et la description
3. Effectue une review technique
4. Si ✓ acceptée:
   - Merge la PR
   - Ajoute label `status:done`
   - Supprime labels de review
5. Si ✗ rejetée:
   - Lit le label `review-cycle:X`
   - Si X < 3:
     - Commente les problèmes
     - Ajoute label `status:ready`
     - Incrémente à `review-cycle:X+1`
   - Si X >= 3:
     - Ajoute label `status:blocked`
     - Commente "Bloqué après 3 cycles"

## Configuration GitHub

### Labels requis
- `status:ready`
- `status:in-progress`
- `status:in-review`
- `status:done`
- `status:blocked`
- `review-cycle:1`, `review-cycle:2`, `review-cycle:3`

### Variables d'environnement
- `GITHUB_TOKEN`: Token d'authentification
- `GITHUB_REPO`: Owner/Repo
- `CLAUDE_API_KEY`: Clé API Anthropic

## Limites de Sécurité

✓ Max 3 cycles de review pour éviter boucles infinies
✓ État "Blocked" pour les cas non-résolubles
✓ Review manuelle requise pour approuver
✓ Branche séparée pour chaque ticket
