package cz.zcu.kiv.crce.rest.client.indexer.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.osgi.framework.Bundle;

public class ConfigTools {

    private static final String JAR_URI_SCHEME = "jar";
    private static final String BUNDLE_URI_SCHEME = "bundle";
    private static Set<String> httpTypeEnums = null;
    private static EnumConfigMap enumDefinitionsMap = null;
    private static MethodConfigMap methodDefinitionsMap = null;
    private static EDataContainerConfigMap eDataContainerConfigMap = null;

    private static final String DEF_DIR_NAME = "definition";
    private static final String DEF_DIR_ABS = "/" + DEF_DIR_NAME;
    private static final List<String> configs =
            List.of(DEF_DIR_ABS + "/" + "jax-rs.yml", DEF_DIR_ABS + "/" + "spring_resttemplate.yml",
                    DEF_DIR_ABS + "/" + "spring_webclient.yml");
    private static final String DEF_DIR_REL = DEF_DIR_NAME + "/";
    private static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    /**
     * Preloads all filenames into list (JAR version)
     * 
     * @param path Path to resource file
     * @throws IOException
     */
    private static List<String> loadFilesFromJar(String path) throws IOException {
        final String jarPath = path.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        JarFile jarFile = new JarFile(jarPath);
        Enumeration<JarEntry> entries = jarFile.entries();
        List<String> filesInDirectory = new LinkedList<String>();

        while (entries.hasMoreElements()) {
            final JarEntry entry = entries.nextElement();
            final String entryName = entry.getName();

            if (entryName.startsWith(DEF_DIR_REL) && !entryName.equals(DEF_DIR_REL)) {
                filesInDirectory.add("/" + entryName);
            }
        }

        jarFile.close();
        return filesInDirectory;
    }

    /**
     * Wrappes loader for configuration file
     * 
     * @param defDirPath Directory of an configuration file
     * @param file Filename of an configuration file
     * @throws Exception
     */
    private static void loadConfigurationFile(String defDirPath, String file) throws Exception {
        final String path = defDirPath + "/" + file;
        loadConfigurationFile(path);
    }

    /**
     * Loads configuration into private static field
     * 
     * @param path Path to configuration file
     * @throws Exception
     */
    private static void loadConfigurationFile(String path) throws Exception {
        final InputStream inputStream = ConfigTools.class.getResourceAsStream(path);
        if (inputStream == null)
            throw new Exception("Resource not found: " + path);
        Config config =
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .readValue(inputStream, Config.class);
        Set<ApiCallConfig> methodsConfig = config.getMethods();
        Set<EnumConfigItem> enumConfigs = config.getEnums();
        Set<String> httpTypeContainer = config.getGenerics();
        Set<EDataContainerConfig> eDataConfig = config.getEndpointDataContainers();
        if (methodsConfig != null) {
            for (ApiCallConfig one : methodsConfig) {
                final String methodDefKey = one.getClassName();
                if (!methodDefinitionsMap.containsKey(methodDefKey)) {
                    methodDefinitionsMap.put(methodDefKey, new HashMap<>());
                }
                for (ApiCallMethodConfig md : one.getMethods()) {
                    methodDefinitionsMap.get(methodDefKey).put(md.getName(), md);
                }
            }
        }
        if (enumConfigs != null) {
            for (EnumConfigItem one : enumConfigs) {
                final String enumDefKey = one.getClassName();
                if (!enumDefinitionsMap.containsKey(enumDefKey)) {
                    enumDefinitionsMap.put(enumDefKey, new HashMap<>());
                }
                for (EnumFieldOrMethodConfig field : one.getFields()) {
                    enumDefinitionsMap.get(enumDefKey).put(field.getName(), field);
                }
            }
        }
        if (httpTypeContainer != null) {
            httpTypeEnums.addAll(httpTypeContainer);
        }
        if (eDataConfig != null) {
            for (EDataContainerConfig one : eDataConfig) {
                final String key = one.getClassName();
                if (!eDataContainerConfigMap.containsKey(key)) {
                    eDataContainerConfigMap.put(key, new HashMap<>());
                }
                for (EDataContainerMethodConfig method : one.getMethods()) {
                    eDataContainerConfigMap.get(key).put(method.getName(), method);
                }
            }
        }
    }

    /**
     * Loads configuration file into definition map
     * 
     * @throws Exception
     */
    private static void loadDefinitions() {
        try {
            final URL resource_url = ConfigTools.class.getResource(DEF_DIR_ABS);
            // System.out.println("RESOURCE_URL="+resource_url.toURI().getScheme());
            if (resource_url == null) {
                throw new Exception("Directory not found: " + DEF_DIR_ABS);
            }

            File directory = null;
            List<String> filesInDirectory = null;
            methodDefinitionsMap = new MethodConfigMap();
            String fullPath = resource_url.getFile();

            if (resource_url.toURI().getScheme().equals(JAR_URI_SCHEME)) { // inside JAR

                filesInDirectory = loadFilesFromJar(fullPath);
                for (final String path : filesInDirectory) {
                    loadConfigurationFile(path);
                }
            } else if (resource_url.toURI().getScheme().equals(BUNDLE_URI_SCHEME)) {
                for (final String path : configs) {
                    loadConfigurationFile(path);
                }
            } else {
                directory = new File(resource_url.toURI());
                filesInDirectory = Arrays.asList(directory.list());
                for (final String file : filesInDirectory) {
                    loadConfigurationFile(DEF_DIR_ABS, file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initStructures() {
        httpTypeEnums = new HashSet<>();
        methodDefinitionsMap = new MethodConfigMap();
        enumDefinitionsMap = new EnumConfigMap();
        eDataContainerConfigMap = new EDataContainerConfigMap();
    }

    /**
     * 
     * @return Definition of methods
     */
    public static MethodConfigMap getMethodDefinitions() {
        if (methodDefinitionsMap == null) {
            initStructures();
            loadDefinitions();
        }
        return methodDefinitionsMap;
    }

    /**
     * 
     * @return Definition of enums
     */
    public static EnumConfigMap getEnumDefinitions() {
        if (enumDefinitionsMap == null) {
            initStructures();
            loadDefinitions();
        }
        return enumDefinitionsMap;
    }

    /**
     * @return the typeHolders
     */
    public static Set<String> getGenerics() {
        if (httpTypeEnums == null) {
            initStructures();
            loadDefinitions();
        }
        return httpTypeEnums;
    }

    /**
     * @return the eDataContainerConfigMap
     */
    public static EDataContainerConfigMap getEDataContainerConfigMap() {
        if (eDataContainerConfigMap == null) {
            initStructures();
            loadDefinitions();
        }
        return eDataContainerConfigMap;
    }

}
