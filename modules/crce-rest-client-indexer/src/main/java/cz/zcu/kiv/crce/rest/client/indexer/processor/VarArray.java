package cz.zcu.kiv.crce.rest.client.indexer.processor;

public class VarArray {
    private String[] value;
    private int position;

    public VarArray(int size) {
        position = 0;
        value = new String[size];
    }

    public String[] getInnerArray() {
        return value;
    }

    public void set(String val) {
        value[position] = val;
    }

    public void setPosition(int pos) {
        position = pos;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        String toStringVal = "";
        final String delimeter = ", ";
        for (String item : value) {
            toStringVal += item + delimeter;
        }
        int finalSize = toStringVal.length() - delimeter.length();
        if (finalSize <= 0) {
            return "{}";
        }
        toStringVal = toStringVal.substring(0, toStringVal.length() - delimeter.length());
        return toStringVal;
    }

}
