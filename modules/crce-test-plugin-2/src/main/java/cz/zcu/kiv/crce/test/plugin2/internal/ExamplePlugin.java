package cz.zcu.kiv.crce.test.plugin2.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;
import cz.zcu.kiv.crce.test.plugin2.namespace.NsMavenArtifact;
import cz.zcu.kiv.crce.test.plugin2.namespace.NsMvnArtifactIdentity;
//import org.apache.felix.bundlerepository.impl.FileUtil;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
//import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * An Example plugin2 for CRCE.
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
//
    public Resource loadCrceIdentity(Resource resource){
        URL url = null;

        try {
            url = getUrl(resource);
            fillResource(url, resource);
            addCategories(resource);
        } catch (MalformedURLException ex) {
            logger.error("Exception occurred while obtaining resource url: " + ex.getMessage());
            throw new IllegalStateException("Unexpected malformed url exception!", ex);
//        } catch (XmlPullParserException ex) {
//            logger.error("Exception occurred while parsing artifact pom: " + ex.getMessage());
//            metadataService.addCategory(resource, NsMavenArtifact.CATEGORY__MAVEN_CORRUPTED);
        } catch (IOException ex) {
            logger.error("I/O exception occurred while loading maven artifact: " + ex.getMessage());
        } catch (Exception ex) {
            // todo: ignore non-mvn stuff
            logger.error("Exception occurred: "+ex.getMessage());
        }

        return resource;
    }


    /**
     * Adds categories for mvn artifact.
     * @param resource
     */
    public void addCategories(Resource resource) {
        metadataService.addCategory(resource, NsMavenArtifact.CATEGORY__MAVEN_ARTIFACT);
    }

    /**
     * Returns the URI taken from the resource identity capability.
     * @param resource
     * @return
     */
    public URL getUrl(Resource resource) throws MalformedURLException {
        URI uri = metadataService.getUri(resource);
        return uri.toURL();
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
    public Resource fillResource(final URL artifactUrl, Resource resource) throws /*IOException, XmlPullParserException*/Exception {
        // try to load the pom
        Model pomModel = loadPom(artifactUrl);

        // populate the resource
        populate(pomModel, resource);

        return resource;
    }

    /**
     * Will try to load pom.xml file from provided maven artifact.
     * If the &lt;version&gt; or &lt;groupId&gt; of artifact aren't specified,
     * the parent ones (if &lt;parent&gt; exists) will be used.
     *
     * @param artifactUrl Url to maven artifact (jar or zip).
     * @return Model of pom file.
     * @throws IOException
     */
    public Model loadPom(final URL artifactUrl) throws /*IOException, XmlPullParserException */Exception{
        byte[] pom = loadEntry(artifactUrl, POM_NAME);
        if (pom == null) {
            throw new IllegalArgumentException("The specified url is not a valid maven artifact (can't read pom.xml): " + artifactUrl);
        }

        // create a model object for pom file
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model pomModel = reader.read(new InputStreamReader(new ByteArrayInputStream(pom), Charset.defaultCharset()));

        return adjustModel(pomModel);
    }

    /**
     * If some tags are not specified, this method will try to find them in &lt;parent&gt; tag.
     * @param model Model to be adjusted.
     * @return Adjusted model.
     */
    public Model adjustModel(Model model) {
        boolean parentNull = model.getParent() == null;

        // artifact id tag
        if(model.getArtifactId() == null && !parentNull) {
            model.setArtifactId(model.getParent().getArtifactId());
        }

        // version tag
        if(model.getVersion() == null && !parentNull) {
            model.setVersion(model.getParent().getVersion());
        }

        // group id tag
        if(model.getGroupId() == null && !parentNull) {
            model.setGroupId(model.getParent().getGroupId());
        }

        return model;
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
    //TODO: test populate() method
    private void populate(Model pomModel, Resource resource) {

        // todo: external id to the crce identity

        // get root capability for maven artifact
        Capability rootCap = metadataService.getSingletonCapability(resource, NsMvnArtifactIdentity.NAMESPACE__MVN_ARTIFACT_IDENTITY);

        // add stuff to that capability
        // TODO: null checking
        rootCap.setAttribute(NsMvnArtifactIdentity.ATTRIBUTE__ARTIFACT_ID, pomModel.getArtifactId());
        rootCap.setAttribute(NsMvnArtifactIdentity.ATTRIBUTE__GROUP_ID, pomModel.getGroupId());
        rootCap.setAttribute(NsMvnArtifactIdentity.ATTRIBUTE__VERSION, pomModel.getVersion());
    }

    @Override
    public Resource afterUploadToBuffer(Resource resource, Buffer buffer, String name) throws RefusedArtifactException {
        loadCrceIdentity(resource);
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

    public void setMetadataService(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    public void setMetadataFactory(MetadataFactory metadataFactory) {
        this.metadataFactory = metadataFactory;
    }
}
