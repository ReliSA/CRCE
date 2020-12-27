package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Entity;

/**
 * Wrapper for JSON serializer which allows to have an optional OSGi/DM dependency.
 * <p>If optional package 'cz.zcu.kiv.crce.metadata.json' is not present on classpath,
 * then this class is not loaded. So metadata entities implementations
 * should not call this class directly.
 *
 * TODO this could be put to public API - it would allow to have other than JSON implementation.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface LogHelper {

    String toString(Entity entity);

    /**
     * Regular implementation returns <code>true</code>, "null object" created by DM
     * when optional dependency is not available returns <code>false</code>. This allows
     * to decide whether or not to use the <code>LogHelper</code> to produce toString message.
     *
     * @return <code>true</code> if the interface implementation is available.
     */
    boolean available();
}
