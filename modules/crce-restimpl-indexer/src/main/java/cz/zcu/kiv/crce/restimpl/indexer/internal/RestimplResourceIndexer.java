package cz.zcu.kiv.crce.restimpl.indexer.internal;

import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.indexer.AbstractResourceIndexer;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.rest.client.indexer.processor.Processor;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.extracting.MyClassVisitor;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.extracting.ResultCollector;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.ClassStruct;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting.RestApiReconstructor;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting.RestApiReconstructorImpl;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.Endpoint;
import cz.zcu.kiv.crce.restimpl.indexer.util.WebXmlParser;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by ghessova on 23.03.2018.
 *
 * @author Gabriela Hessova
 *
 * Indexer for REST model extraction from input archive.
 */
public class RestimplResourceIndexer extends AbstractResourceIndexer {

    // injected by dependency manager (in Activator)
    private volatile MetadataFactory metadataFactory;
    private volatile MetadataService metadataService;

    private static final Logger logger = LoggerFactory.getLogger(RestimplResourceIndexer.class);

    /**
     * Indexer's entry point.
     *
     * Indexing consists of three steps:
     * 1. Class model representation is created from the archive.
     * 2. REST API model is created based on analysis of the class model.
     * 3. REST model is converted to metadata, which are added to the resource.
     *
     * @param input archive input stream
     * @param resource CRCE resource the metadata are set to
     * @return list of categories assigned by this indexer ('restimpl' or empty list)
     */
    @Override
    public List<String> index(InputStream input, Resource resource) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            input.transferTo(baos);
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        InputStream firstClone = new ByteArrayInputStream(baos.toByteArray());
        InputStream secondClone = new ByteArrayInputStream(baos.toByteArray());
        logger.info("Indexing resource " + resource.getId());
        Map<String, cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint> endpointClients =
                new HashMap<>();
        try {
            endpointClients = Processor.process(firstClone);
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            logger.error(e2.getMessage());
        }
        // ARCHIVE PROCESSING
        WebXmlParser.Result webXmlResult = null;
        try { // borrowed code..  -  parsing input stream and collecting class entry information
            ZipInputStream jis = new ZipInputStream(secondClone);
            ClassVisitor classVisitor = new MyClassVisitor(Opcodes.ASM5);
            for (ZipEntry e = jis.getNextEntry(); e != null; e = jis.getNextEntry()) {
                if (e.getName().endsWith(".class")) {
                    logger.debug("Parsing class file");
                    parseClass(new ClassReader(getEntryInputStream(jis)), classVisitor);
                } else if (e.getName().endsWith("web.xml") && webXmlResult == null) {
                    logger.debug("Parsing web.xml");
                    WebXmlParser webXmlParser = new WebXmlParser();
                    webXmlResult = webXmlParser.parseWebXml(getEntryInputStream(jis));
                }
            }
        } catch (IOException e) {
            logger.error("Could not create class model.", e);
        }
        Map<String, ClassStruct> classes = ResultCollector.getInstance().getClasses();

        logger.debug("Class model extracted");

        // REST API RECONSTRUCTION
        RestApiReconstructor restApiReconstructor =
                new RestApiReconstructorImpl(classes, webXmlResult);
        Collection<Endpoint> endpoints = null;

        try {
            endpoints = restApiReconstructor.extractEndpoints();
        } catch (Exception e) {
            logger.error("Could not extract endpoints", e);
        }
        for (Endpoint endpoint : endpoints) {

            for (cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint endpointClient : endpointClients
                    .values()) {
                if (endpointClient.getDependency().contains(endpoint.getName())) {
                    endpoint.addDependency(endpointClient.getUrl());
                }
            }
        }


        String framework = restApiReconstructor.getFramework(); // 'undefined' when no framework was identified

        // CONVERTING REST API MODEL TO METADATA
        if (endpoints == null || endpoints.isEmpty()) {
            logger.info("No endpoints found for resource " + resource.getId());
            return Collections.emptyList();
        } else {
            logger.debug("REST API model extracted");
            // save endpoints and metadata
            RestimplMetadataManager metadataManager = new RestimplMetadataManager(metadataFactory);
            metadataManager.setMetadata(resource, endpoints, framework);

            // label the resource with categories and other common attributes
            metadataService.addCategory(resource, RestimplMetadataConstants.MAIN_CATEGORY); // assign main category tag
            logger.debug("Restimpl indexing finished");

            return Collections.singletonList(RestimplMetadataConstants.MAIN_CATEGORY);
        }
    }

    // borrowed code
    private static InputStream getEntryInputStream(ZipInputStream jis) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n;
        while ((n = jis.read(buf, 0, buf.length)) > 0) {
            baos.write(buf, 0, n);
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    private static void parseClass(ClassReader classReader, ClassVisitor visitor) {
        classReader.accept(visitor, ClassReader.SKIP_DEBUG);

    }

    public MetadataFactory getMetadataFactory() {
        return metadataFactory;
    }

    public void setMetadataFactory(MetadataFactory metadataFactory) {
        this.metadataFactory = metadataFactory;
    }

    public MetadataService getMetadataService() {
        return metadataService;
    }

    public void setMetadataService(MetadataService metadataService) {
        this.metadataService = metadataService;
    }
}
