package cz.zcu.kiv.crce.vo.internal.dozer;

import java.net.URL;

import org.dozer.util.DozerClassLoader;
import org.osgi.framework.BundleContext;

/**
 * Classloader for Dozer bean mapper which allows loading multiple
 * mapping files from various OSGi bundles.
 *
 * Date: 18.5.15
 *
 * @author http://modio.io/dozer-pojo-mapper-in-osgi/
 */
public class OSGiDozerClassLoader implements DozerClassLoader {
    private BundleContext context;

    @Override
    public Class<?> loadClass(String className) {
        try {
            return context.getBundle().loadClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public URL loadResource(String uri) {
        URL url;

        url = context.getBundle().getResource(uri);

        if (url == null) {
            url = DozerClassLoader.class.getClassLoader().getResource(uri);
        }

        return url;
    }

    public void setContext(BundleContext context) {
        this.context = context;
    }
}
