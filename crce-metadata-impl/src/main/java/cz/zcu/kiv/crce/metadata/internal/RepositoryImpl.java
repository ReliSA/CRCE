package cz.zcu.kiv.crce.metadata.internal;

import java.net.URI;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Repository;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class RepositoryImpl implements Repository {

    private static final long serialVersionUID = -5217714862846966653L;

    private URI uri;

    public RepositoryImpl(@Nonnull URI uri) {
        this.uri = uri;
    }

    @Override
    public URI getURI() {
        return uri;
    }

    @Override
    public long getIncrement() {
        return -1;
    }

}
