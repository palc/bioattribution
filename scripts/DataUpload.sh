#!/bin/bash -x
date
# Download XML
./DataDownload.sh

date
# Split XML (so sizes are manageable)
./DataSplit.sh
date
# Map BioSample to BioProject
./DataMapping.sh
date
# Update database with data contained in latest download
./DataUpdate.sh
date
