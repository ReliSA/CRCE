package cz.zcu.kiv.crce.metadata.dao.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Properties;

import javax.annotation.Nonnull;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public abstract class AbstractResourceDAO implements ResourceDAO, ManagedService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractResourceDAO.class);

    protected static final String MYBATIS_DEFAULT_CONFIG = "META-INF/mybatis/config.xml";

    protected static final String CFG__JDBC_DRIVER = "jdbc.driver";
    protected static final String CFG__JDBC_URL = "jdbc.url";
    protected static final String CFG__JDBC_USERNAME = "jdbc.username";
    protected static final String CFG__JDBC_PASSWORD = "jdbc.password";
    protected static final String CFG__MYBATIS_CONFIG = "mybatis.config";

    private SqlSessionFactory factory;
    private Properties properties;

    /**
     * Opens and returns new session.
     * @return session.
     * @throws IOException
     */
    protected synchronized SqlSession getSession() throws IOException {
        if (factory != null) {
            return factory.openSession();
        } else if (properties != null) {
            String config = (String) properties.get("config");
            if (config != null && createFactory(config, properties)) {
                return factory.openSession();
            }
        }
        throw new IllegalStateException("Could not create MyBatis SqlSessionFactory.");
    }

    /**
     * Creates new SqlSessionFactory.
     *
     * @param config MyBatis configuration file path.
     * @param properties Configuration properties.
     * @return true if factory was created.
     * @throws IOException Thrown when the given configuration file can't be opened.
     */
    private boolean createFactory(@Nonnull String config, @Nonnull Properties properties) throws IOException {
        try (InputStream is = Resources.getResourceAsStream(config)) {
            factory = new SqlSessionFactoryBuilder().build(is, properties);
            factoryPostConfiguration(factory.getConfiguration());
        } catch (RuntimeException e) { // thrown by build()
            logger.error("Could not create MyBatis SqlSessionFactory.", e);
            return false;
        }
        return true;
    }

    abstract void factoryPostConfiguration(Configuration configuration);

    @Override
    public synchronized void updated(Dictionary<String, ?> dict) throws ConfigurationException {

        if (dict != null) {
            @SuppressWarnings("LocalVariableHidesMemberVariable")
            Properties properties = new Properties();

            String driver = getProperty(dict, CFG__JDBC_DRIVER);
            if (driver == null) {
                throw new ConfigurationException(CFG__JDBC_DRIVER, "JDBC driver must be specified.");
            }
            properties.put("driver", driver);

            String url = getProperty(dict, CFG__JDBC_URL);
            if (url == null) {
                throw new ConfigurationException(CFG__JDBC_URL, "JDBC URL must be specified.");
            }
            properties.put("url", url);

            properties.put("username", getProperty(dict, CFG__JDBC_USERNAME, ""));
            properties.put("password", getProperty(dict, CFG__JDBC_PASSWORD, ""));

            String config = getProperty(dict, CFG__MYBATIS_CONFIG, MYBATIS_DEFAULT_CONFIG);
            if (config.isEmpty()) {
                config = MYBATIS_DEFAULT_CONFIG;
            }

            Resources.setDefaultClassLoader(getClass().getClassLoader());

            try {
                if (createFactory(config, properties)) {
                    // Factory is created, so the new setting can be used as a default setting.
                    properties.put("config", config);
                    this.properties = new Properties(properties);
                }
            } catch (IOException e) { // thrown by getResourceAsStream()
                throw new ConfigurationException(CFG__MYBATIS_CONFIG, "Could not open MyBatis configuration file.", e);
            }
        }
    }

    /**
     * Helper method to safely obtain a property value from the given dictionary.
     *
     * @param properties the dictionary to retrieve the value from, can be <code>null</code>;
     * @param key the name of the property to retrieve, cannot be <code>null</code>;
     * @param defaultValue the default value to return in case the property does not exist, or the given dictionary was <code>null</code>.
     * @return a property value, can be <code>null</code>.
     */
    String getProperty(Dictionary<String, ?> properties, String key, String defaultValue) {
        String value = getProperty(properties, key);
        return (value == null) ? defaultValue : value;
    }

    /**
     * Helper method to safely obtain a property value from the given dictionary.
     *
     * @param properties the dictionary to retrieve the value from, can be <code>null</code>;
     * @param key the name of the property to retrieve, cannot be <code>null</code>.
     * @return a property value, can be <code>null</code>.
     */
    String getProperty(Dictionary<String, ?> properties, String key) {
        if (properties != null) {
            Object value = properties.get(key);
            if (value != null && value instanceof String) {
                return (String) value;
            }
        }
        return null;
    }
}
