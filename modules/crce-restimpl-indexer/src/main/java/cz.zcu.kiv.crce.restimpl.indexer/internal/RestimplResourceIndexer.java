package cz.zcu.kiv.crce.restimpl.indexer.internal;

import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.indexer.AbstractResourceIndexer;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.extracting.MyClassVisitor;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.extracting.ResultCollector;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.ClassType;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting.RestApiReconstructor;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.Endpoint;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting.RestApiReconstructorImpl;
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
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by ghessova on 23.03.2018.
 */
public class RestimplResourceIndexer extends AbstractResourceIndexer {

    // injected by dependency manager (in Activator)
    private volatile MetadataFactory metadataFactory;
    private volatile MetadataService metadataService;

    private static final Logger logger = LoggerFactory.getLogger(RestimplResourceIndexer.class);


    @Override
    public List<String> index(InputStream input, Resource resource) {

        WebXmlParser.Result webXmlResult = null;
        try {   // borrowed code..  -  parsing input stream and collecting class entry information
            ZipInputStream jis = new ZipInputStream(input);
            ClassVisitor classVisitor = new MyClassVisitor(Opcodes.ASM5);
            for (ZipEntry e = jis.getNextEntry(); e != null; e = jis.getNextEntry()) {
                if (e.getName().endsWith(".class")) {
                    parseClass(new ClassReader(getEntryInputStream(jis)), classVisitor);
                }
                else if (e.getName().endsWith("web.xml") && webXmlResult == null) {
                    WebXmlParser webXmlParser = new WebXmlParser();
                    webXmlResult = webXmlParser.parseWebXml(getEntryInputStream(jis));
                }
            }
        } catch (IOException e) {
            logger.error("Could not index resource.", e);
        }

        Map<String, ClassType> classes = ResultCollector.getInstance().getResources();
        RestApiReconstructor restApiReconstructor = new RestApiReconstructorImpl(classes, webXmlResult);

        Collection<Endpoint> endpoints = restApiReconstructor.extractEndpoints();
        String framework = restApiReconstructor.getFramework();

        if (endpoints.isEmpty()) {
            return Collections.emptyList();
        }
        else {
            // save endpoints and metadata
            RestimplMetadataManager metadataManager = new RestimplMetadataManager(metadataFactory, metadataService);
            metadataManager.setMetadata(resource, endpoints, framework);

            // label the resource with categories and other common attributes
            metadataService.addCategory(resource, RestimplMetadataConstants.MAIN_CATEGORY); // assign main category tag

            return Collections.singletonList(RestimplMetadataConstants.MAIN_CATEGORY);
        }
    }

    private static InputStream getEntryInputStream(ZipInputStream jis) throws IOException	{
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
