#!/bin/bash

work_dir=$(cd $(dirname $0); pwd)
pid=$(jps | grep caddy-server | awk '{print $1}')

if [ -n "$pid" ]; then
  echo "Caddy Server is running, pid: $pid"
  echo "killing $pid"
  kill -9 $pid
fi

echo "Starting Caddy Server"
# please copy ./config/application.yml to ./config/application.local.yml
# and modify the config file
java -jar $work_dir/target/caddy-server-0.0.1-SNAPSHOT.jar --spring.config.location=$work_dir/config/application-local.yml > run.log