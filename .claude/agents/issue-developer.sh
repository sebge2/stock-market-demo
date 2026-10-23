#!/bin/bash
# Issue Developer Agent - Wrapper Script
# Usage: ./issue-developer.sh <issue-number>

set -e

ISSUE_NUMBER=$1
REPO=${GITHUB_REPO:-"sebge2/stock-market-demo"}
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

if [ -z "$ISSUE_NUMBER" ]; then
  echo "❌ Usage: $0 <issue-number>"
  echo ""
  echo "Example:"
  echo "   $0 5        # Develop issue #5"
  echo "   $0 '#10'    # Develop issue #10"
  exit 1
fi

# Normaliser le numéro de l'issue
ISSUE_NUMBER=$(echo "$ISSUE_NUMBER" | sed 's/^#//')

if ! [[ "$ISSUE_NUMBER" =~ ^[0-9]+$ ]]; then
  echo "❌ Invalid issue number: $ISSUE_NUMBER"
  exit 1
fi

echo "🤖 Issue Developer Agent"
echo "📍 Repository: $REPO"
echo "📋 Issue: #$ISSUE_NUMBER"
echo ""

# Vérifier que le token GitHub est disponible
if [ -z "$GITHUB_TOKEN" ]; then
  echo "⚠️  GITHUB_TOKEN not set"
  echo "   Run: gh auth login"
  echo "   Or: export GITHUB_TOKEN=ghp_xxxx"
  exit 1
fi

# Vérifier qu'on est dans le bon répertoire
if [ ! -f "$PROJECT_ROOT/package.json" ]; then
  echo "❌ Not in project root directory"
  exit 1
fi

cd "$PROJECT_ROOT"

# Lancer l'agent avec TypeScript
if command -v npx &> /dev/null; then
  # TypeScript version
  if [ -f "$SCRIPT_DIR/issue-developer-impl.ts" ]; then
    echo "🚀 Launching Issue Developer Agent..."
    GITHUB_REPO="$REPO" GITHUB_TOKEN="$GITHUB_TOKEN" \
      npx ts-node "$SCRIPT_DIR/issue-developer-impl.ts" "$ISSUE_NUMBER"
  fi
else
  echo "❌ npx not found. Install Node.js"
  exit 1
fi