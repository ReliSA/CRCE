package cz.zcu.kiv.crce.restimpl.indexer.classmodel.extracting;


import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Annotation;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.ClassType;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.PathPart;

/**
 * Created by ghessova on 02.03.2018.
 */
public class State {

    private static final State state = new State();

    public static State getInstance() {
        return state;
    }

    private int parametersProcessed = 0;

    private ClassType classType;

    private Annotation annotation;

    private PathPart pathPart;

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
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
