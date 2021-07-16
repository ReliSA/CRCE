package cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Annotation;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Variable;

import java.util.Map;
import java.util.Set;

/**
 * Created by ghessova on 30.03.2018.
 */
public class ParameterNameProcessor {

    private Set<AnnotationProcessor> annotationProcessors;
    @JsonProperty("from_name")
    private boolean fromName;

    public String process(Variable variable) {
        // first look for annotations
        Map<String, Annotation> annotationMap = variable.getAnnotations();
        for (AnnotationProcessor annotationProcessor : annotationProcessors) {
            Annotation annotation = annotationMap.get(annotationProcessor.getAnnotationName());
            if (annotation != null) {
                Set<String> results = annotationProcessor.processAnnotation(annotation);
                if (results != null && !results.isEmpty()) {
                    return results.iterator().next();
                }
            }
        }
        // no relevant annotation found
        if (fromName) return variable.getName();
        return null;
    }

    public Set<AnnotationProcessor> getAnnotationProcessors() {
        return annotationProcessors;
    }

    public void setAnnotationProcessors(Set<AnnotationProcessor> annotationProcessors) {
        this.annotationProcessors = annotationProcessors;
    }

    public boolean isFromName() {
        return fromName;
    }

    public void setFromName(boolean fromName) {
        this.fromName = fromName;
    }
}

