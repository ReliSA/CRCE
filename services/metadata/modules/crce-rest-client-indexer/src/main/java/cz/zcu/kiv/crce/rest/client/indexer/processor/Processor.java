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
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.ClassMap;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.ClassWrapper;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ClassTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.EndpointTools;

public class Processor {
    /**
     * Finds endpoints in JAR file
     * 
     * @param jar JAR file
     * @return Found endpoints
     * @throws IOException
     */
    public static Map<String, Endpoint> process(File jar) throws IOException {
        Collector.init();
        Loader.loadClasses(jar);
        return process();
    }

    /**
     * Finds endpoints in Java InputStream
     * 
     * @param jis InputStream
     * @return Found endpoints
     * @throws IOException
     */
    public static Map<String, Endpoint> process(InputStream jis) throws IOException {
        Collector.init();
        Loader.loadClasses(new ZipInputStream(jis));
        return process();
    }

    /**
     * Processes classes to found some endpoints
     * 
     * @return Found endpoints
     */
    private static Map<String, Endpoint> process() {
        Map<String, Endpoint> endpoints = new HashMap<>();
        ClassMap classes = ClassTools.convertStructMap(Collector.getInstance().getClasses());
        EndpointProcessor endpointProcessor = new EndpointProcessor(classes);
        for (ClassWrapper class_ : classes.values()) {
            endpointProcessor.process(class_);
            EndpointTools.merge(endpoints, endpointProcessor.getEndpoints());
        }
        BeanProcessor.process(endpoints.values(), classes);
        DependencyProcessor.process(endpointProcessor.getCallingChains(), endpoints.values());
        return endpoints;
    }
}
