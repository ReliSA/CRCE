#!/bin/bash
if [ ! -d ./sandbox ]; then
  mkdir ./sandbox
  cp -r ./conf ./sandbox
fi
java -jar pax-runner.jar --ups --workingDirectory=./sandbox scan-dir:required-bundles scan-dir:crce-bundles war:file:crce-wars/crce-webui.war scan-bundle:file:crce-wars/crce-rest-v2.war scan-file:file:platform.properties
