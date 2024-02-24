#!/bin/bash

cd /Users/khaledadib/harp-java 
mvn package
java -jar /Users/khaledadib/harp-java/target/HARP-1.0-SNAPSHOT-shaded.jar 
