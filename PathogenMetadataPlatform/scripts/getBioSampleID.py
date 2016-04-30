import xml.dom.minidom
from xml.parsers.expat import ExpatError
import os
import sys

IDMapPath = sys.argv[1]
IDMapPath = IDMapPath[1:-1]
scriptPath = sys.argv[2] 
scriptPath = scriptPath[1:-1]
xmlIndex = open(IDMapPath + 'PRJNA_parsedID.out').readlines()
outfile = open(scriptPath + 'BioSampleIDs.out', 'a')
firstline = 0

for j in range (firstline,len(xmlIndex)):
        BioProjectID = str(xmlIndex[j][0:len(xmlIndex[j])-1])
        XMLfile = IDMapPath + "BioProject" + BioProjectID + ".xml"
        if os.path.isfile(XMLfile) == True:
            try:
                dom = xml.dom.minidom.parse(XMLfile)
                Link = dom.getElementsByTagName('Link')
                k = 0
                for k in range(0, len(Link)):
                        Id = Link[k].getElementsByTagName('Id')[0].firstChild.nodeValue
                        MappedIDs = str(Id) + "\n"
                        outfile.write(MappedIDs)
            except ExpatError:
                print "Ignoring BioProject "+BioProjectID
outfile.close()


