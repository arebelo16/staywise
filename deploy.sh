#!/bin/bash

### ───────── CONFIG ─────────
PROJECT_NAME="Staywise"
PROJECT_DIR=$(pwd)
LOG_FILE="$PROJECT_DIR/deploy.log"
BRANCH="CORE"
DOCKER_COMPOSE="docker-compose"
### ──────────────────────────

log() {
  echo "$(date '+%Y-%m-%d %H:%M:%S') [INFO] $1" | tee -a "$LOG_FILE"
}

error() {
  echo "$(date '+%Y-%m-%d %H:%M:%S') [ERROR] $1" | tee -a "$LOG_FILE" >&2
  exit 1
}

log "===== Restarting $PROJECT_NAME ====="

# Pull latest code
log "Fetching latest code from branch '$BRANCH'..."
git reset --hard || error "Git reset failed"
git clean -fd || error "Git clean failed"
git pull origin "$BRANCH" || error "Git pull failed"

# Stop current containers
log "Stopping Docker containers..."
$DOCKER_COMPOSE down || error "Failed to stop containers"

# Rebuild everything
log "Rebuilding project..."
$DOCKER_COMPOSE build || error "Docker build failed"

# Start again
log "Starting services..."
$DOCKER_COMPOSE up -d || error "Failed to start containers"

log "$PROJECT_NAME successfully restarted ✅"
