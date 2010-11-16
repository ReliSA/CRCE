package cz.zcu.kiv.ccer.internal;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.ace.obr.storage.BundleStore;
import org.osgi.service.obr.RepositoryAdmin;

public class Test {

    private volatile BundleStore m_store; /* will be injected by dependencymanager */

    private volatile RepositoryAdmin m_repositoryAdmin;

    public void main() {

        System.out.println("***** main");
        System.out.println("m_store == null: " + (m_store == null));
        System.out.println("m_repositoryAdmin == null: " + (m_repositoryAdmin == null));

        
//        try {
//            m_store.put("a.pdf", new FileInputStream(new File("C:\\a.pdf")));
//        } catch (IOException ex) {
//            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        try {
//            m_store.put("b.pdf", new FileInputStream(new File("C:\\b.pdf")));
//        } catch (IOException ex) {
//            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
//        }

        try {
            m_store.get("repository.xml");
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
