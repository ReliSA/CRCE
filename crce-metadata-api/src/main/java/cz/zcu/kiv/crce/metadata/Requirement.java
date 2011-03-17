package cz.zcu.kiv.crce.metadata;

/**
 *
 * @author kalwi
 */
public interface Requirement {
    
    String getName();

    String getFilter();

    boolean isMultiple();

    boolean isOptional();

    boolean isExtend();

    String getComment();
    
    boolean isWritable();
    
    
    Requirement setFilter(String filter);
    
    Requirement setMultiple(boolean multiple);
    
    Requirement setOptional(boolean optional);
    
    Requirement setExtend(boolean extend);
    
    Requirement setComment(String comment);

}
