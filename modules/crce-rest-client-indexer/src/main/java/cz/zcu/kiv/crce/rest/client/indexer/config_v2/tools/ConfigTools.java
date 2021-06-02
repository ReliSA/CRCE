package cz.zcu.kiv.crce.rest.client.indexer.config_v2.tools;

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
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.ArgConfig;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.Config;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.EnumConfig;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.MethodArgType;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.WSClientMethodConfig;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures.EnumItem;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures.WSClient;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.WSClientConfig;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.MethodType;


public class ConfigTools {

    private static final String JAR_URI_SCHEME = "jar";
    private static final String BUNDLE_URI_SCHEME = "bundle";
    private static Map<String, EnumItem> enums = null;
    private static Map<String, Map<String, WSClient>> wsClients = null;
    private static Map<String, Map<String, WSClient>> wsClientData = null;

    private static final String DEF_DIR_NAME = "definition" + "/v2";
    private static final String DEF_DIR_ABS = "/" + DEF_DIR_NAME;
    private static final List<String> configs = List.of(DEF_DIR_ABS + "/" + "new_version.yml");
    private static final String DEF_DIR_REL = DEF_DIR_NAME + "/";
    private static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    /**
     * Preloads all filenames into list (JAR version)
     * 
     * @param path Path to resource file
     * @throws IOException
     */
    private static List<String> loadFilesFromJar(final String path) throws IOException {
        final String jarPath = path.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        final JarFile jarFile = new JarFile(jarPath);
        final Enumeration<JarEntry> entries = jarFile.entries();
        final List<String> filesInDirectory = new LinkedList<String>();

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
    private static void loadConfigurationFile(final String defDirPath, final String file)
            throws Exception {
        final String path = defDirPath + "/" + file;
        loadConfigurationFile(path);
    }

    /**
     * Processes ws client configurations
     * @param wsClientConfigs ws client configurations
     * @param argDefinitions definitions of arguments for client methods
     * @throws Exception
     */
    private static void processWSClientConfigurations(final Set<WSClientConfig> wsClientConfigs,
            final Map<String, ArgConfig> argDefinitions,
            final Map<String, Map<String, WSClient>> wsData) throws Exception {
        if (wsClientConfigs != null) {
            for (final WSClientConfig item : wsClientConfigs) {
                final String methodOwner = item.getClassName();
                if (!wsData.containsKey(methodOwner)) {
                    wsData.put(methodOwner, new HashMap<>());
                }
                for (final MethodType httpMethodType : item.getMethods().keySet()) {
                    for (final WSClientMethodConfig currentMethod : item.getMethods()
                            .get(httpMethodType)) {
                        final Set<Set<ArgConfig>> argConfig = new HashSet<>();
                        for (final Set<String> argReferences : currentMethod.getArgsReferences()) {

                            final Set<ArgConfig> args = new HashSet<>();
                            argConfig.add(args);
                            for (final String argReference : argReferences) {
                                if (argDefinitions.containsKey(argReference)) {
                                    args.add(argDefinitions.get(argReference));
                                } else {
                                    throw new Exception(
                                            "Arg reference of " + argReference + " not found");
                                }
                            }
                        }
                        for (final String name : currentMethod.getNames()) {
                            final WSClient client = new WSClient(methodOwner, name,
                                    EnumTools.methodTypeToHttpMethod(httpMethodType), argConfig);
                            wsData.get(client.getOwner()).put(client.getName(), client);
                        }
                    }
                }
            }
        }
    }

    /**
     * Processes enum configurations (header types, http types etc.)
     * @param enumConfigs Enum configurations
     */
    private static void processEnumConfigurations(final Set<EnumConfig> enumConfigs) {
        if (enumConfigs != null) {
            for (final EnumConfig one : enumConfigs) {
                final String enumDefKey = one.getClassName();
                if (!enums.containsKey(enumDefKey)) {
                    for (final MethodArgType enumType : one.getFields().keySet()) {
                        final Map<String, String> fields = one.getFields().get(enumType);
                        for (final String key : fields.keySet()) {
                            final EnumItem newEnumItem = new EnumItem(enumType, fields.get(key));
                            enums.put(key, newEnumItem);
                        }
                    }
                }
            }
        }
    }

    /**
     * Loads configuration into private static field
     * 
     * @param path Path to configuration file
     * @throws Exception
     */
    private static void loadConfigurationFile(final String path) throws Exception {
        final InputStream inputStream = ConfigTools.class.getResourceAsStream(path);

        if (inputStream == null) {
            throw new Exception("Resource not found: " + path);
        }

        final Config config =
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .readValue(inputStream, Config.class);

        final Map<String, ArgConfig> argDefinitions = config.getArgDefinitions();
        final Set<WSClientConfig> wsClientConfigs = config.getWsClients();
        final Set<EnumConfig> enumConfigs = config.getEnums();
        final Set<WSClientConfig> wsDataConfigs = config.getWsClientDataHolders();


        processWSClientConfigurations(wsClientConfigs, argDefinitions, wsClients);
        processWSClientConfigurations(wsDataConfigs, argDefinitions, wsClientData);
        processEnumConfigurations(enumConfigs);

    }

    /**
     * Loads configuration file into definition map
     * 
     * @throws Exception
     */
    private static void loadDefinitions() {
        try {
            final URL resource_url = ConfigTools.class.getResource(DEF_DIR_ABS);
            if (resource_url == null) {
                throw new Exception("Directory not found: " + DEF_DIR_ABS);
            }

            File directory = null;
            List<String> filesInDirectory = null;
            final String fullPath = resource_url.getFile();

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
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize needed structures
     */
    private static void initStructures() {
        wsClients = new HashMap<>();
        enums = new HashMap<>();
        wsClientData = new HashMap<>();
    }

    /**
     * 
     * @return WS clients
     */
    public static Map<String, Map<String, WSClient>> getWSClientConfigs() {
        if (wsClients == null) {
            initStructures();
            loadDefinitions();
        }
        return wsClients;
    }

    /**
     * 
     * @return Data containers for WS clients
     */
    public static Map<String, Map<String, WSClient>> wsClientData() {
        if (wsClientData == null) {
            initStructures();
            loadDefinitions();
        }
        return wsClientData;
    }

    /**
     * 
     * @return Definition of enums
     */
    public static Map<String, EnumItem> getEnumConfigs() {
        if (enums == null) {
            initStructures();
            loadDefinitions();
        }
        return enums;
    }


    public static void main(final String[] args) {
        getWSClientConfigs();
    }

}
