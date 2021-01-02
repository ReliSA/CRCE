package cz.zcu.kiv.crce.metadata.json.internal;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Constants {

    String REPOSITORY__URI = "uri";

    String ATTRIBUTE__NAME = "name";
    String ATTRIBUTE__TYPE = "type";
    String ATTRIBUTE__OPERATOR = "operator";
    String ATTRIBUTE__VALUE = "value";
    String ATTRIBUTE__VERSION_MAJOR = "major";
    String ATTRIBUTE__VERSION_MINOR = "minor";
    String ATTRIBUTE__VERSION_MICRO = "micro";
    String ATTRIBUTE__VERSION_QUALIFIER = "qualifier";

    String RESOURCE__ID = "id";
    String RESOURCE__REPOSITORY = "repository";
    String RESOURCE__CAPABILITIES = "capabilities";
    String RESOURCE__REQUIREMENTS = "requirements";
    String RESOURCE__PROPERTIES = "properties";

    String CAPABILITY__ID = "id";
    String CAPABILITY__NAMESPACE = "namespace";
    String CAPABILITY__ATTRIBUTES = "attributes";
    String CAPABILITY__DIRECTIVES = "directives";
    String CAPABILITY__REQUIREMENTS = "requirements";
    String CAPABILITY__PROPERTIES = "properties";
    String CAPABILITY__CHILDREN = "children";

    String REQUIREMENT__ID = "id";
    String REQUIREMENT__NAMESPACE = "namespace";
    String REQUIREMENT__ATTRIBUTES = "attributes";
    String REQUIREMENT__DIRECTIVES = "directives";
    String REQUIREMENT__CHILDREN = "children";

    String PROPERTY__ID = "id";
    String PROPERTY__NAMESPACE = "namespace";
    String PROPERTY__ATTRIBUTES = "attributes";
}
