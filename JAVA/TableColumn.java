package biosampleparser;

/**
 *
 * @author WCHANG
 */
public interface TableColumn {
   
    public String getName();
    public String getType();
    public boolean isPrimaryKey();
    public boolean isAutoIncrement();
    public void setAutoIncrement();
    public void removeAutoIncrement();
    public void makePrimaryKey();
    public void removePrimaryKey();
    public void setForeignKey(TableSQL table,TableColumn column);
    public void removeForeignKey();
    public boolean isForeignKey();
    public String toCreateString();
    public String validValue(String value);
    
}
