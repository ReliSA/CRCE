package cz.zcu.kiv.crce.repository.plugins;

import cz.zcu.kiv.crce.plugin.AbstractPlugin;
import java.util.Properties;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public abstract class AbstractExecutable extends AbstractPlugin implements Executable {

    @Override
    public boolean isExclusive() {
        return false;
    }

    @Override
    public Properties getProperties() {
        return new Properties();
    }

}
