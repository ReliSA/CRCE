package cz.zcu.kiv.crce.repository.maven.internal;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.indexer.AbstractResourceIndexer;



public class MavenArtifactMetadataIndexer extends AbstractResourceIndexer {

	@Override
	public List<String> index(InputStream input, Resource resource) {
		// TODO create maven artifact metadata Capability and Requirements
		return Collections.emptyList();
	}
}
