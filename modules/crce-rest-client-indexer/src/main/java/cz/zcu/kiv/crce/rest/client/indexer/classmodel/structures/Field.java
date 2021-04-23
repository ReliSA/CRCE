package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

/**
 * Created by ghessova on 01.05.2018.
 */
public class Field extends Variable {

    private int access;
    private String val;
    private String signature;

    public Field(DataType dataType) {
        super(dataType);
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public void setConstValue(String val) {
        this.val = val;
    }

    public String getConstValue() {
        return val;
    }

    // public void setDataTypeChain()

    @Override
    public String toString() {
        return super.toString() + ", Field{" + "access=" + access + '}';
    }

    /**
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param signature the signature to set
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }
}
