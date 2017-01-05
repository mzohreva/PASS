#!/bin/bash

usage()
{
    echo
    echo "Please specify jar classifier to use: 'local' or 'remote'"
    echo
    exit 1
}

if [ "$#" -lt "1" ]; then
    usage
fi

if [ "$1" != "local" ] && [ "$1" != "remote" ]; then
    usage
fi

classifier=$1
source $(pwd)/env.sh

$JSVC $OPTIONS -pidfile $PID -cwd `pwd` -cp $CLASSPATH $MAIN_CLASS $ARGS

