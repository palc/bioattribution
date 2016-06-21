#!/usr/bin/python
import os
import sys
def getproperty(var,check_file):
  confirm=1
  make=0
  while confirm:
    make=0
    append=""
    parm=raw_input(var +":")
    if check_file == 1:
      if(not os.path.isdir(parm)):
        append=parm+" does not exist and will be created if you confirm."
        make=1
    if check_file == 2:
      if(not os.path.isdir(parm)):
        append=parm+" does not exist.  It should correspond to a directory that was created during the unzip."
        make=2

    confirm_string=raw_input("You entered "+parm+".  "+append+"\nIs this correct? (Y or N)")
      
    if(("Y"  == confirm_string) or ("y" == confirm_string)):
      confirm=0
    if make == 2:
      print "Answer ignored.  Must pick correct "+parm
      confirm=1
  if make:
    os.mkdir(parm) 
  return parm

filename="config.properties"
if(os.path.isfile(filename)):
  if(os.path.isfile(filename+".bak")):
    print "No backup can be made. Please rm " +filename+".bak and retry"
    sys.exit()
  else:
    os.rename(filename,filename+".bak")
f=open(filename,'w')

biosamplefilepath=getproperty("Please select where the biosample will be downloaded",1)
bioprojectfilepath=getproperty("Please select where the bioproject will be downloaded",1)
idMapDir=getproperty("Please select directory where the mapping between Samples and Projects will go",1)
scriptpath=getproperty("Please select where the scripts are",2)
javapath=getproperty("Please select where the JAVA directory is\nShould not include biosampleparser/ directory",2)
SERVERNAME=getproperty("Please enter the server where postgres is",0)
DATABASE_NAME=getproperty("Please enter the name of the database",0)
USERNAME=getproperty("Please enter username of the database",0)
PASSWORD=getproperty("Please enter the username's password",0)

f.write("biosamplefilepath="+biosamplefilepath+"\n")
f.write("bioprojectfilepath="+bioprojectfilepath+"\n")
f.write("idMapDir="+idMapDir+"\n")
f.write("scriptpath="+scriptpath+"\n")
f.write("javapath="+javapath+"\n")
f.write("SERVERNAME="+SERVERNAME+"\n")
f.write("DATABASE_NAME="+DATABASE_NAME+"\n")
f.write("USERNAME="+USERNAME+"\n")
f.write("PASSWORD="+PASSWORD+"\n")
f.close()

print "Cleaning scripts syntax"
os.system("dos2unix *.sh")

print "Making scripts executable"
os.system("chmod +x *.sh")

print "Building Java files"
os.system("./BuildJava.sh")
