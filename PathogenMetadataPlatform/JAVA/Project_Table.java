package biosampleparser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author WCHANG
 */
public class Project_Table extends TableSQL {
    private TableRow myRow;
    public Project_Table() {
        super("Project");
        TableColumn lProjectId = new VarcharColumn("project_id", 50);
        lProjectId.makePrimaryKey();
        addColumn(lProjectId);
        addColumn(new TextColumn("project_name"));
        addColumn(new TextColumn("project_title"));
        addColumn(new TextColumn("description"));
        addColumn(new TimestampColumn("release_date"));
        addColumn(new VarcharColumn("ncbiproject_id", 50));
        addColumn(new VarcharColumn("target_capture", 100));
        addColumn(new VarcharColumn("target_material", 100));        
        addColumn(new VarcharColumn("sample_scope", 100));
        addColumn(new VarcharColumn("method_type", 100));
        addColumn(new VarcharColumn("organism_species", 255));
        addColumn(new VarcharColumn("taxid", 50));
        addColumn(new VarcharColumn("organism_name", 255));
        addColumn(new VarcharColumn("strain", 255));
        addColumn(new VarcharColumn("supergroup", 255));
        
        myRow=preparedRow();
        addRow(myRow);

    }
    
    /**
     * Examines a RootElement and parses out the values which correspond to
     * a single row in the Project table.
     * @param bioProjectElement
     * @return A SQL String which inserts the values from XML into a row in 
     * the Project table.
     */
    StringBuffer parseBioProjectXML(Element bioProjectElement) {
        myRow.clear();
        
        Element lArchiveElement =  
                (Element)bioProjectElement.getElementsByTagName("ArchiveID").item(0);
        myRow.pullAttributeWithPrefix("project_id",
                                      lArchiveElement,"id",
                                      "BioProject");
        myRow.pullAttribute("ncbiproject_id",lArchiveElement,"id");
        Element lProjectDescriptionElement =  
                (Element)bioProjectElement.getElementsByTagName("ProjectDescr").item(0);
        Element lNameElement = 
                (Element)lProjectDescriptionElement.getElementsByTagName("Name").item(0);
        myRow.pullValue("project_name", lNameElement);
        
        Element lTitleElement =
                (Element)lProjectDescriptionElement.getElementsByTagName("Title").item(0);
        myRow.pullValue("project_title",lTitleElement);
        
        Element lDescriptionElement =
                (Element)lProjectDescriptionElement.getElementsByTagName("Description").item(0);
        myRow.pullValue("description",lDescriptionElement);
        Element lReleaseDate=
                (Element)lProjectDescriptionElement.getElementsByTagName("ProjectReleaseDate").item(0);
        myRow.pullValue("release_date",lReleaseDate);
        Element lProjectTypeElement =  
                (Element)bioProjectElement.getElementsByTagName("ProjectType").item(0);
        Element lTargetElement=
                (Element)lProjectTypeElement.getElementsByTagName("Target").item(0);
        myRow.pullAttribute("target_capture",lTargetElement,"capture");
        myRow.pullAttribute("target_material", lTargetElement,"material");
        myRow.pullAttribute("sample_scope", lTargetElement,"sample_scope");
        Element lOrganismElement=
                (Element)lProjectTypeElement.getElementsByTagName("Organism").item(0);
        if(lOrganismElement != null) {
            myRow.pullAttribute("organism_species", lOrganismElement, "species");
            myRow.pullAttribute("taxid", lOrganismElement, "taxID");
            Element lOrganismNameElement=
                    (Element)lOrganismElement.getElementsByTagName("OrganismName").item(0);
            myRow.pullValue("organism_name",lOrganismNameElement);
            Element lStrainElement=
                    (Element)lOrganismElement.getElementsByTagName("Strain").item(0);
            myRow.pullValue("strain",lStrainElement);
            Element lSupergroupElement=
                    (Element)lOrganismElement.getElementsByTagName("Supergroup").item(0);
            myRow.pullValue("supergroup",lSupergroupElement);
        }
        
        Element lMethodElement=
                (Element)lProjectTypeElement.getElementsByTagName("Method").item(0);
        myRow.pullAttribute("method_type", lMethodElement, "method_type");
                
        return toInsertReturningString();
    }
}
