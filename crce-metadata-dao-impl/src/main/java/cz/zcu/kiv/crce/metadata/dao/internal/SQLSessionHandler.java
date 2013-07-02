package cz.zcu.kiv.crce.metadata.dao.internal;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cihla za cihlou
 */
public class SQLSessionHandler {

    private static final Logger logger = LoggerFactory.getLogger(SQLSessionHandler.class);

    private String conf;
    private InputStream inputStream;
    private SqlSessionFactory factory;
    private SqlSession session;

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public SqlSessionFactory getFactory() {
        return factory;
    }

    public void setFactory(SqlSessionFactory factory) {
        this.factory = factory;
    }

    public SqlSession getSession() {
        init();
        return session;
    }

    public void setSession(SqlSession session) {
        this.session = session;
    }

    public void init() {
        try {
            if (session == null) {
                logger.debug("Initializing DB session.");

                Resources.setDefaultClassLoader(getClass().getClassLoader());

                setConf("META-INF/mybatis/config.xml");
                setInputStream(Resources.getResourceAsStream(getConf()));
                setFactory(new SqlSessionFactoryBuilder().build(getInputStream()));
                setSession(getFactory().openSession());

                session.update("cz.zcu.kiv.crce.metadata.dao.internal.mapper.InitDbMapper.createResource");
                session.update("cz.zcu.kiv.crce.metadata.dao.internal.mapper.InitDbMapper.createCapability");
                session.update("cz.zcu.kiv.crce.metadata.dao.internal.mapper.InitDbMapper.createCap_attribute");
                session.update("cz.zcu.kiv.crce.metadata.dao.internal.mapper.InitDbMapper.createCap_directive");
                session.update("cz.zcu.kiv.crce.metadata.dao.internal.mapper.InitDbMapper.createRequirement");
                session.update("cz.zcu.kiv.crce.metadata.dao.internal.mapper.InitDbMapper.createReq_attribute");
                session.update("cz.zcu.kiv.crce.metadata.dao.internal.mapper.InitDbMapper.createReq_directive");
                
                session.commit();

                logger.debug("DB session initialized.");
            }
        } catch (IOException e) {
            logger.error("Can't load MyBatis configuration, DB session could not be initialized.", e);
        }
    }

    public void closeSession() {
        session.close();
        logger.debug("DB session closed.");
    }
}
