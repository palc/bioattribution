package biosampleparser;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.Hashtable;
/**
 *
 * @author WCHANG
 */
public class TableSQL {
    private String myTableName;
    private ArrayList<TableColumn> myColumns;
    private ArrayList<TableRow> myRows;
    
    public TableSQL(String name) {
        myTableName = name;
        myColumns=new ArrayList<TableColumn>();
        myRows = new ArrayList<TableRow>();
    }


    /**
     * addRow. Adds another row to the current table
     * @param row  A row to add to the table
     */
    public void addRow(TableRow row) {
        myRows.add(row);
    }

    /**
     * clearRows. Clears all the rows from the table.
     */
    public void clearRows() {
        myRows.clear();
    }

    /**
     * addColumn. Adds another column to the table
     * @param column  A column to add to the table
     */
    public void addColumn(TableColumn columnName) {
        myColumns.add(columnName);
    }

    /**
     * preparedRow.  Creates a row containing all the columns in the 
     * table, and returns that row for use
     * @return  A row containing all the columns in the table
     */
    public TableRow preparedRow() {
        TableRow lRow = new TableRow();
        
        for(int c=0;c<myColumns.size();c++) {
            TableColumn lColumn = myColumns.get(c);
            lRow.addColumn(lColumn);
        }
        return lRow;       
    }
    
    /**
     * getPrimaryColumn.  Returns the column in the table which is a primary 
     * key 
     * @return The column which has been designated as a primary key or null
     * if no primary key has been specified.
     */
    public TableColumn getPrimaryColumn() {
        TableColumn lPrimaryColumn = null;
        for(int c=0;c<myColumns.size();c++) {
            TableColumn lColumn = myColumns.get(c);
            if(lColumn.isPrimaryKey()) {
                lPrimaryColumn = lColumn;
            }

        }
        return lPrimaryColumn;       
    }

    /**
     * toDropString. Returns SQL which drops the table
     * @return  An SQL string which drops the table
     */
    public String toDropString() {
        StringBuffer lBuffer = new StringBuffer();
        lBuffer.append("DROP TABLE IF EXISTS ");
        lBuffer.append(myTableName);
        lBuffer.append(";");
        return lBuffer.toString();
    }

    /**
     * toDeleteString. Returns SQL to delete all rows from table
     * @return  SQL to delete all rows from table
     */
    public String toDeleteString() {
        StringBuffer lBuffer = new StringBuffer();
        lBuffer.append("DELETE FROM ");
        lBuffer.append(myTableName);
        lBuffer.append(";");
        return lBuffer.toString();
    }

    /**
     * toInsertString. Returns SQL to insert all values in the rows into the
     * table
     * @return  returns SQL to insert all values in the rows into the table
     */
    public String toInsertString() {
        StringBuffer lBuffer = new StringBuffer();
        if(myRows.size()>0) {
            lBuffer.append("INSERT INTO ");
            lBuffer.append(myTableName);
            lBuffer.append(" (");
            int c=0;
            for(;c<myColumns.size()-1;c++) {
                TableColumn lColumn = myColumns.get(c);
                String lColumnName = lColumn.getName();
                lBuffer.append(lColumnName);
                lBuffer.append(",");
            }
            TableColumn lColumn = myColumns.get(c);
            String lColumnName = lColumn.getName();
            lBuffer.append(lColumnName);
            lBuffer.append(") VALUES\n");
            
            int i=0;
            for(;i<myRows.size()-1;i++) {
                TableRow lRow = myRows.get(i);
                lBuffer.append(lRow.toValueString());
                lBuffer.append(",");
                lBuffer.append("\n");
            }
            TableRow lRow = myRows.get(i);
            lBuffer.append(lRow.toValueString());
            lBuffer.append(";");
            lBuffer.append("\n");
            
        } else {
          lBuffer.append("");
        }
        return lBuffer.toString();
    }

    /**
     * getName. Returns the name of the table
     * @return   the name of the table
     */
    public String getName() {
        return myTableName;
    }

    /**
     * toCreateString.  Returns SQL to create the table
     * @return  SQL to create the table
     */
    public String toCreateString() {
        StringBuffer lBuffer = new StringBuffer();
        lBuffer.append("CREATE TABLE ");
        lBuffer.append(myTableName);
        lBuffer.append(" (\n");
        int c=0;
        TableColumn lColumn = null;
        for(;c<myColumns.size()-1;c++) {
            lColumn = myColumns.get(c);
            lBuffer.append(lColumn.toCreateString());
            lBuffer.append(",\n");
        }
        lColumn = myColumns.get(c);
        lBuffer.append(lColumn.toCreateString());
        lBuffer.append("\n);");
        
        for(c=0;c<myColumns.size();c++) {
            lColumn = myColumns.get(c);
            if(lColumn.isForeignKey()) {
                lBuffer.append("CREATE INDEX ");
                lBuffer.append(getName());
                lBuffer.append("_");
                lBuffer.append(lColumn.getName());
                lBuffer.append("_");
                lBuffer.append("idx ON ");
                lBuffer.append(getName());
                lBuffer.append(" (");
                lBuffer.append(lColumn.getName());
                lBuffer.append(");\n");
            }
        }
        return lBuffer.toString();
    }
    

    /**
     * toExclusiveLockString.  Returns SQL to lock the table
     * @return  SQL to lock the table
     */
    public String toExclusiveLockString() {
        StringBuffer lBuffer = new StringBuffer();
        lBuffer.append("LOCK TABLE ");
        lBuffer.append(myTableName);
        lBuffer.append(" IN ACCESS EXCLUSIVE MODE;");
        return lBuffer.toString();
    }
    

    /**
     * toDeclarePrimaryKeyVariable.  Returns SQL for the primary key variable
     * @return SQL for the primary key variable
     */
    public String toDeclarePrimaryKeyVariable() {
        StringBuffer lBuffer = new StringBuffer();
        TableColumn lReturningColumn=null;
        for(int c=0;c<myColumns.size();c++) {
            TableColumn lColumn = myColumns.get(c);
            String lColumnName = lColumn.getName();
            if(lColumn.isPrimaryKey()) {
                lReturningColumn=lColumn;
            }
        }
        lBuffer.append("var_");
        lBuffer.append(lReturningColumn.getName());
        lBuffer.append(" bigint;");
        return lBuffer.toString();
    }
    

    /**
     * toReferencePrimaryKeyVariable.  Returns a reference to the primary key
     * @return  A reference to the value of the primary key
     */
    public String toReferencePrimaryKeyVariable() {
        StringBuffer lBuffer = new StringBuffer();
        TableColumn lReturningColumn=null;
        for(int c=0;c<myColumns.size();c++) {
            TableColumn lColumn = myColumns.get(c);
            if(lColumn.isPrimaryKey()) {
                lReturningColumn=lColumn;
            }
        }
        if(lReturningColumn.isAutoIncrement()) {
            lBuffer.append("currval('");
            lBuffer.append(myTableName.toLowerCase());
            lBuffer.append("_");
            lBuffer.append(lReturningColumn.getName().toLowerCase());
            lBuffer.append("_seq')");
        } else {
            lBuffer.append("DEFAULT");
        }
        return lBuffer.toString();
    }
    

    /**
     * toInsertReturningString. Returns a value containing all values to
     * be inserted into the table
     * @return  SQL with insert command for the rows contained in the table
     */
    public StringBuffer toInsertReturningString() {
        StringBuffer lBuffer = new StringBuffer();
        if(myRows.size()>0) {
            lBuffer.append("INSERT INTO ");
            lBuffer.append(myTableName);
            lBuffer.append(" (");
            int c=0;
            for(;c<myColumns.size()-1;c++) {
                TableColumn lColumn = myColumns.get(c);
                String lColumnName = lColumn.getName();
                lBuffer.append(lColumnName);
                lBuffer.append(",");
            }
            TableColumn lColumn = myColumns.get(c);
            String lColumnName = lColumn.getName();
            lBuffer.append(lColumnName);
            lBuffer.append(") VALUES\n");
            
            int i=0;
            for(;i<myRows.size()-1;i++) {
                TableRow lRow = myRows.get(i);
                lBuffer.append(lRow.toValueString());
                lBuffer.append(",");
                lBuffer.append("\n");
            }
            TableRow lRow = myRows.get(i);
            lBuffer.append(lRow.toValueString());
            lBuffer.append(";");
            lBuffer.append("\n");
            
        } else {
          lBuffer.append("");
        }
        return lBuffer;
    }

    private static TableRow table1Row(String submitter,String datasourceid,String submitDate) {
        TableRow lRow = new TableRow();
        lRow.addColumn(new VarcharColumn("Submitter",5));
        lRow.addColumn(new IntegerColumn("DataSourceId"));
        lRow.addColumn(new TimestampColumn("SubmitDate"));
        lRow.setItem("Submitter",submitter);
        lRow.setItem("DataSourceId",datasourceid);
        lRow.setItem("SubmitDate",submitDate);
        return lRow;
    }

    private static TableRow table1IncrementRow(String submitter,String submitDate) {
        TableRow lRow = new TableRow();
        lRow.addColumn(new VarcharColumn("Submitter",5));
        lRow.addColumn(new TimestampColumn("SubmitDate"));
        lRow.setItem("Submitter",submitter);
        lRow.setItem("SubmitDate",submitDate);
        return lRow;
    }

    public static void main(String[] args) {
        TableSQL lTable = new TableSQL("DataSource");
        IntegerColumn lDataSourceId = new IntegerColumn("DataSourceId");
        lDataSourceId.makePrimaryKey();
        lDataSourceId.setAutoIncrement();
        lTable.addColumn(new VarcharColumn("Submitter",5));
        lTable.addColumn(lDataSourceId);
        lTable.addColumn(new TimestampColumn("SubmitDate"));
        System.out.println(lTable.toInsertReturningString());
        lTable.addRow(table1Row("Whatever","234234","2012-03-24"));
        System.out.println(lTable.toInsertReturningString());
        lTable.addRow(table1Row("Whatever","234234","2012-03-25"));
        System.out.println(lTable.toInsertReturningString());
        lTable.addRow(table1Row("Whatever","234234","2012-03-25"));
        System.out.println(lTable.toInsertString());
        lTable.addRow(table1IncrementRow("Whatever","happy"));
        System.out.println(lTable.toInsertString());
        System.out.println(lTable.toInsertReturningString());
    }
}
