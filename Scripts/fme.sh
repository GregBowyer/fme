#!/bin/sh
# ***********************************************
#
#   File: fme.sh
#
#   Author: Matthias Ladkau
#
#   Description:
#
#     This is the FME start script.
#
# ***********************************************

ROOT_PATH=${0/%\/fme.sh}
cd $ROOT_PATH
ROOT_PATH=`pwd`

. $ROOT_PATH/env.sh
if [ $? -ne 0 ]; then 
    echo "Could not setup environment"
    exit 1
fi

java -classpath ./lib/fme.jar:./lib/stringsearch-2.jar:./lib/toniclf-1.0.5.jar:./lib/xercesImpl.jar:./lib/xmlParserAPIs.jar:./lib/zvtm.jar fme.FMEMain > FME_out.txt 2> FME_err.txt
