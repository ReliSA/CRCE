package cz.zcu.kiv.crce.repository.maven.internal;

import java.io.File;
import java.net.URI;

/**
 * Wrapper, which holds informations about repository
 * Coulbe remote or local
 * 
 * @author Miroslav Brozek
 *
 */
public class RepositoryWrapper {
	
	private URI uri;
	
	private String name;
	
	boolean update;
	
	boolean local;

	public RepositoryWrapper(URI uri, String name, boolean update, boolean local) {		
		this.uri = uri;
		this.name = name;
		this.update = update;
		this.local = local;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public boolean isLocal() {
		return local;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}
	
	public String getURItoPath() {
		File file = new File(this.uri);
		String path = file.getAbsolutePath();
		path = path.replace('\\', '/');
		return path;

	}

}
