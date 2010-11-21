
package cz.zcu.kiv.ccer;

import org.osgi.service.obr.Resource;

public interface Target {
    public Object getComponent();

    public Resource[] getComponentList();

    
}
