#!/bin/bash
. config.properties
cd ${javapath}
mkdir -p ${javapath}/biosampleparser
javac *.java
mv *.class ${javapath}/biosampleparser
