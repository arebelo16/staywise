#!/bin/bash

set -euo pipefail

BRANCH="${1:-CORE}"
LOG_FILE="./deploy.log"
TIMESTAMP() { date "+%Y-%m-%d %H:%M:%S"; }

log() {
  echo "[$(TIMESTAMP)] $1" | tee -a "$LOG_FILE"
}

error_exit() {
  echo "[$(TIMESTAMP)] [ERROR] $1" | tee -a "$LOG_FILE"
  exit 1
}

log "[INFO] Starting deployment for branch '$BRANCH'..."

if [ ! -d .git ]; then
  error_exit "This is not a Git repository."
fi

log "[INFO] Checking out branch '$BRANCH'..."
git fetch origin || error_exit "Git fetch failed"
git checkout "$BRANCH" || error_exit "Branch '$BRANCH' does not exist"
git pull origin "$BRANCH" --no-rebase || error_exit "Git pull failed"

log "[INFO] Stopping running containers..."
docker-compose down || error_exit "Failed to stop containers"

log "[INFO] Building and starting containers..."
docker-compose up -d --build || error_exit "Failed to start containers"

log "[SUCCESS] Deployment complete for branch '$BRANCH'."
