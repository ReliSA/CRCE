# CRCE - Component Repository supporting Compatibility Evaluation

CRCE is an experimental repository, designed to support research into component-based and modular systems undertaken by ReliSA research group at the Faculty of Applied Sciences, University of West Bohemia (http://relisa.kiv.zcu.cz).  The project website is over at Assembla: https://www.assembla.com/spaces/crce/wiki .

## Prerequisities

- **JDK 7** set in `JAVA_HOME` environment variable, tested on 1.7.0_80 (there is a problem with web UI using JDK 8, need to update dependencies)
- **MongoDB**, tested on v3.4.10
- **Maven 3**, tested on 3.5.2

## Build

1. `crce-parent` in `/pom` directory
2. `shared-build-settings` in `/build`
3. everything in `/third-party`
4. `crce-core-reactor` in `/core`
5. `crce-modules-reactor` in `/modules`

## Start up

Run CRCE using Maven plugin for pax in crce-modules-reactor module:

`mvn pax:provision`

The output log should write up some info about dependencies terminated by lines similar to the following:

```
Listening for transport dt_socket at address: 65505
____________________________
Welcome to Apache Felix Gogo

g! X 10, 2017 10:38:47 DOP. org.glassfish.jersey.server.ApplicationHandler initialize
INFO: Initiating Jersey application, version Jersey: 2.9.1 2014-06-01 23:30:50...
```

At the moment, a bunch of errors will probably come up:

```
[Fatal Error] :1:1: Content is not allowed in prolog.
```

The cause of the latter is a badly loaded binary of mathematical solver which does not affect common application run. Any other error/exception (typically OSGi complaining about a thing) is a problem that needs to be examined as such. However, it should not happen with this version.

Started up, the application is accessible at:

- web UI: http://localhost:8080/crce
- REST web services: http://localhost:8080/rest/v2/

Updated (more or less) REST WS documentation is available at [Apiary](https://crceapi.docs.apiary.io/).

## Code update

After modifying a part of code, only the parental module needs to be rebuilt (no need to rebuild all). After that, the pax process must be restarted.
