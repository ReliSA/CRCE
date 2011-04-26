package cz.zcu.kiv.crce.metadata.wrapper.felix;

import cz.zcu.kiv.crce.metadata.Reason;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resolver;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.internal.ReasonImpl;
import org.apache.felix.bundlerepository.RepositoryAdmin;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class ConvertedResolver implements Resolver {
    
    private org.apache.felix.bundlerepository.RepositoryAdmin m_repoAdmin;
    private org.apache.felix.bundlerepository.Resolver m_resolver;
    private Repository[] m_repositories;
    
    public ConvertedResolver(RepositoryAdmin repoAdmin, Repository... repositories) {
        m_repoAdmin = repoAdmin;
        if (repositories != null) {
            m_repositories = repositories;
        } else {
            m_repositories = new Repository[0];
        }
    }

    @Override
    public void add(Resource resource) {
        m_resolver.add(new ResourceWrapper(resource));
    }

    @Override
    public void clean() {
        m_resolver = m_repoAdmin.resolver(Wrapper.wrap(m_repositories));
    }
    
    @Override
    public Reason[] getUnsatisfiedRequirements() {
        org.apache.felix.bundlerepository.Reason[] reasons = m_resolver.getUnsatisfiedRequirements();
                
        Reason[] out = new Reason[reasons.length];
        
        for (int i = 0; i < reasons.length; i++) {
            
            Resource res = Wrapper.unwrap(reasons[i].getResource());
            
            Requirement req = ((RequirementWrapper) reasons[i].getRequirement()).requirement;
            
            out[i] = new ReasonImpl(res, req);
        }
        
        return out;
        
    }

    @Override
    public Resource[] getOptionalResources() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Requirement[] getReason(Resource resource) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Resource[] getResources(Requirement requirement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Resource[] getRequiredResources() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Resource[] getAddedResources() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean resolve() {
        return m_resolver.resolve();
    }
    
}