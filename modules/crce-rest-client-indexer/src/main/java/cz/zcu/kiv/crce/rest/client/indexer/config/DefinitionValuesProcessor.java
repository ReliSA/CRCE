package cz.zcu.kiv.crce.rest.client.indexer.config;

public class DefinitionValuesProcessor {

    /**
     * Helper function for processing classnames in config. file
     * 
     * @param className Classname
     * @return
     */
    public static String processClassName(String className) {
        String convertedClassName = className.replace(".", "/");
        return convertedClassName;
    }
}
