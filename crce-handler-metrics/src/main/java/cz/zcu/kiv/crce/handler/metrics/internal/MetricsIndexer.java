package cz.zcu.kiv.crce.handler.metrics.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.indexer.AbstractResourceIndexer;
import cz.zcu.kiv.crce.metadata.service.MetadataService;


public class MetricsIndexer extends AbstractResourceIndexer {
	
	private static final Logger logger = LoggerFactory.getLogger(MetricsIndexer.class);
	
	private volatile ResourceFactory resourceFactory;
	private volatile MetadataService metadataService;
	
	private List<ClassMetrics> classMetrics;
	private Clause[] exportPackageClauses;
	
	@Override
	public List<String> index(final InputStream input, Resource resource) {
		int size = 0;	
		
		classMetrics = new ArrayList<ClassMetrics>();
		exportPackageClauses = null;
		
		try {			
			size = input.available();					
			ZipInputStream jis = new ZipInputStream(input);			
            for (ZipEntry e = jis.getNextEntry(); e != null; e = jis.getNextEntry()) {
            	
                if (JarFile.MANIFEST_NAME.equalsIgnoreCase(e.getName())) {                   
                    parseManifest(new Manifest(getEntryImputStream(jis)), resource);
                }
                
                if (e.getName().endsWith(".class")) {
                	parseClass(new ClassReader(getEntryImputStream(jis)));
                }
            }
				
			input.close(); // TODO close input stream by its creator.
		} catch (IOException e) {
            
			logger.error("Could not index resource.", e);
            return Collections.emptyList();
		} 
		
		Capability identity = metadataService.getSingletonCapability(resource, "crce.content");
		identity.setAttribute("size", Long.class, (long)size);
		
		apiComplexity(resource);
				
		return Collections.emptyList();
	}
	
	private InputStream getEntryImputStream(ZipInputStream jis) throws IOException	{		
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n;
        while ((n = jis.read(buf, 0, buf.length)) > 0) {
            baos.write(buf, 0, n);
        }
        
        return new ByteArrayInputStream(baos.toByteArray());
	}
	
	private void parseManifest(Manifest manifest, Resource resource) {
		int numOfImports = -1;
        String importPackageHeader = null;
        Clause[] importPackageClauses = null;
		if (manifest != null)  {
			importPackageHeader = manifest.getMainAttributes().getValue(Constants.IMPORT_PACKAGE);
		}
		if (importPackageHeader != null) {
			importPackageClauses = Parser.parseHeader(importPackageHeader);				
		}
		if (importPackageClauses != null) {
			numOfImports = importPackageClauses.length;
		}
		
		if (numOfImports != -1) {
			Capability numOfImportsCap = resourceFactory.createCapability("crce.metrics");
			numOfImportsCap.setAttribute("name", String.class, "number-of-imports");
			numOfImportsCap.setAttribute("value", Long.class, (long)numOfImports);
			metadataService.addRootCapability(resource, numOfImportsCap);
		}
		
        String exportPackageHeader = null;
		if (manifest != null)  {
			exportPackageHeader = manifest.getMainAttributes().getValue(Constants.EXPORT_PACKAGE);
		}
		if (exportPackageHeader != null) {
			exportPackageClauses = Parser.parseHeader(exportPackageHeader);				
		}
	}
	
	private void parseClass(ClassReader classReader) {
        ClassNode byteCodeNode = new ClassNode();
        classReader.accept(byteCodeNode, ClassReader.SKIP_DEBUG);
        
        classMetrics.add(new ClassMetrics(byteCodeNode));
	}
	
	private void apiComplexity(Resource resource) {
		if (exportPackageClauses == null) {
			return;
		}
		
		for (Clause exportPackageClause : exportPackageClauses) {
			String packageName = exportPackageClause.getName();
			
			int weightedNumPublicMethods = 0;
			int numOfClasses = 0;
			
			for (ClassMetrics classMetric : classMetrics) {
				if (classMetric.isPublic() && !classMetric.isInterface() && classMetric.getPackageName().compareTo(packageName) == 0) {
					numOfClasses++;
					weightedNumPublicMethods += classMetric.getParameterWeightedNumPublicMethods();
				}
			}
			
			if (numOfClasses > 0) {			
				double complexity = (double)weightedNumPublicMethods / numOfClasses;
				
				Capability capability = resourceFactory.createCapability("osgi.wiring.package");
				capability.setAttribute("name", String.class, packageName);
				metadataService.addRootCapability(resource, capability);
				
				Capability metricsCapability = resourceFactory.createCapability("crce.metric");
				metricsCapability.setAttribute("name", String.class, "api-complexity");
				metricsCapability.setAttribute("value", Double.class, complexity);		
				metadataService.addChild(capability, metricsCapability);
			}
		}
	}
	
    @Override
    public List<String> getRequiredCategories() {
        return Collections.singletonList("osgi");
    }
}
