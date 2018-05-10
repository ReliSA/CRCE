package cz.zcu.kiv.crce.restimpl.indexer.util;

import cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting.AnnotationProcessor;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Annotation;

import java.util.*;

/**
 * Created by ghessova on 10.03.2018.
 */
public class Util {

    /**
     * Multiple results can be found.
     * @param processors
     * @param annotations
     * @return
     */
    public static List<String> getResultsFromAnnotations(Set<AnnotationProcessor> processors, Map<String, Annotation> annotations) {
        List<String> results = new ArrayList<>();
        for (AnnotationProcessor processor : processors) {
            String annotationName = processor.getAnnotationName();
            Annotation annotation = annotations.get(annotationName);
            if (annotation != null) {
                results.addAll(processor.processAnnotation(annotation));
            }
        }
        return results;
    }

    /**
     * Only one value expected.
     * First value returned, others are discarded.
     * @param processors
     * @param annotations
     * @return
     */
    public static String getResultFromAnnotations(Set<AnnotationProcessor> processors, Map<String, Annotation> annotations) {
        for (AnnotationProcessor processor : processors) {
            String annotationName = processor.getAnnotationName();
            Annotation annotation = annotations.get(annotationName);
            if (annotation != null) {
                Set<String> results = processor.processAnnotation(annotation);
                if (results != null && !results.isEmpty()) {
                    return results.iterator().next();
                }
            }
        }
        return null;
    }

}
