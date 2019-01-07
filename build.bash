#!/bin/bash

echo "Building crce-parent in ./pom"
cd pom
mvn clean install
retVal=$?
cd ..
if [ $retVal -ne 0 ]; then
    echo "Error";
	exit $retVal;
fi

#==============================================================

echo "Building shared-build-settings in ./build"
cd build
mvn clean install
retVal=$?
cd ..
if [ $retVal -ne 0 ]; then
    echo "Error";
	exit $retVal;
fi

#==============================================================

echo "Building third party libraries in ./third-party"
cd third-party
for d in * ; do cd $d ; mvn clean install ; cd .. ; done
retVal=$?
cd ..
if [ $retVal -ne 0 ]; then
    echo "Error";
	exit $retVal;
fi

#==============================================================

echo "Building crce-core-reactor in ./core"
cd core
mvn clean install
retVal=$?
cd ..
if [ $retVal -ne 0 ]; then
    echo "Error";
	exit $retVal;
fi

#==============================================================

echo "Building crce-modules-reactor in ./modules"
cd modules
mvn clean install
retVal=$?
cd ..
if [ $retVal -ne 0 ]; then
    echo "Error";
	exit $retVal;
fi

echo "Done building"
