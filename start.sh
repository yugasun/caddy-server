#!/bin/bash

# please copy ./config/application.yml to ./config/application.local.yml
# and modify the config file
nohup java -jar target/caddy-server-0.0.1-SNAPSHOT.jar --spring.config.location=./config/application.local.yml > run.log 2>&1 &