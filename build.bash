#!/bin/bash

CFG=$1
BUILD=clean
PARAMS=
echo "CRCE build type: ${CFG:-(plain)}"

if [ "$CFG" == "notest" ]; then
    PARAMS="-Dmaven.test.skip=true -Dfindbugs.skip=true";
elif [ "$CFG" == "fast" ]; then
    BUILD=
    PARAMS="-Dmaven.test.skip=true -Dfindbugs.skip=true";
fi

echo $'\n\n\n'; echo "=============================================================="
echo "Building crce-parent in ./pom"
echo "#==============================================================\n\n\n"
cd pom
mvn $BUILD install $PARAMS
retVal=$?
cd ..
if [ $retVal -ne 0 ]; then
    echo "Error";
	exit $retVal;
fi

#==============================================================

echo $'\n\n\n'; echo "=============================================================="
echo "Building shared-build-settings in ./build"
echo "#==============================================================\n\n\n"
cd build
mvn $BUILD install $PARAMS
retVal=$?
cd ..
if [ $retVal -ne 0 ]; then
    echo "Error";
	exit $retVal;
fi

#==============================================================

echo $'\n\n\n'; echo "=============================================================="
echo "Building third party libraries in ./third-party"
echo "#==============================================================\n\n\n"
cd third-party
for d in * ; do cd $d ; mvn $BUILD install  $PARAMS; cd .. ; done
retVal=$?
cd ..
if [ $retVal -ne 0 ]; then
    echo "Error";
	exit $retVal;
fi

#==============================================================

echo $'\n\n\n'; echo "=============================================================="
echo "Building crce-core-reactor in ./core"
echo "#==============================================================\n\n\n"
cd core
mvn $BUILD install $PARAMS
retVal=$?
cd ..
if [ $retVal -ne 0 ]; then
    echo "Error";
	exit $retVal;
fi

#==============================================================

echo $'\n\n\n'; echo "=============================================================="
echo "Building crce-modules-reactor in ./modules"
echo "#==============================================================\n\n\n"
cd modules
mvn $BUILD install $PARAMS
retVal=$?
cd ..
if [ $retVal -ne 0 ]; then
    echo "Error";
	exit $retVal;
fi

echo $'\n\n\n'; echo "=============================================================="
echo "Done building"
echo "#==============================================================\n\n\n"
