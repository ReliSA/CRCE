package cz.zcu.kiv.crce.rest.internal;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

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
        return classes;
    }
}
