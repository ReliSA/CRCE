package cz.zcu.kiv.crce.rest.client.indexer.classmodel.extracting;

import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.ClassStruct;

/**
 * Inspired by ghessova on 02.03.2018.
 */
public class State {

    private static final State state = new State();

    public static State getInstance() {
        return state;
    }

    private int parametersProcessed = 0;

    private cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.ClassStruct currClass;

    /**
     * Return current classtructure
     * @return
     */
    public ClassStruct getClassStruct() {
        return currClass;
    }

    /**
     * Set current scope (Class) for the state
     * @param classStruct
     */
    public void setClassStruct(ClassStruct classStruct) {
        this.currClass = classStruct;
    }

    /**
     * Returns number of processed parameters
     * @return
     */
    public int getNumParametersProcessed() {
        return parametersProcessed;
    }

    /**
     * Sets number of processed parameters
     * @param parametersProcessed
     */
    public void setNumParametersProcessed(int parametersProcessed) {
        this.parametersProcessed = parametersProcessed;
    }

}
