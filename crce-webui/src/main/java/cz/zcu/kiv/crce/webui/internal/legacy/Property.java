package cz.zcu.kiv.crce.webui.internal.legacy;

/**
 * A property that can be set to a Resource or a Capability.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public interface Property {

    String getName();

    Type getType();

    String getValue();

    Object getConvertedValue();
    
    boolean isWritable();
            
//    void setValue(String value, Type type);
//    
//    void setValue(String string);
//    
//    void setValue(Version version);
//
//    void setValue(URL url);
//    
//    void setValue(URI uri);
//    
//    void setValue(long llong);
//    
//    void setValue(double ddouble);
//    
//    void setValue(Set values);
    
}
