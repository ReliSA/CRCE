cmd /c mvn install:install-file -DgroupId=org.apache.felix -DartifactId=org.apache.felix.dependencymanager -Dversion=3.0.0-SNAPSHOT -Dpackaging=jar -Dfile=external-snapshots/org.apache.felix.dependencymanager_3.0.0.SNAPSHOT.jar
cmd /c mvn install:install-file -DgroupId=org.apache.felix -DartifactId=org.apache.felix.dependencymanager.shell -Dversion=3.0.0-SNAPSHOT -Dpackaging=jar -Dfile=external-snapshots/org.apache.felix.dm.shell_3.0.0.SNAPSHOT.jar
cmd /c mvn install:install-file -DgroupId=org.apache.ace -DartifactId=ace-obr-metadata -Dversion=0.8.0-SNAPSHOT -Dpackaging=jar -Dfile=external-snapshots/org.apache.ace.obr.metadata_0.8.0.SNAPSHOT.jar
cmd /c mvn install:install-file -DgroupId=org.apache.ace -DartifactId=ace-obr-storage -Dversion=0.8.0-SNAPSHOT -Dpackaging=jar -Dfile=external-snapshots/org.apache.ace.obr.storage_0.8.0.SNAPSHOT.jar
cmd /c mvn install:install-file -DgroupId=org.apache.ace -DartifactId=ace-obr-servlet -Dversion=0.8.0-SNAPSHOT -Dpackaging=jar -Dfile=external-snapshots/org.apache.ace.obr.servlet_0.8.0.SNAPSHOT.jar
cmd /c mvn install:install-file -DgroupId=org.apache.ace -DartifactId=ace-httplistener -Dversion=0.8.0-SNAPSHOT -Dpackaging=jar -Dfile=external-snapshots/org.apache.ace.http.listener_0.8.0.SNAPSHOT.jar

