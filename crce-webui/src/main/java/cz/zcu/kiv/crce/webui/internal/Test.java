
package cz.zcu.kiv.crce.webui.internal;

import java.net.URL;
import org.apache.ace.obr.storage.BundleStore;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.RepositoryAdmin;

/**
 *
 * @author kalwi
 */
public class Test {
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
            ex.printStackTrace();
        }
    }
    
    public void print() {
        System.out.println("\n*** this is test " + id);
        for (Repository r: m_ra.listRepositories()) {
            System.out.println("repo: " + r.getURL());
        }
        System.out.println("");
        System.out.println("m_vbs: " + m_vbs.getClass());
        System.out.println("end of test " + id);
    }
}
