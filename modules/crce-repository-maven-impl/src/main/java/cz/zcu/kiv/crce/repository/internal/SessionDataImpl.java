package cz.zcu.kiv.crce.repository.internal;

import org.apache.felix.dm.Component;

import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionData;

/**
 * Implementation of <code>SessionData</code>
 *
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
