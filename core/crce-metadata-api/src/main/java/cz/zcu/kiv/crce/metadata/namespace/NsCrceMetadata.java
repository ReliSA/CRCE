package cz.zcu.kiv.crce.metadata.namespace;

import java.net.URI;
import java.util.List;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.type.Version;

/**
 * Date: 10.5.15
 *
 * @author Jakub Danek
 */
public class NsCrceMetadata {

    public static final AttributeType<String> ATTRIBUTE__NAME = new SimpleAttributeType<>("name", String.class);
    public static final AttributeType<URI> ATTRIBUTE__URI = new SimpleAttributeType<>("uri", URI.class);
    public static final AttributeType<String> ATTRIBUTE__FILE_NAME = new SimpleAttributeType<>("file-name", String.class);
    public static final AttributeType<Long> ATTRIBUTE__SIZE = new SimpleAttributeType<>("size", Long.class);



    public static class Identity {
        public static final String NAMESPACE__CRCE_IDENTITY = "crce.identity";

        public static final AttributeType<List<String>> ATTRIBUTE__TYPES = new ListAttributeType("types");
        public static final AttributeType<List<String>> ATTRIBUTE__CATEGORIES = new ListAttributeType("categories");
        public static final AttributeType<Version> ATTRIBUTE__VERSION = new SimpleAttributeType<>("version", Version.class);
    }


}
