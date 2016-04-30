#!/bin/bash

# Download XML
./DataDownload.sh

# Split XML (so sizes are manageable)
./DataSplit.sh

# Map BioSample to BioProject
./DataMapping.sh

# Update database with data contained in latest download
./DataUpdate.sh

