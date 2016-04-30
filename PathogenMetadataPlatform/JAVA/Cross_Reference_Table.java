package biosampleparser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author WCHANG
 */
public class Cross_Reference_Table extends TableSQL {
    private TableRow myRow;
    private Sample_Table mySample_Table;
    
    public Cross_Reference_Table(Sample_Table sampleTable) {
        super("Cross_Reference");
        mySample_Table=sampleTable;
        TableColumn lCrossReferenceId = new IntegerColumn("cross_reference_id");
        lCrossReferenceId.makePrimaryKey();
        lCrossReferenceId.setAutoIncrement();
        addColumn(lCrossReferenceId);
        TableColumn lSampleId = new VarcharColumn("sample_id",50);
        lSampleId.setForeignKey(mySample_Table,mySample_Table.getPrimaryColumn());
        addColumn(lSampleId);
        addColumn(new VarcharColumn("source",100));
        addColumn(new TextColumn("source_id"));

        myRow=preparedRow();
        addRow(myRow);
    }
    
   /**
     * Examines a RootElement and parses out the values which correspond to
     * a single row in the Cross_Reference table.
     * @param biosampleRootElement
     * @return A SQL String which inserts the values from XML into a row in 
     * the Cross_Reference table.
     */
    public StringBuffer parseBioSampleXML(Element biosampleRootElement) {
        myRow.clear();
        myRow.pullAttributeWithPrefix("sample_id",
                              biosampleRootElement,"id",
                              "BioSample");

        StringBuffer lBuffer = new StringBuffer();
        //Parse the XML
        NodeList lIds = biosampleRootElement.getElementsByTagName("Id");
        boolean lFoundSampleData=false;
        for(int i=0;i<=lIds.getLength();i++) {
            Element lElement = (Element)lIds.item(i);
            if(lElement != null) {
                lFoundSampleData = myRow.pullAttribute("source",lElement,"db")
                        || lFoundSampleData;
                lFoundSampleData = myRow.pullValue("source_id", lElement)
                        || lFoundSampleData;
            }
        }
        if(lFoundSampleData) {
            lBuffer.append(toInsertReturningString());
        }
        return lBuffer;
    }
}
