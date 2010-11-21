package cz.zcu.kiv.ccer.internal;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.apache.ace.obr.storage.BundleStore;
import org.osgi.service.obr.Capability;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.RepositoryAdmin;
import org.osgi.service.obr.Requirement;
import org.osgi.service.obr.Resolver;
import org.osgi.service.obr.Resource;

public class Test {

//    private volatile BundleStore m_store; /* will be injected by dependencymanager */

    private volatile RepositoryAdmin m_repositoryAdmin;

    public void main() {

        System.out.println("--- test ---");
        
            try {
                m_repositoryAdmin.addRepository(new URL("file:///U:/repository.xml"));
            } catch (Exception ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }

        Repository[] repos = m_repositoryAdmin.listRepositories();
        System.out.println("repos size: " + repos.length);

        Resource[] resources = m_repositoryAdmin.discoverResources("(uri=*)");
        
        System.out.println("resources size: " + resources.length);

        System.out.println();
        
        System.out.println("--- resources ---");
        for (Resource res : resources) {
            System.out.println("ID:          " + res.getId());
            System.out.println("Pres. name:  " + res.getPresentationName());
            System.out.println("Sym. name:   " + res.getSymbolicName());
            System.out.println("Version:     " + res.getVersion());
            System.out.println("URL:         " + res.getURL().toString());

            for (Capability cap : res.getCapabilities()) {
                System.out.println("capability:  " + cap.getName() + ", "  + cap.getProperties());
            }
            for (Requirement req : res.getRequirements()) {
                System.out.println("requirement: " + req.getName() + ", " + req.getFilter());
            }
            System.out.println();
        }

        System.out.println("");


        Resolver resolver = m_repositoryAdmin.resolver();

        resources = m_repositoryAdmin.discoverResources("(symbolicname=eu.kalwi.osgi.OSGi-Bundle1)");
        Resource resource = resources[0];

        resources = m_repositoryAdmin.discoverResources("(&(symbolicname=eu.kalwi.osgi.OSGi-Bundle2)(version=1.0.0.SNAPSHOT))");
        Resource resource2 = resources[0];

        resources = m_repositoryAdmin.discoverResources("(&(symbolicname=eu.kalwi.osgi.OSGi-Bundle2)(version=1.2.0.SNAPSHOT))");
        Resource resource3 = resources[0];

        System.out.println("resource: " + resource.getId());
        System.out.println("resource2: " + resource2.getId());
        System.out.println("resource3: " + resource3.getId());
        System.out.println("");

        
        
//        resolver.add(resource);
//        resolver.add(resource2);
        resolver.add(resource3);

        System.out.println("--- resolving ---");



        for (Resource res : resolver.getAddedResources()) {
            System.out.println("Added: " + res.getId());
        }
        
        System.out.println("");

        System.out.println("resolved: " + resolver.resolve());

        System.out.println("");
        System.out.println("");
        

        // getReasons()

        System.out.println("* getReasons() *");
        for (Resource res : new Resource[] {resource, resource2, resource3}) {
            Requirement[] reqs = resolver.getReason(res);
                System.out.println("reasons for " + res.getId() + ":");
            if (reqs == null) {
                System.out.println("null");
            } else {
                for (Requirement req : reqs) {
                    System.out.println("reason: " + req.getFilter());
                }
            }
            System.out.println("");
        }

        System.out.println("");
        
        
        // getResources()
        
        System.out.println("* getResources() *");
        for (Resource res : new Resource[] {resource, resource2, resource3}) {
            System.out.println("resource: " + res.getId());
            for (Requirement req : res.getRequirements()) {
                System.out.println("requirement: " + req.getFilter());
                Resource[] ress = resolver.getResources(req);
                if (ress == null) {
                    System.out.println(" resource: null");
                } else {
                    for (Resource res2 : ress) {
                        System.out.println(" resource: " + res2.getId());
                    }
                }
            }
            System.out.println("");
        }
        
        System.out.println("");

        
        // getUnsatisfiedRequirements()
        
        System.out.println("* getUnsatisfiedRequirements() *");
        for (Requirement req : resolver.getUnsatisfiedRequirements()) {
            System.out.println("unsatisfied: " + req.getName() + ", " + req.getFilter());
            for (Resource res : resolver.getResources(req)) {
                System.out.println(" * resource: " + res.getId());
            }
            System.out.println("");
        }
        
        System.out.println("");

        
        // getRequiredResources()
        
        System.out.println("* getRequiredResources() *");
        for (Resource res : resolver.getRequiredResources()) {
            System.out.println("Required res.: " + res.getId());
            for (Requirement req : resolver.getReason(res)) {
                System.out.println(" * reason: " + req.getName() + ", " + req.getFilter());
            }
            System.out.println("");
        }
        
        System.out.println("");

        
        // getOptionalResources()
        
        System.out.println("* getOptionalResources() *");
        for (Resource res : resolver.getOptionalResources()) {
            System.out.println("Optional res.: " + res.getId());
            for (Requirement req : resolver.getReason(res)) {
                System.out.println(" * reason: " + req.getName() + ", " + req.getFilter());
            }
            System.out.println("");
        }


    }
}
