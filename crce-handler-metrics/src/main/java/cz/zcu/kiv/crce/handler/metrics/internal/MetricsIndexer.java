package cz.zcu.kiv.crce.handler.metrics.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.indexer.AbstractResourceIndexer;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiPackage;
import cz.zcu.kiv.crce.metadata.service.MetadataService;


public class MetricsIndexer extends AbstractResourceIndexer {
	
	private static final double CLASS_WEIGHT = 1.0;
	
	private static final Logger logger = LoggerFactory.getLogger(MetricsIndexer.class);
	
	private volatile ResourceFactory resourceFactory;
	private volatile MetadataService metadataService;
	
	private List<ClassMetrics> classMetrics;
	private List<Capability> exportPackageCapabilities;
	
	@Override
	public List<String> index(final InputStream input, Resource resource) {
		int size = 0;	
		
		classMetrics = new ArrayList<ClassMetrics>();
		exportPackageCapabilities = resource.getCapabilities(NsOsgiPackage.NAMESPACE__OSGI_PACKAGE);
		
		try {			
			size = input.available();					
			ZipInputStream jis = new ZipInputStream(input);			
            for (ZipEntry e = jis.getNextEntry(); e != null; e = jis.getNextEntry()) {
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
		
		numberOfImports(resource);
		apiComplexity();
				
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
	
	private void numberOfImports(Resource resource) {
		List<Requirement> requirements = resource.getRequirements();
				
		int numOfImports = requirements.size();

		Capability numOfImportsCap = resourceFactory.createCapability(NsMetrics.NAMESPACE__METRICS);
		numOfImportsCap.setAttribute(NsMetrics.ATTRIBUTE__NAME, "number-of-imports");
		numOfImportsCap.setAttribute(NsMetrics.ATTRIBUTE__LONG__VALUE, (long)numOfImports);
		metadataService.addRootCapability(resource, numOfImportsCap);
	}
		
	private void parseClass(ClassReader classReader) {
        ClassNode byteCodeNode = new ClassNode();
        classReader.accept(byteCodeNode, ClassReader.SKIP_DEBUG);
        
        classMetrics.add(new ClassMetrics(byteCodeNode));
	}
	
	private void apiComplexity() {	
		for (Capability exportPackageCapability : exportPackageCapabilities) {
		
			Attribute<String> packageNameAttribute = exportPackageCapability.getAttribute(NsOsgiPackage.ATTRIBUTE__NAME);
			
			if (packageNameAttribute != null) {			
				String packageName = packageNameAttribute.getValue();
				
				double cmpC = 0; 
				double sumClassComplexity = 0; 
				double sumMethodComplexity = 0;
				
				int classCount = 0;
				int interfaceCount = 0;
				double weightedMethodCountSum = 0;
				
				for (ClassMetrics classMetric : classMetrics) {
					if (classMetric.isPublic() && classMetric.getPackageName().compareTo(packageName) == 0) {
						sumClassComplexity += classMetric.getClassComplexity();
						
						sumMethodComplexity += classMetric.getMethodsComplexity();
						
						weightedMethodCountSum += classMetric.getWeightedMethodCount();
						
						if (classMetric.isInterface()) {
							interfaceCount++;
						}
						else {
							classCount++;
						}
					}
				}
				
				cmpC = classCount * CLASS_WEIGHT + interfaceCount + weightedMethodCountSum;
				
				double complexity = cmpC + sumClassComplexity + sumMethodComplexity;
								
				Capability metricsCapability = resourceFactory.createCapability(NsMetrics.NAMESPACE__METRICS);
				metricsCapability.setAttribute(NsMetrics.ATTRIBUTE__NAME, "api-complexity");
				metricsCapability.setAttribute(NsMetrics.ATTRIBUTE__DOUBLE__VALUE, complexity);		
				metadataService.addChild(exportPackageCapability, metricsCapability);
			}
		}
	}
	
    @Override
    public List<String> getRequiredCategories() {
        return Collections.singletonList("osgi");
    }
}
