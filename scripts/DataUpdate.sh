#!/usr/bin/env bash
set -eux -o pipefail

. config.properties
x=""
if [ -z ${CLASSPATH+x} ]; 
then export CLASSPATH=${javapath}:${javapath}/postgresql-9.1-902.jdbc4.jar:${javapath}/xom-1.2.8.jar;
else export CLASSPATH=$CLASSPATH:${javapath}:${javapath}/postgresql-9.1-902.jdbc4.jar:${javapath}/xom-1.2.8.jar;
fi


# Updating BioProject and BioSample records in the Database
java -cp ${javapath}:${javapath}/postgresql-9.1-902.jdbc4.jar:${javapath}/xom-1.2.8.jar biosampleparser.BioSampleParser rebuilddb ${scriptpath}/config.properties
countbiosamples=`ls -1 ${biosamplefilepath}/ | egrep "FtpBioSample" | wc -l`
countbioprojects=`ls -1 ${bioprojectfilepath}/ | egrep "FtpBioProject" | wc -l` 
java -cp ${javapath}:${javapath}/postgresql-9.1-902.jdbc4.jar:${javapath}/xom-1.2.8.jar biosampleparser.BioSampleParser loadnewdata ${countbiosamples} "${biosamplefilepath}/FtpBioSample" ${countbioprojects} "${bioprojectfilepath}/FtpBioProject" "${idMapDir}/FilteredMappedIDs.out" "${scriptpath}/geomap.tsv" ${scriptpath}/config.properties >& ${javapath}/error.out

rm -f ${scriptpath}/*.out
