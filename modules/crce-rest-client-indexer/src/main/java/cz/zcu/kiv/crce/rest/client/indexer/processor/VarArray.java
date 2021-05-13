package cz.zcu.kiv.crce.rest.client.indexer.processor;

public class VarArray {
    private Variable[] value;
    private int position;

    /**
     * VarArray is used to store array created by bytecode
     * @param size Initiate VarArray on given size
     */
    public VarArray(int size) {
        position = 0;
        value = new Variable[size];
    }

    /**
     * 
     * @return get inner array
     */
    public Variable[] getInnerArray() {
        return value;
    }

    /**
     * Sets variable on set index
     * @param var New variable
     */
    public void set(Variable var) {
        value[position] = var;
    }

    /**
     * Sets position for furthure manipulation
     * @param pos New position
     */
    public void setPosition(int pos) {
        position = pos;
    }

    /**
     * To string ...
     */
    @Override
    public String toString() {
        String toStringVal = "";
        final String delimeter = ", ";
        for (Variable item : value) {
            if (item == null) {
                continue;
            }
            toStringVal += item.getValue() + delimeter;
        }
        int finalSize = toStringVal.length() - delimeter.length();
        if (finalSize <= 0) {
            return "{}";
        }
        toStringVal = toStringVal.substring(0, toStringVal.length() - delimeter.length());
        return toStringVal;
    }

}
