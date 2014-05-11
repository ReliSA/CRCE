package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionData;
import org.apache.felix.dm.Component;

/**
 * Implementation of <code>SessionData</code>
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class SessionDataImpl implements SessionData {

    Component bufferComponent;
    Buffer buffer;

    @Override
    public Buffer getBuffer() {
        return buffer;
    }
}
