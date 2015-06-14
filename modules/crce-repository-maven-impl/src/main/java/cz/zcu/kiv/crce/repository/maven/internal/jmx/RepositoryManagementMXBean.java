package cz.zcu.kiv.crce.repository.maven.internal.jmx;

/**
 *
 * @author jkucera
 */
public interface RepositoryManagementMXBean {

    String getName();
    
    String getUri();
    
    String getType();
    
    void index();
    
    int deleteResources();
}
 