package cz.zcu.kiv.crce.rest.client.indexer.classmodel;

import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.ClassMap;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.ClassStruct;

public class Collector {
    private ClassMap resources;

    private static Collector ourInstance;

    /**
     * Init
     */
    public static void init() {
        ourInstance = new Collector();
    }

    /**
     * Return instance of collector
     * @return Collector
     */
    public static Collector getInstance() {
        return ourInstance;
    }

    /**
     * Init resources Class Map
     */
    private Collector() {
        this.resources = new ClassMap();
    }

    /**
     * Filled up resources
     * @return Resources (Classes)
     */
    public ClassMap getClasses() {
        return resources;
    }

    /**
     * Adds class into resources
     * @param resource New Class
     */
    public void addClass(ClassStruct resource) {
        resources.put(resource.getName(), resource);
    }

}
