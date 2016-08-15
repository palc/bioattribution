package biosampleparser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author WCHANG
 */
public class Human_Host_Table extends TableSQL {
    private TableRow myRow;
    private TableSQL mySample_Table;
    
    
    public Human_Host_Table(Sample_Table sampleTable) {
        super("Human_Host");
                
        mySample_Table=sampleTable;
        TableColumn lHumanHostId = new IntegerColumn("human_host_id");
        lHumanHostId.setAutoIncrement();
        lHumanHostId.makePrimaryKey();
        addColumn(lHumanHostId);
        
        TableColumn lSampleId = new VarcharColumn("sample_id",50);
        lSampleId.setForeignKey(sampleTable,
                                   sampleTable.getPrimaryColumn());
        addColumn(lSampleId);
        addColumn(new VarcharColumn("host_subject_id",50));
        addColumn(new IntegerColumn("age"));
        addColumn(new CharColumn("gender",1));
        addColumn(new VarcharColumn("race",50));
        addColumn(new VarcharColumn("diet",255));
        addColumn(new VarcharColumn("host",255));
        myRow=preparedRow();
        addRow(myRow);
    }
    
    /**
     * Examines a RootElement and parses out the values which correspond to
     * a single row in the Human_Host table.
     * @param biosampleRootElement
     * @return A SQL String which inserts the values from XML into a row in 
     * the Human_Host table.
     */
    public StringBuffer parseBioSampleXML(Element biosampleRootElement) {
        myRow.clear();
        StringBuffer lBuffer = new StringBuffer();
        //Parse the XML
        NodeList lAttributes = biosampleRootElement.getElementsByTagName("Attribute");
        boolean lFoundHuman=false;
        String lGender = null;
        String lHost = null;
        String lRace = null;
        String lDiet = null;
        String lSubjectID = null;
        
        for(int i=0;i<=lAttributes.getLength();i++) {

            Element lAttributeNode = (Element)lAttributes.item(i);
            if(lAttributeNode != null) {
                String lAttributeNodeName = lAttributeNode.getAttribute("attribute_name");
                String lHarmonizedNodeName = lAttributeNode.getAttribute("harmonized_name");
 
                if("host".equals(lHarmonizedNodeName)) {
                    String lSpecificHost=lAttributeNode.getFirstChild().getNodeValue();
                    if(Check_Host.isHuman(lSpecificHost)) {
                        lFoundHuman=true;
                        lHost=lSpecificHost;
                    }
                }
                if("sex".equals(lHarmonizedNodeName) || 
                   "host_sex".equals(lHarmonizedNodeName)) {
                    lGender=lAttributeNode.getFirstChild().getNodeValue();
                    if("FEMALE".equalsIgnoreCase(lGender)) {
                        lGender="F";
                    } else if("MALE".equalsIgnoreCase(lGender)) {
                        lGender="M";
                    } else {
                        lGender="U";
                    }
                }
                if("diet".equals(lHarmonizedNodeName)) {
                    lDiet=lAttributeNode.getFirstChild().getNodeValue();
                }
                if("race".equals(lHarmonizedNodeName)) {
                    lRace=lAttributeNode.getFirstChild().getNodeValue();
                }
                if("host_subject_id".equals(lHarmonizedNodeName)) {
                    lSubjectID=lAttributeNode.getFirstChild().getNodeValue();
                }
            }
        }
        if(lFoundHuman) {
            myRow.pullAttributeWithPrefix("sample_id",
                                          biosampleRootElement,"id",
                                          "BioSample");
            myRow.setItem("host",lHost);
            myRow.setItem("gender",lGender);
            myRow.setItem("race",lRace);
            myRow.setItem("diet",lDiet);
            myRow.setItem("host_subject_id",lSubjectID);
            lBuffer.append(toInsertReturningString());
        }
        
        return lBuffer;

    }
    
    
    
    
}
