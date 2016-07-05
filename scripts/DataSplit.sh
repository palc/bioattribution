#!/usr/bin/env bash
set -eux -o pipefail
. config.properties

# Prepare BioSample XML
rm -f ${biosamplefilepath}/FtpBioSample*.xml
awk -v filePrefix="${biosamplefilepath}/FtpBioSample" -v openingTag="<BioSampleSet>" -v closingTag="</BioSampleSet>" -v beginTag1="<BioSample>" -v beginTag2="<BioSample " -v endTag="</BioSample>" -v maxRecords="10000" -f ${scriptpath}/split_xml.awk ${biosamplefilepath}/biosample_set.xml

# Prepare BioProject XML
rm -f ${bioprojectfilepath}/FtpBioProject*.xml
awk -v filePrefix="${bioprojectfilepath}/FtpBioProject" -v openingTag="<PackageSet>" -v closingTag="</PackageSet>" -v beginTag1="<Package>" -v beginTag2="<Package " -v endTag="</Package>" -v maxRecords="20000" -f ${scriptpath}/split_xml.awk ${bioprojectfilepath}/bioproject.xml


