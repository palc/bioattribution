package biosampleparser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.Hashtable;
import java.util.Date;
import java.util.regex.*;

/**
 *
 * @author WCHANG
 */
public class Collection_Table extends TableSQL {
    private TableRow myRow;
    private Sample_Table mySample_Table;
    private Hashtable<String,String> myGeoMapper;
    private Pattern myCollectionYearPattern;
    public Collection_Table(Sample_Table sampleTable,Hashtable<String,String> geoMapper) {
        super("Collection");
        mySample_Table=sampleTable;
        TableColumn lCollectionId = new IntegerColumn("collection_id");
        lCollectionId.makePrimaryKey();
        lCollectionId.setAutoIncrement();
        addColumn(lCollectionId);
        TableColumn lSampleId = new VarcharColumn("sample_id",50);
        lSampleId.setForeignKey(sampleTable,sampleTable.getPrimaryColumn());
        addColumn(lSampleId);
        addColumn(new TextColumn("lat_lon"));
        addColumn(new VarcharColumn("country",255));
        addColumn(new VarcharColumn("geo_loc_name", 255));
	addColumn(new VarcharColumn("geo_loc_name_mod", 255));
        addColumn(new VarcharColumn("collection_date",50));
        addColumn(new VarcharColumn("collection_year",4));
        addColumn(new TextColumn("environment_type"));
        addColumn(new VarcharColumn("collection_site",100));
        addColumn(new VarcharColumn("transmission_mode",100));
        addColumn(new VarcharColumn("notes",250));
        addColumn(new VarcharColumn("geocode_lat", 15));
        addColumn(new VarcharColumn("geocode_lon", 15));
        addColumn(new VarcharColumn("geocode_address", 255));
        myGeoMapper = geoMapper; 
        myRow=preparedRow();
        addRow(myRow);
        myCollectionYearPattern=Pattern.compile("[0-9]{4}");
    }
    
   /**
     * Examines a RootElement and parses out the values which correspond to
     * a single row in the Collection table.
     * @param biosampleRootElement
     * @return A SQL String which inserts the values from XML into a row in 
     * the Collection table.
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
        for(int i=0;i<=lAttributes.getLength();i++) {

            Element lAttributeNode = (Element)lAttributes.item(i);
            if(lAttributeNode != null) {
                String lHarmonizedNodeName = lAttributeNode.getAttribute("harmonized_name");
                String lAttributeNodeName = lAttributeNode.getAttribute("attribute_name");
 
                if("geo_loc_name".equals(lHarmonizedNodeName)) {
                    lFoundSampleData = myRow.pullValue("geo_loc_name",lAttributeNode) | lFoundSampleData;
                    String lGeoLocName = lAttributeNode.getFirstChild().getNodeValue();
                    String lGeoLocMod = myGeoMapper.get(lGeoLocName);
                    if(lGeoLocMod != null) {
                        myRow.setItem("geo_loc_name_mod",lGeoLocMod);
                    }
                }
                if("lat_lon".equals(lHarmonizedNodeName)) {
                    lFoundSampleData= myRow.pullValue("lat_lon",lAttributeNode) | lFoundSampleData;
                }
                if("body_sites".equals(lAttributeNodeName)) {
                    lFoundSampleData = myRow.pullValue("collection_site",lAttributeNode) | lFoundSampleData;
                }
                if("collection_date".equals(lHarmonizedNodeName)) {
                    if(lAttributeNode != null) {
                        if(lAttributeNode.getFirstChild() != null) {
                            String lDate = lAttributeNode.getFirstChild().getNodeValue();
                            if(lDate != null) {
                                myRow.setItem("collection_date",lDate);
                                lFoundSampleData=true;
                                String lYear = getYear(lDate);
                                if(!"undefined".equals(lYear)) {
                                    myRow.setItem("collection_year",lYear);
                                }
                            }
                        }
                    }
                }
                if("environment".equals(lAttributeNodeName)) {
                    lFoundSampleData = myRow.pullValue("environment_type",lAttributeNode) | lFoundSampleData;
                }
             
            }
        }
        
        if(lFoundSampleData) {
            lBuffer.append(toInsertReturningString());
        }
        return lBuffer;

    }
    public String getYear(String lDate) {
        String lYear = "undefined";
        if(lDate.length() >= 4) {
            String lFirstFour = lDate.substring(0,4);
            String lLastFour = lDate.substring((lDate.length()-4),lDate.length());
            Matcher lMatcher = myCollectionYearPattern.matcher(lLastFour);
            if(lMatcher.matches()) {
                lYear=lLastFour;
            } else {
                lMatcher = myCollectionYearPattern.matcher(lFirstFour);
                if(lMatcher.matches()) {
                    lYear=lFirstFour;
                }else {
                    for(int i=lDate.length()-5;i>=0;i--) {
                         
                        // Check for formats like
                        // something.04.03 -> 03 is the last 2 of the year
                        // something/04/03 -> 03 is the last 2 of the year
                        // something-3-03 -> 03 is the last 2 of the year
                        if((lDate.charAt(i)==lLastFour.charAt(1)) &&
                           (lLastFour.charAt(1)=='/' || lLastFour.charAt(1)=='-'
                           || lLastFour.charAt(1)=='.' || lLastFour.charAt(1)==' ')) {
                            
                            // Assumes 00-19 are in the 2000s
                            // 20-99 are in the 1900s
                            if(lLastFour.charAt(2)<'2') {
                                lYear="20"+lLastFour.substring(2);
                            } else {
                                lYear="19"+lLastFour.substring(2);
                            }
                        }
                    }
                }
            }
        }
        return lYear;
    }
    public static void main(String []args) {
        Sample_Table lSampleTable = new Sample_Table();
        Hashtable<String,String> lHashTable = new Hashtable<String,String>();
        Collection_Table lCollectionTable = 
            new Collection_Table(lSampleTable,lHashTable);
        for(int i=0;i<args.length; i++) {
            System.out.println(args[i]+" "+lCollectionTable.getYear(args[i]));
        }     
    }
}
