FROM alpine:latest

#OPENJDK11 START
ENV JAVA_HOME="/usr/lib/jvm/default-jvm/"
RUN apk add openjdk11;\
    apk add bash;
ENV PATH=$PATH:${JAVA_HOME}/bin
#OPENJDK11 END

ENV PROJECT_PATH="/home/crce"
ENV MVN_DIR_PATH=".mvn"
ENV MVN_WRAPPER_PATH="mvnw"
ENV SHARED_MODULES_PATH="./shared-modules"
ENV VOLUME_CONTAINING_JARS="/shared-modules"
ENV BUILD_SHARED_MODULES_SCRIPT_NAME="build_shared_modules.sh"
ENV BUILD_SHARED_MODULES_SCRIPT_PATH="./build/${BUILD_SHARED_MODULES_SCRIPT_NAME}"

#COPY PROJECT INTO DOCKER
WORKDIR ${PROJECT_PATH}
COPY ${MVN_DIR_PATH} ./.mvn
COPY ${MVN_WRAPPER_PATH} .
COPY ./build/compiled ./compiled
COPY ./build/wrappers ./wrappers
COPY ./build/pom.xml .
COPY ${SHARED_MODULES_PATH} "./shared-modules"
COPY ${BUILD_SHARED_MODULES_SCRIPT_PATH} "./${BUILD_SHARED_MODULES_SCRIPT_NAME}"
#END

CMD ["bash", "build_shared_modules.sh"]