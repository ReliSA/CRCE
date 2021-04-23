package cz.zcu.kiv.crce.rest.client.indexer.classmodel.extracting;


import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.ClassStruct;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.PathPart;

/**
 * Created by ghessova on 02.03.2018.
 */
public class State {

    private static final State state = new State();

    public static State getInstance() {
        return state;
    }

    private int parametersProcessed = 0;

    private cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.ClassStruct classType;

    private PathPart pathPart;


    public ClassStruct getClassType() {
        return classType;
    }

    public void setClassType(ClassStruct classType) {
        this.classType = classType;
        this.pathPart = classType;
    }

    public int getParametersProcessed() {
        return parametersProcessed;
    }

    public void setParametersProcessed(int parametersProcessed) {
        this.parametersProcessed = parametersProcessed;
    }

    public PathPart getPathPart() {
        return pathPart;
    }

    public void setPathPart(PathPart pathPart) {
        this.pathPart = pathPart;
    }

}
