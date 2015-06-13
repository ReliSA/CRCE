package cz.zcu.kiv.crce.repository.maven.internal;

import org.apache.felix.dm.Component;

/**
 *
 * @author jkucera
 */
public class ComponentContext {

    private final String pid;
    private final Component component;
    private final String absoluteUri;
    private final MavenStoreConfiguration mavenStoreConfiguration;

    public ComponentContext(String pid, Component component, String absoluteUri, MavenStoreConfiguration mavenStoreConfiguration) {
        this.pid = pid;
        this.component = component;
        this.absoluteUri = absoluteUri;
        this.mavenStoreConfiguration = mavenStoreConfiguration;
    }

    public String getPid() {
        return pid;
    }

    public Component getComponent() {
        return component;
    }

    public String getAbsoluteUri() {
        return absoluteUri;
    }

    public MavenStoreConfiguration getMavenStoreConfiguration() {
        return mavenStoreConfiguration;
    }
}
