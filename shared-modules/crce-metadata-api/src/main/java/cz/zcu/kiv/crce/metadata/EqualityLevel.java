package cz.zcu.kiv.crce.metadata;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public enum EqualityLevel {

    /**
     * Objects are compared by their key attributes.
     */
    KEY,
    /**
     * Objects are compared by their attribute values excluding key attribute.
     * References to other objects in hierarchical structure are ignored.
     */
    SHALLOW_NO_KEY,
    /**
     * Objects are compared by their attribute values including key attribute.
     * References to other objects in hierarchical structure are ignored.
     */
    SHALLOW_WITH_KEY,
    /**
     * Objects are compared by their attribute values excluding key attribute.
     * References to other objects in hierarchical structure are also compared.<p>
     */
    DEEP_NO_KEY,
    /**
     * Objects are compared by their attribute values including key attribute.
     * References to other objects in hierarchical structure are also compared.<p>
     */
    DEEP_WITH_KEY
}
