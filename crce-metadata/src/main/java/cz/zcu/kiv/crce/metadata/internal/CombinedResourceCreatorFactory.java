package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.ResourceCreatorFactory;

/**
 *
 * @author kalwi
 */
public class CombinedResourceCreatorFactory implements ResourceCreatorFactory {

    private ResourceCreator m_staticCreator = new StaticResourceCreator();
    private ResourceCreator m_writableCreator = new MetafileResourceCreator();
    
    @Override
    public ResourceCreator getResourceCreator() {
        ResourceCreator combinedCreator = new CombinedResourceCreator(m_staticCreator, m_writableCreator);
        return combinedCreator;
    }
}
