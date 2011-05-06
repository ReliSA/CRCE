package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionData;
import org.apache.felix.dm.Component;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class SessionDataImpl implements SessionData {

    Component m_bufferComponent;
    Buffer m_buffer;

    @Override
    public Buffer getBuffer() {
        return m_buffer;
    }
}
