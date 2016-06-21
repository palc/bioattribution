package biosampleparser;

/**
 *
 * @author WCHANG
 */
public class CharColumn extends GenericTableColumn {
    private int myLength;
    public CharColumn(String name, int length) {
       super(name);
       myLength = length;
    }
    public String getType() {
        return "char("+myLength+")";
    }
}
