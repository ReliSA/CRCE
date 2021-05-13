package cz.zcu.kiv.crce.rest.client.indexer.processor;

public class VarArray {
    private Variable[] value;
    private int position;

    public VarArray(int size) {
        position = 0;
        value = new Variable[size];
    }

    public Variable[] getInnerArray() {
        return value;
    }

    public void set(Variable var) {
        value[position] = var;
    }

    public void setPosition(int pos) {
        position = pos;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
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
