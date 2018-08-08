package cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghessova on 27.03.2018.
 */
public class PathPartAttributes {

    private String name;
    private List<String> methods;           // Spring resource can also contain information about http methods
    private List<String> paths;              // hierarchical path of URL
    private List<String> produces;          // MIME types
    private List<String> consumes;

    public PathPartAttributes() {
        this.paths = new ArrayList<>();
        this.produces = new ArrayList<>();
        this.consumes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addPath(String path) {
        paths.add(path);
    }

    public void addConsumes(String consumes) {
        this.consumes.add(consumes);
    }

    public void addProduces(String produces) {
        this.produces.add(produces);
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public List<String> getProduces() {
        return produces;
    }

    public void setProduces(List<String> produces) {
        this.produces = produces;
    }

    public List<String> getConsumes() {
        return consumes;
    }

    public void setConsumes(List<String> consumes) {
        this.consumes = consumes;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }
}
