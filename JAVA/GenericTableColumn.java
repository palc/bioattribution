package biosampleparser;

/**
 *
 * @author WCHANG
 */
public abstract class GenericTableColumn implements TableColumn {
    private String myName;
    private boolean myPrimaryKey;
    private TableSQL myForeignKeyTable;
    private TableColumn myForeignKey;
    private boolean myAutoIncrement;

    
   /**
     * toCreateString.  Returns an SQL string to create this column in
     * a table
     * @return A SQL String which creates this column
     */
    public String toCreateString() {
        StringBuffer lBuffer = new StringBuffer();
        lBuffer.append(myName);
        lBuffer.append(" ");
        if(isAutoIncrement() && isPrimaryKey()) {
            lBuffer.append(" SERIAL PRIMARY KEY");
        } else if (isPrimaryKey()) {
            lBuffer.append(getType());
            lBuffer.append(" UNIQUE");
        } else if(myForeignKey != null) {
            lBuffer.append(getType());
            lBuffer.append(" REFERENCES ");
            lBuffer.append(myForeignKeyTable.getName());
            lBuffer.append("(");
            lBuffer.append(myForeignKey.getName());
            lBuffer.append(")");
        } else {
            lBuffer.append(getType());
        }

        return lBuffer.toString();
    }

   /**
     * isAutoIncrement.  Whether the column is auto increment or not
     * @return true if the column is auto increment, and false otherwise
     */
    public boolean isAutoIncrement() {
        return myAutoIncrement;
    }

   /**
     * setAutoIncrement.  Set the column to be auto increment
     */
    public void setAutoIncrement() {
        myAutoIncrement = true;
    }

   /**
     * removeAutoIncrement.  Set the column to not be automatically incremented
     */
    public void removeAutoIncrement() {
        myAutoIncrement=false;
    }


   /**
     * makePrimaryKey.  Makes this column the primary key column
     */
    public void makePrimaryKey() {
        myPrimaryKey = true;
    }

   /**
     * removePrimaryKey.  Makes this column not a primary key
     */
    public void removePrimaryKey() {
        myPrimaryKey = false;
    }

   /**
     * setForeignKey.  Creates the reference for a foreign key 
     * @param table               the table with the foreign column 
     * @param foreignTableColumn  the foreign column
     */
    public void setForeignKey(TableSQL table, TableColumn foreignTableColumn) {
        myForeignKeyTable = table;
        myForeignKey = foreignTableColumn;
    }

   /**
     * isForeignKey.  Whether the column has a foreign key constraint or not
     * @return true if the column is a foreign key or false otherwise
     */
    public boolean isForeignKey() {
        return (myForeignKey!=null);
    }

   /**
     * removeForeignKey.  Removes the foreign key constraint
     */
    public void removeForeignKey() {
        myForeignKey = null;
        myForeignKeyTable = null;
    }

   /**
     * isPrimaryKey.  Whether the column is a primary key or not
     * @return true if the column is a primary key or false otherwise
     */
    public boolean isPrimaryKey() {
        return myPrimaryKey;
    }

    public GenericTableColumn(String name) {
       myName = name;
       myPrimaryKey = false;
       myForeignKey = null;
       myAutoIncrement = false;
    }

   /**
     * getName.  Returns the name of the column
     * @return  The name of the column
     */
    public String getName() {
        return myName;
    }

   /**
     * getType.  Returns the type of the column
     * @return  the type of the column
     */
    public String getType() {
        return "genericoverride";
    }

   /**
     * validValue.  Returns a value that is valid in the context of the column
     * @return  valid value for the column
     */
    public String validValue(String value) {
        return value;
    }
       
}
