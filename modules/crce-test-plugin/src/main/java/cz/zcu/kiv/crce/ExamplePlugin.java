package cz.zcu.kiv.crce;

import aQute.bnd.maven.PomParser;
import aQute.bnd.maven.support.Pom;
import com.sun.org.apache.xpath.internal.operations.Mod;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;
import org.apache.felix.bundlerepository.impl.FileUtil;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * An Example plugin for CRCE.
 *
 * Created by Zdenek Vales on 27.12.2016.
 */
public class ExamplePlugin extends AbstractActionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExamplePlugin.class);

    public static final String POM_NAME = "pom.xml";

    private volatile MetadataService metadataService;
    private volatile MetadataFactory metadataFactory;

    public ExamplePlugin() {
        logger.trace("New instance.");
    }

    public Resource loadCrceIdentity(Resource resource) {

        System.out.println("Resource: "+resource.getId());

        return resource;
    }

    /**
     * Tries to read pom.xml file from jar specified by artifactUrl.
     * If the pom is found it will be used to fill provided resource, which
     * will be returned.
     *
     * @param artifactUrl Url to maven artifact (jar or zip).
     * @param resource Resource to be filled.
     * @return Filled resource.
     */
    public Resource fillResource(final URL artifactUrl, Resource resource) throws IOException, XmlPullParserException {
        // try to load the pom
        Model pomModel = loadPom(artifactUrl);

        // populate the resource
        populate(pomModel, resource);

        return resource;
    }

    /**
     * Will try to load pom.xml file from provided maven artifact.
     * @param artifactUrl Url to maven artifact (jar or zip).
     * @return Model of pom file.
     * @throws IOException
     * @throws XmlPullParserException
     */
    public Model loadPom(final URL artifactUrl) throws IOException, XmlPullParserException {
        byte[] pom = loadEntry(artifactUrl, POM_NAME);
        if (pom == null) {
            throw new IllegalArgumentException("The specified url is not a valid maven artifact (can't read pom.xml): " + artifactUrl);
        }

        // create a model object for pom file
        MavenXpp3Reader reader = new MavenXpp3Reader();
        return reader.read(new InputStreamReader(new ByteArrayInputStream(pom)));
    }

    /**
     * Taken from OsgiManigestBundleIndexer.
     *
     * Loads a file as an array of bytes from maven artifact (jar).
     * @param artifactUrl Url to maven artifact.
     * @param name Name of the file in jar.
     * @return Bytes from file.
     * @throws IOException
     */
    private byte[] loadEntry(final URL artifactUrl, String name) throws IOException {
        try (InputStream is = FileUtil.openURL(artifactUrl); ZipInputStream jis = new ZipInputStream(is)) {
            for (ZipEntry e = jis.getNextEntry(); e != null; e = jis.getNextEntry()) {
                if (name.equalsIgnoreCase(e.getName())) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int n;
                    while ((n = jis.read(buf, 0, buf.length)) > 0) {
                        baos.write(buf, 0, n);
                    }
                    return baos.toByteArray();
                }
            }
        }
        return null;
    }

    /**
     * Will load the identity capability of resource and populate it with the data
     * taken from pomModel.
     * @param pomModel Model of pom file.
     * @param resource Resource to be filled with data.
     */
    private void populate(Model pomModel, Resource resource) {

    }

    @Override
    public Resource afterUploadToBuffer(Resource resource, Buffer buffer, String name) throws RefusedArtifactException {
        loadCrceIdentity(resource);
        logger.trace("Metadata service: "+metadataService.toString()+". Metadata factory: "+metadataFactory);
        return resource;
    }

    @Override
    public List<Resource> afterBufferCommit(List<Resource> resources, Buffer buffer, Store store) {
        logger.trace("Metadata service: "+metadataService.toString()+". Metadata factory: "+metadataFactory);
        for (Resource r : resources) {
            loadCrceIdentity(r);
        }
        return super.afterBufferCommit(resources, buffer, store);
    }
}
