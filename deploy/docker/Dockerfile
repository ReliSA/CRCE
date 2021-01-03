FROM alpine:latest

#OPENJDK11 START
ENV JAVA_HOME="/usr/lib/jvm/default-jvm/"
RUN apk add openjdk11;\
    apk add bash;\
    apk add zip;
ENV PATH=$PATH:${JAVA_HOME}/bin
#OPENJDK11 END

ENV PROJECT_PATH="/home/crce"
ENV BUILD_CODE_NAME="build-code.bash"
ENV BUILD_CODE_PATH="./deploy/docker/${BUILD_CODE_NAME}"
ENV MVN_DIR_PATH="/.mvn"
ENV MVN_WRAPPER_PATH="mvnw"
ENV FELIX_PATH="/felix"
ENV FELIX_V_PATH="${FELIX_PATH}/felix-framework-6.0.3"
ARG SERVICE_PATH_PREFIX

ENV METADATA_VOLUME_PATH="${PROJECT_PATH}/metadata"

#COPY PROJECT INTO DOCKER
WORKDIR ${PROJECT_PATH}
COPY ${MVN_DIR_PATH} ./.mvn
COPY ${MVN_WRAPPER_PATH} .

COPY ${SERVICE_PATH_PREFIX}/build ./build
COPY ${SERVICE_PATH_PREFIX}/modules ./modules_tmp
#COPY ${SERVICE_PATH_PREFIX}/pom.xml .
COPY ${SERVICE_PATH_PREFIX}/parent-aggregation ./parent-aggregation
COPY ${SERVICE_PATH_PREFIX}/third-party ./third-party
#END

# Alias on mvn wrapper 
RUN echo -e '#!/bin/bash\n'"${PROJECT_PATH}/mvnw"' "$@"' > /usr/bin/mvn && \
    chmod +x /usr/bin/mvn

COPY ./deploy ./deploy

# Copy building script
COPY ${BUILD_CODE_PATH} .

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

CMD cp -r modules_tmp/* ./modules ; bash ${BUILD_CODE_NAME}
# ; cd ./deploy ; bash ${PREPARE_BUNDLE_NAME} ; cd ${FELIX_V_PATH} ; java -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar ./bin/felix.jar