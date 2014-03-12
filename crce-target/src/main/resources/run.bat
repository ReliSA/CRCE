@ECHO OFF
IF EXIST .\sandbox GOTO START

MKDIR .\sandbox\conf
COPY .\conf .\sandbox\conf

:START
CALL java -jar pax-runner.jar --ups --workingDirectory=.\sandbox scan-dir:required-bundles scan-dir:crce-bundles scan-file:file:platform.properties %1 %2 %3
