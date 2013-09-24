package cz.zcu.kiv.crce.metadata;

/**
 * Represents a requirement to a capability with the same name.
 * 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Requirement {
    
    String getName();

    String getFilter();

    boolean isMultiple();

    boolean isOptional();

    boolean isExtend();

    String getComment();
    
    boolean isWritable();
    
    boolean isSatisfied(Capability capability);
    
    Requirement setFilter(String filter);
    
    Requirement setMultiple(boolean multiple);
    
    Requirement setOptional(boolean optional);
    
    Requirement setExtend(boolean extend);
    
    Requirement setComment(String comment);

}
