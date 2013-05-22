package cz.zcu.kiv.crce.metadata.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cz.zcu.kiv.crce.metadata.DirectiveProvider;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class AbstractDirectiveProvider implements DirectiveProvider {

    protected final Map<String, String> directivesMap = new HashMap<>();

    @Override
    public String getDirective(String name) {
        return directivesMap.get(name);
    }

    @Override
    public Map<String, String> getDirectives() {
        return Collections.unmodifiableMap(directivesMap);
    }

    @Override
    public boolean setDirective(String name, String directive) {
        directivesMap.put(name, directive);
        return true;
    }

    @Override
    public boolean unsetDirective(String name) {
        return directivesMap.remove(name) != null;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.directivesMap);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractDirectiveProvider other = (AbstractDirectiveProvider) obj;
        if (!Objects.equals(this.directivesMap, other.directivesMap)) {
            return false;
        }
        return true;
    }
}
