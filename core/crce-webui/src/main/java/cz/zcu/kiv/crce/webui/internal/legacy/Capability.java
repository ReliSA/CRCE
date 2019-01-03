package cz.zcu.kiv.crce.webui.internal.legacy;

/**
 * Represents an OBR Capability.
 * 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Capability extends PropertyProvider<Capability> {

    String getName();
    
    NewProperty[] getNewProperties();
}
