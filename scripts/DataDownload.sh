#!/usr/bin/env bash
set -eux -o pipefail
. config.properties

# Download BioSample XML
rm ${biosamplefilepath}/biosample_set.xml
wget -O ${biosamplefilepath}/biosample_set.xml.gz ftp://ftp.ncbi.nlm.nih.gov/biosample/biosample_set.xml.gz
gunzip ${biosamplefilepath}/biosample_set.xml.gz

# Download BioProject XML
rm ${bioprojectfilepath}/bioproject.xml
wget -O ${bioprojectfilepath}/bioproject.xml ftp://ftp.ncbi.nlm.nih.gov/bioproject/bioproject.xml

