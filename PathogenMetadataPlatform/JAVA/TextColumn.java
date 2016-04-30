package biosampleparser;

/**
 *
 * @author WCHANG
 */
public class TextColumn extends GenericTableColumn {
    
    public TextColumn(String name) {
       super(name);
    }

   /**
     * getType.  Returns the type of the column
     * @return  the type of the column
     */
    public String getType() {
        return "text";
    }
}
