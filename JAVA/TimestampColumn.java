package biosampleparser;

/**
 *
 * @author WCHANG
 */
public class TimestampColumn extends GenericTableColumn {

    public TimestampColumn(String name) {
        super(name);
    }

   /**
     * getType.  Returns the type of the column
     * @return  the type of the column
     */
    public String getType() {
        return "timestamp";
    }
}
