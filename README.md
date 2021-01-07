# CRCE - Component Repository supporting Compatibility Evaluation

CRCE is an experimental repository, designed to support research into component-based and modular systems undertaken by ReliSA research group at the Faculty of Applied Sciences, University of West Bohemia (http://relisa.kiv.zcu.cz).  The project website is over at Assembla: https://www.assembla.com/spaces/crce/wiki .

## Prerequisities

- **Docker compose**, tested on v1.27.4
- **Docker**, tested on v20.10.1

## Build & Start up

You need to build everything (shared-modules, metadata-modules) if you **building CRCE** for the first time.


```zsh
user@localhost:<projects_root>$ ./build.sh all
```

You can execute build without param **all** if build.sh was executed before.

```zsh
user@localhost:<projects_root>$ ./build.sh
```

Now all containers (crce_mongodb, crce_metadata, crce_rest-api, crce_external-extensions, crce_ws-indexers, crce_internal-extensions). To show all **running containers** execute this:

```zsh
user@localhost:~$ docker ps
```

You should see something like this:

```zsh
CONTAINER ID   IMAGE                     COMMAND                  CREATED          STATUS         PORTS                                  NAMES
d7c0d0fcda41   crce_externalextensions   "/bin/sh -c 'rm -rf …"   18 minutes ago   Up 6 minutes   0.0.0.0:8080->8080/tcp                 crce_external-extensions
1e8ba316da50   crce_restapi              "/bin/sh -c 'rm -rf …"   18 minutes ago   Up 6 minutes   0.0.0.0:8082->8080/tcp                 crce_rest-api
06cd03b77bd5   mongo                     "docker-entrypoint.s…"   41 minutes ago   Up 6 minutes   0.0.0.0:27017-27019->27017-27019/tcp   crce_mongodb

```

If you want to skip the building of the metadata service and shared modules you can execute this:

```zsh
user@localhost:<projects_root>$ ./start.sh
```

Each script **build.sh** and **start.sh** have options to build services in **detached** mode see this:

```zsh
user@localhost:<projects_root>$ ./build.sh all -d

user@localhost:<projects_root>$ ./build.sh -d

user@localhost:<projects_root>$ ./start.sh -d
```
Be aware that detached mode does not provide building or any other **informations** while starting up containers.

If you want to shutdown your services execute this:

```zsh
user@localhost:<projects_root>$ docker-compose down
```

## Services

Each running service can be **entered** with this command:

```zsh
user@localhost:<projects_root>$ docker-compose exec <name-of-the-service-from-docker-compose.yml> <command (bash)>
```
## Volumes

Services use **docker volumes**. Volumes hold shared source code (metadata-modules, shared-modules) and also the cache directory `.m2` which is used for maven caching.

To list docker volumes just execute this:

```zsh
user@localhost:<projects_root>$ docker volume list
```

Docker volumes have data stored in a host system. The specific **directory** can be seen with this command:

```zsh
user@localhost:<projects_root>$ docker inspect <volume-name>
```

Now you can **enter volume** directly from your fs (usually you need to enter super user mode with `sudo su`)

### Metadata modules

This volume holds all source code of last metadata build.

```
crce-api-compatibility-checker
crce-compatibility-api
...
pom.xml
shared-third-party-dependencies
```

### Shared modules

This volume holds all source code of last shared modules build.

```
crce-metadata-api
crce-metadata-impl
crce-metadata-json-api
crce-metadata-json-impl
crce-plugin-api
```

### Metadata Apache Felix

Cached JARs in `/felix/<felix-version>` directory of metadata container (after `maven clean pax:directory` and `prepare-bundles.sh`).

### Maven cache

Shared maven cache `.m2` for all services in this project.

## Build flow

1. Creating volumes
2. Creating containers for building metadata and shared modules
3. Building metadata and shared volumes
   1. Prefixes for path to each service src is specified inside `docker-compose.yml`
   2. Inside **Dockerfile** for each kind of service - *metadata* and *not-metadata* (`<project_root>/deploy/docker/Dockerfile` and `<project_root>/deploy/docker/not-metadata-service/Dockerfile`) copying, preparing fs structure and installing all needed libraries (Apache Felix etc.) is performed. For more detail information check Dockerfiles directly.
4. Starting up all services from `docker-compose.yml`
   1. Build all modules like `aggregation`, `crce-...`
   2. Perform `mvn clean pax:directory` inside copied `deploy` directory
   3. Run `prepare-bundles.sh` which collects all needed information for each JAR and copy them into `target/pax-runner` directory inside container
   4. Run `felix.jar` **inside the Apache Felix directory**  

<hr>

### lpsolve installation

To solve the issue with mathematical solver, you need to install [lpsolve library](https://sourceforge.net/projects/lpsolve/) to your computer. To do that, follow [their guide](http://lpsolve.sourceforge.net/5.5/Java/README.html#install) step by step.

> Note that on Windows, you do not have to place the libs to `\WINDOWS` or `\WINDOWS\SYSTEM32` as the guide states. Put it wherever you wish and add the directory to your `Path`.

## Debugging

Remote debugging is possible when running CRCE in Docker. To enable remote debug, application needs to be started with following flags:

```bash
-Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
```

And expose the port 5005 by adding `-p 5005:5005` to the `docker run` command.

Check the `deploy/Dockerfile` to see the difference between running the app in normal and debug mode.

## Code updates

After modifying a part of code, only the parental module needs to be rebuilt (no need to rebuild all). After that, the pax process must be restarted.

## Configuration

Configuration is done via OSGI service called [Configuration Admin](https://osgi.org/specification/osgi.cmpn/7.0.0/service.cm.html). 
Details on how it's implemented in Felix can be found in  [Apache Felix Configuration Admin Service](https://felix.apache.org/documentation/subprojects/apache-felix-config-admin.html).

By default, Felix will look into the `config` directory for possible configuration (example of such directory can be found in `deploy/conf.default`)


## Issues

### Required runtime environment capability in some bundles

Some bundles (mostly those from the third parties) either require a particular version of OSGi runtime environment or require the capability `osgi.ee` to be set to a specific value. This may or may not work with newer version of Java and to avoid runtime problems, these requirements need to be removed.

To remove them, it is necessary to edit manifest (`META-INF/MANIFEST.MF`) of such bundle and remove lines containing:
`Require-Capability: osgi.ee;` or `Bundle-RequiredExecutionEnvironment: J2SE-`. This is done in the `deploy/prepare-bundles.sh` script.

An ideal solution would be to either found newer version of such bundles or replace them with different bundles with same functionality. This however may introduce compatibility issues (as some other bundles may depend on the current versions) or may not even be possible (replacement bundle does not exist). 
