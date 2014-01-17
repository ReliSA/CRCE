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

/**
 * Implementation of <code>AbstractResourceIndexer</code> which provides measurement
 * of metrics computed on osgi bundle implementation (jar file).
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public class MetricsIndexer extends AbstractResourceIndexer {
	
	private static final Logger logger = LoggerFactory.getLogger(MetricsIndexer.class);
	
	private volatile ResourceFactory resourceFactory;
	private volatile MetadataService metadataService;
	
	private List<ClassMetrics> classesMetrics;
	
	@Override
	public List<String> index(final InputStream input, Resource resource) {
		int size = 0;	
		
		classesMetrics = new ArrayList<ClassMetrics>();
		
		// parsing imput stream and collect class entry informations (ClassMetrics)
		try {			
			size = input.available();					
			ZipInputStream jis = new ZipInputStream(input);			
            for (ZipEntry e = jis.getNextEntry(); e != null; e = jis.getNextEntry()) {
                if (e.getName().endsWith(".class")) {
                	parseClass(new ClassReader(getEntryImputStream(jis)));
                }
            }
		} catch (IOException e) {
            
			logger.error("Could not index resource.", e);
            return Collections.emptyList();
		} 
		
		// save jar file size to crce.content
		Capability identity = metadataService.getSingletonCapability(resource, "crce.content");
		identity.setAttribute("size", Long.class, (long)size);
		
		// save number of imports
		numberOfImports(resource);
		
		// save api complexity
		apiComplexity(resource.getCapabilities(NsOsgiPackage.NAMESPACE__OSGI_PACKAGE));
		
		rippleEffect(resource.getCapabilities(NsOsgiPackage.NAMESPACE__OSGI_PACKAGE));
				
		return Collections.emptyList();
	}
	
	/**
	 * Reading single entry from ZipInputStream.
	 * 
	 * @param jis ZipInputStream.
	 * @return Single entry InputStream.
	 * @throws IOException
	 */
	private InputStream getEntryImputStream(ZipInputStream jis) throws IOException	{		
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n;
        while ((n = jis.read(buf, 0, buf.length)) > 0) {
            baos.write(buf, 0, n);
        }
        
        return new ByteArrayInputStream(baos.toByteArray());
	}
	
	/**
	 * Parse class metrics (data) from single ClassReader using ASM.
	 * 
	 * @param classReader ClassReader of single class node.
	 */
	private void parseClass(ClassReader classReader) {
        ClassNode byteCodeNode = new ClassNode();
        classReader.accept(byteCodeNode, ClassReader.SKIP_DEBUG);
        
        classesMetrics.add(new ClassMetrics(byteCodeNode));
	}
	
	/**
	 * Parse and save number of imports into resource.
	 * 
	 * @param resource Resource to update.
	 */
	private void numberOfImports(Resource resource) {
		List<Requirement> requirements = resource.getRequirements();
				
		int numOfImports = requirements.size();

		Capability numOfImportsCap = resourceFactory.createCapability(NsMetrics.NAMESPACE__METRICS);
		numOfImportsCap.setAttribute(NsMetrics.ATTRIBUTE__NAME, "number-of-imports");
		numOfImportsCap.setAttribute(NsMetrics.ATTRIBUTE__LONG__VALUE, (long)numOfImports);
		metadataService.addRootCapability(resource, numOfImportsCap);
	}
		
	/**
	 * Save complexity (CPC) to each export package node in Resource.
	 * 
	 * @param exportPackageCapabilities List of Capability with exported packages.
	 */
	private void apiComplexity(List<Capability> exportPackageCapabilities) {	
		
		// log if no export packages are set in Resources -> nothing to compute
		if (exportPackageCapabilities.isEmpty()) {
			
			logger.error("No export packages found in metadata capabilities.");			
			return;
		}
		
		// create CpsMetrics from all parsed classes data
		CpcMetrics cpcMetrics = new CpcMetrics(classesMetrics);
		
		// for each package
		for (Capability exportPackageCapability : exportPackageCapabilities) {
		
			Attribute<String> packageNameAttribute = exportPackageCapability.getAttribute(NsOsgiPackage.ATTRIBUTE__NAME);
			
			if (packageNameAttribute != null) {			
				String packageName = packageNameAttribute.getValue();
								
				// get complexity for this package and save it into capability
				double complexity = cpcMetrics.computeCpcForPackage(packageName);
								
				Capability metricsCapability = resourceFactory.createCapability(NsMetrics.NAMESPACE__METRICS);
				metricsCapability.setAttribute(NsMetrics.ATTRIBUTE__NAME, "api-complexity");
				metricsCapability.setAttribute(NsMetrics.ATTRIBUTE__DOUBLE__VALUE, complexity);		
				metadataService.addChild(exportPackageCapability, metricsCapability);
			}
		}
	}
	
	private void rippleEffect(List<Capability> exportPackageCapabilities) {	
		
		// log if no export packages are set in Resources -> nothing to compute
		if (exportPackageCapabilities.isEmpty()) {
			
			logger.error("No export packages found in metadata capabilities.");			
			return;
		}
		
		RippleEffectMetrics rippleEffectMetrics = new RippleEffectMetrics(classesMetrics);
		rippleEffectMetrics.init();
		
		for (Capability exportPackageCapability : exportPackageCapabilities) {
		
			Attribute<String> packageNameAttribute = exportPackageCapability.getAttribute(NsOsgiPackage.ATTRIBUTE__NAME);
			
			if (packageNameAttribute != null) {			
				String packageName = packageNameAttribute.getValue();
								
				RippleEffect rippleEffect = rippleEffectMetrics.getRippleEffectForPackage(packageName);
								
				Capability metricsCapability = resourceFactory.createCapability(NsMetrics.NAMESPACE__METRICS);
				metricsCapability.setAttribute(NsMetrics.ATTRIBUTE__NAME, "ripple-effect");
				metricsCapability.setAttribute(NsMetrics.ATTRIBUTE__LONG__VALUE, 
						(long)(rippleEffect.getInternalNonAbstractMethodsCount() 
								+ rippleEffect.getInternalAbstractMethodsCount()
								+ rippleEffect.getExternalMethodsCount()));		
				metadataService.addChild(exportPackageCapability, metricsCapability);
			}
		}
	}
	
    @Override
    public List<String> getRequiredCategories() {
        return Collections.singletonList("osgi");
    }
}
