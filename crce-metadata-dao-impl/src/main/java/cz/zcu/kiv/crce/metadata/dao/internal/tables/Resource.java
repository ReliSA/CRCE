/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.crce.metadata.dao.internal.tables;

/**
 *
 * @author cihlator
 */
public class Resource {
    private int internal_id;
    private String id;
    private String uri;
    private String repository_uri;

    public int getInternal_id() {
        return internal_id;
    }

    public void setInternal_id(int internal_id) {
        this.internal_id = internal_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getRepository_uri() {
        return repository_uri;
    }

    public void setRepository_uri(String repository_uri) {
        this.repository_uri = repository_uri;
    }
}
