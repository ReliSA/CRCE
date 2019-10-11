#!/bin/bash

#
# Simple CRCE build script. 
#
# HOWTO: call with one parameter, see the if-switch below for values
# plus default values as set here.
#

CFG=$1
BUILD="clean install"
PARAMS=
echo "CRCE build type: ${CFG:-(plain)}"

if [ "$CFG" == "notest" ]; then
    PARAMS="-Dmaven.test.skip=true -Dfindbugs.skip=true";
elif [ "$CFG" == "onlyclean" ]; then
    BUILD="clean"
elif [ "$CFG" == "fast" ]; then
    BUILD="install"
    PARAMS="-Dmaven.test.skip=true -Dfindbugs.skip=true";
fi


# Start the machine ...


echo $'\n\n\n'; echo "=============================================================="
echo "Building crce-parent in ./pom"
echo "#==============================================================\n\n\n"
cd pom
mvn $BUILD $PARAMS
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
mvn $BUILD $PARAMS
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
for d in * ; do cd $d ; mvn $BUILD $PARAMS; cd .. ; done
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
mvn $BUILD $PARAMS
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
mvn $BUILD $PARAMS
retVal=$?
cd ..
if [ $retVal -ne 0 ]; then
    echo "Error";
	exit $retVal;
fi

echo $'\n\n\n'; echo "=============================================================="
echo "Done building"
echo "#==============================================================\n\n\n"
