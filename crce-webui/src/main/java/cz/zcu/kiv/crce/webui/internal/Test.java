
package cz.zcu.kiv.crce.webui.internal;

import java.net.URL;
import org.apache.ace.obr.storage.BundleStore;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.RepositoryAdmin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kalwi
 */
public class Test {
    
    private static final Logger logger = LoggerFactory.getLogger(Test.class);
    
//    private volatile Stack m_stack;
    
    private volatile RepositoryAdmin m_ra;
    private volatile BundleStore m_vbs;
    
    private static int sid = 0;
    private int id;
    
    
    public Test() {
        id = sid++;
        
    }
    
    public void add(String url) {
        try {
//            System.out.println("m_ra: " + m_ra.getClass());
            m_ra.addRepository(new URL(url));
        } catch (Exception ex) {
            logger.error("Test error", ex);
        }
    }
    
    public void print() {
        logger.info("\n*** this is test {}", id);
        
        for (Repository r: m_ra.listRepositories()) {
            logger.info("repo: {}", r.getURL());
        }
        logger.info("m_vbs: {}", m_vbs.getClass());
        logger.info("end of test {}", id);
    }
}
