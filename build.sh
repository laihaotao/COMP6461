#!/bin/sh
 
mvn package
# cp target/COMP6461-1.0-SNAPSHOT-jar-with-dependencies.jar httpc.jar
cp target/COMP6461-1.0-SNAPSHOT-jar-with-dependencies.jar httpfs.jar
