#!/bin/bash
if [ ! -d ./sandbox ]; then
  mkdir ./sandbox
  cp -r ./conf ./sandbox
fi
java -jar pax-runner.jar --ups --workingDirectory=./sandbox scan-dir:required-bundles scan-dir:crce-bundles scan-file:file:platform.properties
