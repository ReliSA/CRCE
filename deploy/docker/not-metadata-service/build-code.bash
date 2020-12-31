while IFS="" read -r file_line || [ -n "$file_line" ]
do
   cp -r ${METADATA_VOLUME_PATH}/${file_line} ${PROJECT_PATH}/modules
done <${CONFIG_METADATA_MODULES}

ls ${PROJECT_PATH}/modules

bash ${BASE_BUILD_SCRIPT_NAME} $1