#!/bin/bash

source $(pwd)/env.sh

$JSVC $OPTIONS -stop -pidfile $PID -cwd `pwd` $MAIN_CLASS

