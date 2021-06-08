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
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.RequestParamConfig;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.WSClientConfig;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.RequestParamFieldType;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.WSClientMethodConfig;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures.IWSClient;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures.RequestMethod;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures.RequestParam;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures.SettingsMethod;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures.SettingsType;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures.WSClient;
import cz.zcu.kiv.crce.rest.client.indexer.shared.HttpMethod;
import cz.zcu.kiv.crce.rest.client.indexer.shared.HttpMethodExt;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.WSClientDataConfig;


public class ConfigTools {

    private static final String JAR_URI_SCHEME = "jar";
    private static final String BUNDLE_URI_SCHEME = "bundle";
    private static Map<String, Map<String, RequestParam>> requestParams = null;
    private static Map<String, Map<String, IWSClient>> wsClients = null;
    private static Map<String, Map<String, IWSClient>> wsClientData = null;

    private static final String DEF_DIR_NAME = "definition" + "/v2";
    private static final String DEF_DIR_ABS = "/" + DEF_DIR_NAME;
    private static final List<String> configs = List.of(DEF_DIR_ABS + "/" + "new_version.yml");
    private static final String DEF_DIR_REL = DEF_DIR_NAME + "/";
    private static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    private static HttpMethod httpMethodExtToHttpMethod(HttpMethodExt httpMethodExt) {
        if (httpMethodExt.ordinal() > (HttpMethod.values().length - 1)) {
            return null;
        }
        return HttpMethod.values()[httpMethodExt.ordinal()];
    }

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
            final Map<String, ArgConfig> argDefinitions) throws Exception {
        if (wsClientConfigs != null) {
            for (final WSClientConfig item : wsClientConfigs) {
                Set<String> methodOwners = item.getClassNames();
                for (final String methodOwner : methodOwners) {
                    if (!wsClients.containsKey(methodOwner)) {
                        wsClients.put(methodOwner, new HashMap<>());
                    }
                }
                methodOwners = item.getInterfaces();
                for (final String methodOwner : methodOwners) {
                    if (!wsClients.containsKey(methodOwner)) {
                        wsClients.put(methodOwner, new HashMap<>());
                    }
                }
                final Map<SettingsType, Set<WSClientMethodConfig>> settings = item.getSettings();
                setSettingsMethod(settings, argDefinitions, methodOwners, wsClientData);
                for (final HttpMethodExt httpMethodType : item.getRequest().keySet()) {
                    for (final WSClientMethodConfig currentMethod : item.getRequest()
                            .get(httpMethodType)) {
                        final Set<Set<ArgConfig>> argConfig =
                                loadArgs(currentMethod.getArgsReferences(), argDefinitions);
                        final Set<Set<ArgConfig>> varArgConfig =
                                loadArgs(currentMethod.getVarArgsReferences(), argDefinitions);
                        RequestMethod settingsMethod = new RequestMethod(currentMethod.getReturns(),
                                httpMethodType, argConfig, varArgConfig);

                        for (final String name : currentMethod.getNames()) {
                            final WSClient client = new WSClient(name,
                                    httpMethodExtToHttpMethod(httpMethodType), argConfig);
                            for (final String methodOwner : methodOwners) {
                                wsClients.get(methodOwner).put(client.getName(), client);
                            }
                        }
                    }
                }
            }
        }
    }


    private static void setSettingsMethod(Map<SettingsType, Set<WSClientMethodConfig>> settings,
            final Map<String, ArgConfig> argDefinitions, Set<String> methodOwners,
            Map<String, Map<String, IWSClient>> data) throws Exception {
        for (final SettingsType settingsKey : settings.keySet()) {
            final Set<WSClientMethodConfig> settingsScope = settings.get(settingsKey);
            for (final WSClientMethodConfig methodConfig : settingsScope) {
                final Set<Set<ArgConfig>> argConfig =
                        loadArgs(methodConfig.getArgsReferences(), argDefinitions);

                final Set<Set<ArgConfig>> varArgConfig =
                        loadArgs(methodConfig.getVarArgsReferences(), argDefinitions);
                SettingsMethod settingsMethod = new SettingsMethod(methodConfig.getReturns(),
                        settingsKey, argConfig, varArgConfig);
                for (final String methodName : methodConfig.getNames()) {
                    for (final String methodOwner : methodOwners) {
                        data.get(methodOwner).put(methodName, settingsMethod);
                    }
                }
            }
        }
    }

    private static Set<Set<ArgConfig>> loadArgs(Set<Set<String>> argsReferences,
            Map<String, ArgConfig> argDefinitions) throws Exception {

        Set<Set<ArgConfig>> output = new HashSet<>();
        for (final Set<String> argReferences : argsReferences) {

            final Set<ArgConfig> args = new HashSet<>();
            output.add(args);
            for (final String argReference : argReferences) {
                if (argDefinitions.containsKey(argReference)) {
                    args.add(argDefinitions.get(argReference));
                } else {
                    throw new Exception("Arg reference of " + argReference + " not found");
                }
            }
        }
        return output;
    }

    /**
     * Processes ws client configurations
     * @param wsClientConfigs ws client data configurations
     * @param argDefinitions definitions of arguments for client methods
     * @throws Exception
     */
    private static void processWSClientDataConfigurations(
            final Set<WSClientDataConfig> wsClientDataConfigs,
            final Map<String, ArgConfig> argDefinitions) throws Exception {
        if (wsClientDataConfigs != null) {
            for (final WSClientDataConfig item : wsClientDataConfigs) {
                //load all classes
                Set<String> methodOwners = item.getClasses();
                for (final String methodOwner : methodOwners) {
                    if (!wsClientData.containsKey(methodOwner)) {
                        wsClientData.put(methodOwner, new HashMap<>());
                    }
                }
                //load all interfaces
                methodOwners = item.getInterfaces();
                for (final String methodOwner : methodOwners) {
                    if (!wsClientData.containsKey(methodOwner)) {
                        wsClientData.put(methodOwner, new HashMap<>());
                    }
                }
                final Map<SettingsType, Set<WSClientMethodConfig>> settings = item.getSettings();
                setSettingsMethod(settings, argDefinitions, methodOwners, wsClientData);
            }
        }
    }

    /**
     * Processes class which contains prameters in its fields for requests (header types, http types etc.)
     * @param requestParamsConfigs Enum configurations
     */
    private static void processRequestParamConfiguration(
            final Set<RequestParamConfig> requestParamsConfigs) {
        if (requestParamsConfigs != null) {
            for (final RequestParamConfig one : requestParamsConfigs) {
                for (final String className : one.getClassNames()) {
                    if (!requestParams.containsKey(className)) {
                        requestParams.put(className, new HashMap<>());
                    }
                }
                for (final RequestParamFieldType enumType : one.getFields().keySet()) {
                    final Map<String, String> fields = one.getFields().get(enumType);
                    for (final String key : fields.keySet()) {
                        final RequestParam newEnumItem =
                                new RequestParam(enumType, fields.get(key));
                        for (final String className : one.getClassNames()) {
                            requestParams.get(className).put(key, newEnumItem);
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
        final Set<RequestParamConfig> requestParamConfigs = config.getRequestParams();
        final Set<WSClientDataConfig> wsDataConfigs = config.getWsClientDataHolders();


        processWSClientConfigurations(wsClientConfigs, argDefinitions);
        processWSClientDataConfigurations(wsDataConfigs, argDefinitions);
        processRequestParamConfiguration(requestParamConfigs);

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
        requestParams = new HashMap<>();
        wsClientData = new HashMap<>();
    }

    /**
     * 
     * @return WS clients
     */
    public static Map<String, Map<String, IWSClient>> getWSClients() {
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
    public static Map<String, Map<String, IWSClient>> getWSClientDataContainers() {
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
    public static Map<String, Map<String, RequestParam>> getRequestParams() {
        if (requestParams == null) {
            initStructures();
            loadDefinitions();
        }
        return requestParams;
    }


    public static void main(final String[] args) {
        getWSClients();
        System.out.println("TEST");
    }

}
