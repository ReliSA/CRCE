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
import javax.annotation.ParametersAreNonnullByDefault;

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

/**
 * Implementation of <code>ActionHandler</code> which compares commited OSGi
 * bundle to existing bundles with the same symbolic name and sets a new version
 * based on comparison.
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 * @author Jan Reznicek
 */
@ParametersAreNonnullByDefault
public class VersioningActionHandler extends AbstractActionHandler {


    private static final Logger logger = LoggerFactory.getLogger(VersioningActionHandler.class);

    private static final String CATEGORY_VERSIONED = "versioned";

    private volatile TaskRunnerService taskRunnerService;   // injected by dependency manager
    private volatile VersionService versionService;   // injected by dependency manager
    //private volatile ResourceDAO m_resourceDao;         //injected by dependency manager
    private volatile MetadataService metadataService; //injected by dependency manager
    private volatile MetadataFactory metadataFactory; //injected by dependency manager
    private volatile ResourceIndexerService resourceIndexer; //injected by dependency manager

    //private int BUFFER_SIZE = 8 * 1024;

    /**
     * Create file from bundle in form of InputSteam.
     *
     * @param bundleAsStream bundle as InputStream
     * @return file with bundle
     * @throws IOException reading from stream or creation of file or write to file failed
     */
    private File copyFromStream(InputStream bundleAsStream, URI newLocation) throws IOException {

        File bundleFile = new File(newLocation);
        try (OutputStream output = new FileOutputStream(bundleFile)) {
            IOUtils.copy(bundleAsStream, output);
        }

        return bundleFile;
    }

    /**
     * Create version of the OSGi bundle, before is committed to the Store.
     * Before resource is committed to the Store, check if the resource is unversioned OSGi bundle.
     * If resource is OSGi bundle, generate its version based on difference from previous bundle with same name in repository.
     * @param resource
     * @param store
     * @return
     * @throws cz.zcu.kiv.crce.repository.RefusedArtifactException
     */
    @Override
    public Resource beforePutToStore(Resource resource, Store store) throws RefusedArtifactException {
        logger.debug("Entering beforePutToStore method");

        //get all categories of the resource
        List<String> categories = metadataService.getCategories(resource);

        //not available for versioning
        if (resource == null || !categories.contains("osgi")) { // TODO constant
            logger.debug("Resource " + (resource == null ? "is null." : "doesnt have category osgi."));
            return resource;
        }

        //not versioned yet, do it now
        if (!categories.contains(CATEGORY_VERSIONED)) {
            //get symbolic name of the resource
            Capability cap = metadataService.getSingletonCapability(resource, NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
            String name = cap.getAttributeValue(NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME);

            logger.debug("Resource {} doesnt have category versioned", name);

            Resource baseResource = null;
            if (name != null) {
                Requirement filterByName = metadataFactory.createRequirement(NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
                filterByName.addAttribute(NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME, name);

                /*
                 * candidate for base resource is selected as bundle with same symbolic name and highest version in repository.
                 */
                logger.debug("Searching for previous versions of: {}", name);
                Version candVersion = null;

                for (Resource i : store.getResources(filterByName)) {
                    Capability iCapability = metadataService.getSingletonCapability(i, NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
                    String iName = iCapability.getAttributeValue(NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME);
                    Version iVersion = new Version(iCapability.getAttributeValue(NsOsgiIdentity.ATTRIBUTE__VERSION));

                    logger.debug("Candidate: {}", iName);
                    if (baseResource == null || candVersion == null || candVersion.compareTo(iVersion) < 0) {
                        logger.debug("Candidate selected");
                        baseResource = i;
                        candVersion = iVersion;
                    }
                }
            }

            if (baseResource == null) { //no candidate found, therefore this is the base version in the repository

                logger.debug("No candidate found");
                metadataService.addCategory(resource, CATEGORY_VERSIONED);
                metadataService.addCategory(resource, "initial-version"); // TODO constant

            } else {

                logger.debug("Candidate found, commencing bundle comparison.");

                URI baseUri = metadataService.getUri(baseResource);
                URI resourceUri = metadataService.getUri(resource);

                logger.debug("Base URI: {}", baseUri);
                logger.debug("Uploaded bundle URI: {}", resourceUri);

                try (InputStream baseInputStream = new FileInputStream(new File(baseUri));
                    InputStream resourceInputStream = new FileInputStream(new File(resourceUri))) {

                    HashMap<String, String> options = new HashMap<>();

                    //micro part of version is generated too
                    options.put("keep-micro-if-identical", "false"); // TODO constant

                    try (InputStream versionedBundleIs =
                            versionService.createVersionedBundle(baseInputStream, resourceInputStream, options)) {

                        if (versionedBundleIs != null) {
                            updateResourceMetadata(resource, versionedBundleIs);
                        }

                    } catch (IOException e) {
                        logger.error("Could not reload changed resource", e);
                        throw new RefusedArtifactException("I/O error when versioning " + resource.getId() + "(" + e.getMessage() + ")");
                    }

                    metadataService.addCategory(resource, CATEGORY_VERSIONED);
                } catch (IOException ex) {
                    logger.error("Could not update version due to I/O error", ex);
                } catch (IllegalArgumentException e) {
                    logger.warn("Could not update version (Not osgi bundle): {}", e.getMessage());
                    metadataService.addCategory(resource, "non-versionable"); // TODO constant
                } catch (BundlesIncomparableException e) {
                    logger.warn("Could not update version (incomparable bundles): {}", e.getMessage());
                    metadataService.addCategory(resource, "non-versionable"); // TODO constant
                } catch (Exception e) {
                    logger.error("Could not update version (unknown error)", e);
                }
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
    private void updateResourceMetadata(Resource resource, @Nonnull InputStream versionedBundleIs) throws IOException {
        //create resource from file with bundle with generated version
        URI resourceURI = metadataService.getUri(resource);
        File versionedBundleFile = copyFromStream(versionedBundleIs, resourceURI);
        logger.debug("Created resource from uploaded bundle:" + versionedBundleFile.toURI());

        //get modified metadata directly from file
        Resource versionedRes = resourceIndexer.indexResource(versionedBundleFile);

        metadataService.setSize(resource, versionedBundleFile.length());
        String[] namespacesToReplace = {NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY, NsOsgiBundle.NAMESPACE__OSGI_BUNDLE};

        Capability tmp;
        for (String ns : namespacesToReplace) {
            tmp = metadataService.getSingletonCapability(resource, ns);
            metadataService.removeCapability(resource, tmp);
            //add new version of capability
            tmp = metadataService.getSingletonCapability(versionedRes, ns);
            metadataService.addRootCapability(resource, tmp);
        }
    }

    /**
     * Save an original file name and version to metadata (as resource capability) when resource is uploaded to Buffer.
     * Activate only for unversioned osgi resources
     * @param resource
     * @param buffer
     * @param name
     * @return
     * @throws cz.zcu.kiv.crce.repository.RefusedArtifactException
     */
    @Override
    public Resource onUploadToBuffer(Resource resource, Buffer buffer, String name) throws RefusedArtifactException {
        logger.debug("Entering onUploadToBuffer");
        List<String> categories = metadataService.getCategories(resource);

        Capability osgiIdentity = metadataService.getSingletonCapability(resource, NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
        String osgiName = osgiIdentity.getAttributeValue(NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME);
        String osgiVersion = osgiIdentity.getAttributeValue(NsOsgiIdentity.ATTRIBUTE__VERSION);
        checkAlreadyInBuffer(osgiName, buffer);

        if (!categories.contains("osgi")) { // TODO constant
            logger.debug("Resource doesnt have category osgi");
            return resource;
        }
        if (!categories.contains("versioned")) { // TODO constant
            logger.debug("Resource doesnt have category versioned.");

            String ext = name.substring(name.lastIndexOf("."));

            List<Capability> caps = resource.getCapabilities("file"); // TODO constant
            Capability cap = caps.isEmpty() ? metadataFactory.createCapability("file") : caps.get(0); // TODO constant
            cap.setAttribute("original-name", String.class, name); // TODO constant
            cap.setAttribute("name", String.class, osgiName + "-" + (osgiVersion != null ? osgiVersion : "0.0.0") + ext); // TODO constant
            // TODO composition of name could be done in some service (maybe some OSGi service similar to MetadataService)

            cap.setAttribute("original-version", String.class, osgiVersion != null ? osgiVersion : "unknown"); // TODO constant

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
            Task<Object> compTask = new CompatibilityCalculationTask(resource.getId(), resource);
            taskRunnerService.scheduleTask(compTask);
        }
        return resource;
    }

    private void checkAlreadyInBuffer(@Nullable String resourceName, @Nonnull Buffer buffer) throws RefusedArtifactException {
        logger.debug("Checking for presence of a resource with the same symbolic name in the buffer.");

        if (resourceName == null) {
            return; //for sure not in buffer
        }

        Requirement filteryByName = metadataFactory.createRequirement(NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
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
