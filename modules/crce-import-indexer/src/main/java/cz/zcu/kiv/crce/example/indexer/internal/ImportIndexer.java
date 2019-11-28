package cz.zcu.kiv.crce.example.indexer.internal;

import cz.zcu.kiv.crce.example.indexer.namespace.NsExampleNs;
import cz.zcu.kiv.crce.example.indexer.namespace.NsImportPackage;
import cz.zcu.kiv.crce.metadata.*;
import cz.zcu.kiv.crce.metadata.indexer.AbstractResourceIndexer;
import cz.zcu.kiv.crce.metadata.namespace.NsCrceIdentity;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * This class represents example indexer (which is plugin in CRCE). This indexer (/plugin) will save JAR in temp folder
 * in current system, extracts given JAR and then reads imported packages from all .class files in given JAR.
 * Number of imported packages is saved as property of given resource and list of these packages is saved as
 * requirement of this resource.
 *
 * @author vit.mazin@seznam.cz
 */
public class ImportIndexer extends AbstractResourceIndexer { //AbstractResourceIndexer implements Plugin interface
    private static final Logger logger = LoggerFactory.getLogger(ImportIndexer.class);

    private volatile MetadataFactory metadataFactory; //injected
    private volatile MetadataService metadataService;

    private static final String EXAMPLE_CATEGORY = "example-category";

    /**
     * This method saves all imported packages as requirement for given resource (and count of packages as property).
     *
     * @param input - stream from JAR which is being indexed
     * @param resource - resource object which represents given JAR
     * @return list of categories which this indexer provides
     */
    @Override
    public List<String> index(InputStream input, Resource resource) {
        logger.debug("Import indexer - starting indexing");

        //checking if file is zip
        if (!checkResourceIfZip(resource)) {
            logger.debug("Resource is not zip file");
            return Collections.emptyList();
        }

        //saving jar to tmp
        String jarPath;
        try {
            jarPath = saveJarToTmp(input);
        } catch (IOException ie) {
            logger.error("Could not save JAR");
            return Collections.emptyList();
        }

        //unziping into temp dir
        String destination = System.getProperty("java.io.tmpdir") + File.pathSeparator + "resource" + System.currentTimeMillis();
        try {
            ZipFile zipFile = new ZipFile(new File(jarPath));
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            logger.error("Could not unzip JAR");
            return Collections.emptyList();
        }

        //find all .class files of given JAR
        String[] extensions = {"class"};
        File dir = new File(destination);
        Collection<File> files = FileUtils.listFiles(dir, extensions, true);

        //reading imported packages from all .class files in given JAR
        Set<String> jarClassesPackages = new HashSet<>(); //packages inside JAR
        Set<String> resultSet = new HashSet<>(); //All imported packages in JAR
        for (File file : files) {
            try {
                ImportedPackagesReader reader = new ImportedPackagesReader(file);
                reader.readConstantPool();
                resultSet.addAll(reader.getImportedPackages());

                String currPackage = reader.getPackageOfCurrentFile();
                if (currPackage != null) {
                    jarClassesPackages.add(currPackage);
                }
            } catch (IOException e) {
                logger.warn("Could not open file " + file.getAbsolutePath());
            }
        }

        //remove packages from java.* and packages from this JAR from result
        Set<String> found = new HashSet<>();
        for (String jarPkg : jarClassesPackages) {
            for (String result : resultSet) {
                if (result.contains(jarPkg) || result.substring(0, 4).equals("java")) {
                    found.add(result);
                }
            }
            resultSet.removeAll(found);
            found.clear();
        }

        List<String> resultList = null;
        if (resultSet.size() > 0) {
            resultList = new ArrayList<>(resultSet);
            resultList.sort(String::compareTo);
        }

        //saving number of imported packages as property under example namespace
        Property importProp = metadataFactory.createProperty(NsExampleNs.NAMESPACE__EXAMPLE_NS);
        importProp.setAttribute(NsExampleNs.ATTRIBUTE__IMPORT_COUNT, "" + resultSet.size());
        resource.addProperty(importProp);

        if (resultList != null) {
            //saving list of imported packages as requirement under JAR import namespace
            Requirement req = metadataFactory.createRequirement(NsImportPackage.NAMESPACE__JAR_IMPORT);
            req.addAttribute(NsImportPackage.ATTRIBUTE__IMPORTED_PACKAGES, resultList);
            resource.addRequirement(req);
        }

        //saving example category which this indexer provides
        metadataService.addCategory(resource,EXAMPLE_CATEGORY);

        try {
            cleanup(jarPath, destination);
        } catch (IOException ie) {
            logger.error("Could not remove JAR file");
        }

        return Collections.singletonList(EXAMPLE_CATEGORY);
    }

    /**
     * Method checks if given resource is ZIP. This information will be already present in resource from file indexer
     * from CRCE Core.
     *
     * @param resource - given resource
     * @return true if resource is zip
     */
    private boolean checkResourceIfZip(Resource resource) {
        Capability identity = metadataService.getSingletonCapability(resource, NsCrceIdentity.NAMESPACE__CRCE_IDENTITY, false);
        if (identity == null) {
            return false;
        }
        List<String> categories = identity.getAttributeValue(NsCrceIdentity.ATTRIBUTE__CATEGORIES);
        return categories != null && categories.contains("zip");
    }

    /**
     * This method saves given JAR (as stream) into temp folder of current OS.
     *
     * @param input - stream from source JAR
     * @return absolute path of JAR
     * @throws IOException - if stream is corrupted
     */
    private String saveJarToTmp(InputStream input) throws IOException {
        String tmpName = "Resource" + System.currentTimeMillis();
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File tempFile = File.createTempFile(tmpName, ".jar", tempDir);

        FileUtils.copyInputStreamToFile(input, tempFile);

        return tempFile.getAbsolutePath();
    }

    /**
     * Method removes JAR (and all other files which were unzipped from this JAR) from temp system
     *
     * @param jar - Path to JAR
     * @param unzipped - Path to unzipped JAR
     * @throws IOException - if files are not present
     */
    private void cleanup(String jar, String unzipped) throws IOException {
        FileUtils.forceDelete(new File(jar));
        FileUtils.deleteDirectory(new File(unzipped));
    }

    /**
     * Returns list of provided categories. Overridden from AbstractResourceIndexer.
     *
     * @return list of provided categories
     */
    @Override
    public List<String> getProvidedCategories() {
        List<String> provided = new ArrayList<>();
        Collections.addAll(provided, EXAMPLE_CATEGORY);
        return provided;
    }

    /**
     * Returns map of indexed attribute types under namespaces.
     *
     * @return map of indexed attributes
     */
    @Override
    public Map<String, List<AttributeType>> getIndexedAttributes() {
        Map<String, List<AttributeType>> map = new HashMap<>();
        map.put(NsExampleNs.NAMESPACE__EXAMPLE_NS, Collections.singletonList(NsExampleNs.ATTRIBUTE__IMPORT_COUNT));
        map.put(NsImportPackage.NAMESPACE__JAR_IMPORT, Collections.singletonList(NsImportPackage.ATTRIBUTE__IMPORTED_PACKAGES));

        return map;
    }

    /**
     * Returns list of required categories so this indexer could work with given resource.
     *
     * @return list of required categories
     */
    @Override
    public List<String> getRequiredCategories() {
        return Collections.singletonList("zip");
    }

    /**
     * Returns priority of this plugin. Overridden from AbstractPlugin.
     *
     * @return priority of this Plugin
     */
    @Override
    public int getPluginPriority() {
        return Integer.MIN_VALUE; //ensure that example indexer will always used as last indexer (and also last plugin)
    }
}
