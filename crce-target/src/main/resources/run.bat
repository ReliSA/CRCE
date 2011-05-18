@echo off
call java -jar pax-runner.jar --ups --workingDirectory=. scan-dir:required-bundles scan-dir:crce-bundles scan-file:file:platform.properties %1 %2 %3