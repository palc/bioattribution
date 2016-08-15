package biosampleparser;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

/**
 * 
 * @author WCHANG
 */
public class BioSampleParser {
    private Document myDocument;
    private Properties myProperties;
    
    // What follows is a list of tables which are defined in BioAttributionDB
    // Enterprise Architect file.  Each table typically has a corresponding
    // Row which represents the values.  This parser currently interprets a
    // single record from an XML file and makes all necessary updates
    // 1 row at a time.
    // After inserting the row, the row is cleared for subsequent records
    // from the XML file.
    
    private Owner_Table myOwner_Table;
    private Sample_Table mySample_Table;
    
    private Project_Table myProject_Table;
    private Project_Sample_Table myProject_Sample_Table;
    private Project_Publication_Table myProject_Publication_Table;
    
    private Cross_Reference_Table myCross_Reference_Table;
    
    private Submitter_Table mySubmitter_Table;
    
    private Collection_Table myCollection_Table;
    
    private Collection_Owner_Table myCollection_Owner_Table;
   
    private Human_Host_Table myHuman_Host_Table;
    
    private Non_Human_Host_Table myNon_Human_Host_Table;
    
    private Study_Method_Table myStudy_Method_Table;
    // The JDBC connection.
    private Connection myConnection;
    
    // The statement used to execute queries.
    private Statement myStatement;
    
 
    /**
     * initialize.  Creates all the table objects which are used for parsing
     *              the BioSample XML Format.
     */
    private void initialize(Hashtable<String,String> geoMapper,
                            Properties prop) {
        
        myProject_Table = new Project_Table();
        mySample_Table = new Sample_Table();
        myProject_Sample_Table = 
                new Project_Sample_Table(myProject_Table,mySample_Table);
        myOwner_Table= new Owner_Table(mySample_Table,myProject_Table);
        
        
        myCross_Reference_Table = new Cross_Reference_Table(mySample_Table);
        
        mySubmitter_Table = new Submitter_Table(myOwner_Table,mySample_Table);

        myCollection_Table = new Collection_Table(mySample_Table,geoMapper);

        myCollection_Owner_Table = new Collection_Owner_Table(myCollection_Table,
                                                          myOwner_Table);

        myProject_Publication_Table = 
                new Project_Publication_Table(myProject_Table);
        myHuman_Host_Table = new Human_Host_Table(mySample_Table);
        
        myNon_Human_Host_Table = new Non_Human_Host_Table(mySample_Table);
        
        myStudy_Method_Table = new Study_Method_Table(mySample_Table);
        myProperties = prop;
    }

     /**
     * Loads the XML File into the DOM.
     */
    private void parseXmlFile(String filename){
        //get the factory
        DocumentBuilderFactory lDocumentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {
            //Using factory get an instance of document builder
            DocumentBuilder lDocumentBuilder = lDocumentBuilderFactory.newDocumentBuilder();
            myDocument=lDocumentBuilder.parse(filename);
        }catch(  ParserConfigurationException pce) {
            myDocument = null;
            pce.printStackTrace();
        }catch (SAXException se) {
            myDocument = null;
            se.printStackTrace();
        }catch (IOException ioe) {
            myDocument = null;
            ioe.printStackTrace();
        }
    }

    /**
     * Pulls items out of the XML document.
     */
    private void parseBioSampleDebug(){
        //get the root element
        if(myDocument != null) {
            Element lRootElement = myDocument.getDocumentElement();
            NodeList lNodeList = lRootElement.getElementsByTagName("BioSample");
            StringBuffer lBuffer = new StringBuffer();
            if(lNodeList != null && lNodeList.getLength() > 0) {
                for(int i = 0 ; i < lNodeList.getLength();i++) {
                    Element lBiosampleElement = (Element)lNodeList.item(i);
                    lBuffer.append(mySample_Table.parseBioSampleXML(lBiosampleElement));
                    lBuffer.append(myHuman_Host_Table.parseBioSampleXML(lBiosampleElement));
                    lBuffer.append(myNon_Human_Host_Table.parseBioSampleXML(lBiosampleElement));
                    StringBuffer lCollectionTableBuffer = myCollection_Table.parseBioSampleXML(lBiosampleElement);
                    lBuffer.append(lCollectionTableBuffer);
                    lBuffer.append(mySubmitter_Table.parseBioSampleXML(lBiosampleElement));
                    lBuffer.append(myOwner_Table.parseBioSampleXML(lBiosampleElement,
                            mySubmitter_Table,myCollection_Owner_Table,lCollectionTableBuffer));
                    lBuffer.append(myCross_Reference_Table.parseBioSampleXML(lBiosampleElement));
                    lBuffer.append(myStudy_Method_Table.parseBioSampleXML(lBiosampleElement));

                    try {
                        myStatement.execute(lBuffer.toString());
                    } catch (SQLException sqle) {
                        sqle.printStackTrace(System.out);
                    }
                    lBuffer.setLength(0);
                }
            }
        }
    }
    
    private void parseBioProjectDebug(){
        //get the root element
        if(myDocument != null) {
            Element lRootElement = myDocument.getDocumentElement();
            NodeList lNodeList = lRootElement.getElementsByTagName("Package");
            StringBuffer lBuffer = new StringBuffer();
            if(lNodeList != null && lNodeList.getLength() > 0) {
                for(int i = 0 ; i < lNodeList.getLength();i++) {
                    System.out.println("Parsing Node "+i);
                    Element lPackageElement = (Element)lNodeList.item(i);
                    lBuffer.append(myProject_Table.parseBioProjectXML(lPackageElement));
                    lBuffer.append(myOwner_Table.parseBioProjectXML(lPackageElement));
                    lBuffer.append(myProject_Publication_Table.parseBioProjectXML(lPackageElement));
                    try {
                        myStatement.execute(lBuffer.toString());
                    } catch (SQLException sqle) {
                        sqle.printStackTrace(System.out);
                    }
                    lBuffer.setLength(0);
                }
            }
        }
    }
    
    private void parseIdMap(String mappingFile){
        try {
            myStatement.execute("copy project_sample (project_id, sample_id) from '" + mappingFile+"'");
        } catch (SQLException sqle) {
            sqle.printStackTrace(System.out);
        }
    }
    private void createTable(TableSQL table) {
        try {
            myStatement.executeUpdate(table.toCreateString());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
    /**
     * For debugging purposes only. (Stopped updating after myCollection_Table)
     */
    public void printCreateTables() {
        System.out.println(mySample_Table.toCreateString());
        System.out.println(myOwner_Table.toCreateString());
        System.out.println(myCross_Reference_Table.toCreateString());
        System.out.println(mySubmitter_Table.toCreateString());
        System.out.println(myCollection_Table.toCreateString());
    }
    
    public void clearDocument() {
	myDocument=null;
    }
    /**
     * Create all the tables via the JDBC Connection.
     */
    public void createTables() {
        createTable(mySample_Table);
        createTable(myProject_Table);
        createTable(myProject_Sample_Table);
        createTable(myProject_Publication_Table);
        createTable(myOwner_Table);
        createTable(myCross_Reference_Table);
        createTable(mySubmitter_Table);
        createTable(myCollection_Table);
        createTable(myCollection_Owner_Table);
        createTable(myHuman_Host_Table);
        createTable(myNon_Human_Host_Table);
        createTable(myStudy_Method_Table);
    }

    private void lockTable(TableSQL table) {
        try{
            myStatement.executeUpdate(table.toExclusiveLockString());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
    /**
     * Lock all the tables via the JDBC Connection.
     */
    public void lockTables() {
        lockTable(mySample_Table);
        lockTable(myOwner_Table);
        lockTable(myCross_Reference_Table);
        lockTable(mySubmitter_Table);
        lockTable(myCollection_Table);
        lockTable(myCollection_Owner_Table);
        lockTable(myHuman_Host_Table);
        lockTable(myNon_Human_Host_Table);
        lockTable(myStudy_Method_Table);
    }
    /**
     * Drops the table from the JDBC connection.
     * @param table  Represents the corresponding table to be dropped.
     */
    private void deleteTable(TableSQL table) {
        try {
            myStatement.executeUpdate(table.toDeleteString());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * Drops the table from the JDBC connection.
     * @param table  Represents the corresponding table to be dropped.
     */
    private void dropTable(TableSQL table) {
        try {
            myStatement.executeUpdate(table.toDropString());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
    /**
     * Drops each table defined by the parser over the JDBC Connection.
     * Usually the bridge tables should be dropped first.
     */
    public void deleteTables() {
        
        deleteTable(myCross_Reference_Table);
        deleteTable(mySubmitter_Table);
        deleteTable(myCollection_Owner_Table);
        deleteTable(myOwner_Table);
        deleteTable(myNon_Human_Host_Table);
        deleteTable(myHuman_Host_Table);
        deleteTable(myCollection_Table);
        deleteTable(myProject_Sample_Table);
        deleteTable(myProject_Publication_Table);
        deleteTable(myProject_Table);
        deleteTable(myStudy_Method_Table);
        deleteTable(mySample_Table);
        
    }
    
    /**
     * Drops each table defined by the parser over the JDBC Connection.
     * Usually the bridge tables should be dropped first.
     */
    public void dropTables() {
        
        dropTable(myCross_Reference_Table);
        dropTable(mySubmitter_Table);
        dropTable(myCollection_Owner_Table);
        dropTable(myOwner_Table);
        dropTable(myNon_Human_Host_Table);
        dropTable(myHuman_Host_Table);
        dropTable(myCollection_Table);
        dropTable(myProject_Publication_Table);
        dropTable(myStudy_Method_Table);
        dropTable(myProject_Sample_Table);
        dropTable(mySample_Table);
        dropTable(myProject_Table);
        
    }
    
    /**
     * Establishes the JDBC Connection to the database.
     */
    public void connectToDatabase() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {

                System.out.println("Where is your PostgreSQL JDBC Driver? "
                                + "Include in your library path!");
                e.printStackTrace();
                return;

        }
        Connection connection = null;
        try {
            String lServer=myProperties.getProperty("SERVERNAME");
            String lDb=myProperties.getProperty("DATABASE_NAME");
            String lUsername=myProperties.getProperty("USERNAME");
            String lPassword=myProperties.getProperty("PASSWORD");
            connection = DriverManager.getConnection(
                "jdbc:postgresql://"+lServer+"/"+lDb,lUsername, lPassword);
            connection.setAutoCommit(false);
        } catch (SQLException sqle) {
            System.out.println("Connection failed!  Check output console!");
            sqle.printStackTrace();
            return;
        }
        if(connection !=null ) {
            myConnection=connection;
            try {
                myStatement = myConnection.createStatement();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
            System.out.println("Connected.");
        } else {
            System.out.println("Failed to connect!");
        }
        
    }
    
    /**
     * Closes the JDBC Connection to the Database.
     */
    public void closeConnectionToDatabase() {
        if(myConnection != null) {

            try {
                myConnection.commit();
                if(myStatement != null) {
                    myStatement.close();
                    myStatement = null;
                }
                myConnection.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } finally {
                myStatement=null;
                myConnection=null;
            }
            
        }
    }
    
    /**
     * Default constructor which initializes everything.
     */
    public BioSampleParser(Hashtable<String,String> geoMapper,Properties prop) {
        initialize(geoMapper,prop);
    }

    public static void usage() {
        System.out.println("usage: BioSampleParser [rebuilddb]");
        System.out.println("Drops all tables and re-creates all tables in the schema.");
        System.out.println("       or");
        System.out.println("       BioSampleParser [loadnewdata] <samplenum> <sampleprefix> <projectnum> <projectprefix> <mappath>");
        System.out.println("Overwrites all existing data in the database and loads the data from the files specified.");        System.out.println("where  <samplenum> is the number of XML files containing BioSample XML data");
        System.out.println("       <sampleprefix> is the absolute path and prefix of XML files containing BioSample XML data");
        System.out.println("       The files would be named <sampleprefix>0.xml thru <sampleprefix><samplenum>-1.xml");
        System.out.println("       <projectnum> is the number of XML files containing BioProject XML data");
        System.out.println("       <projectprefix> is the absolute path and prefix of XML files containing BioProject XML data");
        System.out.println("       The files would be named <projectprefix>0.xml thru <projectprefix><projectnum>-1.xml");
        System.out.println("       <mappath> is the file containing the mapping between BioProject ids and BioSample ids.");

        
    }
    public static Hashtable<String,String> loadGeoMapper(String filename) {
        Hashtable<String,String> lGeoMapper = new Hashtable<String,String>();
        try {
            BufferedReader lReader = new BufferedReader(new FileReader(filename));
            String lLine=lReader.readLine();
            while(lLine != null) {
               String []lKey_and_Value = lLine.split("\t");
               String lKey=lKey_and_Value[0];
               String lValue=lKey_and_Value[1];
               lGeoMapper.put(lKey,lValue);
               lLine=lReader.readLine();
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
            lGeoMapper.clear();
        }
        return lGeoMapper; 
    }

    public static Properties loadProperties(String filename) {
        Properties lProp = new Properties();
        try {
            lProp.load(new FileReader(filename)); 
        } catch(IOException ioe) {
            ioe.printStackTrace();
            lProp = null;
        }
        return lProp; 
    }
    /**
     * Connects to the database, drops all existing tables,
     * creates all necessary tables, reads a Biosample XML file into
     * a DOM structure, and parses the DOM structure into a series of
     * SQL statments which are used to update the database.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String lFunction = args[0];
        if("loadnewdata".equals(lFunction)) {
            try {
                int lNumOfBioSampleFiles = Integer.parseInt(args[1]);
                String lBioSampleFileName = args[2];
                int lNumOfBioProjectFiles = Integer.parseInt(args[3]);
                String lBioProjectFileName = args[4];
                String lBioProjectBioSampleIDMapping = args[5];
                String lGeoFileName = args[6];
                Hashtable<String,String> lGeoMapper=loadGeoMapper(lGeoFileName);
                String lConfigFileName = args[7];
                Properties lProp = loadProperties(lConfigFileName);
                BioSampleParser lParser = new BioSampleParser(lGeoMapper,lProp);
                lParser.connectToDatabase();
                lParser.deleteTables();
                for(int i=0;i<lNumOfBioSampleFiles;i++) {
                    String lFileName = lBioSampleFileName +i+".xml";
                    System.out.println("Parsing "+lFileName);
                    lParser.parseXmlFile(lFileName);
                    lParser.parseBioSampleDebug();
	                lParser.clearDocument();
                }
                for(int i=0;i<lNumOfBioProjectFiles;i++) {
                    String lFileName = lBioProjectFileName +i+".xml";
                    System.out.println("Parsing "+lFileName);
                    lParser.parseXmlFile(lFileName);
                    lParser.parseBioProjectDebug();
		    lParser.clearDocument();
                }
                lParser.closeConnectionToDatabase();
                lParser.connectToDatabase();
                lParser.parseIdMap(lBioProjectBioSampleIDMapping);
                lParser.closeConnectionToDatabase();
            } catch (NumberFormatException nfe) {
                System.err.println("Number of file must be a valid integer.");
                System.exit(-1);
            }
        } else  if ("rebuilddb".equals(lFunction)) {
            Hashtable<String,String> lGeoMapper=new Hashtable<String,String>();
            String lConfigFileName = args[1];
            Properties lProp = loadProperties(lConfigFileName);
            BioSampleParser lParser = new BioSampleParser(lGeoMapper,lProp);
            lParser.connectToDatabase();
            lParser.dropTables();
            lParser.createTables();
            lParser.closeConnectionToDatabase();
        } else {
            usage();
        }
    }
}
