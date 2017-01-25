package cz.zcu.kiv.crce.metadata.service.internal.validation;

import cz.zcu.kiv.crce.metadata.service.validation.Reason;
import cz.zcu.kiv.crce.metadata.service.validation.ReasonType;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class ReasonImpl implements Reason {

    private final ReasonType type;
    private final String id;
    private final String text;

    public ReasonImpl(ReasonType type, String id, String text) {
        this.type = type;
        this.id = id;
        this.text = text;
    }

    @Override
    public ReasonType getType() {
        return type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }
}
