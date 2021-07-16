#!/bin/bash

# Building script for every module
# ================================

CFG=$1
BUILD="clean install -U --no-transfer-progress"
PARAMS="-Dmaven.test.skip=true -Dfindbugs.skip=true -Denforcer.skip=true";
#PARAMS=
#echo "CRCE build type: ${CFG:-(plain)}"
PARAMS="-Dmaven.test.skip=true -Dfindbugs.skip=true -Denforcer.skip=true -Dpmd.skip=true";
if [ "$CFG" == "notest" ]; then
    PARAMS="-Dmaven.test.skip=true -Dfindbugs.skip=true";
elif [ "$CFG" == "onlyclean" ]; then
    BUILD="clean"
elif [ "$CFG" == "fast" ]; then
    BUILD="install"
    PARAMS="-Dmaven.test.skip=true -Dfindbugs.skip=true -Denforcer.skip=true -Dpmd.skip=true";
fi

echo $'\n\n\n'; echo "=============================================================="
echo "Building parent pom in ./parent-aggregation"
echo "#=============================================================="
cd parent-aggregation
mvn $BUILD $PARAMS
retVal=$?
cd ..
if [ $retVal -ne 0 ]; then
    echo "Error";
	exit $retVal;
fi

echo $'\n\n\n'; echo "=============================================================="
echo "Building shared-build-settings in ./build"
echo "#=============================================================="
cd build
mvn $BUILD $PARAMS
retVal=$?
cd ..
if [ $retVal -ne 0 ]; then
    echo "Error";
	exit $retVal;
fi

echo $'\n\n\n'; echo "=============================================================="
echo "Building third party libraries in ./third-party"
echo "#=============================================================="

cd third-party
for d in $(ls) ; do cd $d ; mvn $BUILD $PARAMS; cd .. ; done

retVal=$?;
if [ $retVal -ne 0 ]; then
        echo "Error";
        exit $retVal;
fi

cd ..
echo $'\n\n\n'; echo "=============================================================="
echo "Building modules for crce service"
echo "#=============================================================="
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
echo "#=============================================================="
