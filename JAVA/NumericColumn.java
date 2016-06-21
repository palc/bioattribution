package biosampleparser;

/**
 *
 * @author WCHANG
 */
public class NumericColumn extends GenericTableColumn {
    private int myPrecision;
    private int myScale;
    public NumericColumn(String name, int precision,int scale) {
       super(name);
       myPrecision = precision;
       myScale=scale;
    }

   /**
     * getType.  Returns the type of the column
     * @return  the type of the column
     */
    public String getType() {
        return "numeric("+myPrecision+","+myScale+")";
    }
}
