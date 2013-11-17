package cz.zcu.kiv.crce.metadata;

import java.io.Serializable;
import java.net.URI;

import javax.annotation.Nonnull;

/**
 * Descriptor of resources storage.
 *
 * PENDING Attributes and directives could be added instead of hard-coded ones like URI.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Repository extends Serializable {

    /**
     * Return the associated URL for the repository.
     *
     * @return
     */
    @Nonnull
    URI getURI();
}