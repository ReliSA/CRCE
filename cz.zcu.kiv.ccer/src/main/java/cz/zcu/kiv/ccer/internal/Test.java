package cz.zcu.kiv.ccer.internal;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.ace.obr.storage.BundleStore;
import org.osgi.service.obr.Capability;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.RepositoryAdmin;
import org.osgi.service.obr.Resource;

public class Test {

    private volatile BundleStore m_store; /* will be injected by dependencymanager */

    private volatile RepositoryAdmin m_repositoryAdmin;

    public void main() {

        System.out.println("***** main");
        
            try {
                m_repositoryAdmin.addRepository(new URL("file:///U:/repository.xml"));
            } catch (Exception ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }

        Repository[] repos = m_repositoryAdmin.listRepositories();
        System.out.println("repos size: " + repos.length);

        Resource[] resources = m_repositoryAdmin.discoverResources("(version=1.0.0.SNAPSHOT)");
        System.out.println("resources size: " + resources.length);

        System.out.println();
        
        System.out.println("--- resources ---");
        for (Resource res : resources) {
            System.out.println("ID:         " + res.getId());
            System.out.println("Pres. name: " + res.getPresentationName());
            System.out.println("Sym. name:  " + res.getSymbolicName());
            System.out.println("Version:    " + res.getVersion());
            System.out.println("URL:        " + res.getURL().toString());

            for (Capability cap : res.getCapabilities()) {
                System.out.println("capability: " + cap.getName());
            }
            System.out.println();
        }

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
