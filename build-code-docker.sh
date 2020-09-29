#!/bin/bash
docker image build --tag builder:1.0 ./build
docker run -it --rm --name crce-project -v "$(pwd)":/usr/src/crce -w /usr/src/crce builder:1.0 bash build-code.bash $1
docker run -it --rm --name crce-project -v "$(pwd)":/usr/src/crce -w /usr/src/crce/deploy builder:1.0 mvn clean pax:directory
docker run -it --rm --name crce-project -v "$(pwd)":/usr/src/crce -w /usr/src/crce/deploy builder:1.0 bash prepare-bundles.sh 
docker-compose up --build