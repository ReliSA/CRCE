package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.log.LogService;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.plugin.MetadataIndexingResultService;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionRegister;
import cz.zcu.kiv.crce.repository.Store;
//import org.ops4j.pax.logging.PaxLoggingService;


/**
 * Activator of this bundle
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public final class Activator extends DependencyActivatorBase {

    private static volatile Activator m_instance;

    private volatile BundleContext m_context;           /* injected by dependency manager */
    private volatile PluginManager m_pluginManager;     /* injected by dependency manager */
    private volatile SessionRegister m_sessionRegister;   /* injected by dependency manager */
    private volatile LogService m_log;                  /* injected by dependency manager */
    private volatile Store m_store;                  	/* injected by dependency manager */
    private volatile ResourceCreator m_creator;        	/* injected by dependency manager */
    //private volatile PaxLoggingService m_logger;
    private org.slf4j.Logger m_slf4jLogger = LoggerFactory.getLogger( Activator.class );

    /** MetadataIndexingResultService instance provides by simple way information
     * about metadata indexing process result. */
    private volatile MetadataIndexingResultService m_metadataIndexingResult;    /* injected by dependency manager */

    public static Activator instance() {
        return m_instance;
    }

    public PluginManager getPluginManager() {
        return m_pluginManager;
    }
    
    public SessionRegister getSessionFactory() {
        return m_sessionRegister;
    }
    
    public ResourceCreator getCreator(){
    	return this.m_creator;
    }
    
    public org.slf4j.Logger getLog() {
        return m_slf4jLogger;
    }
    public Store getStore(){
    	return m_store;
    }
    public Buffer getBuffer(HttpServletRequest req) {
        if (req == null) {
            return null;
        }

        String sid = req.getSession(true).getId();
        return m_sessionRegister.getSessionData(sid).getBuffer();
    }

    /**
     * @return instance of MetadataIndexingResultService provides info about metadata indexing process.
     */
    public MetadataIndexingResultService getMetadataIndexerResult() {
    	return m_metadataIndexingResult;
    }

    /**
     * Updates Pax Logging configuration to a specifid conversion pattern.
     *
     * @param bundleContext bundle context
     * @param pattern       layout conversion pattern
     *
     * @throws IOException - Re-thrown
     */
    private void updateConfiguration( BundleContext bundleContext,
                                      final String pattern )
        throws IOException
    {
        final ConfigurationAdmin configAdmin = getConfigurationAdmin( bundleContext );
        final Configuration configuration = configAdmin.getConfiguration( "org.ops4j.pax.logging", null );

        final Properties log4jProps = new Properties();
        log4jProps.setProperty( "log4j.rootLogger", "INFO, CONSOLE" );
        log4jProps.setProperty( "log4j.appender.CONSOLE", "org.apache.log4j.ConsoleAppender" );
        log4jProps.setProperty( "log4j.appender.CONSOLE.layout", "org.apache.log4j.PatternLayout" );
        log4jProps.setProperty( "log4j.appender.CONSOLE.layout.ConversionPattern", pattern );

        configuration.update( log4jProps );
    }

    /**
     * Gets Configuration Admin service from service registry.
     *
     * @param bundleContext bundle context
     *
     * @return configuration admin service
     *
     * @throws IllegalStateException - If no Configuration Admin service is available
     */
    private ConfigurationAdmin getConfigurationAdmin( final BundleContext bundleContext )
    {
        final ServiceReference ref = bundleContext.getServiceReference( ConfigurationAdmin.class.getName() );
        if( ref == null )
        {
            throw new IllegalStateException( "Cannot find a configuration admin service" );
        }
        return (ConfigurationAdmin) bundleContext.getService( ref );
    }


    
    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        m_instance = this;

        manager.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(SessionRegister.class).setRequired(true))
                .add(createServiceDependency().setService(LogService.class).setRequired(false))
                .add(createServiceDependency().setService(PluginManager.class).setRequired(true))
                .add(createServiceDependency().setService(Store.class).setRequired(true))
                .add(createServiceDependency().setService(ResourceCreator.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataIndexingResultService.class).setRequired(false))
                );
        
        //updateConfiguration(context, "%5p [%t] - %m%n");

        //Logger log4jLogger = Logger.getLogger(Activator.class);
        //System.out.println("LOG4J "+log4jLogger.toString());
        //System.out.println(log4jLogger.isInfoEnabled());
        //log4jLogger.info("log4j test");
        //log4jLogger.error("log4j error test");
        
        m_slf4jLogger.info(  "Starting Example...    (slf4j)" );
        m_slf4jLogger.error(  "Error test...    (slf4j)" );

        
        

        //ServiceReference ref = context.getServiceReference( PaxLoggingService.class.getName() );
        //PaxLoggingService service = (PaxLoggingService) context.getService( ref );
        
       /* if(m_logger != null) {
        
        System.out.println("Log level "+ m_logger.getLogLevel());
        m_logger.log(1, "Pax logg hello"); 
        } else {
        	System.out.println("PaxLoggingService reference is null.");
        }
        
        //m_logger.info( "MyActivator is started." );
        */
        /*ServiceReference ref = context.getServiceReference(LogService.class.getName());
        if (ref != null)
        {
        	System.out.println("Reference to log service gained");
            LogService log = (LogService) context.getService(ref);
            m_log = log;
            m_log.log(LogService.LOG_INFO, "Logger activated");
            
        } else {
        	System.out.println("Reference to log service not gained.");
        }*/ 

        		
//        final Test t1 = new Test();
//        final Test t2 = new Test();
//
//        manager.add(createComponent().setImplementation(t1).add(createServiceDependency().setService(RepositoryAdmin.class).setRequired(true)));
//        manager.add(createComponent().setImplementation(t2).add(createServiceDependency().setService(RepositoryAdmin.class).setRequired(true)));
//
//        manager.add(createComponent().setImplementation(t1).add(createServiceDependency().setService(BundleStore.class).setRequired(true)));
//        manager.add(createComponent().setImplementation(t2).add(createServiceDependency().setService(BundleStore.class).setRequired(true)));
//
//
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException ex) {
//                }
//
//                t1.add("file:///U:/repository.xml");
//                t2.add("file:///Q:/DIP/m2repo/repository.xml");
//
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException ex) {
//                }
//                t1.print();
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException ex) {
//                }
//                t2.print();
//            }
//        }).start();
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // nothing to do
    }
}
