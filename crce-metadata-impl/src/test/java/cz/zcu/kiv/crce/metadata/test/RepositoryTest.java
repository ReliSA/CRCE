package cz.zcu.kiv.crce.metadata.test;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.WritableRepository;
import cz.zcu.kiv.crce.metadata.internal.RepositoryImpl;
import cz.zcu.kiv.crce.metadata.internal.ResourceImpl;
import java.net.URI;
import org.junit.*;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class RepositoryTest {

    private WritableRepository repository;
    private Resource resource;
    
    @Before
    public void before() throws Exception {
        repository = new RepositoryImpl(new URI("some:uri"));
        resource = new ResourceImpl();
        
        resource.setSymbolicName("r1");
        resource.setVersion("1.0.0");
    }
    
    @Test
    public void add() throws Exception {
        repository.addResource(resource);
        repository.addResource(resource);
        assert repository.contains(resource);
        assert repository.getResources().length == 1;
    }
    
    @Test
    public void remove() throws Exception {
        repository.addResource(resource);
        repository.removeResource(resource);
        
        assert !repository.contains(resource);
        assert repository.getResources().length == 0;
    }
    
    @Test
    public void changeSymbolicName() throws Exception {
        repository.addResource(resource);
        resource.setSymbolicName("r2");
        assert repository.contains(resource);
    }
    
    @Test
    public void changeVersion() throws Exception {
        repository.addResource(resource);
        resource.setVersion("1.0.1");
        assert repository.contains(resource);
    }
    
}
