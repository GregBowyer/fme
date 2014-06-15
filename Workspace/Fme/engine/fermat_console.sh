#!/bin/sh
# ***********************************************
#
#   File: console.sh
#
#   Author: Matthias Ladkau
#
#   Description:
#
#     This is a start script for the 
#     FermaT transformation engine console.
#
# ***********************************************

if [ ! "$1" = "noecho" ]; then
    ROOT_PATH=`pwd ${0/%\/fermat_console.sh}`
    cd $ROOT_PATH/..
    . ./env.sh
    cd $ROOT_PATH
fi

# Make sure the environment is set up

if [ ! "$1" = "noecho" ]; then
    echo
    echo
    echo "FermaT transformation engine console"
    echo "===================================="
    echo "fermat_console.sh: Testing for perl ..."
fi

if ( ! perl -e "exit 0" ); then
    echo "fermat_console.sh: Can't find Perl! (Check installation and/or PATH variable)"
    exit 1
fi

if [ ! "$1" = "noecho" ]; then
    echo "fermat_console.sh: Found perl!"
    echo "fermat_console.sh: Changing directory to $PWD/engine/fermat3"
fi

cd $FermaT

if [ ! "$1" = "noecho" ]
then
echo "fermat_console.sh: Setting the environment variables ..."
fi

scmfmt
