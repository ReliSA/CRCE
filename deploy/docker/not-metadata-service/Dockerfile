FROM alpine:latest

#OPENJDK11 START
ENV JAVA_HOME="/usr/lib/jvm/default-jvm/"
RUN apk add openjdk11;\
    apk add bash;\
    apk add zip;
ENV PATH=$PATH:${JAVA_HOME}/bin
#OPENJDK11 END

ENV PROJECT_PATH="/home/crce"
ENV PREPARE_BUNDLE_NAME="prepare-bundles.sh"
ENV PREPARE_BUNDLE_PATH="./deploy/${PREPARE_BUNDLE_NAME}"
ENV BUILD_CODE_NAME="build-code.bash"
ENV DEPLOY_POM_PATH="./deploy/pom.xml"
ENV PROVISION_POM_PATH="./deploy/provision"
ENV MVN_DIR_PATH="/.mvn"
ENV MVN_WRAPPER_PATH="mvnw"
ENV FELIX_PATH="/felix"
ENV FELIX_V_PATH="/felix/felix-framework-6.0.3"
ENV TARGET_DIR="target/pax-runner-dir/bundles"
ENV FELIX_EXEC_ENV_PROFILE_PATH="./deploy/J2SE-11.Profile"
ENV BUNDLE_TMP_PATH="${TARGET_DIR}/bundle-tmp"
ARG SERVICE_PATH_PREFIX

#NEW ENVs

ENV METADATA_VOLUME_PATH="${PROJECT_PATH}/metadata"
ENV BASE_BUILD_SCRIPT_NAME="base_build-code.sh"
ENV BASE_BUILD_CODE_PATH="./deploy/docker/${BUILD_CODE_NAME}"
ENV BUILD_CODE_PATH="./deploy/docker/not-metadata-service/${BUILD_CODE_NAME}"
ENV CONFIG_METADATA_MODULES="metadata-modules.txt"

#


#COPY PROJECT INTO DOCKER
WORKDIR ${PROJECT_PATH}


COPY ${MVN_DIR_PATH} ./.mvn
COPY ${MVN_WRAPPER_PATH} .

RUN echo ${SERVICE_PATH_PREFIX}

COPY ${SERVICE_PATH_PREFIX}/build ./build
COPY ${SERVICE_PATH_PREFIX}/modules ./modules
#COPY ${SERVICE_PATH_PREFIX}/pom.xml .
COPY ${SERVICE_PATH_PREFIX}/parent-aggregation ./parent-aggregation
COPY ${SERVICE_PATH_PREFIX}/third-party ./third-party
COPY ${DEPLOY_POM_PATH} .
COPY ${PROVISION_POM_PATH} ./provision
#END

# Alias on mvn wrapper 
RUN echo -e '#!/bin/bash\n'"${PROJECT_PATH}/mvnw"' "$@"' > /usr/bin/mvn && \
    chmod +x /usr/bin/mvn

RUN mkdir -p ${TARGET_DIR};

# NEW Copy base building script
COPY ${BASE_BUILD_CODE_PATH} ${BASE_BUILD_SCRIPT_NAME}

# NEW Copy building script
COPY ${BUILD_CODE_PATH} ${BUILD_CODE_NAME}

# NEW Copy configuration (list of metadata modules)
COPY ${SERVICE_PATH_PREFIX}/${CONFIG_METADATA_MODULES} .

# Copy prepare-bundles script
COPY ${PREPARE_BUNDLE_PATH} .

COPY ${FELIX_EXEC_ENV_PROFILE_PATH} .

#FELIX OSGi START
# Prepare environment
RUN mkdir -p ${FELIX_V_PATH}
WORKDIR ${FELIX_PATH}

# Download and unpack Felix (6.0.3 is the latest version)
ADD https://archive.apache.org/dist/felix/org.apache.felix.main.distribution-6.0.3.tar.gz ./apache-felix.tar.gz
RUN tar xvfz apache-felix.tar.gz && rm apache-felix.tar.gz

# Remove duplicate gogo runtime
RUN rm ${FELIX_V_PATH}/bundle/org.apache.felix.gogo.runtime*

# Add CRCE modules to Felix autodeploy dir
#RUN cp ${PROJECT_PATH}/target/pax-runner-dir/bundles/* ${FELIX_V_PATH}/bundle/

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
#CMD cd ${FELIX_V_PATH} && java -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar ./bin/felix.jar

# Run Felix (with debug disabled)
# CMD cd ${FELIX_V_PATH} && java -jar ./bin/felix.jar

#FELIX OSGi END

#RUN addgroup -S spring && adduser -S spring -G spring
#USER spring:spring
WORKDIR ${PROJECT_PATH}

#CMD bash ${BUILD_CODE_NAME} ; bash ${PREPARE_BUNDLE_NAME} ; cd ${FELIX_V_PATH} ; ls ./bundle ; echo "BIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIN" ; ls ;ls ./bin ; java -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar ./bin/felix.jar
CMD bash ${BUILD_CODE_NAME} ; mvn clean pax:directory ; bash ${PREPARE_BUNDLE_NAME} ; cd ${FELIX_V_PATH} ;  echo $'BIIIIIIIIIIIIIIIII\nIIIIIIIIIIIIIII\nIN' ; ls ./bundle ; java -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar ./bin/felix.jar