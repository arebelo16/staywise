#!/bin/bash

set -e

LOG_FILE="./deploy.log"
TIMESTAMP=$(date "+%Y-%m-%d %H:%M:%S")
BRANCH=${1:-CORE}

log() {
  echo "[$TIMESTAMP] $1" | tee -a "$LOG_FILE"
}

error_exit() {
  echo "[$TIMESTAMP] [ERROR] $1" | tee -a "$LOG_FILE"
  exit 1
}

log "[INFO] Starting deployment process..."

# Check if .git exists
if [ ! -d .git ]; then
  error_exit "This is not a Git repository."
fi

# Pull latest code
log "[INFO] Pulling latest code from Git branch '$BRANCH'..."
git pull origin "$BRANCH" || error_exit "Git pull failed."

# Stop and remove old containers
log "[INFO] Stopping existing containers..."
docker-compose down || error_exit "Failed to stop containers."

# Build and start new containers
log "[INFO] Building and starting containers..."
docker-compose up -d --build || error_exit "Failed to start containers."

log "[SUCCESS] Deployment complete!"
