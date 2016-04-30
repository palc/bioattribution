#!/usr/bin/python
import os
import sys
def getproperty(var):
  confirm=1
  while confirm:
    parm=raw_input(var +":")
    
    confirm_string=raw_input("You entered "+parm+".  Is this correct? (Y or N)")
    if(("Y"  == confirm_string) or ("y" == confirm_string)):
      confirm=0
  return parm

filename="config.properties"
if(os.path.isfile(filename)):
  os.rename(filename,filename+".bak")
f=open(filename,'w')

biosamplefilepath=getproperty("Please select where the biosample will be downloaded")
bioprojectfilepath=getproperty("Please select where the bioproject will be downloaded")
scriptpath=getproperty("Please select where the scripts are")
idMapDir=getproperty("Please select directory where the mapping between Samples and Projects will go")
javapath=getproperty("Please select where the compiled JAVA/biosampleparser directory is\nShould not include biosampleparser/ directory")
SERVERNAME=getproperty("Please enter the server where postgres is")
DATABASE_NAME=getproperty("Please enter the name of the database")
USERNAME=getproperty("Please enter username of the database")
PASSWORD=getproperty("Please enter the username's password")

f.write("biosamplefilepath="+biosamplefilepath+"\n")
f.write("bioprojectfilepath="+bioprojectfilepath+"\n")
f.write("scriptpath="+scriptpath+"\n")
f.write("idMapDir="+idMapDir+"\n")
f.write("javapath="+javapath+"\n")
f.write("SERVERNAME="+SERVERNAME+"\n")
f.write("DATABASE_NAME="+DATABASE_NAME+"\n")
f.write("USERNAME="+USERNAME+"\n")
f.write("PASSWORD="+PASSWORD+"\n")
