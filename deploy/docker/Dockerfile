FROM alpine:latest

#OPENJDK11 START
ENV JAVA_HOME="/usr/lib/jvm/default-jvm/"
RUN apk add openjdk11;\
    apk add bash;\
    apk add zip;\
    apk add tree;
ENV PATH=$PATH:${JAVA_HOME}/bin
#OPENJDK11 END

# Project ENVs
ENV DEPLOY_PATH="./deploy"
ENV PREPARE_BUNDLE_NAME="prepare-bundles.sh"
ENV PREPARE_BUNDLES_PATH="${DEPLOY_PATH}/${PREPARE_BUNDLE_NAME}"
ENV TARGET_PATH="${DEPLOY_PATH}/target"
ENV PROJECT_PATH="/home/crce"
ENV BUILD_CODE_NAME="build-code.bash"
ENV BUILD_CODE_PATH="./deploy/docker/${BUILD_CODE_NAME}"
ENV MVN_DIR_PATH="/.mvn"
ENV MVN_WRAPPER_PATH="mvnw"

# Felix ENVs
ENV FELIX_PATH="/felix"
ENV FELIX_V_PATH="${FELIX_PATH}/felix-framework-6.0.3"

ENV METADATA_VOLUME_PATH="${PROJECT_PATH}/metadata"

WORKDIR ${PROJECT_PATH}

ARG SERVICE_PATH_PREFIX

COPY ${DEPLOY_PATH} ./deploy
COPY ${MVN_DIR_PATH} ./.mvn
COPY ${MVN_WRAPPER_PATH} .
COPY ${SERVICE_PATH_PREFIX}/build ./build
COPY ${SERVICE_PATH_PREFIX}/modules ./modules_tmp
COPY ${SERVICE_PATH_PREFIX}/parent-aggregation ./parent-aggregation
COPY ${SERVICE_PATH_PREFIX}/third-party ./third-party

# Executable prepare-bundles.sh
RUN chmod u+x ${PREPARE_BUNDLES_PATH}

# Alias on mvn wrapper 
RUN echo -e '#!/bin/bash\n'"${PROJECT_PATH}/mvnw"' "$@"' > /usr/bin/mvn && \
    chmod +x /usr/bin/mvn

# Copy building script
COPY ${BUILD_CODE_PATH} ${BUILD_CODE_NAME}
RUN chmod u+x ${BUILD_CODE_NAME}

#FELIX OSGi START
# Prepare environment
RUN mkdir -p ${FELIX_V_PATH}
WORKDIR ${FELIX_PATH}

# Download and unpack Felix (6.0.3 is the latest version)
ADD https://archive.apache.org/dist/felix/org.apache.felix.main.distribution-6.0.3.tar.gz ./apache-felix.tar.gz
RUN tar xvfz apache-felix.tar.gz && rm apache-felix.tar.gz

# Remove duplicate gogo runtime
RUN rm ${FELIX_V_PATH}/bundle/org.apache.felix.gogo.runtime*

# Create directory for installing new bundles
RUN mkdir ${FELIX_V_PATH}/dist


# Copy configuration for Configuration Admin service
# 'conf' is the directory to watch (configured by 'felix.fileinstall.dir')
#RUN mkdir ${FELIX_V_PATH}/load
ADD ./deploy/conf.default/* ${FELIX_V_PATH}/conf/

# Felix framework configuration override
COPY ./deploy/felix-configuration/config.properties ${FELIX_V_PATH}/conf/
COPY ./deploy/felix-configuration/system.properties ${FELIX_V_PATH}/conf/

# Run Felix (with debug enabled)
#cd ${FELIX_V_PATH} && java -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar ./bin/felix.jar

# Run Felix (with debug disabled)
#cd ${FELIX_V_PATH} && java -jar ./bin/felix.jar

#FELIX OSGi END

#RUN addgroup -S spring && adduser -S spring -G spring
#USER spring:spring
WORKDIR ${PROJECT_PATH}

CMD rm -rf ./modules/* ; cp -r modules_tmp/* ./modules ; bash ${BUILD_CODE_NAME} ; cd deploy/ ; mvn clean pax:directory --no-transfer-progress ; bash ${PREPARE_BUNDLE_NAME}