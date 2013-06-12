/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.crce.metadata.dao.internal.tables;

/**
 *
 * @author cihlator
 */
public class Cap_directive {
    // private int internal id; // not needed
    private String name;
    private String value;
    private int capability_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCapability_id() {
        return capability_id;
    }

    public void setCapability_id(int capability_id) {
        this.capability_id = capability_id;
    }
    
    
}
