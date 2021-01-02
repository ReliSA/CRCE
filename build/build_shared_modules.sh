rm -fr ${VOLUME_BASE_PATH}/*
mkdir -p ${VOLUME_CONTAINING_JARS}

cd ${SHARED_MODULES_ABS_PATH}

mvn clean install -U -DskipTests 
mvn package

find . -name \*.jar -exec cp {} ${VOLUME_CONTAINING_JARS} \;

cd ${VOLUME_CONTAINING_JARS};
echo "----------------------------------------------------- NAD LS -----------------------------------------------"
ls
echo "----------------------------------------------------- POD LS -----------------------------------------------"
for filename in ./*.jar; do
    if [ -z $(echo "${filename}"|sed "/crce/d") ]; then
        filename=`echo "${filename/.\//''}"`;
        jar_filename=${filename};
        version=`echo "${filename/\.jar/''}" | sed -e 's/[^0-9]*//' -e 's/.jar//'`;
        module_name=`echo "${filename/-${version}\.jar/''}"`;
        
        new_path=./${module_name}/${version};
        mkdir -p ${new_path};
        mv ${jar_filename} ${new_path};

        echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
            <project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">
                <modelVersion>4.0.0</modelVersion>
                <groupId>${GROUP_ID}</groupId>
                <artifactId>${module_name}</artifactId>
                <version>${version}</version>
            </project>" > ${new_path}/${module_name}-${version}.pom;
    fi
done
rm -f ${VOLUME_CONTAINING_JARS}/*.jar