package cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Annotation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by ghessova on 27.03.2018.
 */
public class AnnotationProcessor {

    @JsonProperty("annotation")
    private String annotationName;
    @JsonProperty("processing_way")
    private AnnotationProcessingWay processingWay;
    @JsonProperty
    private Map<String, String> mapping; // needed for FROM_NAME processing
    @JsonProperty
    private Set<String> valueKeys;
    @JsonProperty
    private String result;

    public Set<String>  processAnnotation(Annotation annotation) {
        if (!annotationName.equals(annotation.getName())) return null;
        if (processingWay ==  AnnotationProcessingWay.FROM_NAME) {
            Set<String> results = new HashSet<>();
            if (mapping != null) {
                results.add(mapping.get(annotationName));
            }
            else {
                results.add(result);
            }
            return results;
        }
        else {
            Set<String> results = new HashSet<>();
            for (String valueKey : valueKeys) {

                Object result = annotation.getValues().get(valueKey);
                if (result != null) {
                    if (mapping != null) { // else throw IllegalArgumentException
                        results.add(mapping.get(annotationName));
                    }
                    else { // return result directly if there is no mapping defined
                        if (result instanceof Set) {
                            for (Object s : (Set)result) {
                                results.add(String.valueOf(s));
                            }
                        }
                        else {
                            results.add(String.valueOf(result));
                        }
                    }
                }
            }
            return results;
        }
    }


    enum AnnotationProcessingWay {
        FROM_NAME,  // result is determined by annotation name
        FROM_VALUE; // result is determined by one of the annotation values
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public void setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
    }

    public AnnotationProcessingWay getProcessingWay() {
        return processingWay;
    }

//    public void setProcessingWay(AnnotationProcessingWay processingWay) {
//        this.processingWay = processingWay;
//    }

    public void setProcessingWay(String processingWay) {
        this.processingWay = AnnotationProcessingWay.valueOf(processingWay.toUpperCase());
    }

    public Map<String, String> getMapping() {
        return mapping;
    }

    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }

    public Set<String> getValueKeys() {
        return valueKeys;
    }

    public void setValueKeys(Set<String> valueKeys) {
        this.valueKeys = valueKeys;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
