package cz.zcu.kiv.crce.rest.client.indexer.processor;

import java.util.Map;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.ClassStruct;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassWrapper;

// TODO move to folder tools and create seperated classes StringC, OpcodeC......

public class Helpers {


    /**
     * Wrappes class structure with additional fields and functions
     * 
     * @param classes Map of classes
     * @return Wrapped classes
     */
    public static ClassMap convertStructMap(Map<String, ClassStruct> classes) {
        ClassMap converted = new ClassMap();
        for (String key : classes.keySet()) {
            converted.put(key, new ClassWrapper(classes.get(key)));
        }
        return converted;
    }
}
