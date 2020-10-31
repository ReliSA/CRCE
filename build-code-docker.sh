#!/bin/bash
docker image build --tag builder:1.0 ./build
docker run -it --rm --name crce-project -v "$(pwd)":/usr/src/crce -w /usr/src/crce builder:1.0 bash build-code.bash $1 && cd ./deploy && mvn clean pax:directory && ./prepare-bundles.sh
docker-compose up --build
