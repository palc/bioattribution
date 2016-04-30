package biosampleparser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author WCHANG
 */
public class Owner_Table extends TableSQL {
    private TableRow myRow;
    public Sample_Table mySample_Table;
    public Project_Table myProject_Table;
    public Owner_Table(Sample_Table sampleTable, Project_Table projectTable) {
        super("Owner");
        mySample_Table=sampleTable;
        myProject_Table = projectTable;
        TableColumn lOwnerId = new IntegerColumn("Owner_id");
        lOwnerId.makePrimaryKey();
        lOwnerId.setAutoIncrement();
        addColumn(lOwnerId);
        TableColumn lSampleId = new VarcharColumn("sample_id",50);
        lSampleId.setForeignKey(mySample_Table,mySample_Table.getPrimaryColumn());
        addColumn(lSampleId);
        
        TableColumn lProjectId = new VarcharColumn("project_id",50);
        lProjectId.setForeignKey(myProject_Table,myProject_Table.getPrimaryColumn());
        addColumn(lProjectId);

        addColumn(new VarcharColumn("fname",255));
        addColumn(new VarcharColumn("mname",50));
        addColumn(new VarcharColumn("lname",255));
        addColumn(new VarcharColumn("title",50));
        addColumn(new VarcharColumn("institution",255));
        addColumn(new VarcharColumn("institution_abbreviation",255));
        addColumn(new VarcharColumn("department",50));
        addColumn(new VarcharColumn("address",50));
        addColumn(new VarcharColumn("city",50));
        addColumn(new VarcharColumn("state",50));
        addColumn(new VarcharColumn("postal_code",50));
        addColumn(new VarcharColumn("country",100));
        addColumn(new VarcharColumn("email",255));
        addColumn(new VarcharColumn("phone",15));
        addColumn(new VarcharColumn("fax",15));
        addColumn(new TextColumn("other_contact_info"));

        myRow=preparedRow();
        addRow(myRow);
    }
    
    /**
     * Examines a RootElement and parses out the values which correspond to
     * a single row in the Owner table.
     * @param biosampleRootElement
     * @return A SQL String which inserts the values from XML into a row in 
     * the Owner table.
     */
    public StringBuffer parseBioSampleXML(Element biosampleRootElement,
                                          Submitter_Table submitterTable,
                                          Collection_Owner_Table collectionOwnerTable,
                                          StringBuffer collectionTableBuffer) {
        myRow.clear();
        myRow.pullAttributeWithPrefix("sample_id",
                                      biosampleRootElement,"id",
                                      "BioSample");

        StringBuffer lBuffer = new StringBuffer();
        //Parse the XML
        NodeList lOwners = biosampleRootElement.getElementsByTagName("Owner");
        Element lOwnerNode = (Element)lOwners.item(0);
        NodeList lOwnerNameList = lOwnerNode.getElementsByTagName("Name");
        Element lNameNode = (Element)lOwnerNameList.item(0);
        myRow.pullValue("institution", lNameNode);
        myRow.pullAttribute("institution_abbreviation", lNameNode, "abbreviation");
                
        NodeList lContacts = biosampleRootElement.getElementsByTagName("Contact");
        for(int i=0;i<=lContacts.getLength();i++) {
            Element lContactNode = (Element)lContacts.item(i);
            if(lContactNode != null) {
                NodeList lContactNameList = lContactNode.getElementsByTagName("Name");
                
                Element lContactNameElement = (Element)lContactNameList.item(0);
                if(lContactNameElement != null) {
                    NodeList lLastNameList=lContactNameElement.getElementsByTagName("Last");
                    myRow.pullValue("lname",(Element)lLastNameList.item(0));
                    NodeList lFirstNameList=lContactNameElement.getElementsByTagName("First");
                    myRow.pullValue("fname",(Element)lFirstNameList.item(0));
                }
                myRow.pullAttribute("email",lContactNode,"email");
                lBuffer.append(toInsertReturningString());
                lBuffer.append(submitterTable.toInsertReturningString());
                if(collectionTableBuffer.length() > 0) {
                    lBuffer.append(collectionOwnerTable.toInsertReturningString());                                              
                }
            }

        }
        return lBuffer;
    }

    public StringBuffer parseBioProjectXML(Element bioProjectElement) {
        myRow.clear();
        Element lArchiveElement =  
            (Element)bioProjectElement.getElementsByTagName("ArchiveID").item(0);
        System.out.println(lArchiveElement.getAttribute("id"));
        myRow.pullAttributeWithPrefix("project_id",
                                      lArchiveElement,"id",
                                      "BioProject");
        Element lOrganizationElement =
                (Element)bioProjectElement.getElementsByTagName("Organization").item(0);
        Element lNameElement = 
                (Element)lOrganizationElement.getElementsByTagName("Name").item(0);
        
        myRow.pullValue("institution", lNameElement);
        myRow.pullAttribute("institution_abbreviation",lNameElement,"abbr");
        
        Element lProjectTypeElement = 
                (Element)bioProjectElement.getElementsByTagName("ProjectType").item(0);
        NodeList lProjectTypeSubmissionList =
                (NodeList)lProjectTypeElement.getElementsByTagName("ProjectTypeSubmission");
        if(lProjectTypeSubmissionList.getLength()>0) {
            Element lProjectTypeSubmissionElement =
                    (Element)lProjectTypeSubmissionList.item(0);
            NodeList lProviderList = 
                    lProjectTypeSubmissionElement.getElementsByTagName("Provider");
            if(lProviderList.getLength() > 0) {
                Element lProvider = (Element)lProviderList.item(0);
                myRow.pullValue("other_contact_info",lProvider);
            }
        }
        return toInsertReturningString();

    }
}
