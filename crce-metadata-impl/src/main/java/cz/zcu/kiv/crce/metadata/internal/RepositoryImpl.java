package cz.zcu.kiv.crce.metadata.internal;

import java.net.URI;
import java.util.Objects;

import javax.annotation.ParametersAreNonnullByDefault;

import cz.zcu.kiv.crce.metadata.Repository;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public class RepositoryImpl implements Repository {

    private static final long serialVersionUID = -5217714862846966653L;

    private final String id;
    private final URI uri;

    public RepositoryImpl(URI uri, String id) {
        this.uri = uri;
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.uri);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RepositoryImpl other = (RepositoryImpl) obj;
        return Objects.equals(this.uri, other.uri);
    }

    @Override
    public String toString() {
        return "RepositoryImpl{" + "uri=" + uri + '}';
    }
}
