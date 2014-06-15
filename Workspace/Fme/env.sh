#!/bin/sh

# ***********************************************
#
#   File: env.sh
#
#   Author: Matthias Ladkau
#
#   Description:
#
#     Script to set up the environment for the FME.
#
# ***********************************************

# Set paths
ROOT_PATH=`pwd ${0/%\/env.sh}`
SZIP_HOME=$ROOT_PATH/res/p7zip_9_13_linux
FERMAT3_HOME=$ROOT_PATH/engine/fermat3/Linux

PATH=$FERMAT3_HOME:$SZIP_HOME/bin:$PATH
export PATH

# Extract external resources if necessary

if [ ! -d "$ROOT_PATH/engine" ]; then mkdir $ROOT_PATH/engine; fi
if [ ! -d "$ROOT_PATH/engine/fermat3" ]; then 7za x -o$ROOT_PATH/engine $ROOT_PATH/res/fermat3.7z; fi

# Fermat initialisation

FermaT=$ROOT_PATH/engine/fermat3
export FermaT
PATH=$FermaT/bin:$PATH
export PATH
SCHEME_LIBRARY_PATH="$FermaT/slib/"
export SCHEME_LIBRARY_PATH
SCM_INIT_PATH="$FermaT/scm/Init5f1.scm"
export SCM_INIT_PATH

# Test commands

java -version > /dev/null 2>&1
if [ $? -ne 0 ]; then 
    echo "'java' command not found. Please install a current JDK and put its bin directory in your PATH variable."
    exit 1
fi

which scmfmt > /dev/null 2>&1
if [ $? -ne 0 ]; then 
    echo "'scmfmt' command not found. Cannot continue..."
    exit 1
fi
