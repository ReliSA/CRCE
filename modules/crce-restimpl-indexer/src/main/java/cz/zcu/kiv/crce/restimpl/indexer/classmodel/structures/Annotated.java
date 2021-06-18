package cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures;

import java.util.Map;

/**
 * Created by ghessova on 10.03.2018.
 */
public interface Annotated {

    void addAnnotation(Annotation annotation);
    Map<String, Annotation> getAnnotations();
}
