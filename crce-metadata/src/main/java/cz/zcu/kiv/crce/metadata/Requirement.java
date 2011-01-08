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
    
    
    void setFilter(String filter);
    
    void setMultiple(boolean multiple);
    
    void setOptional(boolean optional);
    
    void setExtend(boolean extend);
    
    void setComment(String comment);

}
