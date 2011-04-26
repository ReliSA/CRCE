package cz.zcu.kiv.crce.metadata;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface Reason {

    Resource getResource();

    Requirement getRequirement();
}
