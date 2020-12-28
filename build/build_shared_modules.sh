./mvnw install -DskipTests 
./mvnw package

rm -fr ${VOLUME_BASE_PATH}/*
mkdir -p ${VOLUME_CONTAINING_JARS}
find . -name \*.jar -exec cp {} ${VOLUME_CONTAINING_JARS} \;

cd ${VOLUME_CONTAINING_JARS};
for filename in ./*.jar; do
    if [ -z $(echo "${filename}"|sed "/crce/d") ]; then
        filename=`echo "${filename/.\//''}"`;
        jar_filename=${filename};
        version=`echo "${filename/\.jar/''}" | sed -e 's/[^0-9]*//' -e 's/.jar//'`;
        module_name=`echo "${filename/-${version}\.jar/''}"`;
        
        new_path=./${module_name}/${version};
        mkdir -p ${new_path};
        mv ${jar_filename} ${new_path};
    fi
done
rm -f ${VOLUME_CONTAINING_JARS}/*.jar