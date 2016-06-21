package biosampleparser;

import org.w3c.dom.Element;

/**
 *
 * @author WCHANG
 */
public class Submitter_Table extends TableSQL {
    private TableRow myRow;
    
    public Submitter_Table(Owner_Table OwnerTable,Sample_Table sampleTable) {
        super("Submitter");
        TableColumn lSubmitterId = new IntegerColumn("submitter_id");
        lSubmitterId.makePrimaryKey();
        lSubmitterId.setAutoIncrement();
        addColumn(lSubmitterId);
        TableColumn lOwnerId = new IntegerColumn("Owner_id");
        lOwnerId.setForeignKey(OwnerTable,OwnerTable.getPrimaryColumn());
        addColumn(lOwnerId);
        
        TableColumn lSampleId = new VarcharColumn("sample_id",50);
        lSampleId.setForeignKey(sampleTable,sampleTable.getPrimaryColumn());
        addColumn(lSampleId);
        
        myRow = preparedRow();
        addRow(myRow);
        myRow.setVariable("Owner_id",OwnerTable.toReferencePrimaryKeyVariable());
    }
    
    /**
     * Examines a RootElement and parses out the values which correspond to
     * a single row in the Submitter table.
     * @param biosampleElement
     * @return A SQL String which inserts the values from XML into a row in 
     * the Submitter table.
     */
    StringBuffer parseBioSampleXML(Element biosampleRootElement) {
        myRow.pullAttributeWithPrefix("sample_id",
                                      biosampleRootElement,"id",
                                      "BioSample");   
        StringBuffer lResult = new StringBuffer();
        return lResult;
    }
}
