package cz.zcu.kiv.crce.repository.plugins;

import java.util.Properties;

import cz.zcu.kiv.crce.plugin.AbstractPlugin;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
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
