cmd /c mvn install:install-file -DgroupId=org.apache.felix -DartifactId=org.apache.felix.dependencymanager -Dversion=3.0.0 -Dpackaging=jar -Dfile=runner/bundles/org.apache.felix.dependencymanager_3.0.0.jar
cmd /c mvn install:install-file -DgroupId=org.apache.ace -DartifactId=ace-obr-metadata -Dversion=0.8.0 -Dpackaging=jar -Dfile=runner/bundles/org.apache.ace.obr.metadata_0.8.0.jar
cmd /c mvn install:install-file -DgroupId=org.apache.ace -DartifactId=ace-obr-storage -Dversion=0.8.0 -Dpackaging=jar -Dfile=runner/bundles/org.apache.ace.obr.storage_0.8.0.jar

