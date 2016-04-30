package biosampleparser;

/**
 *
 * @author WCHANG
 */
public class BooleanColumn extends GenericTableColumn {

    public BooleanColumn(String name) {
        super(name);
    }
    public String getType() {
        return "boolean";
    }
    
}
