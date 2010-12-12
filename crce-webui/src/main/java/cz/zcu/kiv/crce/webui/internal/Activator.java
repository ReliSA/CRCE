package cz.zcu.kiv.crce.webui.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public final class Activator extends DependencyActivatorBase {

//    public void start(BundleContext bc) throws Exception {
//        System.out.println("STARTING cz.zcu.kiv.crce.webui");
//    }
//
//    public void stop(BundleContext bc) throws Exception {
//        System.out.println("STOPPING cz.zcu.kiv.crce.webui");
//    }
//    private ServiceReference m_webContainerRef;

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
//        manager.add(createComponent()
//                    .setInterface(HttpServlet.class.getName(), null)
//                    .setImplementation(TestServlet.class)
//                    .add(createConfigurationDependency()
//                    .setPropagate(true).setPid("cz.zcu.kiv.crce.webui")));

//        Dictionary props = new Hashtable();
//        props.put( "alias", "/whiteboard" );
//        props.put("servlet-name", "My Servlet");
//        context.registerService( TestServlet.class.getName(), new TestServlet(), props );

//        m_webContainerRef = context.getServiceReference(WebContainer.class.getName());
//        if (m_webContainerRef != null) {
//            final WebContainer webContainer = (WebContainer) context.getService(m_webContainerRef);
//            if (webContainer != null) {
//                // create a default context to share between registrations
//                final HttpContext httpContext = webContainer.createDefaultHttpContext();
//
//                // register jsp support
//                webContainer.registerJsps(
//                        new String[]{"/jsp/*"}, // url patterns
//                        httpContext // http context
//                        );
//                // register images as resources
////                webContainer.registerResources(
////                    "/images",
////                    "/images",
////                    httpContext
////                );
//            }
//        }
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet.");
//        if (m_webContainerRef != null) {
//            context.ungetService(m_webContainerRef);
//            m_webContainerRef = null;
//        }
    }
}
