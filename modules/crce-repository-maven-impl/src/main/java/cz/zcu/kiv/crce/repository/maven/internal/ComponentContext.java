package cz.zcu.kiv.crce.repository.maven.internal;

import java.util.Objects;

import javax.management.ObjectName;

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
    private final ObjectName objectName;

    public ComponentContext(String pid, Component component, String absoluteUri, MavenStoreConfiguration mavenStoreConfiguration, ObjectName objectName) {
        this.pid = pid;
        this.component = component;
        this.absoluteUri = absoluteUri;
        this.mavenStoreConfiguration = mavenStoreConfiguration;
        this.objectName = objectName;
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

    public ObjectName getObjectName() {
        return objectName;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.pid);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ComponentContext other = (ComponentContext) obj;
        if (!Objects.equals(this.pid, other.pid)) {
            return false;
        }
        return true;
    }
}
