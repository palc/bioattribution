#!/usr/bin/env bash
set -euf -o pipefail

. config.properties

# Download BioSample XML (or a newer version if it exists)
if [[ -e ${biosamplefilepath}/biosample_set.xml ]]; then ZFLAG="-z ${biosamplefilepath}/biosample_set.xml"; else ZFLAG= ; fi
curl -k $ZFLAG -o ${biosamplefilepath}/biosample_set.xml.gz ftp://ftp.ncbi.nlm.nih.gov/biosample/biosample_set.xml.gz

if [[ -e ${biosamplefilepath}/biosample_set.xml.gz ]]; then yes y | gunzip ${biosamplefilepath}/biosample_set.xml.gz; fi

# Download BioProject XML
if [[ -e ${bioprojectfilepath}/bioproject.xml ]]; then ZFLAG="-z ${bioprojectfilepath}/bioproject.xml"; else ZFLAG= ; fi
curl -k $ZFLAG -o ${bioprojectfilepath}/bioproject.xml ftp://ftp.ncbi.nlm.nih.gov/bioproject/bioproject.xml
