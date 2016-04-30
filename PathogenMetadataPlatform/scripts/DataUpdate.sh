#!/bin/bash
. config.properties
export CLASSPATH=$CLASSPATH:${javapath}:${javapath}/postgresql-9.1-902.jdbc4.jar:${javapath}/xom-1.2.8.jar


# Updating BioProject and BioSample records in the Database
/usr/local/java/bin/java -cp ${javapath}:${javapath}/postgresql-9.1-902.jdbc4.jar:${javapath}/xom-1.2.8.jar biosampleparser.BioSampleParser rebuilddb ${scriptpath}/config.properties
/usr/local/java/bin/java -cp ${javapath}:${javapath}/postgresql-9.1-902.jdbc4.jar:${javapath}/xom-1.2.8.jar biosampleparser.BioSampleParser loadnewdata `ls -1 ${biosamplefilepath}/FtpBioSample*.xml | wc -l` "${biosamplefilepath}/FtpBioSample" `ls -1 ${bioprojectfilepath}/FtpBioProject*.xml | wc -l` "${bioprojectfilepath}/FtpBioProject" "${idMapDir}/FilteredMappedIDs.out" "${scriptpath}/geomap.tsv" ${scriptpath}/config.properties >& ${javapath}/error.out

mv ${scriptpath}/nohup.out ${scriptpath}/nohup.txt
rm ${scriptpath}/*.out

