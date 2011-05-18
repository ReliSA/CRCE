package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Type;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.felix.utils.version.VersionTable;
import org.osgi.framework.Version;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class PropertyImpl implements Property {

    private String m_name;
    private Type m_type;
    private String m_value;

    public PropertyImpl(String name) {
        if (name == null) {
            name = "null";
        }
        m_name = name.intern();
    }

    public PropertyImpl(String name, Type type, String value) {
        this(name);
        m_value = value;
        m_type = type;
    }

    @Override
    public String getName() {
        return m_name;
    }

    @Override
    public Type getType() {
        return m_type;
    }

    @Override
    public String getValue() {
        return m_value;
    }

    @Override
    public Object getConvertedValue() {
        try {

            switch (m_type) {
                case DOUBLE:
                    return new Double(m_value);

                case LONG:
                    return new Long(m_value);

                case SET:
                    StringTokenizer st = new StringTokenizer(m_value, ",");
                    Set<String> s = new HashSet<String>();
                    while (st.hasMoreTokens()) {
                        s.add(st.nextToken().trim());
                    }
                    return s;

                case STRING:
                    return m_value;

                case URI:
                    return new URI(m_value);

                case URL:
                    return new URL(m_value);

                case VERSION:
                    return VersionTable.getVersion(m_value);

            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        assert false : "Uknown type of property value";
        return m_value;
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PropertyImpl other = (PropertyImpl) obj;
        if ((this.m_name == null) ? (other.m_name != null) : !this.m_name.equals(other.m_name)) {
            return false;
        }
        if (this.m_type != other.m_type) {
            return false;
        }
        if ((this.m_value == null) ? (other.m_value != null) : !this.m_value.equals(other.m_value)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.m_name != null ? this.m_name.hashCode() : 0);
        hash = 59 * hash + (this.m_type != null ? this.m_type.hashCode() : 0);
        hash = 59 * hash + (this.m_value != null ? this.m_value.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return m_value;
    }

//    @Override
    protected void setValue(String string) {
        m_value = string;
        m_type = Type.STRING;
    }

//    @Override
    protected void setValue(Version version) {
        m_value = version.toString();
        m_type = Type.VERSION;
    }

//    @Override
    protected void setValue(URL url) {
        m_value = url.toString();
        m_type = Type.URL;
    }

//    @Override
    protected void setValue(URI uri) {
        m_value = uri.toString();
        m_type = Type.URI;
    }

//    @Override
    protected void setValue(long llong) {
        m_value = String.valueOf(llong);
        m_type = Type.LONG;
    }

//    @Override
    protected void setValue(double ddouble) {
        m_value = String.valueOf(ddouble);
        m_type = Type.DOUBLE;
    }

//    @Override
    protected void setValue(Set values) {
        StringBuilder result;

        Iterator i = values.iterator();

        if (i.hasNext()) {
            result = new StringBuilder(i.next().toString());
        } else {
            m_value = "";
            return;
        }

        while (i.hasNext()) {
            result.append(",");
            result.append(i.next().toString());
        }
    }

//    @Override
    protected void setValue(String value, Type type) {
        m_value = value;
        m_type = type;
    }
}
