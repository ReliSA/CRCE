package cz.zcu.kiv.ccer.internal;

import cz.zcu.kiv.ccer.Plugin;
import cz.zcu.kiv.ccer.Stack;
import cz.zcu.kiv.ccer.Session;
import java.io.InputStream;
import org.osgi.service.obr.Resource;

/**
 *
 * @author kalwi
 */
public class StackImpl implements Stack {

    @Override
    public void store(String name, InputStream resource) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void commit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void runTestsOnComponent(Object component) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Resource[] getStoredResources() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void executeOnStored(Plugin[] plugins) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
