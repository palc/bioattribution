#!/bin/bash
. config.properties

# Prepare BioSample XML
rm ${biosamplefilepath}/FtpBioSample*.xml
awk --posix -v filePrefix="${biosamplefilepath}/FtpBioSample" -v openingTag="<BioSampleSet>" -v closingTag="</BioSampleSet>" -v beginTag1="<BioSample>" -v beginTag2="<BioSample " -v endTag="</BioSample>" -v maxRecords="1000" -f ${scriptpath}/split_xml.awk ${biosamplefilepath}/biosample_set.xml

# Prepare BioProject XML
rm ${bioprojectfilepath}/FtpBioProject*.xml
awk --posix -v filePrefix="${bioprojectfilepath}/FtpBioProject" -v openingTag="<PackageSet>" -v closingTag="</PackageSet>" -v beginTag1="<Package>" -v beginTag2="<Package " -v endTag="</Package>" -v maxRecords="2000" -f ${scriptpath}/split_xml.awk ${bioprojectfilepath}/bioproject.xml


