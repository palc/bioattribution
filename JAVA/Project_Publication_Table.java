package biosampleparser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author WCHANG
 */
public class Project_Publication_Table extends TableSQL {
    private Project_Table myProjectTable;
    private TableRow myRow;
    Project_Publication_Table(Project_Table projectTable) {
        super("Project_Publication");
        TableColumn lProjectPublicationId = new IntegerColumn("project_publication_id");
        lProjectPublicationId.setAutoIncrement();
        lProjectPublicationId.makePrimaryKey();
        addColumn(lProjectPublicationId);
        TableColumn lProjectId = new VarcharColumn("project_id", 50);
        lProjectId.setForeignKey(projectTable,projectTable.getPrimaryColumn());
        addColumn(lProjectId);
        TableColumn lPublicationId = new VarcharColumn("publication_id", 50);
        addColumn(lPublicationId);
        myProjectTable = projectTable;
        myRow=preparedRow();
        addRow(myRow);     
    }
    
    
    /**
     * Examines a RootElement and parses out the values which correspond to
     * a single row in the Project_Publication table.
     * @param bioProjectElement
     * @return A SQL String which inserts the values from XML into a row in 
     * the Project_Publication table.
     */
    StringBuffer parseBioProjectXML(Element bioProjectElement) {
        myRow.clear();
        StringBuffer lBuffer = new StringBuffer();
        
        Element lArchiveElement =  
                (Element)bioProjectElement.getElementsByTagName("ArchiveID").item(0);
        myRow.pullAttributeWithPrefix("project_id",
                                      lArchiveElement,"id",
                                      "BioProject");
        Element lProjectDescriptionElement =  
                (Element)bioProjectElement.getElementsByTagName("ProjectDescr").item(0);
        NodeList lPublicationList =  
                lProjectDescriptionElement.getElementsByTagName("Publication");
        for(int i=0;i<lPublicationList.getLength();i++) {
            if(myRow.pullAttribute("publication_id", (Element)lPublicationList.item(i), "id")) {
              lBuffer.append(toInsertReturningString());
            }
        }
        
        return lBuffer;
    }
}
