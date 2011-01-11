package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Capability;

/**
 *
 * @author kalwi
 */
public class CapabilityImpl extends AbstractPropertyProvider implements Capability {

    private String m_name;

    public CapabilityImpl(String name) {
        m_name = name.intern();
    }

    @Override
    public String getName() {
        return m_name;
    }
}
