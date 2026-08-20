#!/bin/sh
set -eu

cat > /usr/share/nginx/html/config.js <<EOF
window.CANISCARE_CONFIG = {
  API_BASE_URL: "${API_BASE_URL:-http://localhost:8081}",
};
EOF
