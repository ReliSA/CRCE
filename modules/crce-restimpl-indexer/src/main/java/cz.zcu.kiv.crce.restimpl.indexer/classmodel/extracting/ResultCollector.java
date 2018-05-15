package cz.zcu.kiv.crce.restimpl.indexer.classmodel.extracting;


import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.ClassStruct;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ghessova on 05.03.2018.
 */
public class ResultCollector {

    private Map<String, ClassStruct> resources;

    private static ResultCollector ourInstance = new ResultCollector();
    public static ResultCollector getInstance() {
        return ourInstance;
    }
    private ResultCollector() {
        this.resources = new HashMap<>();
    }

    public Map<String, ClassStruct> getClasses() {
        return resources;
    }

    public void addClass(ClassStruct resource) {
        resources.put(resource.getName(), resource);
    }
}
