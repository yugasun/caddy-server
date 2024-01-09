#!/bin/bash

# please copy ./config/application.yml to ./config/application.local.yml
# and modify the config file
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.config.location=./config/application.local.yml"