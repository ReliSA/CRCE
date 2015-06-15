package cz.zcu.kiv.crce.repository.filebased.internal;

import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionData;
import org.apache.felix.dm.Component;

/**
 * Implementation of <code>SessionData</code>
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class SessionDataImpl implements SessionData {

    // buffer for uploaded file artefacts
    Component bufferComponent;
    Buffer buffer;

    // buffer for parsed webservice IDLs
    Component wsBufferComponent;
    Buffer wsBuffer;

    @Override
    public Buffer getBuffer() {
        return buffer;
    }
    
    @Override
    public Buffer getWsBuffer() {
        return wsBuffer;
    }
}
