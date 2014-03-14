package cz.zcu.kiv.crce.handler.metrics.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import cz.zcu.kiv.crce.handler.metrics.ComponentMetrics;
import cz.zcu.kiv.crce.handler.metrics.Metrics;
import cz.zcu.kiv.crce.handler.metrics.PackageMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.ClassMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.ClassesMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.impl.ClassMetricsImpl;
import cz.zcu.kiv.crce.handler.metrics.asm.impl.ClassesMetricsImpl;
import cz.zcu.kiv.crce.handler.metrics.impl.AverageCyclomaticComplexity;
import cz.zcu.kiv.crce.handler.metrics.impl.CpcMetrics;
import cz.zcu.kiv.crce.handler.metrics.impl.MaximumCyclomaticComplexity;
import cz.zcu.kiv.crce.handler.metrics.impl.MinimumCyclomaticComplexity;
import cz.zcu.kiv.crce.handler.metrics.impl.NumberOfImportsMetrics;
import cz.zcu.kiv.crce.handler.metrics.impl.RippleEffectMetrics;
import cz.zcu.kiv.crce.handler.metrics.impl.WTCohMetrics;
import cz.zcu.kiv.crce.handler.metrics.impl.WTCoupMetrics;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiPackage;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

/**
 * Measurement of metrics computed on OSGI bundle implementation (jar file).
 * Store computed values into Resources.
 *
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public class MetricsIndexer {

	private static final Logger logger = LoggerFactory.getLogger(MetricsIndexer.class);

	private MetadataFactory metadataFactory;
	private MetadataService metadataService;

	private List<ClassMetrics> classMetricsList;
	private ClassesMetrics classesMetrics;

	/**
	 * New instance.
	 *
	 * @param metadataFactory MetadataFactory
	 * @param metadataService MetadataService
	 */
	public MetricsIndexer(@Nonnull MetadataFactory metadataFactory, @Nonnull MetadataService metadataService) {
		this.metadataFactory = metadataFactory;
		this.metadataService = metadataService;
	}

	/**
	 * Compute metrics for jar file (OSGI bundle).
	 *
	 * @param input InputStream of jar file.
	 * @param resource Resource to save computed data.
	 */
	public void index(final InputStream input, @Nonnull Resource resource) {

		classMetricsList = new ArrayList<ClassMetrics>();

		// parsing input stream and collect class entry informations (ClassMetrics)
		try {
			ZipInputStream jis = new ZipInputStream(input);
            for (ZipEntry e = jis.getNextEntry(); e != null; e = jis.getNextEntry()) {
                if (e.getName().endsWith(".class")) {
                	parseClass(new ClassReader(getEntryImputStream(jis)));
                }
            }
		} catch (IOException e) {

			logger.error("Could not index resource.", e);
            return;
		}

		classesMetrics = new ClassesMetricsImpl(classMetricsList);
	}

	/**
	 * Create metrics objects, compute metrics and computed values put into Resource.
	 *
	 * This method is a point, where to add new implemented metrics. You should create metrics computing object
	 * and add it to at least 'allMetrics' collection (init call). You probably will add it to at least one other
	 * metrics collection to compute values and add it into metadata.
	 *
	 * @param resource Resource to save computed data.
	 */
	public void computeAndSaveMetrics(Resource resource) {

		// create objects
		List<Metrics> allMetrics = new ArrayList<Metrics>();
		List<ComponentMetrics> componentMetrics = new ArrayList<ComponentMetrics>();
		List<PackageMetrics> packageMetrics = new ArrayList<PackageMetrics>();

		NumberOfImportsMetrics numberOfImportsMetrics = new NumberOfImportsMetrics(resource);
		CpcMetrics cpcMetrics = new CpcMetrics(classesMetrics);
		RippleEffectMetrics rippleEffectMetrics = new RippleEffectMetrics(classesMetrics);
		AverageCyclomaticComplexity averageCyclomaticComplexity = new AverageCyclomaticComplexity(classesMetrics);
		MaximumCyclomaticComplexity maximumCyclomaticComplexity = new MaximumCyclomaticComplexity(classesMetrics);
		MinimumCyclomaticComplexity minimumCyclomaticComplexity = new MinimumCyclomaticComplexity(classesMetrics);
		WTCohMetrics wTCohMetrics = new WTCohMetrics(classesMetrics);
		WTCoupMetrics wTCoupMetrics = new WTCoupMetrics(classesMetrics);

		// fill metrics lists
		allMetrics.add(numberOfImportsMetrics);
		allMetrics.add(cpcMetrics);
		allMetrics.add(rippleEffectMetrics);
		allMetrics.add(averageCyclomaticComplexity);
		allMetrics.add(maximumCyclomaticComplexity);
		allMetrics.add(minimumCyclomaticComplexity);
		allMetrics.add(wTCohMetrics);
		allMetrics.add(wTCoupMetrics);

		componentMetrics.add(numberOfImportsMetrics);
		componentMetrics.add(averageCyclomaticComplexity);
		componentMetrics.add(maximumCyclomaticComplexity);
		componentMetrics.add(minimumCyclomaticComplexity);
		componentMetrics.add(wTCohMetrics);
		componentMetrics.add(wTCoupMetrics);

		packageMetrics.add(cpcMetrics);
		packageMetrics.add(rippleEffectMetrics);

		// init metrics
		for (Metrics metric : allMetrics) {
			metric.init();
		}

		// compute metrics
		for (ComponentMetrics metric : componentMetrics) {
			computeMetricsForComponent(metric, resource);
		}

		for (PackageMetrics metric : packageMetrics) {
			computeMetricsForPackages(metric, resource.getCapabilities(NsOsgiPackage.NAMESPACE__OSGI_PACKAGE));
		}
		
		// "metrics" tag indicate, that the resource has been measured
		metadataService.addCategory(resource, "metrics");
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

        classMetricsList.add(new ClassMetricsImpl(byteCodeNode));
	}

	/**
	 * Compute selected metrics for entire component and save it into resource.
	 *
	 * @param metrics Object of metrics to be computed.
	 * @param resource Resource to save computed metrics property.
	 */
	private void computeMetricsForComponent(ComponentMetrics metrics, Resource resource) {

		Property<Resource> metricsProperty = metadataFactory.createProperty(NsMetrics.NAMESPACE__METRICS);
		addAtrributesToMetricsProperty(metricsProperty, metrics.getName(), metrics.computeValue());

		resource.addProperty(metricsProperty);
	}

	/**
	 * Compute selected metrics for all packages and save it into corresponding capability.
	 *
	 * @param metrics Object of metrics to be computed.
	 * @param exportPackageCapabilities Package capabilities.
	 */
	private void computeMetricsForPackages(PackageMetrics metrics, List<Capability> exportPackageCapabilities) {

		// log if no export packages are set in Resources -> nothing to compute
		if (exportPackageCapabilities.isEmpty()) {

			logger.error("No export packages found in metadata capabilities.");
			return;
		}

		// for each package
		for (Capability exportPackageCapability : exportPackageCapabilities) {

			Attribute<String> packageNameAttribute = exportPackageCapability.getAttribute(NsOsgiPackage.ATTRIBUTE__NAME);

			if (packageNameAttribute != null) {
				String packageName = packageNameAttribute.getValue();

				Property<Capability> metricsProperty = metadataFactory.createProperty(NsMetrics.NAMESPACE__METRICS);
				addAtrributesToMetricsProperty(metricsProperty, metrics.getName(), metrics.computeValueForPackage(packageName));

				exportPackageCapability.addProperty(metricsProperty);
			}
		}
	}

	/**
	 * Fill metrics property with metrics values.
	 *
	 * @param metricsProperty Metrics property.
	 * @param name Name of property.
	 * @param value Metrics value.
	 */
	private void addAtrributesToMetricsProperty(Property<?> metricsProperty, String name, Object value) {

		metricsProperty.setAttribute(NsMetrics.ATTRIBUTE__NAME, name);
		if (value instanceof Long) {
			metricsProperty.setAttribute(NsMetrics.ATTRIBUTE__LONG__VALUE, (Long)value);
		}
		else if (value instanceof Double) {
			metricsProperty.setAttribute(NsMetrics.ATTRIBUTE__DOUBLE__VALUE, (Double)value);
		}
		else {
			metricsProperty.setAttribute(NsMetrics.ATTRIBUTE__STRING__VALUE, value.toString());
		}
	}
}
