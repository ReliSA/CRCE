# Use java 7 jdk which is compatible with CRCE
FROM java:7-jdk

# Prepare environment
RUN mkdir /felix
WORKDIR /felix

# Download and upack Felix
# 4.6.1 is compatible with Java 1.7
ADD https://archive.apache.org/dist/felix/org.apache.felix.main.distribution-5.0.0.tar.gz ./apache-felix.tar.gz
RUN tar xvfz apache-felix.tar.gz && rm apache-felix.tar.gz
ENV FELIX_PATH /felix/felix-framework-5.0.0

# Remove duplicate gogo runtime
RUN rm ${FELIX_PATH}/bundle/org.apache.felix.gogo.runtime-0.16.2.jar

# Add CRCE modules to Felix autodeploy dir
ADD ./runner/bundles/* ${FELIX_PATH}/bundle/

# Create directory for installing new bundles
RUN mkdir ${FELIX_PATH}/dist

# Set environmental variable for connection string to mongodb
ENV mongo_connection mongodb://mongoserver:27017

# Run Felix
CMD cd ${FELIX_PATH} && java -jar ./bin/felix.jar
