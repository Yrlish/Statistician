#!/bin/bash

CURRENT_DIR=$(pwd)
WORKING_DIR=$(mktemp -d)


##
## Prepare packaging artifact
##

mkdir -p ${WORKING_DIR}/Plugin
mkdir -p ${WORKING_DIR}/Webpanel


##
## Plugin
##

cp -f ${CURRENT_DIR}/Plugin/target/Statistician-*.jar ${WORKING_DIR}/Plugin/


##
## Webportal
##



##
## Package artifact
##

cd ${WORKING_DIR}
zip -r ${CURRENT_DIR}/Statistician-${POM_VERSION}-${BUILD_NUMBER}.zip *

