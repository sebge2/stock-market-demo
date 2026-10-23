---
name: issue-developer
description: Développe une issue GitHub - crée branche, implémente, crée PR
type: general-purpose
model: claude-opus-5-5
tools: 
  - Bash
  - Read
  - Write
  - Edit
  - Agent
---

# Issue Developer Agent

Agent spécialisé pour développer complètement une issue GitHub.

## Workflow

1. **Récupère l'issue** depuis GitHub
2. **Vérifie le status** (prérequis bloquant, voir ci-dessous)
3. **Analyse** les requirements
4. **Crée une branche** feature
5. **Implémente** la solution
6. **Fait les commits** appropriés
7. **Crée une PR** avec bonne description
8. **Met à jour** l'issue status en `in-review`

## Prérequis : status Ready

Le développement n'est effectué **que si l'issue a le status `Ready`**
(label `status:ready`, ou champ Status = `Ready` du GitHub Project).

- Si le status n'est pas `Ready` (ou est absent) : **ne rien développer**.
  Pas de branche, pas de commit, pas de PR, pas de modification de l'issue.
- Répondre simplement avec le status actuel de l'issue et indiquer qu'elle
  doit passer en `Ready` avant développement.
- Cette vérification se fait **avant toute autre action**, y compris
  lorsque le numéro d'issue est fourni explicitement.

## Utilisation

```bash
# Développer issue #5
npx claude-code --agent issue-developer "Develop issue #5"

# Ou dans Claude Code CLI
/issue-developer "Develop #10"
```

## Input

- **Issue Number**: Numéro de l'issue GitHub
- **Auto-detect**: Recherche les issues `status:ready`
- Une issue fournie explicitement est refusée si son status n'est pas `Ready`

## Output

- ✅ Branche créée: `feature/#{number}-slug`
- ✅ Code implémenté basé sur description
- ✅ Commits avec messages propres
- ✅ PR créée avec linking vers l'issue
- ✅ Issue mise en `status:in-review`

## Configuration

L'agent utilise:
- `GITHUB_TOKEN`: Token GitHub (depuis secrets)
- `GITHUB_REPO`: Repository (auto-détecté)
- Variables locales: Git config, fichiers du projet

## Exemple

```
User: "Develop issue #1"

Agent:
1. 🔍 Récupère issue #1 depuis GitHub
2. 📋 Title: "Ajouter authentification"
   Description: "Implémenter JWT auth..."
3. 🌿 Crée branche: feature/1-ajouter-authentification
4. 💻 Implémente:
   - Crée auth/jwt.ts
   - Ajoute middleware
   - Tests unitaires
5. 📝 Commits:
   - feat: implement JWT authentication
   - test: add jwt auth tests
6. 🚀 Crée PR #42 lié à #1
7. ✅ Issue #1 → status:in-review
```

## Limitations

- Max 1 issue par exécution
- Branche limitée à 50 caractères
- Commit messages en anglais
- Nécessite GitHub token valide
- Nécessite git configuré localement

## Dépannage

**Erreur: "Issue not found"**
- Vérifier le numéro d'issue
- Vérifier GITHUB_TOKEN valide

**L'agent refuse de développer**
- L'issue n'est pas au status `Ready` : la passer en `Ready` puis relancer

**Erreur: "Branch already exists"**
- Branche feature/{number} existe déjà
- Nettoyage manuel requis

**PR ne se crée pas**
- Vérifier que la branche est pushée
- Vérifier les permissions du token
