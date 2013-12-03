package cz.zcu.kiv.crce.handler.metrics.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.felix.utils.manifest.Clause;
import org.apache.felix.utils.manifest.Parser;

import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.indexer.AbstractResourceIndexer;
import cz.zcu.kiv.crce.metadata.service.MetadataService;


public class MetricsIndexer extends AbstractResourceIndexer {
	
	private static final Logger logger = LoggerFactory.getLogger(MetricsIndexer.class);
	
	private volatile ResourceFactory resourceFactory;
	private volatile MetadataService metadataService;
	
	@Override
	public List<String> index(final InputStream input, Resource resource) {

		int size = 0;
		int numOfImports = -1;
				
		try {			
			size = input.available();
			
			Manifest manifest = null;
			
			ZipInputStream jis = new ZipInputStream(input);			
            for (ZipEntry e = jis.getNextEntry(); e != null; e = jis.getNextEntry()) {
                if (JarFile.MANIFEST_NAME.equalsIgnoreCase(e.getName())) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int n;
                    while ((n = jis.read(buf, 0, buf.length)) > 0) {
                        baos.write(buf, 0, n);
                    }
                    manifest = new Manifest(new ByteArrayInputStream(baos.toByteArray()));
                }
            }
            
            String header = null;
            Clause[] clauses = null;
			if (manifest != null)  {
				header = manifest.getMainAttributes().getValue(Constants.IMPORT_PACKAGE);
			}
			if (header != null) {
				clauses = Parser.parseHeader(header);				
			}
			if (clauses != null) {
				numOfImports = clauses.length;
			}			
				
			input.close(); // TODO close input stream by its creator.
		} catch (IOException e) {
            
			logger.error("Could not index resource.", e);
            return Collections.emptyList();
		} 
		
		Capability identity = metadataService.getSingletonCapability(resource, "crce.content");
		identity.setAttribute("size", Long.class, (long)size);
		
		if (numOfImports != -1) {
			Capability numOfImportsCap = resourceFactory.createCapability("crce.metrics");
			numOfImportsCap.setAttribute("name", String.class, "number-of-imports");
			numOfImportsCap.setAttribute("value", Long.class, (long)numOfImports);
			metadataService.addRootCapability(resource, numOfImportsCap);
		}
		
		return Collections.emptyList();
	}
	
    @Override
    public List<String> getRequiredCategories() {
        return Collections.singletonList("osgi");
    }
}
