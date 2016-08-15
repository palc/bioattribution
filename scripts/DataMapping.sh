#!/bin/bash
set -eux -o pipefail

. config.properties
rm -f ${idMapDir}/*appedIDs.out
rm -f ${scriptpath}/filter.awk

# Mapping of BioProject and BioSample IDs
rm -f ${idMapDir}/PRJNA_*.out
grep '<ArchiveID accession="PRJNA' ${bioprojectfilepath}/bioproject.xml > ${idMapDir}/PRJNA_ID.out
awk -F "\"" '{print $6;}' ${idMapDir}/PRJNA_ID.out > ${idMapDir}/PRJNA_parsedID.out
python ${scriptpath}/mapBioSampleBioProjectIDs.py "\"${idMapDir}/\"" "\"${scriptpath}\""

# Remove BioSample IDs that are not associated with BioProject IDs
awk -v scriptpath="\"${scriptpath}\"" -f ${scriptpath}/getIds.awk ${biosamplefilepath}/biosample_set.xml > ${scriptpath}/idlist.out
python ${scriptpath}/getBioSampleID.py "\"${idMapDir}/\"" "\"${scriptpath}/\""
sort -nu ${scriptpath}/BioSampleIDs.out > ${scriptpath}/sorted_BioSampleIDs.out
sort -nu ${scriptpath}/idlist.out > ${scriptpath}/sorted_idlist.out
diff ${scriptpath}/sorted_BioSampleIDs.out ${scriptpath}/sorted_idlist.out > ${scriptpath}/diff_IDs.out
grep "<" ${scriptpath}/diff_IDs.out | awk '{print "array[\"BioSample"$2"\"]=4;";}' > ${scriptpath}/BADIDs.out
cat ${scriptpath}/BEGIN ${scriptpath}/BADIDs.out ${scriptpath}/END > ${scriptpath}/filter.awk
awk -f ${scriptpath}/filter.awk ${idMapDir}/MappedIDs.out > ${idMapDir}/FilteredMappedIDs.out
