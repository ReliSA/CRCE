package cz.zcu.kiv.crce.handler.versioning.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.versionGenerator.exceptions.BundlesIncomparableException;
import cz.zcu.kiv.osgi.versionGenerator.service.VersionService;

import cz.zcu.kiv.crce.concurrency.model.Task;
import cz.zcu.kiv.crce.concurrency.service.TaskRunnerService;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.indexer.ResourceIndexerService;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiBundle;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiIdentity;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;

/**
 * Implementation of <code>ActionHandler</code> which compares commited OSGi
 * bundle to existing bundles with the same symbolic name and sets a new version
 * based on comparison.
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 * @author Jan Reznicek
 */
public class VersioningActionHandler extends AbstractActionHandler implements ActionHandler {

    private TaskRunnerService m_taskRunnerService;   /* injected by dependency manager */

    private static final Logger logger = LoggerFactory.getLogger(VersioningActionHandler.class);

    private static final String CATEGORY_VERSIONED = "versioned";

    private volatile VersionService m_versionService;   /* injected by dependency manager */
    //private volatile ResourceDAO m_resourceDao;         //injected by dependency manager
    private volatile MetadataService m_metadataService; //injected by dependency manager
    private volatile MetadataFactory m_metadataFactory; //injected by dependency manager
    private volatile ResourceIndexerService m_resourceIndexer; //injected by dependency manager

    //private int BUFFER_SIZE = 8 * 1024;

    /**
     * Create file from bundle in form of InputSteam.
     *
     * @param bundleAsStream bundle as InputStream
     * @return file with bundle
     * @throws IOException reading from stream or creation of file or write to file failed
     */
    private File copyFromStream(InputStream bundleAsStream, URI newLocation) throws IOException {

        if (null == bundleAsStream)
            throw new IllegalArgumentException("'null' passed as input stream");

        OutputStream output = null;
        File bundleFile;
        try {
            bundleFile = new File(newLocation);
            output = new FileOutputStream(bundleFile);
            /*byte[] readBuffer = new byte[BUFFER_SIZE];
            for (int count = bundleAsStream.read(readBuffer); count != -1; count = bundleAsStream.read(readBuffer)) {
                output.write(readBuffer, 0, count);
            }*/
            IOUtils.copy(bundleAsStream, output);
        } finally {
            if (output != null) {
                try {
                    output.flush();
                } catch (IOException e) {
                    //ignore
                    logger.debug(e.getMessage(), e);
                } finally {
                    IOUtils.closeQuietly(output);
                }
            }
        }


        return bundleFile;
    }

    /**
     * Create version of the OSGi bundle, before is committed to the Store.
     * Before resource is committed to the Store, check if the resource is unversioned OSGi bundle.
     * If resource is OSGi bundle, generate its version based on difference from previous bundle with same name in repository.
     */
    @Override
    public Resource beforePutToStore(Resource resource, Store store) throws RefusedArtifactException {
        logger.debug("Entering beforePutToStore method");

        //get all categories of the resource
        List<String> categories = m_metadataService.getCategories(resource);

        //get symbolic name of the resource
        Capability cap = m_metadataService.getSingletonCapability(resource, NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
        String name = cap.getAttributeValue(NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME);

        //not available for versioning
        if (resource == null || !categories.contains("osgi")) {
            logger.debug("Resource " + (resource == null ? "is null." : "doesnt have category osgi."));
            return resource;
        }

        //not versioned yet, do it now
        if (!categories.contains(CATEGORY_VERSIONED)) {
            logger.debug("Resource {} doesnt have category versioned", name);

            Requirement filterByName = m_metadataFactory.createRequirement(NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
            filterByName.addAttribute(NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME, name);

            Version candVersion = null;
            Version iVersion;
            String iName;
            Capability iCapability;


            /*candidate for base resource is selected as bundle with same symbolic name
              and highest version in repository.*/
            logger.debug("Searching for previous versions of: {}", name);
            Resource baseResource = null;

            for (Resource i : store.getResources(filterByName)) {
                iCapability = m_metadataService.getSingletonCapability(i, NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
                iName = iCapability.getAttributeValue(NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME);
                iVersion = new Version(iCapability.getAttributeValue(NsOsgiIdentity.ATTRIBUTE__VERSION));

                logger.debug("Candidate: {}", iName);
                if (baseResource == null || candVersion.compareTo(iVersion) < 0) {
                    logger.debug("Candidate selected");
                    baseResource = i;
                    candVersion = iVersion;
                }
            }


            String category = null;
            if (baseResource == null) { //no candidate found, therefore this is the base version in the repository

                logger.debug("No candidate found");
                category = CATEGORY_VERSIONED;
                m_metadataService.addCategory(resource, "initial-version");

            } else {

                logger.debug("Candidate found, commencing bundle comparison.");
                InputStream versionedBundleIs = null;
                InputStream baseInputStream = null;
                InputStream resourceInputStream = null;
                try {
                    URI baseUri = m_metadataService.getUri(baseResource);
                    URI resourceUri = m_metadataService.getUri(resource);

                    logger.debug("Base URI: {}", baseUri);
                    baseInputStream = new FileInputStream(new File(baseUri));
                    logger.debug("Uploaded bundle URI: {}", resourceUri);
                    resourceInputStream = new FileInputStream(new File(resourceUri));

                    HashMap<String, String> options = new HashMap<String, String>();

                    //micro part of version is generated too
                    options.put("keep-micro-if-identical", "false");

                    versionedBundleIs = m_versionService.createVersionedBundle(baseInputStream, resourceInputStream, options);

                    category = CATEGORY_VERSIONED;
                } catch (IOException ex) {
                    logger.error("Could not update version due to I/O error", ex);
                    category = null;
                } catch (IllegalArgumentException e) {
                    logger.warn("Could not update version (Not osgi bundle): {}", e.getMessage());
                    category = "non-versionable";
                } catch (BundlesIncomparableException e) {
                    logger.warn("Could not update version (incomparable bundles): {}", e.getMessage());
                    category = "non-versionable";
                } catch (Exception e) {
                    logger.error("Could not update version (unknown error)", e);
                    category = null;
                } finally {
                    IOUtils.closeQuietly(baseInputStream);
                    IOUtils.closeQuietly(resourceInputStream);
                }

                try {

                    updateResourceMetadata(resource, versionedBundleIs);

                } catch (IOException e) {
                    logger.error("Could not reload changed resource", e);
                    throw new RefusedArtifactException("I/O error when versioning " + resource.getId() + "(" + e.getMessage() + ")");
                } finally {
                    IOUtils.closeQuietly(versionedBundleIs);
                }
            }

            if (category != null) {
                m_metadataService.addCategory(resource, category);
            }
        }

        return resource;
    }

    /**
     * Copy versioned bundle from temp into buffer (replace uploaded bundle) and update related
     * metadata.
     * @param resource reference to the resource in the buffer
     * @param versionedBundleIs input stream of the newly versioned bundle
     * @throws IOException
     */
    private void updateResourceMetadata(Resource resource, InputStream versionedBundleIs) throws IOException {
        //create resource from file with bundle with generated version
        URI resourceURI = m_metadataService.getUri(resource);
        File versionedBundleFile = copyFromStream(versionedBundleIs, resourceURI);
        logger.debug("Created resource from uploaded bundle:" + versionedBundleFile.toURI());

        //get modified metadata directly from file
        Resource versionedRes = m_resourceIndexer.indexResource(versionedBundleFile);

        m_metadataService.setSize(resource, versionedBundleFile.length());
        String[] namespacesToReplace = {NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY,
                NsOsgiBundle.NAMESPACE__OSGI_BUNDLE};

        Capability tmp;
        for (String ns : namespacesToReplace) {
            tmp = m_metadataService.getSingletonCapability(resource, ns);
            m_metadataService.removeCapability(resource, tmp);
            //add new version of capability
            tmp = m_metadataService.getSingletonCapability(versionedRes, ns);
            m_metadataService.addRootCapability(resource, tmp);
        }
    }

    /**
     * Save an original file name and version to metadata (as resource capability) when resource is uploaded to Buffer.
     * Activate only for unversioned osgi resources
     */
    @Override
    public Resource onUploadToBuffer(Resource resource, Buffer buffer, String name) throws RefusedArtifactException {
        logger.debug("Entering onUploadToBuffer");
        List<String> categories = m_metadataService.getCategories(resource);

        Capability osgiIdentity = m_metadataService.getSingletonCapability(resource, NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
        String osgiName = osgiIdentity.getAttributeValue(NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME);
        String osgiVersion = osgiIdentity.getAttributeValue(NsOsgiIdentity.ATTRIBUTE__VERSION);
        checkAlreadyInBuffer(osgiName, buffer);

        if (!categories.contains("osgi")) {
            logger.debug("Resource doesnt have category osgi");
            return resource;
        }
        if (!categories.contains("versioned")) {
            logger.debug("Resource doesnt have category versioned.");

            String ext = name.substring(name.lastIndexOf("."));

            List<Capability> caps = resource.getCapabilities("file");
            Capability cap = caps.size() == 0 ? m_metadataFactory.createCapability("file") : caps.get(0);
            cap.setAttribute("original-name", String.class, name);
            cap.setAttribute("name", String.class, osgiName + "-" + osgiVersion + ext);

            cap.setAttribute("original-version", String.class, osgiVersion);

        }
        return resource;
    }

    @Override
    public Resource afterPutToStore(Resource resource, Store store) throws RefusedArtifactException {
        /*
            After the resource is put to store, start calculation of its compatibility data.
         */
        if (resource == null) {
            return resource;
        }

        if (store == null) { //temporary disabled until migration finished
            Task compTask = new CompatibilityCalculationTask(resource.getId(), resource);
            m_taskRunnerService.scheduleTask(compTask);
        }
        return resource;
    }

    private void checkAlreadyInBuffer(@Nullable String resourceName, @Nonnull Buffer buffer) throws RefusedArtifactException {
        logger.debug("Checking for presence of a resource with the same symbolic name in the buffer.");

        if (resourceName == null) {
            return; //for sure not in buffer
        }

        Requirement filteryByName = m_metadataFactory.createRequirement(NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
        filteryByName.addAttribute(NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME, resourceName);

        logger.debug("Searching for previously buffered versions of: " + resourceName);
        buffer.toString(); //PMD hack, delete when buffer search implemented
        /*  NOT SUPPORTED YET
        if (buffer.getResources(filteryByName).size() != 0) {
            logger.debug("Resource with the same symbolic name found!");
            throw new RefusedArtifactException("There is already a resource with the same symbolic name in the buffer.", RefusedArtifactException.REASON.ALREADY_IN_BUFFER);
        } */

        logger.debug("No resource with the same symbolic name found in the buffer. Moving on...");
    }

    @Override
    public boolean isExclusive() {
        return true;
    }
}
