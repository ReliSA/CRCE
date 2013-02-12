package cz.zcu.kiv.crce.metadata;

/**
 * Represents an Capability.
 * 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Capability extends AttributeProvider, DirectiveProvider {

    public String getNamespace();
    
    public Resource getResource();
}
