package cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures;

/**
 * Created by ghessova on 01.05.2018.
 */
public class Field extends Variable {

    private int access;

    public Field(DataType dataType) {
        super(dataType);
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    @Override
    public String toString() {
        return super.toString() +", Field{" +
                "access=" + access +
                '}';
    }
}
