#!/usr/bin/env bash
set -eux -o pipefail
. config.properties

# Download BioSample XML
rm -f ${biosamplefilepath}/biosample_set.xml
curl -k -o ${biosamplefilepath}/biosample_set.xml.gz ftp://ftp.ncbi.nlm.nih.gov/biosample/biosample_set.xml.gz
gunzip ${biosamplefilepath}/biosample_set.xml.gz

# Download BioProject XML
rm -f ${bioprojectfilepath}/bioproject.xml
curl -o ${bioprojectfilepath}/bioproject.xml ftp://ftp.ncbi.nlm.nih.gov/bioproject/bioproject.xml
