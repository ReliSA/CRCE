package cz.zcu.kiv.crce.repository.plugins;

import java.util.List;
import java.util.Properties;

import cz.zcu.kiv.crce.plugin.AbstractPlugin;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;

/**
 * Abstract implementation of <code>ActionHandler</code> which do nothing and
 * it's methods return unmodified argument.
 *
 * <p>This class is recommended to be used as superclass for all implementations
 * of <code>ActionHandler</code> so then it's not necessary to implement all
 * methods.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public abstract class AbstractActionHandler extends AbstractPlugin implements ActionHandler {

    @Override
    public boolean isExclusive() {
        return false;
    }

    @Override
    public Resource onUploadToBuffer(Resource resource, Buffer buffer, String name) throws RevokedArtifactException {
        return resource;
    }

    @Override
    public List<Resource> beforeExecuteInBuffer(List<Resource> resources, Executable executable, Properties properties, Buffer buffer) {
        return resources;
    }

    @Override
    public List<Resource> afterExecuteInBuffer(List<Resource> resources, Executable executable, Properties properties, Buffer buffer) {
        return resources;
    }

    @Override
    public List<Resource> beforeExecuteInStore(List<Resource> resources, Executable executable, Properties properties, Store store) {
        return resources;
    }

    @Override
    public List<Resource> afterExecuteInStore(List<Resource> resources, Executable executable, Properties properties, Store store) {
        return resources;
    }

    @Override
    public String beforeUploadToBuffer(String name, Buffer buffer) throws RevokedArtifactException {
        return name;
    }

    @Override
    public Resource afterUploadToBuffer(Resource resource, Buffer buffer, String name) throws RevokedArtifactException {
        return resource;
    }

    @Override
    public Resource beforeDownloadFromBuffer(Resource resource, Buffer buffer) {
        return resource;
    }

    @Override
    public Resource afterDownloadFromBuffer(Resource resource, Buffer buffer) {
        return resource;
    }

    @Override
    public Resource beforeDeleteFromBuffer(Resource resource, Buffer buffer) {
        return resource;
    }

    @Override
    public Resource afterDeleteFromBuffer(Resource resource, Buffer buffer) {
        return resource;
    }

    @Override
    public List<Resource> beforeBufferCommit(List<Resource> resources, Buffer buffer, Store store) {
        return resources;
    }

    @Override
    public List<Resource> afterBufferCommit(List<Resource> resources, Buffer buffer, Store store) {
        return resources;
    }

    @Override
    public Resource beforeDeleteFromStore(Resource resource, Store store) {
        return resource;
    }

    @Override
    public Resource afterDeleteFromStore(Resource resource, Store store) {
        return resource;
    }

    @Override
    public Resource beforePutToStore(Resource resource, Store store) throws RevokedArtifactException {
        return resource;
    }

    @Override
    public Resource afterPutToStore(Resource resource, Store store) throws RevokedArtifactException {
        return resource;
    }

    @Override
    public Resource beforeDownloadFromStore(Resource resource, Store store) {
        return resource;
    }

    @Override
    public Resource afterDownloadFromStore(Resource resource, Store store) {
        return resource;
    }
}
