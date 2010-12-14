package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.repository.Plugin;
import cz.zcu.kiv.crce.repository.Stack;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.obr.Resource;

/**
 *
 * @author kalwi
 */
public class StackImpl implements Stack {
    private int BUFFER_SIZE = 8 * 1024;

    @Override
    public boolean store(String name, InputStream resource) throws IOException {
        File file = new File("U:", name);
        
        if (!file.exists()) {
            FileOutputStream output = null;
            File tempFile = null;
            boolean success = false;
            try {
                tempFile = File.createTempFile("ccer", ".tmp");
                output = new FileOutputStream(tempFile);
                byte[] buffer = new byte[BUFFER_SIZE];
                for (int count = resource.read(buffer); count != -1; count = resource.read(buffer)) {
                    output.write(buffer, 0, count);
                }
                success = true;
            }
            finally {
                if (output != null) {
                    output.flush();
                    output.close();
                }
            }
            if (success) {
                tempFile.renameTo(file);
            }
            return success;
        }
        return false;
        
        
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

    @Override
    public void updated(Dictionary dctnr) throws ConfigurationException {
    }

}
