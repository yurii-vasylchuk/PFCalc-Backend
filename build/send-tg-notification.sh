#!/usr/bin/env bash
set -euo pipefail

# Named args
TAG=""
URL=""

# Constants
NL="\n"

while getopts ":t:u:" opt; do
  echo $opt
  echo "$OPTARG"
  case "$opt" in
    t)  TAG="$OPTARG" ;;
    u)  URL="$OPTARG" ;;
    \?) echo "Error: unknown option -$OPTARG"; exit 1 ;;
    :)  echo "Error: option requires value -$OPTARG"; exit 1 ;;
  esac
done

shift $((OPTIND - 1))

# Positional arguments
BOT_TOKEN="$1"
CHAT_ID="$2"
STATUS="$3"
PROJECT="$4"

case "$STATUS" in
  success)   HEADER="✅ Build $PROJECT Succeeded" ;;
  failure)   HEADER="❌ Build $PROJECT Failed" ;;
  cancelled) HEADER="⏹️️ Build $PROJECT Cancelled" ;;
  skipped)   HEADER="⏭️ Build $PROJECT Skipped" ;;
  *) echo "Error: unknown status '$STATUS' (use success|failure|cancelled|skipped)"; exit 1 ;;
esac

TEXT="*${HEADER}*${NL}"

if [[ -n "$TAG" ]]; then
  TEXT="${TEXT}${NL}📦Tag: \`${TAG}\`"
fi

if [[ -n "$URL" ]]; then
  TEXT="${TEXT}${NL}🔗Url: [Details](${URL})"
fi

curl -X POST --location "https://api.telegram.org/bot${BOT_TOKEN}/sendMessage" \
    -H 'Content-Type: application/json' \
    -d "{
          \"chat_id\": \"${CHAT_ID}\",
          \"parse_mode\": \"MarkdownV2\",
          \"text\": \"${TEXT}\"
        }"

echo "Telegram notification sent: ${STATUS}"
