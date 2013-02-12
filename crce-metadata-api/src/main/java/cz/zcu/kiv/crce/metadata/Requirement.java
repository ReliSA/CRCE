package cz.zcu.kiv.crce.metadata;

/**
 * Represents a requirement to a capability with the same name.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Requirement extends AttributeProvider, DirectiveProvider {

    public String getName();

    public Resource getResource();
}
