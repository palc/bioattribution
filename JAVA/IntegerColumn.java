package biosampleparser;

/**
 *
 * @author WCHANG
 */
public class IntegerColumn extends GenericTableColumn {
    public IntegerColumn(String name) {
        super(name);
    }

   /**
     * getType.  Returns the type of the column
     * @return  the type of the column
     */
    public String getType() {
        return "integer";
    }
}
