package biosampleparser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.regex.*;

/**
 *
 * @author WCHANG
 */
public class Sample_Table extends TableSQL {
    private TableRow myRow;
    private Pattern myTaxonomySerovarPattern;
    private Pattern myTaxonomyStrainPattern; 
    
    public Sample_Table() {
        super("Sample");
        TableColumn lSampleId = new VarcharColumn("sample_id",50);
        lSampleId.makePrimaryKey();
        addColumn(lSampleId);
        addColumn(new VarcharColumn("organism",100));
        addColumn(new VarcharColumn("isolate",255));
        addColumn(new TextColumn("isolation_source"));
        addColumn(new TextColumn("strain_name"));
        addColumn(new TextColumn("computed_strain_name"));
        addColumn(new VarcharColumn("serovar",255));
        addColumn(new VarcharColumn("computed_serovar",255));
        addColumn(new VarcharColumn("pathovar",255));
        addColumn(new VarcharColumn("ncbi_taxon_id",255));
        addColumn(new VarcharColumn("collected_by",255));
        
        addColumn(new VarcharColumn("datasource",30));
        addColumn(new TextColumn("sample_project_title"));
        addColumn(new TimestampColumn("submission_date"));
        addColumn(new VarcharColumn("access",50));
        addColumn(new VarcharColumn("accession_id",50));
        addColumn(new VarcharColumn("SRA",50));
        myRow=preparedRow();
        addRow(myRow);
        myTaxonomySerovarPattern=Pattern.compile(".*?(?:serovar\\s)((?:(?!\\sstr).)*)");
        myTaxonomyStrainPattern=Pattern.compile(".*?(?:str.\\s)(.*)");
    }
    
    /**
     * Examines a RootElement and parses out the values which correspond to
     * a single row in the Sample table.
     * @param biosampleElement
     * @return A SQL String which inserts the values from XML into a row in 
     * the Sample table.
     */
    public StringBuffer parseBioSampleXML(Element biosampleRootElement) {
        myRow.clear();
        myRow.pullAttributeWithPrefix("sample_id",
                                      biosampleRootElement,"id",
                                      "BioSample");
        StringBuffer lBuffer = new StringBuffer();
        
        //Parse the XML
        NodeList lAttributes = biosampleRootElement.getElementsByTagName("Attribute");
        boolean lFoundSampleData=false;
        boolean lFoundSerovar=false;
        boolean lFoundStrain=false;
        myRow.setItem("datasource","BioSample");
        myRow.pullAttribute("submission_date",biosampleRootElement,"submission_date");
        
        
        String lAccessString = biosampleRootElement.getAttribute("access");
        if(lAccessString != null) {
            myRow.setItem("access",lAccessString);
        }
        
        NodeList lTitles = biosampleRootElement.getElementsByTagName("Title");
        Element lTitleNode = (Element)lTitles.item(0);
        lFoundSampleData = myRow.pullValue("sample_project_title",lTitleNode)
                           || lFoundSampleData;

        
        lFoundSampleData = myRow.pullAttribute("accession_id",
                                               biosampleRootElement,"id")
                           || lFoundSampleData;
        NodeList lIDs = biosampleRootElement.getElementsByTagName("Id");
        for(int i=0;i<lIDs.getLength();i++)
        {
            Element lIDNode = (Element)lIDs.item(i);
            String lID_dbAttribute = lIDNode.getAttribute("db");
            if("SRA".equals(lID_dbAttribute)) {
                lFoundSampleData = myRow.pullValue("SRA",lIDNode) ||
                           lFoundSampleData;
            }
        }

        for(int i=0;i<=lAttributes.getLength();i++) {

            Element lHarmonizedNode = (Element)lAttributes.item(i);
            if(lHarmonizedNode != null) {
                String lHarmonizedNodeName = lHarmonizedNode.getAttribute("harmonized_name");
 
                if("pathovar".equals(lHarmonizedNodeName)) {
                    lFoundSampleData=myRow.pullValue("pathovar",lHarmonizedNode)
                            ||lFoundSampleData;
                }
                if("serovar".equals(lHarmonizedNodeName)) {
                    lFoundSerovar=true;
                    lFoundSampleData=myRow.pullValue("serovar",lHarmonizedNode)
                            ||lFoundSampleData;
                }

                if("strain".equals(lHarmonizedNodeName)) {
                    lFoundStrain=true;
                    lFoundSampleData=myRow.pullValue("strain_name",lHarmonizedNode)
                            ||lFoundSampleData;
                }
                if("isolation_source".equals(lHarmonizedNodeName)) {
                    lFoundSampleData=myRow.pullValue("isolation_source",lHarmonizedNode)
                            ||lFoundSampleData;
                }
                             
                // May not be the right column; this may actually be the strain name
                if("isolate".equals(lHarmonizedNodeName)) {
                    lFoundSampleData=myRow.pullValue("isolate",lHarmonizedNode);
                }
                if("collected_by".equals(lHarmonizedNodeName)) {
                    lFoundSampleData=myRow.pullValue("collected_by",lHarmonizedNode);
                }
            }
        }
        
        NodeList lOrganismNodeList = biosampleRootElement.getElementsByTagName("Organism");
        Element lOrganismNode = (Element)lOrganismNodeList.item(0);
        
        lFoundSampleData = myRow.pullAttribute("organism",lOrganismNode,"taxonomy_name")
                || lFoundSampleData;
        lFoundSampleData =  myRow.pullAttribute("ncbi_taxon_id",lOrganismNode,"taxonomy_id")
                || lFoundSampleData;
        if(lFoundSerovar) {
            myRow.copyItem("computed_serovar","serovar");
        } else {
            boolean lFoundAttribute = false;
            if(lOrganismNode != null) {
                String lValue = lOrganismNode.getAttribute("taxonomy_name");
                if(lValue != null) {
                    Matcher lMatcher = myTaxonomySerovarPattern.matcher(lValue);
                    if(lMatcher.matches()) {
                        myRow.setItem("computed_serovar",lMatcher.group(1));
                        lFoundAttribute = true;
                    }
                }
            }
            lFoundSampleData= lFoundAttribute||lFoundSampleData;
        }
        if(lFoundStrain) {
            myRow.copyItem("computed_strain_name","strain_name");
        } else {
            boolean lFoundAttribute = false;
            if(lOrganismNode != null) {
                String lValue = lOrganismNode.getAttribute("taxonomy_name");
                if(lValue != null) {
                    Matcher lMatcher = myTaxonomyStrainPattern.matcher(lValue);
                    if(lMatcher.matches()) {
                        myRow.setItem("computed_strain_name",
                                      lMatcher.group(1));
                        lFoundAttribute = true;
                    }
                }
            }
            lFoundSampleData= lFoundAttribute||lFoundSampleData;
        }
        if(lFoundSampleData) {
            lBuffer.append(toInsertReturningString());
        }
        return lBuffer;
    }

}
