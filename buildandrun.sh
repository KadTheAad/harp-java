#!/bin/bash

cd ~/harp-java 
mvn package
java -jar ~/harp-java/target/HARP-1.0-SNAPSHOT-shaded.jar 
