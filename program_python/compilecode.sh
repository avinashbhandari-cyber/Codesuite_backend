#!/bin/bash
mv test01.py main.py 
ulimit -s 100
timeout --signal=SIGTERM 10 python main.py 

exit $?