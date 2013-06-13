package cz.zcu.kiv.crce.metadata.dao.internal;

import org.osgi.framework.BundleContext;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;

import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import java.io.InputStream;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {

        manager.add(createComponent()
                .setInterface(ResourceDAO.class.getName(), null)
                .setImplementation(ResourceDAOImpl.class)
                .add(createServiceDependency().setRequired(true).setService(MetadataService.class)));
        
        String conf = "data/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(conf);
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(inputStream);
        try (SqlSession session = factory.openSession()) {
            session.update("org.apache.ibatis.DB_CreatorMapper.createResource");
            session.commit();
            session.update("org.apache.ibatis.DB_CreatorMapper.createCapability");
            session.commit();
            session.update("org.apache.ibatis.DB_CreatorMapper.createCap_attribute");
            session.commit();
            session.update("org.apache.ibatis.DB_CreatorMapper.createCap_directive");
            session.commit();
            session.update("org.apache.ibatis.DB_CreatorMapper.createRequirement");
            session.commit();
            session.update("org.apache.ibatis.DB_CreatorMapper.createReq_attribute");
            session.commit();
            session.update("org.apache.ibatis.DB_CreatorMapper.createReq_directive");
            session.commit();
            session.close();
        }
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // TODO logging
    }
}
