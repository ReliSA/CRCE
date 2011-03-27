package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.PropertyProvider;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class CapabilityImpl extends AbstractPropertyProvider<Capability> implements Capability, PropertyProvider<Capability> {

    private String m_name;

    public CapabilityImpl(String name) {
        m_name = name.intern();
    }

    @Override
    public String getName() {
        return m_name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CapabilityImpl other = (CapabilityImpl) obj;
        if ((this.m_name == null) ? (other.m_name != null) : !this.m_name.equals(other.m_name)) {
            return false;
        }
        return this.m_map.equals(other.m_map);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.m_name != null ? this.m_name.hashCode() : 0);
        return hash;
    }
    
    

    @Override
    Capability getThis() {
        return this;
    }
}
