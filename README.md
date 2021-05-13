# CRCE - Component Repository supporting Compatibility Evaluation

CRCE is an experimental repository, designed to support research into component-based and modular systems undertaken by ReliSA research group at the Faculty of Applied Sciences, University of West Bohemia (http://relisa.kiv.zcu.cz).  The project website is over at Assembla: https://www.assembla.com/spaces/crce/wiki .

## Prerequisities

- **JDK 11** set in `JAVA_HOME` environment variable before starting CRCE, tested on OpenJDK 11.0.4
- **MongoDB**, tested on v2.6.10, v3.4.10
- **Maven 3**, tested on 3.5.2

On linux, switching to JDK 11 for development/build can be done via `sudo update-alternatives --config java`.
## New fast way of building entire project!
With usage of docker and maven, you can run just this script in the `root` of proejct.
```bash
./build-code-docker.sh fast
```

## Build

Build process consists of two parts. Compiling and building the code itself and building the docker image.

### Build CRCE

This process can be done automatically by running the `build-code.bash` script placed in the project root directory.

1. `crce-parent` in `/pom` directory
2. `shared-build-settings` in `/build`
3. everything in `/third-party` (bash: `.../third-party$ for d in * ; do cd $d; mvn clean install; cd .. ; done`)
4. `crce-core-reactor` in `/core`
5. `crce-modules-reactor` in `/modules`
6. `provision-reactor` in `/deploy`

On linux, step 3. can be perfomed via `.../third-party$ for d in * ; do cd $d ; mvn clean install ; cd .. ; done`.  In case of maven error "Received fatal alert: protocol_version", use `mvn -Dhttps.protocols=TLSv1.2 ...` after https://stackoverflow.com/a/50924208/261891.


### Build docker image

Docker image is placed in directory `/deploy` but before it can be used, bundles must be collected. 
Bundles can be collected by running the following commands in `/deploy` directory:

```bash
mvn clean pax:directory
./prepare-bundles.sh

```

The `prepare-bundles.sh` script is needed due to the issues further described in the Issues section.

Before executing build in `/deploy` be sure to make docker build of `builder`
```bash
docker image build --tag builder:1.0 ./build 
```
To finally build the image itself, execute the following commnad in `/deploy` directory:

```bash
docker build . -t ${image-tag}
```

## Start up

Assuimg the dokcer image is already build, run it by following command:

```bash
docker run -it \
        -p 8080:8080 \
        --add-host mongoserver:172.17.0.1 \
        -v /felix/deploy:/felix/deploy \
        ${image-tag}
```

The `-p` parameter maps the port of docker virual machine to the real port on the computer. 
The `--add-host` allows docker to connect to the Mongo database running locally (Docker *usually* uses 172.17.0.1 ip to access localhost from the container). The app expects the database to be listening on port 27017 by default.
The `-v` parameter maps directory in container to the real directory in the host machine so that it can be used to install new bundles via CLI. 


If everything works, the output log should write up some info about dependencies terminated by lines similar to the following:

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

- web UI: http://localhost:8080/crce-webui
- REST web services: http://localhost:8080/rest/v2/

Updated (more or less) REST WS documentation is available at [Apiary](https://crceapi.docs.apiary.io/).

### MongoDB

Mongo DB can either be run locally or in docker using following command:

```bash
sudo docker run -d -p 27017:27017 -v ~/data:/data/db mongo
```

Where `~/data` is directory on host machine to be used as a storage by Mongo.

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
