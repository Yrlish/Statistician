#!/bin/bash

CURRENT_DIR=$(pwd)
BUILD_NUMBER=${BUILD_NUMBER}

####################
## PLUGIN RELEASE ##
####################

cd ${CURRENT_DIR}/Plugin

# Rename jar file to include the build number
rename 's/DEV_BUILD/${BUILD_NUMBER}/' target/*.jar