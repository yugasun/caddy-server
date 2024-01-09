#!/bin/bash

pid=$(jps | grep caddy-server | awk '{print $1}')

if [ -n "$pid" ]; then
  echo "Caddy Server is running, pid: $pid"
  echo "killing $pid"
  kill -9 $pid
fi

echo "Starting Caddy Server"
# please copy ./config/application.yml to ./config/application.local.yml
# and modify the config file
nohup java -jar target/caddy-server-0.0.1-SNAPSHOT.jar --spring.config.location=./config/application-local.yml > run.log 2>&1 &