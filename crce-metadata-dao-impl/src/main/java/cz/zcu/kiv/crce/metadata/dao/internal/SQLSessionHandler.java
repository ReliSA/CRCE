package cz.zcu.kiv.crce.metadata.dao.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 *
 * @author cihla za cihlou
 */
public class SQLSessionHandler {

    private static String conf;
    private static InputStream inputStream;
    private static SqlSessionFactory factory;
    private static SqlSession session;

    public static String getConf() {
        return conf;
    }

    public static void setConf(String conf) {
        SQLSessionHandler.conf = conf;
    }

    public static InputStream getInputStream() {
        return inputStream;
    }

    public static void setInputStream(InputStream inputStream) {
        SQLSessionHandler.inputStream = inputStream;
    }

    public static SqlSessionFactory getFactory() {
        return factory;
    }

    public static void setFactory(SqlSessionFactory factory) {
        SQLSessionHandler.factory = factory;
    }

    public static SqlSession getSession() {
        init();
        return session;
    }

    public static void setSession(SqlSession session) {
        SQLSessionHandler.session = session;
    }

    public static void init() {
        try {
            if (session == null) {
                setConf("data/mybatis-config.xml");
                setInputStream(Resources.getResourceAsStream(getConf()));
                setFactory(new SqlSessionFactoryBuilder().build(getInputStream()));
                setSession(getFactory().openSession());

                session.update("org.apache.ibatis.DBMapper.createResource");
                session.commit();
                session.update("org.apache.ibatis.DBMapper.createCapability");
                session.commit();
                session.update("org.apache.ibatis.DBMapper.createCap_attribute");
                session.commit();
                session.update("org.apache.ibatis.DBMapper.createCap_directive");
                session.commit();
                session.update("org.apache.ibatis.DBMapper.createRequirement");
                session.commit();
                session.update("org.apache.ibatis.DBMapper.createReq_attribute");
                session.commit();
                session.update("org.apache.ibatis.DBMapper.createReq_directive");
                session.commit();
            }
        } catch (IOException ex) {
            Logger.getLogger(SQLSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void closeSession() {
        session.close();
    }
}
