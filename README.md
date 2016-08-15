System architecture
====================
![LabKey system architecture](labkey_system_diagram.png)

This platform is built on the [LabKey data platform](https://www.labkey.org), an open-source data management platform designed for biological data, and uses a [PostgreSQL](http://www.postgresql.org/) database designed for capture of metadata useful for disease outbreak investigations. The platform also employs [D3.js](http://d3js.org/), [R](http://www.r-project.org/), and open-source MITRE geospatial tools. 

Here, we provide a Java program that parses xml files from NCBI describing data from the [BioSample](http://www.ncbi.nlm.nih.gov/biosample) and [BioProject](http://www.ncbi.nlm.nih.gov/bioproject/) databases. We also provide a LabKey module that, when placed into LabKey's `External Modules` folder will allow projects to be created that enable a user to interact with SRA data from within LabKey.

System requirements
===================

Hardware requirements
--------------------
* Linux server

Software requirements
---------------------
- Java (version 1.7.0_02)
- Python (version 2.7.3)
- Shell (bash)
- Awk (version 3.1.8)
- JavaScript
- wget (version 1.13.4)
- PostgreSQL (version 9.1)

Note: The system was tested under the software with version provided in parentheses. Other versions may or may not work.

Processing time
--------------------
We have observed data download, data processing, and database import times of between 7 hours and 8 hours on a dual-processor (Intel Xeon E7-2830 2.13 GhZ) machine with 16 gigabytes of memory. 

On machines with more memory, it may be more efficient to split the initial Biosample and Bioproject XML files into larger segments. This setting can be changed in the `scripts/DataSplit.sh` script.

Project organization
====================

`JAVA`
------		

There are 26 java files and 3 jar files

This code is used to create and update a Posrgres Database with the XML metadata downloaded from NCBI BioSample and NCBI BioProject.

The names of 26 java files required for the project

- BioSampleParser.java
- BooleanColumn.java
- CharColumn.java
- Check_Host.java
- Collection_Owner_Table.java
- Collection_Table.java
- Cross_Reference_Table.java
- GenericTableColumn.java
- Human_Host_Table.java
- IntegerColumn.java
- Non_Human_Host_Table.java
- NumericColumn.java
- Owner_Table.java
- Project_Publication_Table.java
- Project_Sample_Table.java
- Project_Table.java
- Sample_Table.java
- Study_Method_Table.java
- Submitter_Table.java
- TableColumn.java
- TableColumnTypeException.java
- TableRow.java
- TableSQL.java
- TextColumn.java
- TimestampColumn.java
- VarcharColumn.java

The names of 3 jar files required for the project

- commons-lang3-3.1.jar
- xom-1.2.8.jar
- postgresql-9.1-902.jdbc4.jar

`scripts`
----------
There are a total of 14 files in this directory

- BEGIN
- BuildJava.sh
- DataDownload.sh
- DataMapping.sh
- DataSplit.sh
- DataUpdate.sh
- DataUpload.sh
- END
- geomap.tsv
- getBioSampleID.py
- getIds.awk
- install.py
- mapBioSampleBioProjectIDs.py
- split_xml.awk

Installation
============
I. Setup 
A. Hardware Requirements
This software was tested on a Xeon X5550 @2.67 GHz.  It requires at least 24 GB of Disk Space for the NCBI data and 10 GB for the postgresql database.

B. Pre-requisite Postgresql
Postgresql database needs to be installed and database needs to be created prior to running the install.py script. The username, password, server, and databasename will be needed to run the installation script. The user should be a superuser on Postgres. This can be done by running the SQL "ALTER USER <username> superuser;".

C. install.py
Run python install.py: It creates a config.properties file, builds the JAVA code, and moves the resulting class files into a biosampleparser sub-directory. Please answer the questions with results that correspond to your environment.  Note that directories entered should contain absolute paths.  The script will attempt to make directories if they are missing, but the parent directory must exist.  This is a sample run

	Please select where the biosample will be downloaded:/path/to/biosampledownload
	You entered /path/to/biosampledownload.  /path/to/biosampledownload does not exist and will be created if you confirm.
	Is this correct? (Y or N)y
	Please select where the bioproject will be downloaded:/path/to/bioprojectdownload
	You entered /path/to/bioprojectdownload.  /path/to/bioprojectdownload does not exist and will be created if you confirm.
	Is this correct? (Y or N)y
	Please select directory where the mapping between Samples and Projects will go:/path/to/mapping
	You entered /path/to/mapping.  /path/to/mapping does not exist and will be created if you confirm.=
	Is this correct? (Y or N)y
	Please select where the scripts are:/path/to/scripts
	You entered /path/to/scripts.
	Is this correct? (Y or N)y
	Please select where the JAVA directory is
	Should not include biosampleparser/ directory:/path/to/JAVA
	You entered /path/to/JAVA.
	Is this correct? (Y or N)y
	Please enter the server where postgres is:localhost
	You entered localhost.
	Is this correct? (Y or N)y
	Please enter the name of the database:db_name
	You entered db_name.
	Is this correct? (Y or N)y
	Please enter username of the database:username
	You entered username.
	Is this correct? (Y or N)y
	Please enter the username's password:password
	You entered password.
	Is this correct? (Y or N)y
	Cleaning scripts syntax
	dos2unix: converting file BuildJava.sh to Unix format ...
	dos2unix: converting file DataDownload.sh to Unix format ...
	dos2unix: converting file DataMapping.sh to Unix format ...
	dos2unix: converting file DataSplit.sh to Unix format ...
	dos2unix: converting file DataUpdate.sh to Unix format ...
	dos2unix: converting file DataUpload.sh to Unix format ...
	dos2unix: converting file testupdate.sh to Unix format ...
	Making scripts executable
	Building Java files

II. After these paths have been configured, and the java code has been compiled, DataUpload.sh is the script which executes all the necessary helper programs to populate the database with the latest information from BioSample and BioProject.  It calls DataDownload.sh (to download files), DataSplit.sh (to split the files to a manageable size for Java) DataMapping.sh (to map BioSample IDs to BioProject IDs) and DataUpdate.sh (to populate the posrgresql database).  I.e. running ./DataUpload from the scripts/ directory should populate the database with all the information. 
Note: The data from NCBI may change over time.  One particular area where this can be problematic is in the DataSplit.sh script.  If there are too many items specified in an XML file, the Java code may have an OutOfMemoryError.  The current distribution has maxRecords set to "1000" for BioSample and "2000" for BioProject.  If an OutOfMemoryError is observed, the maxRecords value should be decreased.  A recent run for the BioSampleSet generated over 4000 files.  If maxRecords were 500, then there would be 8000 files generated on a similar run.
awk -v filePrefix="${biosamplefilepath}/FtpBioSample" -v openingTag="<BioSampleSet>" -v closingTag="</BioSampleSet>" -v beginTag1="<BioSample>" -v beginTag2="<BioSample " -v endTag="</BioSample>" -v maxRecords="1000" -f ${scriptpath}/split_xml.awk ${biosamplefilepath}/biosample_set.xml

Connecting LabKey to Postgres
-----------------------------
The LabKey project provides [instructions](https://www.labkey.org/wiki/home/Documentation/page.view?name=externalPostgresql&_docid=wiki%3A97d16462-c1dc-102e-bae9-987439a6cbac) that will allow you to connect LabKey to the PostgreSQL database that is generated with the provided Java program.  	

LabKey Module
-------------
We provide a LabKey module that is designed to work with the database generated with the provided java program. For more on modules please refer to the LabKey [documentation](https://www.labkey.org/wiki/home/Documentation/page.view?name=moduleqvr).

Within the labkey module, located at `datatools`, there are two webpart html files in `views`. These files, called `geodata.html` and `metadata.html`. Within these files, there are two code snippets that will need to be modified to work with the name of the schema of your database: 

		_qwp1 = new LABKEY.QueryWebPart({renderTo       : "grid",
                						 title          : "SRA Search",
	                                     schemaName     : <DATABASE_NAME>,
        	                             dataRegionName : "metaDataRegion",
                	                     sql            : _query.toString()
              							});
and

		LABKEY.Query.GetData.getRawData({source  : {type      : 'sql',
		                                            schemaName: <DATABASE_NAME>,
		                                            sql       : _query.toString()
		                                           },
		                                 success : onSuccess,
		                                 failure : onError
		                                });

Replace `<DATABASE_NAME>` with the name of your scheme, which defaults to `bioatt`.

In order to install the LabKey module, move the `datatools` directory to the `externalModules` folder of your labkey installation. For example, if LabKey is installed at `/usr/local/labkey`, `datatools` would be located at `/usr/local/labkey/externalModules/labkey`.

©2015 The MITRE Corporation. ALL RIGHTS RESERVED. Approved for Public Release; Distribution Unlimited. Case Number 15-0792.
