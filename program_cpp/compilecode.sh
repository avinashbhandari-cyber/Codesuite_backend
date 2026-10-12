#!/bin/bash
mv test01.cpp main.cpp
g++ main.cpp -o output
if [ $? -ne 0 ]
  then
    exit 2
fi
ulimit -s 100
timeout --signal=SIGTERM 10 ./output

exit $?