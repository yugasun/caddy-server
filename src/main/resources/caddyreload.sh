#!/bin/bash

CADDY_HOME=/home/data/caddy
BASE_URL="https://admin.dev.futurefab.cn"

caddy_file=$CADDY_HOME/Caddyfile
temp_json=$CADDY_HOME/temp.json
caddy_json=$CADDY_HOME/caddy.json
layer4_file=$CADDY_HOME/layer4.txt

echo "Removing existing caddy.json file"
rm $caddy_json

echo "Generating caddy.json file"
#caddy adapt --config $caddy_file --pretty --validate >$caddy_json
curl "$BASE_URL/adapt" \
	-H "Content-Type: text/caddyfile" \
	--data-binary @$caddy_file

echo "Reading $layer4_file"
layer4_content=$(cat "$layer4_file")

echo "Inserting layer4 content into caddy.json"
jq --argjson layer4 "$layer4_content" '.apps.layer4 = $layer4' $caddy_json > $temp_json && mv $temp_json $caddy_json

echo "Upload caddy.json to caddy server"
curl "$BASE_URL/load" \
  -H "Content-Type: application/json" \
  -d @$caddy_json
