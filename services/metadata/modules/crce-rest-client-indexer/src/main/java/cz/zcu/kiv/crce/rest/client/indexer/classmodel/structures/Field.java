package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

/**
 * Created by ghessova on 01.05.2018.
 */
public class Field extends Variable {

    private int access;
    private String val;
    private String signature;

    /**
     * 
     * @param dataType Type of field
     */
    public Field(DataType dataType) {
        super(dataType);
    }


    /**
     * Sets const value
     * @param val New const value
     */
    public void setConstValue(String val) {
        this.val = val;
    }

    /**
     * 
     * @return Const value
     */
    public String getConstValue() {
        return val;
    }

    @Override
    public String toString() {
        return super.toString() + ", Field{" + "access=" + access + '}';
    }

    /**
     * @return Signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Sets signature
     * @param signature Signature
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }
}
