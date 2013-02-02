package cz.zcu.kiv.crce.metadata.wrapper.felix;

import cz.zcu.kiv.crce.metadata.Reason;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resolver;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.internal.ReasonImpl;    // FIXME probably not visible to another bundles
import java.util.ArrayList;
import java.util.List;
import org.apache.felix.bundlerepository.RepositoryAdmin;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
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
        m_resolver = m_repoAdmin.resolver(Wrapper.wrap(m_repositories));
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
        return convertReasons(m_resolver.getUnsatisfiedRequirements());
        
    }

    @Override
    public Resource[] getOptionalResources() {
        return Wrapper.unwrap(m_resolver.getOptionalResources());
    }

    @Override
    public Reason[] getReason(Resource resource) {
        return convertReasons(m_resolver.getReason(Wrapper.wrap(resource)));
    }

    @Override
    public Resource[] getResources(Requirement requirement) {
        Reason[] reasons = convertReasons(m_resolver.getUnsatisfiedRequirements());
        List<Resource> list = new ArrayList<Resource>();
        
        for (Reason reason : reasons) {
            if (reason.getRequirement().equals(requirement)) {
                list.add(reason.getResource());
            }
        }
        
        return list.toArray(new Resource[list.size()]);
    }

    @Override
    public Resource[] getRequiredResources() {
        return Wrapper.unwrap(m_resolver.getRequiredResources());
    }

    @Override
    public Resource[] getAddedResources() {
        return Wrapper.unwrap(m_resolver.getAddedResources());
    }

    @Override
    public boolean resolve() {
        return m_resolver.resolve();
    }
    
    public static Reason[] convertReasons(org.apache.felix.bundlerepository.Reason[] reasons) {
        Reason[] out = new Reason[reasons.length];

        for (int i = 0; i < reasons.length; i++) {
            Resource res = Wrapper.unwrap(reasons[i].getResource());
            Requirement req = ((RequirementWrapper) reasons[i].getRequirement()).requirement;
            out[i] = new ReasonImpl(res, req);
        }

        return out;
    }
}
