package biosampleparser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author WCHANG
 */
public class Non_Human_Host_Table extends TableSQL {
    private TableRow myRow;
    private TableSQL mySample_Table;
    
    public Non_Human_Host_Table(Sample_Table sampleTable) {
        super("Non_Human_Host");
        mySample_Table=sampleTable;
        TableColumn lNonHumanHostId = new IntegerColumn("nonhuman_id");
        lNonHumanHostId.setAutoIncrement();
        lNonHumanHostId.makePrimaryKey();
        addColumn(lNonHumanHostId);
        
        TableColumn lSampleId = new VarcharColumn("sample_id",50);
        lSampleId.setForeignKey(sampleTable,
                                sampleTable.getPrimaryColumn());
        addColumn(lSampleId);
        addColumn(new VarcharColumn("host_identifier",50));
        addColumn(new VarcharColumn("host",255));
        addColumn(new VarcharColumn("variety",100));
        addColumn(new VarcharColumn("growth_condition",255));
        myRow=preparedRow();
        addRow(myRow);

    }
    
    /**
     * Examines a RootElement and parses out the values which correspond to
     * a single row in the Non_Human_Host table.
     * @param biosampleRootElement
     * @return A SQL String which inserts the values from XML into a row in 
     * the Non_Human_Host table.
     */
    public StringBuffer parseBioSampleXML(Element biosampleRootElement) {
        myRow.clear();
        StringBuffer lBuffer = new StringBuffer();
        //Parse the XML
        NodeList lAttributes = biosampleRootElement.getElementsByTagName("Attribute");

        boolean lFoundNonHuman=false;
        String lHost=null;
        for(int i=0;i<=lAttributes.getLength();i++) {

            Element lAttributeNode = (Element)lAttributes.item(i);
            if(lAttributeNode != null) {
                String lAttributeNodeName = lAttributeNode.getAttribute("attribute_name");
                String lHarmonizedNodeName = lAttributeNode.getAttribute("harmonized_name");
 
                if("host".equals(lHarmonizedNodeName)) {
                    String lSpecificHost=lAttributeNode.getFirstChild().getNodeValue();
                    if(!Check_Host.isHuman(lSpecificHost)) {
                        lFoundNonHuman=true;
                        lHost=lSpecificHost;
                    }
                }
                
                if("variety".equals(lHarmonizedNodeName)) {
                    myRow.pullValue("variety",lAttributeNode);
                }
                if("growth_condition".equals(lHarmonizedNodeName)) {
                    myRow.pullValue("growth_condition",lAttributeNode);
                }
                if("host_taxid".equals(lHarmonizedNodeName)) {
                    myRow.pullValue("host_identifier",lAttributeNode);
                }
            }
        }
        if(lFoundNonHuman) {
            myRow.pullAttributeWithPrefix("sample_id",
                                          biosampleRootElement,"id",
                                          "BioSample");

            myRow.setItem("host",lHost);
            lBuffer.append(toInsertReturningString());
        }
        
        return lBuffer;

    }

}
