package cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting;

import java.util.Set;

/**
 * Created by ghessova on 07.05.2018.
 */
public class ExceptionHandler {

    private Set<String> annotations;
    private Set<String> interfaces;
    private String method;

    public Set<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Set<String> annotations) {
        this.annotations = annotations;
    }

    public Set<String> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Set<String> interfaces) {
        this.interfaces = interfaces;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
