package cz.zcu.kiv.crce.efp.indexer.internal;

import java.util.List;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.efps.assignment.api.ComponentEfpAccessor;
import cz.zcu.kiv.efps.assignment.api.EfpAwareComponentLoader;
import cz.zcu.kiv.efps.assignment.types.Feature;
import cz.zcu.kiv.efps.types.lr.LR;

/**
 *
 */
public class IndexerDataContainer {

	/** Path of resource file entering indexing process. */
	private String sourceFilePath;

	/** This interface serves API to obtain EFPs attached to a component. */
	private EfpAwareComponentLoader loader;

	/** This interface accesses EFPs attached to a component.
	 * It allows to read all feature, EFPs and values attached to the EFPs on the component. */
	private ComponentEfpAccessor accessor;

	/** In OSGi context this list contain exported or imported packages. */
	private List<Feature> featureList;

	/** Local Registry array. */
	private LR []arrayLR;

	/** Resource can be OSGi bundle or other artifact uploaded into CRCE buffer. */
	private Resource resource;

	//------------------------------------------
	// Getters and Setters

	/**
	 * @return the sourceFilePath
	 */
	public final String getSourceFilePath() {
		return sourceFilePath;
	}

	/**
	 * @param sourceFilePath the sourceFilePath to set
	 */
	public final void setSourceFilePath(final String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
	}

	/**
	 * @return the loader
	 */
	public final EfpAwareComponentLoader getLoader() {
		return loader;
	}

	/**
	 * @param loader the loader to set
	 */
	public final void setLoader(final EfpAwareComponentLoader loader) {
		this.loader = loader;
	}

	/**
	 * @return the accessor
	 */
	public final ComponentEfpAccessor getAccessor() {
		return accessor;
	}

	/**
	 * @param accessor the accessor to set
	 */
	public final void setAccessor(final ComponentEfpAccessor accessor) {
		this.accessor = accessor;
	}

	/**
	 * @return the featureList
	 */
	public final List<Feature> getFeatureList() {
		return featureList;
	}

	/**
	 * @param featureList the featureList to set
	 */
	public final void setFeatureList(final List<Feature> featureList) {
		this.featureList = featureList;
	}

	/**
	 * @return the arrayLR
	 */
	public final LR[] getArrayLR() {
		return arrayLR;
	}

	/**
	 * @param arrayLR the arrayLR to set
	 */
	public final void setArrayLR(final LR[] arrayLR) {
		this.arrayLR = arrayLR;
	}

	/**
	 * @return the resource
	 */
	public final Resource getResource() {
		return resource;
	}

	/**
	 * @param resource the resource to set
	 */
	public final void setResource(final Resource resource) {
		this.resource = resource;
	}

}
