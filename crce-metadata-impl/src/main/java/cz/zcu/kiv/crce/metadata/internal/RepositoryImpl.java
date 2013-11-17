package cz.zcu.kiv.crce.metadata.internal;

import java.net.URI;
import java.util.Objects;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Repository;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class RepositoryImpl implements Repository {

    private static final long serialVersionUID = -5217714862846966653L;

    private final URI uri;

    public RepositoryImpl(@Nonnull URI uri) {
        this.uri = uri;
    }

    @Override
    public URI getURI() {
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
