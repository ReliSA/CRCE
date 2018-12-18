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

# Add CRCE modules to Felix autodeploy dir
ADD ./runner/bundles/* ${FELIX_PATH}/bundle/

# Run Felix
CMD cd ${FELIX_PATH} && java -Dgosh.args=--noi -jar ./bin/felix.jar
