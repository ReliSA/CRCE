package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ghessova on 05.03.2018. Path part - class or method
 */
public class PathPart {

    protected String name; // class or method name
    // protected String path; // hierarchical path of URL
    // protected Set<String> produces; // MIME types
    // protected Set<String> consumes; // MIME types

    // protected Map<String, Annotation> annotations = new HashMap<>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
     * 
     * public String getPath() { return path; }
     * 
     * public void setPath(String path) { this.path = path; }
     * 
     * public Set<String> getProduces() { return produces; }
     * 
     * public void setProduces(Set<String> produces) { this.produces = produces; }
     * 
     * public Set<String> getConsumes() { return consumes; }
     * 
     * public void setConsumes(Set<String> consumes) { this.consumes = consumes; }
     * 
     * public void addAnnotation(Annotation annotation) { this.annotations.put(annotation.getName(),
     * annotation); }
     * 
     * public Map<String, Annotation> getAnnotations() { return annotations; }
     */
}

