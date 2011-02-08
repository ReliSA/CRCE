package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Requirement;
import java.util.regex.Pattern;
import org.apache.felix.utils.filter.FilterImpl;
import org.osgi.framework.InvalidSyntaxException;

/**
 *
 * @author kalwi
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
    public void setFilter(String filter) {
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
    }

    @Override
    public void setMultiple(boolean multiple) {
        if (isWritable()) {
            m_multiple = multiple;
        }
    }

    @Override
    public void setOptional(boolean optional) {
        if (isWritable()) {
            m_optional = optional;
        }
    }

    @Override
    public void setExtend(boolean extend) {
        if (isWritable()) {
            m_extend = extend;
        }
    }

    @Override
    public void setComment(String comment) {
        if (isWritable()) {
            m_comment = comment;
        }
    }

    @Override
    public boolean isWritable() {
        return m_writable;
    }
}
