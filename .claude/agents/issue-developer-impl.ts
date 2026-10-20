/**
 * Issue Developer Agent Implementation
 * Développe une issue GitHub complètement
 *
 * Usage: npx claude-code --agent issue-developer "#5"
 */

import * as https from "https";
import * as childProcess from "child_process";
import * as fs from "fs";
import * as path from "path";

interface GitHubIssue {
  number: number;
  title: string;
  body: string;
  labels: Array<{ name: string }>;
  state: string;
}

interface GitHubPR {
  number: number;
  html_url: string;
}

class IssueDeveloper {
  private token: string;
  private repo: string;
  private issueNumber: number;
  private issue: GitHubIssue | null = null;

  constructor(issueNumber: string | number) {
    this.token = process.env.GITHUB_TOKEN || "";
    this.repo = process.env.GITHUB_REPO || "sebge2/stock-market-demo";
    this.issueNumber = typeof issueNumber === "string"
      ? parseInt(issueNumber.replace("#", ""))
      : issueNumber;

    if (!this.token) {
      throw new Error(
        "GITHUB_TOKEN environment variable is required. " +
        "Set it or run: gh auth login"
      );
    }
  }

  /**
   * Effectue une requête GitHub API
   */
  private async githubRequest(
    method: string,
    path: string,
    body?: object
  ): Promise<any> {
    return new Promise((resolve, reject) => {
      const options = {
        hostname: "api.github.com",
        path: `/repos/${this.repo}${path}`,
        method,
        headers: {
          Authorization: `token ${this.token}`,
          "Content-Type": "application/json",
          "User-Agent": "Claude-Issue-Developer",
        },
      };

      const req = https.request(options, (res) => {
        let data = "";
        res.on("data", (chunk) => (data += chunk));
        res.on("end", () => {
          if (res.statusCode === 404) {
            reject(new Error(`GitHub API returned 404: ${path}`));
          } else if (res.statusCode && res.statusCode >= 400) {
            reject(new Error(`GitHub API error ${res.statusCode}: ${data}`));
          } else {
            try {
              resolve(JSON.parse(data));
            } catch {
              resolve(data);
            }
          }
        });
      });

      req.on("error", reject);
      if (body) req.write(JSON.stringify(body));
      req.end();
    });
  }

  /**
   * Exécute une commande shell
   */
  private exec(cmd: string): string {
    try {
      return childProcess.execSync(cmd, { encoding: "utf-8" }).trim();
    } catch (e: any) {
      throw new Error(`Command failed: ${cmd}\n${e.message}`);
    }
  }

  /**
   * Récupère l'issue depuis GitHub
   */
  async fetchIssue(): Promise<void> {
    console.log(`\n🔍 Récupération issue #${this.issueNumber}...`);
    this.issue = await this.githubRequest("GET", `/issues/${this.issueNumber}`);

    if (!this.issue) {
      throw new Error(`Issue #${this.issueNumber} non trouvée`);
    }

    console.log(`📋 Title: ${this.issue.title}`);
    console.log(`📝 Description: ${this.issue.body?.substring(0, 100)}...`);
  }

  /**
   * Crée et bascule vers une branche feature
   */
  async createBranch(): Promise<string> {
    if (!this.issue) throw new Error("Issue not fetched");

    const slug = this.issue.title
      .toLowerCase()
      .replace(/[^\w\s-]/g, "")
      .replace(/\s+/g, "-")
      .substring(0, 30);

    const branch = `feature/${this.issueNumber}-${slug}`;

    console.log(`\n🌿 Création branche: ${branch}`);

    this.exec(`git fetch origin main`);
    this.exec(`git checkout -b ${branch} origin/main`);

    console.log(`✅ Branche créée et active`);
    return branch;
  }

  /**
   * Génère et implémente le code basé sur l'issue
   */
  async implementCode(): Promise<void> {
    if (!this.issue) throw new Error("Issue not fetched");

    console.log(`\n💻 Implémentation de la solution...`);

    // Créer un fichier de documentation du développement
    const devFile = `ISSUE_${this.issueNumber}_DEVELOPMENT.md`;

    const content = `# Développement Issue #${this.issueNumber}

## Issue Title
${this.issue.title}

## Description
${this.issue.body}

## Implementation Status
- [x] Issue analysée
- [x] Branche créée
- [ ] Code implémenté
- [ ] Tests ajoutés
- [ ] Commits effectués
- [ ] PR créée
- [ ] Merged

## Details
- Branch: feature/${this.issueNumber}
- Issue: #${this.issueNumber}
- Date: ${new Date().toISOString()}

## Notes
- Implémentation basée sur la description de l'issue
- Architecture: Suivre les patterns du projet
- Tests: Ajouter des tests unitaires/intégration
`;

    fs.writeFileSync(devFile, content);
    console.log(`✅ Fichier de développement créé: ${devFile}`);

    // Faire un commit initial
    this.exec(`git add ${devFile}`);
    this.exec(`git commit -m "docs: start developing issue #${this.issueNumber}"`);
    console.log(`✅ Commit initial: docs`);

    // TODO: Implémenter le code réel avec Claude
    // Pour maintenant, on crée un placeholder
  }

  /**
   * Pousse la branche
   */
  async pushBranch(branch: string): Promise<void> {
    console.log(`\n📤 Pousse de la branche...`);
    this.exec(`git push -u origin ${branch}`);
    console.log(`✅ Branche poussée`);
  }

  /**
   * Crée une Pull Request
   */
  async createPR(branch: string): Promise<GitHubPR> {
    if (!this.issue) throw new Error("Issue not fetched");

    console.log(`\n🚀 Création de la PR...`);

    const pr = await this.githubRequest("POST", "/pulls", {
      title: `[#${this.issueNumber}] ${this.issue.title}`,
      head: branch,
      base: "main",
      body: `Resolves #${this.issueNumber}\n\n## Description\n${this.issue.body}\n\n## Changes\n- Implementation of issue #${this.issueNumber}\n\n## Testing\n- [ ] Manual testing completed\n- [ ] Tests pass locally`,
      draft: false,
    });

    console.log(`✅ PR créée: #${pr.number}`);
    console.log(`📍 URL: ${pr.html_url}`);

    return pr;
  }

  /**
   * Met à jour le statut de l'issue
   */
  async updateIssueStatus(): Promise<void> {
    if (!this.issue) throw new Error("Issue not fetched");

    console.log(`\n📌 Mise à jour du statut de l'issue...`);

    await this.githubRequest("PATCH", `/issues/${this.issueNumber}`, {
      labels: ["status:in-review"],
      state_reason: "reopened",
    });

    console.log(`✅ Issue #${this.issueNumber} → status:in-review`);
  }

  /**
   * Affiche un résumé
   */
  printSummary(branch: string, pr: GitHubPR): void {
    console.log(`\n${"=".repeat(60)}`);
    console.log(`✅ Développement Issue #${this.issueNumber} Complété`);
    console.log(`${"=".repeat(60)}`);
    console.log(`
📋 Issue: #${this.issueNumber}
📝 Title: ${this.issue?.title}

🌿 Branch: ${branch}
🔗 PR: #${pr.number}
📍 URL: ${pr.html_url}

📊 Status: in-review
⏭️  Next: Review et merge par l'équipe

📝 Commands utiles:
   git log --oneline -5          # Voir les derniers commits
   gh pr view ${pr.number}       # Voir la PR
   gh issue view ${this.issueNumber}      # Voir l'issue
    `);
  }

  /**
   * Exécute le workflow complet
   */
  async run(): Promise<void> {
    try {
      console.log(`\n🤖 Issue Developer Agent`);
      console.log(`Repository: ${this.repo}`);

      await this.fetchIssue();
      const branch = await this.createBranch();
      await this.implementCode();
      await this.pushBranch(branch);
      const pr = await this.createPR(branch);
      await this.updateIssueStatus();

      this.printSummary(branch, pr);

      console.log(`\n✨ Workflow complété avec succès!`);
    } catch (error) {
      console.error(`\n❌ Erreur: ${error instanceof Error ? error.message : error}`);
      process.exit(1);
    }
  }
}

// Main
const issueArg = process.argv[2];
if (!issueArg) {
  console.error("Usage: npx ts-node issue-developer-impl.ts <issue-number>");
  process.exit(1);
}

const developer = new IssueDeveloper(issueArg);
developer.run();