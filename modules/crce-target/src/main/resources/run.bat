@ECHO OFF
IF EXIST .\sandbox GOTO START

MKDIR .\sandbox\conf
COPY .\conf .\sandbox\conf

:START
CALL java -jar pax-runner.jar --ups --workingDirectory=.\sandbox scan-dir:required-bundles scan-dir:crce-bundles war:file:crce-wars/crce-webui.war scan-bundle:file:crce-wars/crce-rest-v2.war scan-file:file:platform.properties %1 %2 %3
