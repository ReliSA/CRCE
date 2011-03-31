package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Requirement;
import java.util.regex.Pattern;
import org.apache.felix.utils.filter.FilterImpl;
import org.osgi.framework.InvalidSyntaxException;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class RequirementImpl implements Requirement {

    private static final Pattern REMOVE_LT = Pattern.compile("\\(([^<>=~()]*)<([^*=]([^\\\\\\*\\(\\)]|\\\\|\\*|\\(|\\))*)\\)");
    private static final Pattern REMOVE_GT = Pattern.compile("\\(([^<>=~()]*)>([^*=]([^\\\\\\*\\(\\)]|\\\\|\\*|\\(|\\))*)\\)");
    private static final Pattern REMOVE_NV = Pattern.compile("\\(version>=0.0.0\\)");
    private String m_name;
    private boolean m_multiple;
    private boolean m_optional;
    private boolean m_extend;
    private String m_comment;
    private FilterImpl m_filter = null;
    private boolean m_writable;

    public RequirementImpl(String name) {
        m_name = name.intern();
        m_writable = true;
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

    @Override
    public Requirement setFilter(String filter) {
        if (isWritable()) {
            try {
                String nf = REMOVE_LT.matcher(filter).replaceAll("(!($1>=$2))");
                nf = REMOVE_GT.matcher(nf).replaceAll("(!($1<=$2))");
                nf = REMOVE_NV.matcher(nf).replaceAll("");
                m_filter = FilterImpl.newInstance(nf, true);
            } catch (InvalidSyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return this;
    }

    @Override
    public Requirement setMultiple(boolean multiple) {
        if (isWritable()) {
            m_multiple = multiple;
        }
        return this;
    }

    @Override
    public Requirement setOptional(boolean optional) {
        if (isWritable()) {
            m_optional = optional;
        }
        return this;
    }

    @Override
    public Requirement setExtend(boolean extend) {
        if (isWritable()) {
            m_extend = extend;
        }
        return this;
    }

    @Override
    public Requirement setComment(String comment) {
        if (isWritable()) {
            m_comment = comment;
        }
        return this;
    }

    @Override
    public boolean isWritable() {
        return m_writable;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RequirementImpl other = (RequirementImpl) obj;
        if ((this.m_name == null) ? (other.m_name != null) : !this.m_name.equals(other.m_name)) {
            return false;
        }
        if (this.m_multiple != other.m_multiple) {
            return false;
        }
        if (this.m_optional != other.m_optional) {
            return false;
        }
        if (this.m_extend != other.m_extend) {
            return false;
        }
        if ((this.m_filter == null) ? (other.m_filter != null) : !this.m_filter.equals(other.m_filter)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.m_name != null ? this.m_name.hashCode() : 0);
        hash = 23 * hash + (this.m_multiple ? 1 : 0);
        hash = 23 * hash + (this.m_optional ? 1 : 0);
        hash = 23 * hash + (this.m_extend ? 1 : 0);
        hash = 23 * hash + (this.m_filter != null ? this.m_filter.hashCode() : 0);
        return hash;
    }
    
    
    
}
