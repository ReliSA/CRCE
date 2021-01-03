#!/bin/bash
function build {
   while IFS="" read -r file_line || [ -n "$file_line" ]
   do
      cp -r ${METADATA_VOLUME_PATH}/${file_line} ${PROJECT_PATH}/modules
   done <${CONFIG_METADATA_MODULES}

   bash ${BASE_BUILD_SCRIPT_NAME} $1
}
#chmod u+x wait-for-it.sh 
#./wait-for-it.sh metadata:8081 --timeout=0 -- echo "TEEEEEST"

