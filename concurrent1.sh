#!/bin/sh

for (( i = 0; i < 100; i++ )); do
    ./start.sh httpc get http://localhost:8080/start.sh
done