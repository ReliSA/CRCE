CFG=$1
BUILD="clean install -U"
PARAMS=
echo "CRCE build type: ${CFG:-(plain)}"
ls *;
ls ./shared-modules;
if [ "$CFG" == "notest" ]; then
    PARAMS="-Dmaven.test.skip=true -Dfindbugs.skip=true";
elif [ "$CFG" == "onlyclean" ]; then
    BUILD="clean"
elif [ "$CFG" == "fast" ]; then
    BUILD="install"
    PARAMS="-Dmaven.test.skip=true -Dfindbugs.skip=true -Denforcer.skip=true";
fi

echo $'\n\n\n'; echo "=============================================================="
echo "Building parent pom in ./parent-aggregation"
echo "#==============================================================\n\n\n"
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
echo "#==============================================================\n\n\n"
cd build
mvn $BUILD $PARAMS
retVal=$?
cd ..
if [ $retVal -ne 0 ]; then
    echo "Error";
	exit $retVal;
fi

echo $'\n\n\n'; echo "=============================================================="
echo "Building modules for crce service"
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
echo $'\n\n\n'; echo "=============================================================="
echo "Done building"
echo "#==============================================================\n\n\n"
