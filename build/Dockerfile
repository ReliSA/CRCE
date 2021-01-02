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
ENV SHARED_MODULES_ABS_PATH="${PROJECT_PATH}/shared-modules"
ENV SHARED_MODULES_PATH="./shared-modules"
ENV GROUP_ID_PATH="cz/zcu/kiv/crce"
ENV GROUP_ID="cz.zcu.kiv.crce"
ENV VOLUME_BASE_PATH="/shared-modules"
ENV VOLUME_CONTAINING_JARS="${VOLUME_BASE_PATH}/${GROUP_ID_PATH}"
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

RUN echo -e '#!/bin/bash\n'"${PROJECT_PATH}/mvnw"' "$@"' > /usr/bin/mvn && \
    chmod +x /usr/bin/mvn

CMD ["bash", "build_shared_modules.sh"]