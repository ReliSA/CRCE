package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionData;
import cz.zcu.kiv.crce.repository.SessionRegister;
import cz.zcu.kiv.crce.repository.Store;
import java.util.HashMap;
import java.util.Map;
import org.apache.felix.dm.DependencyManager;
import org.osgi.service.log.LogService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class SessionFactoryImpl implements SessionRegister {

    private volatile DependencyManager m_dependencyManager; /* injected by dependency manager */
    
    private final Map<String, SessionDataImpl> m_sessions = new HashMap<String, SessionDataImpl>();

    @Override
    public void registerSession(String sessionId) {
        synchronized (m_sessions) {
            if (!m_sessions.containsKey(sessionId)) {
                SessionDataImpl sd = new SessionDataImpl();
                BufferImpl buffer = new BufferImpl(sessionId);
                
                sd.m_buffer = buffer;
                
                sd.m_bufferComponent = m_dependencyManager.createComponent()
                        .setInterface(Buffer.class.getName(), buffer.getSessionProperties())
                        .setImplementation(buffer)
                        .add(m_dependencyManager.createServiceDependency().setService(PluginManager.class).setRequired(true))
                        .add(m_dependencyManager.createServiceDependency().setService(Store.class).setRequired(true))
                        .add(m_dependencyManager.createServiceDependency().setService(LogService.class).setRequired(false));
                
                m_dependencyManager.add(sd.m_bufferComponent);
                m_sessions.put(sessionId, sd);
            }
        }
    }

    @Override
    public void unregisterSession(String sessionId) {
        synchronized (m_sessions) {
            SessionDataImpl sd = m_sessions.remove(sessionId);
            if (sd != null) {
                m_dependencyManager.remove(sd.m_bufferComponent);
            }
        }
    }

    @Override
    public SessionData getSessionData(String sessionId) {
        synchronized (m_sessions) {
            SessionData session = m_sessions.get(sessionId);
            if (session == null) {
                registerSession(sessionId);
                return m_sessions.get(sessionId);
            }
            return session;
        }
    }

}
