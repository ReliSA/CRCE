package cz.zcu.kiv.crce.metadata.osgi.namespace;

import cz.zcu.kiv.crce.metadata.AttributeType;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton class which holds information about namespaces and attributes
 * this indexer is capable of indexing.
 */
public class NsMap implements Serializable {

    /**
     * Map of namespace -> indexable attribtues.
     */
    private static Map<String, List<AttributeType>> attributeMap;

    public static Map<String, List<AttributeType>> getAttributeMap() {
        if (attributeMap == null) {
            initMap();
        }

        return attributeMap;
    }

    private static void initMap() {
        attributeMap = new HashMap<>();
        attributeMap.put(NsOsgiBundle.NAMESPACE__OSGI_BUNDLE, Arrays.asList(
                (AttributeType)NsOsgiBundle.ATTRIBUTE__MANIFEST_VERSION,
                NsOsgiBundle.ATTRIBUTE__PRESENTATION_NAME,
                NsOsgiBundle.ATTRIBUTE__SYMBOLIC_NAME,
                NsOsgiBundle.ATTRIBUTE__VERSION
        ));

        attributeMap.put(NsOsgiExecutionEnvironment.NAMESPACE__OSGI_EXECUTION_ENVIRONMENT, Arrays.asList(
                (AttributeType)NsOsgiExecutionEnvironment.ATTRIBUTE__EXECUTION_ENVIRONMENT
        ));

        attributeMap.put(NsOsgiFragment.NAMESPACE__OSGI_FRAGMENT, Arrays.asList(
                (AttributeType)NsOsgiFragment.ATTRIBUTE__HOST,
                NsOsgiFragment.ATTRIBUTE__VERSION
        ));

        attributeMap.put(NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY, Arrays.asList(
                (AttributeType)NsOsgiIdentity.ATTRIBUTE__CATEGORY,
                NsOsgiIdentity.ATTRIBUTE__COPYRIGHT,
                NsOsgiIdentity.ATTRIBUTE__DESCRIPTION,
                NsOsgiIdentity.ATTRIBUTE__DOCUMENTATION_URI,
                NsOsgiIdentity.ATTRIBUTE__LICENSES,
                NsOsgiIdentity.ATTRIBUTE__NAME,
                NsOsgiIdentity.ATTRIBUTE__PRESENTATION_NAME,
                NsOsgiIdentity.ATTRIBUTE__SOURCE_URI,
                NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME,
                NsOsgiIdentity.ATTRIBUTE__VERSION
        ));

        attributeMap.put(NsOsgiPackage.NAMESPACE__OSGI_PACKAGE, Arrays.asList(
                (AttributeType)NsOsgiPackage.ATTRIBUTE__NAME,
                NsOsgiPackage.ATTRIBUTE__VERSION
        ));

        attributeMap.put(NsOsgiService.NAMESPACE__OSGI_SERVICE, Arrays.asList(
                (AttributeType)NsOsgiService.ATTRIBUTE__NAME
        ));
    }
}
