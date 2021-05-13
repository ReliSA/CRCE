package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

import java.util.HashMap;
import java.util.Map;

/**
 * Inspired by ghessova on 05.03.2018. Path part - class or method
 */
public class PathPart {

    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
