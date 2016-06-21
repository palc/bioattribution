package biosampleparser;

import org.w3c.dom.Element;

/**
 *
 * @author WCHANG
 */
public class Project_Sample_Table extends TableSQL{
    private TableRow myRow;
    
    private Project_Table myProjectTable;
    private Sample_Table mySampleTable;
    
    public Project_Sample_Table(Project_Table projectTable, 
                                Sample_Table sampleTable) {
        super("Project_Sample");
        TableColumn lProjectSampleId = new IntegerColumn("project_sample_id");
        lProjectSampleId.makePrimaryKey();
        lProjectSampleId.setAutoIncrement();
        addColumn(lProjectSampleId);
        TableColumn lProjectId = new VarcharColumn("project_id", 50);
        lProjectId.setForeignKey(projectTable,projectTable.getPrimaryColumn());
        addColumn(lProjectId);
        TableColumn lSampleId = new VarcharColumn("sample_id", 50);
        lSampleId.setForeignKey(sampleTable,sampleTable.getPrimaryColumn());
        addColumn(lSampleId);
        mySampleTable = sampleTable;
        myProjectTable = projectTable;
        myRow=preparedRow();
        addRow(myRow);
    }    

}
