#!/bin/bash
if [ "$1" = "all" ]; then
    docker volume create shared-modules
    docker volume create metadata-modules
    docker volume create metadata-felix

    docker image build --tag sharedmodules_compiler:1.0 -f ./build/Dockerfile .
    docker image build --tag metadatamodules_compiler:1.0 --build-arg SERVICE_PATH_PREFIX="./services/metadata" -f ./deploy/docker/Dockerfile .

    docker run --name s_modules_compiler --rm -v shared-modules:/shared-modules sharedmodules_compiler:1.0
    docker run --name s_metadata_compiler --rm -v metadata-modules:/home/crce/modules \
                                            -v shared-modules:/home/crce/shared-modules:ro \
                                            -v metadata-felix:/felix \
                                            metadatamodules_compiler:1.0;
else
    docker image build --tag metadatamodules_compiler:1.0 --build-arg SERVICE_PATH_PREFIX="./services/metadata" -f ./deploy/docker/Dockerfile .
    docker run --name s_metadata_compiler --rm -v metadata-modules:/home/crce/modules \
                                            -v shared-modules:/home/crce/shared-modules:ro \
                                            -v metadata-felix:/felix \
                                            metadatamodules_compiler:1.0;
fi
docker-compose --env-file .env up --build