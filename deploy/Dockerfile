# Use java 11 jdk
FROM openjdk:11.0.3-jdk-stretch

# Prepare environment
RUN mkdir /felix
WORKDIR /felix

# Download and unpack Felix (6.0.3 is the latest version)
ADD https://archive.apache.org/dist/felix/org.apache.felix.main.distribution-6.0.3.tar.gz ./apache-felix.tar.gz
RUN tar xvfz apache-felix.tar.gz && rm apache-felix.tar.gz
ENV FELIX_PATH /felix/felix-framework-6.0.3

# Remove duplicate gogo runtime
RUN rm ${FELIX_PATH}/bundle/org.apache.felix.gogo.runtime*

# Add CRCE modules to Felix autodeploy dir
ADD ./target/pax-runner-dir/bundles/* ${FELIX_PATH}/bundle/

# Create directory for installing new bundles
RUN mkdir ${FELIX_PATH}/dist

# Set environmental variable for connection string to mongodb
ENV mongo_connection mongodb://mongoserver:27017

# Run Felix
CMD cd ${FELIX_PATH} && java -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar ./bin/felix.jar
