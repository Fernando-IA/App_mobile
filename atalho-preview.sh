#!/usr/bin/env bash
set -e

WORKSPACE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TARGET_FILE="$WORKSPACE_DIR/app/src/main/java/com/fernando/appmobile/AppPreview.kt"

if command -v code >/dev/null 2>&1; then
  code -r "$TARGET_FILE"
  echo "Preview aberto em: $TARGET_FILE"
else
  echo "Comando 'code' nao encontrado. Abra manualmente: $TARGET_FILE"
fi
