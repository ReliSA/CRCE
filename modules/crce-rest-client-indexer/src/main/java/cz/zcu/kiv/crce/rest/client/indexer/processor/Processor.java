package cz.zcu.kiv.crce.rest.client.indexer.processor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

import cz.zcu.kiv.crce.rest.client.indexer.classmodel.Collector;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.extracting.Loader;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassWrapper;
import org.slf4j.Logger;

public class Processor {
    /**
     * Finds endpoints in JAR file
     * 
     * @param jar JAR file
     * @return
     * @throws IOException
     */
    public static Map<String, Endpoint> process(File jar) throws IOException {
        Collector.init();
        Loader.loadClasses(jar);
        Map<String, Endpoint> endpoints = new HashMap<>();
        ClassMap classes = Helpers.convertStructMap(Collector.getInstance().getClasses());
        EndpointProcessor endpointProcessor = new EndpointProcessor(classes);

        for (ClassWrapper class_ : classes.values()) {
            endpointProcessor.process(class_);
            Helpers.EndpointF.merge(endpoints, endpointProcessor.getEndpoints());
        }
        return endpoints;
    }
    public static Map<String, Endpoint> process(InputStream jis) throws  IOException {
        Collector.init();
        Loader.loadClasses(new ZipInputStream(jis));
        Map<String, Endpoint> endpoints = new HashMap<>();
        ClassMap classes = Helpers.convertStructMap(Collector.getInstance().getClasses());
        EndpointProcessor endpointProcessor = new EndpointProcessor(classes);
        for (ClassWrapper class_ : classes.values()) {
            endpointProcessor.process(class_);
            Helpers.EndpointF.merge(endpoints, endpointProcessor.getEndpoints());
        }
        return endpoints;
    }
}
