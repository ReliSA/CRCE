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

}
