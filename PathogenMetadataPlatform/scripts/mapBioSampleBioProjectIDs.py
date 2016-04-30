import xml.dom.minidom
from xml.parsers.expat import ExpatError
import os
import sys

path = sys.argv[1];
path = path[1:-1];
os.system('rm -f '+ path +'MappedIDs.out')
xmlIndex = open(path +'PRJNA_parsedID.out').readlines()
outfile = open(path + 'MappedIDs.out', 'a')
firstline = 0


for i in range (firstline,len(xmlIndex)):
        BioProjectID = str(xmlIndex[i][0:len(xmlIndex[i])-1])

        os.system("wget -q -O "+ path +"BioProject" + BioProjectID + ".xml " + """"http://eutils.ncbi.nlm.nih.gov/entrez/eutils/elink.fcgi?dbfrom=bioproject&db=biosample&id=""" + BioProjectID + """" """)

for j in range (firstline,len(xmlIndex)):
        BioProjectID = str(xmlIndex[j][0:len(xmlIndex[j])-1])
        XMLfile = path + "BioProject" + BioProjectID + ".xml"
        if os.path.isfile(XMLfile) == True:
            try:
                dom = xml.dom.minidom.parse(XMLfile)
                Link = dom.getElementsByTagName('Link')
                k = 0
                for k in range(0, len(Link)):
                        Id = Link[k].getElementsByTagName('Id')[0].firstChild.nodeValue
                        MappedIDs = "BioProject" + BioProjectID + "\tBioSample" + str(Id) + "\n"
                        outfile.write(MappedIDs)
            except ExpatError:
                print "Ignoring "+BioProjectID
outfile.close()



