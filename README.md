# CRCE - Component Repository supporting Compatibility Evaluation

CRCE is an experimental repository, designed to support research into component-based and modular systems undertaken by ReliSA research group at the Faculty of Applied Sciences, University of West Bohemia (http://relisa.kiv.zcu.cz).  The older project website is over at Assembla: https://www.assembla.com/spaces/crce/wiki .

To cite CRCE as a research result, please use the following citation: 

> P.Brada and K.Jezek, “Repository and Meta-Data Design for Efficient Component Consistency Verification,” Science of Computer Programming, vol. 97,  part  3,  pp.  349–365,  2015. Available: http://www.sciencedirect.com/science/article/pii/S0167642314002925

## Prerequisities

- **JDK 8** set in `JAVA_HOME` environment variable before starting CRCE, tested on 1.8.0_181
- **MongoDB**, tested on v2.6.10, v3.4.10
- **Maven 3**, tested on 3.5.2

On linux, switching to JDK 7 for development/build can be done via `update-java-alternatives` (or, less ideal as it does not set all aspects of the environment, `sudo update-alternatives --config java`).

## Build

1. `crce-parent` in `/pom` directory
2. `shared-build-settings` in `/build`
3. everything in `/third-party` (bash: `.../third-party$ for d in * ; do cd $d; mvn clean install; cd .. ; done`)
4. `crce-core-reactor` in `/core`
5. `crce-modules-reactor` in `/modules`

On linux, step 3. can be perfomed via `.../third-party$ for d in * ; do cd $d ; mvn clean install ; cd .. ; done`.  In case of maven error "Received fatal alert: protocol_version", use `mvn -Dhttps.protocols=TLSv1.2 ...` after https://stackoverflow.com/a/50924208/261891.

### Build docker image

1. Build `crce-modules-reactor` in `/deploy` by running `mvn clean install`
1. Build the project (as described previously)
2. Run `mvn pax:directory` in `/deploy` dir to collect all bundles into the `/target/pax-runner-dir/bundles/` 
3. Now you can build docker with `docker build . -r <image-tag>`

## Start up

For run on local machine run command in `/deploy`:

Run CRCE using Maven plugin for pax in `crce-modules-reactor` module (i.e. `/deploy` directory):

```mvn pax:provision```

### Running docker

Assumig the image is build, crce can be run in docker by this command:

```
docker run -it \
        -p 8080:8080 \
        --add-host mongoserver:172.17.0.1 \
        -v /felix/deploy:/felix/deploy \
        <image-tag>
```

The `-add-host ...` and `-v ...` parameters allow docker to connect to mongoDb running locally and to install new bundles 
(from provided directory). These parameters aren't necessary to run CRCE.


In both cases the output log should write up some info about dependencies terminated by lines similar to the following:

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

### lpsolve installation

To solve the issue with mathematical solver, you need to install [lpsolve library](https://sourceforge.net/projects/lpsolve/) to your computer. To do that, follow [their guide](http://lpsolve.sourceforge.net/5.5/Java/README.html#install) step by step.

> Note that on Windows, you do not have to place the libs to `\WINDOWS` or `\WINDOWS\SYSTEM32` as the guide states. Put it wherever you wish and add the directory to your `Path`.

## Code updates

After modifying a part of code, only the parental module needs to be rebuilt (no need to rebuild all). After that, the pax process must be restarted.
