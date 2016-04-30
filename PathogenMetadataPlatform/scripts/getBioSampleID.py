import xml.dom.minidom
import os

IDMapPath = sys.argv[1]
scriptPath = sys.argv[2] 
xmlIndex = open(IDMapPath + 'PRJNA_parsedID.out').readlines()
outfile = open(scriptPath + 'BioSampleIDs.out', 'a')
firstline = 0

for j in range (firstline,len(xmlIndex)):
        BioProjectID = str(xmlIndex[j][0:len(xmlIndex[j])-1])
        XMLfile = IDMapPath + "BioProject" + BioProjectID + ".xml"
        if os.path.isfile(XMLfile) == True:
                dom = xml.dom.minidom.parse(XMLfile)
                Link = dom.getElementsByTagName('Link')
                k = 0
                for k in range(0, len(Link)):
                        Id = Link[k].getElementsByTagName('Id')[0].firstChild.nodeValue
                        MappedIDs = str(Id) + "\n"
                        outfile.write(MappedIDs)
outfile.close()


