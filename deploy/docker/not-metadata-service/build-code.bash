#!/bin/bash

# Building script for non-core modules (modules other than metadata)
# ==================================================================
# Location: inside each non-core module docker container (see <project_root>/deploy/docker/not-metadata-service/Dockerfile)
# Location of metadata-modules.txt: inside every non-core service e.g. <project_root>/services/external-extensions/metadata-modules.txt
#
# 1. read config file metadata-modules.txt line by line
# 2. for each line copy module based on the line of the config file 
# 3. finally run other building script which builds service entirely 

while IFS="" read -r file_line || [ -n "$file_line" ]
do
   cp -r ${METADATA_VOLUME_PATH}/${file_line} ${PROJECT_PATH}/modules

done <${CONFIG_METADATA_MODULES_DOCKER_PATH}

bash ${BASE_BUILD_SCRIPT_NAME} $1
