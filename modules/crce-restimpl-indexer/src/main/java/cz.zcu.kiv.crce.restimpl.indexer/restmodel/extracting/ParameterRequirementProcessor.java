package cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting;



import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Annotation;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Field;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Variable;

import java.util.Map;
import java.util.Set;

/**
 * Created by ghessova on 01.05.2018.
 */
public class ParameterRequirementProcessor {

    private Set<AnnotationProcessor> annotationProcessors;

    private boolean parametersDefault;
    private boolean fieldsDefault;

    /**
     * Returns true if the variable is required.
     * @param variable
     * @return
     */
    public boolean process(Variable variable) {
        // first look for annotations
        Map<String, Annotation> annotationMap = variable.getAnnotations();
        for (AnnotationProcessor annotationProcessor : annotationProcessors) {
            Annotation annotation = annotationMap.get(annotationProcessor.getAnnotationName());
            if (annotation != null) {
                Set<String> results = annotationProcessor.processAnnotation(annotation);
                if (results != null && !results.isEmpty()) {
                    String result = results.iterator().next();
                    return Boolean.valueOf(result);
                }
            }
        }
        // no relevant annotation found
        if (variable instanceof Field) {
            return fieldsDefault;
        }
        else {
            return parametersDefault;
        }
    }

    public Set<AnnotationProcessor> getAnnotationProcessors() {
        return annotationProcessors;
    }

    public void setAnnotationProcessors(Set<AnnotationProcessor> annotationProcessors) {
        this.annotationProcessors = annotationProcessors;
    }

    public boolean isParametersDefault() {
        return parametersDefault;
    }

    public void setParametersDefault(boolean parametersDefault) {
        this.parametersDefault = parametersDefault;
    }

    public boolean isFieldsDefault() {
        return fieldsDefault;
    }

    public void setFieldsDefault(boolean fieldsDefault) {
        this.fieldsDefault = fieldsDefault;
    }
}
