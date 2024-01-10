#!/bin/bash

set -e

if ! command -v caddy &> /dev/null
then
    echo "caddy could not be found, please install caddy first"
    exit 1
fi

# get the first argument
BY_API=$1
CADDY_HOME=./
BASE_URL="https://localhist:2019"

caddy_file=$CADDY_HOME/Caddyfile
temp_json=$CADDY_HOME/temp.json
caddy_json=$CADDY_HOME/caddy.json

echo "Removing existing caddy.json file"
rm $caddy_json

echo "Generating caddy.json file"

# FIXME: can not validate by callling by caddy server, it will hang
if [ "$BY_API" = "BY_API" ]; then
  echo "Reading caddyfile by api"
  caddy adapt --config $caddy_file --pretty > $caddy_json
else
  caddy adapt --config $caddy_file --pretty --validate > $caddy_json
fi

echo "Reading $layer4_file"
layer4_content=$(cat "$layer4_file")

echo "Inserting layer4 content into caddy.json"
jq --argjson layer4 "$layer4_content" '.apps.layer4 = $layer4' $caddy_json > $temp_json && mv $temp_json $caddy_json

echo "Upload caddy.json to caddy server"
curl "$BASE_URL/load" \
  -H "Content-Type: application/json" \
  -d @$caddy_json
