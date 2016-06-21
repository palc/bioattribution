package biosampleparser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author WCHANG
 */
public class Study_Method_Table extends TableSQL {
    private TableRow myRow;
    private Sample_Table mySample_Table;
    
    public Study_Method_Table(Sample_Table sampleTable) {
        super("Study_Method");
        TableColumn lPrimaryColumnId = new IntegerColumn("study_method_id");
        lPrimaryColumnId.makePrimaryKey();
        lPrimaryColumnId.setAutoIncrement();
        addColumn(lPrimaryColumnId);
        mySample_Table=sampleTable;
        TableColumn lSampleId = new VarcharColumn("sample_id",50);
        lSampleId.setForeignKey(mySample_Table,mySample_Table.getPrimaryColumn());
        addColumn(lSampleId);
        addColumn(new VarcharColumn("method",255));
        addColumn(new TextColumn("description"));
        addColumn(new TextColumn("protocol"));
        myRow=preparedRow();
        addRow(myRow);
    }
    
    /**
     * Examines a RootElement and parses out the values which correspond to
     * a single row in the Study_Method table.
     * @param biosampleElement
     * @return A SQL String which inserts the values from XML into a row in 
     * the Study_Method table.
     */
    public StringBuffer parseBioSampleXML(Element biosampleRootElement) {
        myRow.clear();
        myRow.pullAttributeWithPrefix("sample_id",
                                      biosampleRootElement,"id",
                                      "BioSample");

        StringBuffer lBuffer = new StringBuffer();
        StringBuffer lParagraphString = new StringBuffer();
        boolean lFoundSampleData = false;
        //Parse the XML
        NodeList lDescriptionList = biosampleRootElement.getElementsByTagName("Description");
        Element lDescriptionNode = (Element)lDescriptionList.item(0);
        NodeList lParagraphs = lDescriptionNode.getElementsByTagName("Paragraph");
        
        for(int i=0;i<=lParagraphs.getLength();i++) {
            Element lParagraphNode = (Element)lParagraphs.item(i);
            if(lParagraphNode != null && lParagraphNode.getFirstChild() != null) {
               String lParagraphValue = lParagraphNode.getFirstChild().getNodeValue();
               //System.out.println(lParagraphValue+".....END");
               if(lParagraphValue != null) {
                   lParagraphString.append(lParagraphValue);
                   lFoundSampleData=true;
               }
            }
        }
        if(lFoundSampleData) {
            myRow.setItem("protocol",lParagraphString.toString());
           lBuffer.append(toInsertReturningString());
        }
        return lBuffer;
    }

}
