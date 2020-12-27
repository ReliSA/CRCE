#!/bin/bash
if [ "$1" = "all" ]; then
docker image build --tag sharedmodules_compiler:1.0 -f ./build/Dockerfile .
docker volume create shared-modules
docker run --name s_modules_compiler -it --rm -v shared-modules:/shared-modules sharedmodules_compiler:1.0
else
docker-compose --env-file .env up
fi