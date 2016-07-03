import requests
import xml.dom.minidom
from xml.parsers.expat import ExpatError
import os
import os.path
import sys

import pdb

def batch(iterable, n=1):
    l = len(iterable)
    for ndx in range(0, l, n):
        yield iterable[ndx:min(ndx + n, l)]

mappath = sys.argv[1];
mappath = mappath[1:-1];

scriptpath = sys.argv[2];
scriptpath = scriptpath[1:-1];

os.system('rm -f '+ mappath +'MappedIDs.out')
xmlIndex = [index.strip() for index in open(mappath +'PRJNA_parsedID.out').readlines()]
outfile = open(os.path.join(mappath, 'MappedIDs.out'), 'a')
biosample_out = open(os.path.join(scriptpath, 'BioSampleIDs.out'), 'aw')

for id_batch in batch(xmlIndex, 200):
    elink_url = """http://eutils.ncbi.nlm.nih.gov/entrez/eutils/elink.fcgi?retmod=json&dbfrom=bioproject&db=biosample&{}"""
    query = '&'.join(['id={}'.format(index) for index in id_batch])
    response = requests.post(elink_url.format(query))

    if response.status_code == 200:
        try:
            dom = xml.dom.minidom.parseString(response.text)
            LinkSets = dom.getElementsByTagName('LinkSet')
            for LinkSet in LinkSets:
                BioProjectID = LinkSet.getElementsByTagName('Id')[0].firstChild.nodeValue
                Links = LinkSet.getElementsByTagName('Link')
                seenIds = []
                for Link in Links:
                    Id = Link.getElementsByTagName('Id')[0].firstChild.nodeValue
                    if not Id in seenIds:
                        MappedIDs = "BioProject" + BioProjectID + "\tBioSample" + Id + "\n"

                        outfile.write(MappedIDs)
                        biosample_out.write(Id + "\n")

                        seenIds.append(Id)
        except ExpatError:
            print "Ignoring " + BioProjectID
outfile.close()
biosample_out.close()

# for index in xmlIndex:
#         BioProjectID = index.strip()
#         os.system("wget -q -O "+ path +"BioProject" + BioProjectID + ".xml " + base_url + BioProjectID + """" """)

#         XMLfile = path + "BioProject" + BioProjectID + ".xml"
#         if os.path.isfile(XMLfile) == True:
#             try:
#                 dom = xml.dom.minidom.parse(XMLfile)
#                 Link = dom.getElementsByTagName('Link')
#                 k = 0
#                 for k in range(0, len(Link)):
#                         Id = Link[k].getElementsByTagName('Id')[0].firstChild.nodeValue
#                         MappedIDs = "BioProject" + BioProjectID + "\tBioSample" + str(Id) + "\n"
#                         outfile.write(MappedIDs)
#             except ExpatError:
#                 print "Ignoring "+BioProjectID
# outfile.close()