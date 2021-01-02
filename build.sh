#!/bin/bash
if [ "$1" = "all" ]; then
docker volume create shared-modules
docker volume create metadata-modules
docker image build --tag sharedmodules_compiler:1.0 -f ./build/Dockerfile .
docker run --name s_modules_compiler -it --rm -v shared-modules:/shared-modules sharedmodules_compiler:1.0
fi
docker-compose --env-file .env up --build