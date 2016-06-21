package biosampleparser;

/**
 *
 * @author WCHANG
 */
public class VarcharColumn extends GenericTableColumn {
    private int myLength;
    public VarcharColumn(String name, int length) {
       super(name);
       myLength = length;
    }

   /**
     * getType.  Returns the type of the column
     * @return  the type of the column
     */
    public String getType() {
        return "varchar("+myLength+")";
    }

   /**
     * validValue.  Returns a value that is valid in the context of the column
     * @return  valid value for the column
     */
    public String validValue(String value) {
        String lValidValue = value;
        if(value.length() > myLength) {
            lValidValue = value.substring(0,myLength);
            System.err.println("Truncating value:"+value);
            System.err.println("to");
            System.err.println("{"+lValidValue+"}");
        }
        return lValidValue;
    }
}
