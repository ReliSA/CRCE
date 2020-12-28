package cz.zcu.kiv.crce.rest.v2.internal;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Registers features to Jersey.
 * 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        classes.add(MultiPartFeature.class);
        classes.add(LoggingFeature.class);
        classes.add(JacksonFeature.class);
        return classes;
    }
}
