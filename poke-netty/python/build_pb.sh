#!/bin/bash
#
# creates the python classes for our .proto
#

project_base="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

rm ${project_base}/src/comm_pb2.py

protoc -I=../resources/ --python_out=./src ../resources/comm.proto 
