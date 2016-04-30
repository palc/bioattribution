package biosampleparser;

/**
 *
 * @author WCHANG
 */
public class Collection_Owner_Table extends TableSQL {
    private TableRow myRow;
    
    private Collection_Table myCollectionTable;
    private Owner_Table myOwnerTable;
    
    public Collection_Owner_Table(Collection_Table collectionTable, 
                                Owner_Table OwnerTable) {
        super("Collection_Owner");
        TableColumn lCollectionPocId = new IntegerColumn("collection_owner_id");
        lCollectionPocId.makePrimaryKey();
        lCollectionPocId.setAutoIncrement();
        addColumn(lCollectionPocId);
        TableColumn lCollectionId = new IntegerColumn("collection_id");
        lCollectionId.setForeignKey(collectionTable,collectionTable.getPrimaryColumn());
        addColumn(lCollectionId);
        TableColumn lOwnerId = new IntegerColumn("Owner_id");
        lOwnerId.setForeignKey(OwnerTable,OwnerTable.getPrimaryColumn());
        addColumn(lOwnerId);
        myOwnerTable = OwnerTable;
        myCollectionTable = collectionTable;
        myRow=preparedRow();
        addRow(myRow);
        myRow.setVariable("Owner_id",
                OwnerTable.toReferencePrimaryKeyVariable());
        myRow.setVariable("collection_id",
                collectionTable.toReferencePrimaryKeyVariable());
    }

}
