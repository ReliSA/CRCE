package cz.zcu.kiv.crce.repository.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;
import cz.zcu.kiv.crce.repository.SessionData;
import cz.zcu.kiv.crce.repository.SessionRegister;
import cz.zcu.kiv.crce.repository.plugins.Executable;

/**
 * Implementation of <code>SessionFactory.</code>
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class SessionRegisterImpl implements SessionRegister {

    private static final Logger logger = LoggerFactory.getLogger(SessionRegisterImpl.class);

    @Override
    public void registerSession(String sessionId) {
    }

    @Override
    public void unregisterSession(String sessionId) {
    }

    @Override
    public SessionData getSessionData(String sessionId) {
        return new SessionData() {

            @Override
            public Buffer getBuffer() {
                return new Buffer() {

                    @Override
                    public Resource put(String name, InputStream resource) throws IOException, RefusedArtifactException {
                        logger.debug("Buffer put");
                        return null;
                    }

                    @Override
                    public List<Resource> commit(boolean move) throws IOException {
                        logger.debug("Buffer commit");
                        return Collections.emptyList();
                    }

                    @Override
                    public List<Resource> commit(List<Resource> resources, boolean move) throws IOException {
                        logger.debug("Buffer commit");
                        return Collections.emptyList();
                    }

                    @Override
                    public Resource put(Resource resource) throws IOException, RefusedArtifactException {
                        logger.debug("Buffer put");
                        return null;
                    }

                    @Override
                    public boolean remove(Resource resource) throws IOException {
                        logger.debug("Buffer remove");
                        return true;
                    }

                    @Override
                    public List<Resource> getResources() {
                        return Collections.emptyList();
                    }

                    @Override
                    public List<Resource> getResources(Requirement requirement) {
                        return Collections.emptyList();
                    }

                    @Override
                    public void execute(List<Resource> resources, Executable executable, Properties properties) {
                        logger.debug("Buffer execute");
                    }
                };
            }

        };
    }

}
