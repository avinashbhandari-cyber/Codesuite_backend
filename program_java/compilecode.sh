#!/bin/bash
mv test_main.java main.java
javac main.java 
if [ $? -ne 0 ]
  then
    exit 2
fi
ulimit -s 100
timeout --signal=SIGTERM 10 java main

exit $?