package cz.zcu.kiv.crce.metadata;

import java.util.Map;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface DirectiveProvider {

    public String getDirective(String name);

    public Map<String, String> getDirectives();

    public void setDirective(String name, String directive);
}
