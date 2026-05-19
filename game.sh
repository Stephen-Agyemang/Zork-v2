#!/bin/bash
BASE_URL="http://localhost:8080/game"

echo "Starting game..."
curl -s -X POST "$BASE_URL/start" > /dev/null
echo "Type commands (look, go north, inventory, status, quit)"
echo ""

while true; do
  read -r -p ">>> " command
  [[ -z "$command" ]] && continue
  response=$(curl -s -X POST "$BASE_URL/command" -H "Content-Type: text/plain" -d "$command")
  echo ""
  echo "$response"
  echo ""
done
