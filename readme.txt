Component Repository with Compatibility Evaluation

Prerequisities:
 - JDK 1.6
 - Apache Maven (tested on 3.0.3)

1) Copy 'conf.default' folder to 'conf' and edit configuration files,
    - if you don't do that, maven will copy the configuration folder on the first run.

2) Start the application by running start.bat, or
    - to compile by hand:
        > mvn install
    - to run by hand:
        > mvn pax:provision

3) CRCE runs on following URL in default configuration:
http://localhost:8090/crce

4) Apache Felix Web Console is accessible on this URL (login: admin, password: admin):
http://localhost:8090/system/console
