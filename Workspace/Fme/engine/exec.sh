#!/bin/sh
# ***********************************************
#
#   File: exec.sh
#
#   Author: Matthias Ladkau
#
#   Description:
#
#     Script to run a wsl file.
#
# ***********************************************

if [ ! "$1" = "noecho" ]; then
    ROOT_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
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
    echo "fermat_console.sh: Changing directory to $FermaT"
fi

cd $FermaT

if [ ! "$1" = "noecho" ]; then
    echo "fermat_console.sh: Running WSL file ..."
fi


if [ "$1" = "noecho" ]; then
    perl ./bin/wsl $2
else
    perl ./bin/wsl $1
fi

