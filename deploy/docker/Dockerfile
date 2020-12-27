ARG HOST_PATH_PREFIX

FROM alpine:latest

#OPENJDK11 START
ENV JAVA_HOME="/usr/lib/jvm/default-jvm/"
RUN apk add openjdk11;\
    apk add bash;
ENV PATH=$PATH:${JAVA_HOME}/bin
#OPENJDK11 END

ENV PROJECT_PATH="/home/crce"
ENV PREPARE_BUNDLE_NAME="prepare-bundles.sh"
ENV PREPARE_BUNDLE_PATH="./deploy/${PREPARE_BUNDLE_NAME}"
ENV BUILD_CODE_NAME="build-code.bash"
ENV BUILD_CODE_PATH="./deploy/docker/${BUILD_CODE_NAME}"
ENV MVN_DIR_PATH="/.mvn"
ENV MVN_WRAPPER_PATH="mvnw"
ENV FELIX_PATH="/felix"
ENV FELIX_V_PATH="/felix/felix-framework-6.0.3"

#COPY PROJECT INTO DOCKER
WORKDIR ${PROJECT_PATH}
COPY ${HOST_PATH_PREFIX}/modules ./modules
COPY ${MVN_DIR_PATH} ./.mvn
COPY ${MVN_WRAPPER_PATH} .
COPY ${HOST_PATH_PREFIX}/pom.xml .
COPY ${HOST_PATH_PREFIX}/pom ./pom
COPY ${HOST_PATH_PREFIX}/third-party ./pom
#END

# Alias on mvn wrapper 
RUN echo -e "#!/bin/bash\n bash ${PROJECT_PATH}/mvnw" > /usr/bin/mvn && \
    chmod +x /usr/bin/mvn

# Copy building script
COPY ${BUILD_CODE_PATH} .

# Copy prepare-bundles script
COPY ${PREPARE_BUNDLE_PATH} .

# Run building script
RUN bash ${BUILD_CODE_NAME}

# Run prepare-bundles script
RUN bash ${PREPARE_BUNDLE_NAME}

#FELIX OSGi START
# Prepare environment
RUN mkdir ${FELIX_PATH}
WORKDIR ${FELIX_PATH}

# Download and unpack Felix (6.0.3 is the latest version)
ADD https://archive.apache.org/dist/felix/org.apache.felix.main.distribution-6.0.3.tar.gz ./apache-felix.tar.gz
RUN tar xvfz apache-felix.tar.gz && rm apache-felix.tar.gz

# Remove duplicate gogo runtime
RUN rm ${FELIX_V_PATH}/bundle/org.apache.felix.gogo.runtime*

# Add CRCE modules to Felix autodeploy dir
RUN cp ${PROJECT_PATH}/target/pax-runner-dir/bundles/* ${FELIX_V_PATH}/bundle/

# Create directory for installing new bundles
RUN mkdir ${FELIX_V_PATH}/dist

# Copy configuration for Configuration Admin service
# 'conf' is the directory to watch (configured by 'felix.fileinstall.dir')
#RUN mkdir ${FELIX_V_PATH}/load
ADD ./conf.default/* ${FELIX_V_PATH}/conf/

# Felix framework configuration override
COPY ./felix-configuration/config.properties ${FELIX_V_PATH}/conf/
COPY ./felix-configuration/system.properties ${FELIX_V_PATH}/conf/

# Run Felix (with debug enabled)
#CMD cd ${FELIX_V_PATH} && java -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar ./bin/felix.jar

# Run Felix (with debug disabled)
# CMD cd ${FELIX_V_PATH} && java -jar ./bin/felix.jar

#FELIX OSGi END

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

CMD java -jar ./bin/felix.jar