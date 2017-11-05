#!/bin/sh

for (( i = 0; i < 100; i++ )); do
    curl -v http://localhost:8080/start.sh
done