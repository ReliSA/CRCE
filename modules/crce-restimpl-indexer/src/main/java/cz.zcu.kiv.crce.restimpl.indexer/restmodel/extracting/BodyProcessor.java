package cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting;

import java.util.Set;

/**
 * Created by ghessova on 02.04.2018.
 */
public class BodyProcessor {

    private boolean withoutAnnotations;

    private Set<String> annotations;
    private Set<String> excludingAnnotations;

    public boolean isWithoutAnnotations() {
        return withoutAnnotations;
    }

    public Set<String> getExcludingAnnotations() {
        return excludingAnnotations;
    }

    public void setExcludingAnnotations(Set<String> excludingAnnotations) {
        this.excludingAnnotations = excludingAnnotations;
    }

    public void setWithoutAnnotations(boolean withoutAnnotations) {
        this.withoutAnnotations = withoutAnnotations;
    }

    public Set<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Set<String> annotations) {
        this.annotations = annotations;
    }
}