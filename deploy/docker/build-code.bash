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
    PARAMS="-Dmaven.test.skip=true -Dfindbugs.skip=true -Denforcer.skip=true";
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

pom_exists=0;
cd third-party
for filename in ./*.xml; do
    if [ -z $(echo "${filename}"|sed "/pom.xml/d") ]; then
        pom_exists=1;
    fi
done
if [ $pom_exists -eq 1 ]; then
    for d in * ; do cd $d ; mvn $BUILD $PARAMS; cd .. ; done
    retVal=$?;
    if [ $retVal -ne 0 ]; then
        echo "Error";
	    exit $retVal;
    fi
else
    echo $'\n';
    echo '[INFO] No third party modules available';
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