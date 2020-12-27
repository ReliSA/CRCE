./mvnw install -DskipTests 
./mvnw package
find . -name \*.jar -exec cp {} ${VOLUME_CONTAINING_JARS} \;