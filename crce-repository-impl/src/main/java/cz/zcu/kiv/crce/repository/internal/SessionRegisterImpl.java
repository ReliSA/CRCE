package cz.zcu.kiv.crce.repository.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.dm.DependencyManager;

import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.dao.RepositoryDAO;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.indexer.ResourceIndexerService;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.service.validation.MetadataValidator;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionData;
import cz.zcu.kiv.crce.repository.SessionRegister;
import cz.zcu.kiv.crce.repository.Store;

/**
 * Implementation of <code>SessionFactory.</code>
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class SessionRegisterImpl implements SessionRegister {

    private volatile DependencyManager dependencyManager; /* injected by dependency manager */

    private final Map<String, SessionDataImpl> sessions = new HashMap<>();

    @Override
    public void registerSession(String sessionId) {
        synchronized (sessions) {
            if (!sessions.containsKey(sessionId)) {
                SessionDataImpl sd = new SessionDataImpl();
                BufferImpl buffer = new BufferImpl(sessionId);

                sd.buffer = buffer;

                sd.bufferComponent = dependencyManager.createComponent()
                        .setInterface(Buffer.class.getName(), buffer.getSessionProperties())
                        .setImplementation(buffer)
                        .add(dependencyManager.createServiceDependency().setService(PluginManager.class).setRequired(true))
                        .add(dependencyManager.createServiceDependency().setService(Store.class).setRequired(true))
                        .add(dependencyManager.createServiceDependency().setService(MetadataFactory.class).setRequired(true))
                        .add(dependencyManager.createServiceDependency().setService(ResourceDAO.class).setRequired(true))
                        .add(dependencyManager.createServiceDependency().setService(RepositoryDAO.class).setRequired(true))
                        .add(dependencyManager.createServiceDependency().setService(MetadataService.class).setRequired(true))
                        .add(dependencyManager.createServiceDependency().setService(MetadataValidator.class).setRequired(true))
                        .add(dependencyManager.createServiceDependency().setService(ResourceIndexerService.class).setRequired(true));

                dependencyManager.add(sd.bufferComponent);
                sessions.put(sessionId, sd);
            }
        }
    }

    @Override
    public void unregisterSession(String sessionId) {
        synchronized (sessions) {
            SessionDataImpl sd = sessions.remove(sessionId);
            if (sd != null) {
                dependencyManager.remove(sd.bufferComponent);
            }
        }
    }

    @Override
    public SessionData getSessionData(String sessionId) {
        synchronized (sessions) {
            SessionData session = sessions.get(sessionId);
            if (session == null) {
                registerSession(sessionId);
                return sessions.get(sessionId);
            }
            return session;
        }
    }

}
