package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Requirement;
import org.apache.felix.utils.filter.FilterImpl;

/**
 *
 * @author kalwi
 */
public class RequirementImpl implements Requirement {
    
    private String m_name;
    private boolean m_multiple;
    private boolean m_optional;
    private boolean m_extend;
    private String m_comment;
    private FilterImpl m_filter = null;
            
    public RequirementImpl(String name) {
        m_name = name.intern();
    }

    @Override
    public String getName() {
        return m_name;
    }

    @Override
    public String getFilter() {
        return m_filter.toString();
    }

    @Override
    public boolean isMultiple() {
        return m_multiple;
    }

    @Override
    public boolean isOptional() {
        return m_optional;
    }

    @Override
    public boolean isExtend() {
        return m_extend;
    }

    @Override
    public String getComment() {
        return m_comment;
    }

}
