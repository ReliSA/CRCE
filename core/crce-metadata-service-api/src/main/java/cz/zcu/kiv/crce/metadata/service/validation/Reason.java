package cz.zcu.kiv.crce.metadata.service.validation;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Reason {

    String getId();

    ReasonType getType();

    String getText();
}
