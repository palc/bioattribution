package biosampleparser;

import java.util.ArrayList;
import java.util.Hashtable;
import org.w3c.dom.Element;

/**
 *
 * @author WCHANG
 */
public class TableRow {
    private ArrayList<TableColumn> myColumnList;
    private ArrayList<String> myColumnNameList;
    private Hashtable<String,String> myRowMapping;
    public TableRow() {
        myColumnList = new ArrayList<TableColumn>();
        myColumnNameList = new ArrayList<String>();
        myRowMapping = new Hashtable<String,String>();
    }

    /**
     * addColumn. Adds another column to the current row
     * @param column  A column to add to the row
     */
    public void addColumn(TableColumn column) {
        myColumnList.add(column);
        myColumnNameList.add(column.getName());
    }
    /**
     * escapeValue. Apply escape values to strings to ensure that
     * the string is interpreted correctly by a database
     * @param value  A string value
     * @return       A string with the proper escape charcters applied.
     */
    private String escapeValue(String value) {
        String lEscapedItem = value;
        if(lEscapedItem != null) {
             lEscapedItem=lEscapedItem.replace("\\","\\\\");
             lEscapedItem=lEscapedItem.replace("\"","\\\"");
             lEscapedItem=lEscapedItem.replace("'","''");
        }
        return lEscapedItem;
    }

    /**
     * copyItem. If there is a value associated with fromColumnName in the
     * row, then copy that value to toColumnName in the same row
     * @param toColumnName    The name of the originating column
     * @param fromColumnName  The name of the destination column
     */
    public void copyItem(String toColumnName,String fromColumnName) {
        if(myColumnNameList.contains(toColumnName) && myColumnNameList.contains(fromColumnName)) {
            String lValue = myRowMapping.get(fromColumnName);
            if(lValue != null) {
                myRowMapping.put(toColumnName,myRowMapping.get(fromColumnName));
            }
        }
    }

    /**
     * setItem. associate the String item with the column columnName
     * @param columnName  The name of the column
     * @param item        The value in that column
     */
    public void setItem(String columnName,String item) {
        if(myColumnNameList.contains(columnName)) {
            String lEscapedItem=item;
            if("".equals(lEscapedItem)) {
                 lEscapedItem=null;
            }
            if(lEscapedItem != null) {
                 myRowMapping.put(columnName,lEscapedItem);
            }
        }
    }

    /**
     * setVariable. Sets an item to be referenced as a variable
     * from the SQL string.
     * @param columenName  The columnName that will be associated with 
     *                     the variable
     * @param item         The name of the variable to reference
     */
    public void setVariable(String columnName,String item) {
        if(myColumnNameList.contains(columnName)) {
            myRowMapping.put(columnName,"$&$"+item);
        }
    }

    /**
     * clear. Clear the values associated with the row.
     */
    public void clear() {
        myRowMapping.clear();
    }

    /**
     * toValueString. Create an SQL string for a list of the values
     * contained in the current row.
     * @return  an SQL string with the values contained in the current row
     */
    public String toValueString() {
        StringBuffer lBuffer = new StringBuffer();
        if(myColumnNameList.size() > 0) {
            lBuffer.append("(");
            int i=0;
            for(;i<myColumnNameList.size()-1;i++) {
                TableColumn lColumn=myColumnList.get(i);
                String lColumnName=myColumnNameList.get(i);
                String lColumnValue = myRowMapping.get(lColumnName);
                if(lColumnValue != null) {
                    if(lColumnValue.length() > 2 && lColumnValue.charAt(0)=='$' &&
                            lColumnValue.charAt(1)=='&' && lColumnValue.charAt(2)=='$') {
                        // This is a variable, so don't specify single quotes
                        lBuffer.append(lColumnValue.substring(3));
                    }else {
                        lBuffer.append("'");
                        lBuffer.append(escapeValue(lColumn.validValue(lColumnValue)));
                        lBuffer.append("'");
                    }
                } else {
                    lBuffer.append("DEFAULT");
                }
                lBuffer.append(", ");
            }
            TableColumn lColumn=myColumnList.get(i);
            String lColumnName=myColumnNameList.get(i);
            String lColumnValue = myRowMapping.get(lColumnName);
            if(lColumnValue != null) {
                if(lColumnValue.length() > 2 && lColumnValue.charAt(0)=='$' &&
                   lColumnValue.charAt(1)=='&' && lColumnValue.charAt(2)=='$') {
                    // This is a variable, so don't specify single quotes
                    lBuffer.append(lColumnValue.substring(3));
                }else {
                    lBuffer.append("'");
                    lBuffer.append(escapeValue(lColumn.validValue(lColumnValue)));
                    lBuffer.append("'");
                }
            } else {
                lBuffer.append("DEFAULT");
            }

            lBuffer.append(")");
        } else {
            lBuffer.append("()");
        }
// E.g. ('UA502', 'Bananas', 105, '1971-07-13', 'Comedy', '82 minutes');
        return lBuffer.toString();

    }
    
     /**
     * pullAttribute. If the XML Element contains a value for the Attribute 
     * specified by attributeName, then update the column columnName 
     * with the value and return true.  Otherwise, return false.
     * @param columnName     The name of the column which will be associated
     *                       with the attribute value.
     * @param element        The XML Element being parsed.
     * @param attributeName  The name of the attribute which might be contained
     *                       in this Element.
     * @return Whether a valid value was found within this Element
     */
    public boolean pullAttribute(String columnName,
                                  Element element, String attributeName) {
        boolean lFoundAttribute = false;
        if(element != null) {
            String lValue = element.getAttribute(attributeName);
            if(lValue != null) {
                setItem(columnName,lValue);
                lFoundAttribute = true;
            }
        }
        return lFoundAttribute;
    }

    /**
     * pullAttribute. If the XML Element contains a value for the Attribute 
     * specified by attributeName, then update the column columnName 
     * with the value and return true.  Otherwise, return false.
     * @param columnName     The name of the column which will be associated
     *                       with the attribute value.
     * @param element        The XML Element being parsed.
     * @param attributeName  The name of the attribute which might be contained
     *                       in this Element.
     * @param prefix         The specified prefix is appended to the value, if
     *                       it exists, and stored in the row.
     * @return Whether a valid value was found within this Element
     */
    public boolean pullAttributeWithPrefix(String columnName,
                                           Element element, String attributeName,
                                           String prefix) {
        boolean lFoundAttribute = false;
        if(element != null) {
            String lValue = element.getAttribute(attributeName);
            if(lValue != null && prefix != null) {
                setItem(columnName,prefix+lValue);
                lFoundAttribute = true;
            }
        }
        return lFoundAttribute;
    }

    
    /**
     * pullValue. If there is a value within the attributeNode, store that 
     *            value in the columnName column and return true.  Otherwise, 
     *            return false.
     * @param columnName     The corresponding Column where the value is stored
     *                       within the row.
     * @param attributeNode  The XML node which may contain the value.
     * @return Whether a valid value was found within the attributeNode.
     */
    public boolean pullValue(String columnName,Element attributeNode){
        boolean lFoundSampleData = false;
        if(attributeNode != null) {
          if(attributeNode.getFirstChild() != null) {
            String lValue = attributeNode.getFirstChild().getNodeValue();
            if(lValue != null) {
                setItem(columnName,lValue);
                lFoundSampleData=true;
            }
          }
        }
        return lFoundSampleData;
    }
    
    
    public static void main(String []args) {
        TableRow lRow = new TableRow();
        System.out.println(lRow.toValueString());
        lRow.setItem("SubmitDate","2012-03-24");
        System.out.println(lRow.toValueString());
        lRow.addColumn(new TimestampColumn("SubmitDate"));
        System.out.println(lRow.toValueString());
        lRow.setItem("SubmitDate","2012-03-24");
        System.out.println(lRow.toValueString());
        lRow.setItem("SubmitDate","2012-03-28");
        System.out.println(lRow.toValueString());
        lRow.setItem("Submitter","Eileen");
        System.out.println(lRow.toValueString());
        lRow.addColumn(new VarcharColumn("Submitter",5));
        System.out.println(lRow.toValueString());
        lRow.setItem("Submitter","Vitis vinifera \\'Regent\\' and \\'Tricadeira\\' SMART-GATEWAY combined cDNA library");
        System.out.println(lRow.toValueString());
        lRow.clear();
        System.out.println(lRow.toValueString());
    }
}
