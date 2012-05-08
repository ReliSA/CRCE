package cz.zcu.kiv.crce.efp.indexer.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.efps.assignment.types.Feature;
import cz.zcu.kiv.efps.assignment.types.Feature.AssignmentSide;
import cz.zcu.kiv.efps.assignment.values.EfpAssignedValue.AssignmentType;
import cz.zcu.kiv.efps.types.properties.EFP;


/**
 * Class ensures indexing extra-functional properties directly into resource OBR metadata.
 *
 * Transcription format EFP -> OBR is mostly separated from other functional code
 * and person who change this transcription format doesn't need to care much about
 * code in other classes. However addProperty() method used for indexing 'value' attribute
 * is called from EFPAssignmentTypeResolver class, because computing efp-value
 * depends on resolved EFPAssignmentType.
 */
public class EFPToOBRTranscription {

	/**	Resource represents an OSGi artifact to which will be created
	 * 	Capability or Requirement OBR metadata. */
	private Resource res;

	/**	AssignmentSide represents PROVIDED or REQUIRED type of given feature. */
	private AssignmentSide side;

	/**	Requirement instance is used in case of feature with REQUIRED side. */
	private Requirement req;

	/**	Capability instance is used in case of feature with PROVIDED side. */
	private Capability cap;

	/**	String filter is used for collecting attributes of EFP
	 *  in case of feature with REQUIRED side. */
	private String reqFilter;

	/**
	 * Constructor of EFPToOBRTranscription class ensures transfer of basic data instances.
	 * These instances contain metadata which will be indexed
	 * and data for transcript process control.
	 *
	 * @param res2 - Resource represents an OSGi artifact to which will be created
	 * Capability or Requirement OBR metadata.
	 * @param feature - EFP belongs to this feature. Feature instance
	 * carries parent-name and parent-type metadata for given EFP.
	 * @param efp - This is actual processed EFP, which metadata are indexed.
	 * @param type - AssignmentType (direct, formula, named) of given EFP.
	 */
	public EFPToOBRTranscription(final Resource res2, final Feature feature, final EFP efp, final AssignmentType type) {
		this.res = res2;
		this.side = feature.getSide();

		if (side == AssignmentSide.PROVIDED) {
			createCap(feature, efp, type);
		} else if (side == AssignmentSide.REQUIRED) {
			createReq(feature, efp, type);
		}
	}

	/**
	 * Method ensures creating OBR Capability to a resource obtained in constructor
	 * and indexing some essential metadata which belong to processed EFP.
	 *
	 * @param feature - EFP belongs to this feature. Feature instance
	 * carries parent-name and parent-type metadata for given EFP.
	 * @param efp - This is actual processed EFP, which metadata are indexed.
	 * @param type - AssignmentType (direct, formula, named) of given EFP.
	 */
	public final void createCap(final Feature feature, final EFP efp, final AssignmentType type) {
		cap = res.createCapability("EFP");
		cap.setProperty("parent-type", feature.getRepresentElement().toLowerCase());
		cap.setProperty("parent-name", feature.getName());
		cap.setProperty("efp-name", efp.getName());
		cap.setProperty("assignment-type", type.toString().toUpperCase());

		secondaryImportanceMetadataInCapability(efp);
	}

	/**
	 * Method ensures creating OBR Requirement to a resource obtained in constructor
	 * and indexing some essential metadata which belong to processed EFP.
	 *
	 * @param feature - EFP belongs to this feature. Feature instance
	 * carries parent-name and parent-type metadata for given EFP.
	 * @param efp - This is actual processed EFP, which metadata are indexed.
	 * @param type - AssignmentType (direct, formula, named) of given EFP.
	 */
	public final void createReq(final Feature feature, final EFP efp, final AssignmentType type) {
		req = res.createRequirement("EFP");
		reqFilter = "(parent-name=" + feature.getName()
				+ ")(parent-type=" + feature.getRepresentElement().toLowerCase()
				+ ")(efp-name=" + efp.getName()
				+ ")(assignment-type=" + type.toString().toUpperCase() + ")";
		req.setFilter("(&" + reqFilter + ")");
		// Using setFilter() method after every manipulation with reqFilter ensures more stable solution.
	}

	/**
	 * Method ensures indexing of next optional attributes of processed extra-functional property.
	 * Name and string value of attribute enter through method parameters.
	 *
	 * @param name - Name of indexed attribute.
	 * @param value - String value of indexed attribute.
	 */
	public final void addProperty(final String name, final String value) {
		if (side == AssignmentSide.PROVIDED) {
			cap.setProperty(name, value);
		} else if (side == AssignmentSide.REQUIRED) {
			reqFilter += "(" + name + "=" + value + ")";
			req.setFilter("(&" + reqFilter + ")");
			// Using setFilter() method after every manipulation with reqFilter ensures more stable solution.
		}
	}

	/**
	 * Method ensures indexing of next optional attributes of processed extra-functional property.
	 * Name and integer value of attribute enter through method parameters.
	 *
	 * @param name - Name of indexed attribute.
	 * @param value - Integer value of indexed attribute.
	 */
	public final void addProperty(final String name, final int value) {
		if (side == AssignmentSide.PROVIDED) {
			cap.setProperty(name, value);
			// Code of method is same as addProperty(String name, String value).
			// Important difference is that into cap.setProperty() method
			// enters Integer value and this has an impact on indexed metadata.
		} else if (side == AssignmentSide.REQUIRED) {
			reqFilter += "(" + name + "=" + value + ")";
			req.setFilter("(&" + reqFilter + ")");
			// Using setFilter() method after every manipulation with reqFilter ensures more stable solution.
		}
	}

	/**
	 * Method provides indexing of some more EFP metadata into Capability.
	 * Method processes only extra properties which are not indexed
	 * into OBR Requirement format in case of feature with REQUIRED side.
	 *
	 * @param efp - This EFP instance contains metadata which will be indexed.
	 */
	public final void secondaryImportanceMetadataInCapability(final EFP efp) {

		cap.setProperty("efp-id", efp.getId());

		// --- VALUE-TYPE code HEAD
		String wholeType = efp.getValueType().getName();
		// String wholeType gets for example value like this: cz.zcu.kiv.efps.types.datatypes.EfpNumberInterval
		int lastDotIndex = wholeType.lastIndexOf(".");
		String valueType;

		if (lastDotIndex == -1) {
			// In case that "." character was not found in 'wholeType' string.
			valueType = wholeType;
		} else {
			valueType = wholeType.substring(lastDotIndex + 1);
			// There is attempt to get only "EfpNumberInterval" value from example above.
			valueType = valueType.substring(IndexerDataContainer.EFP_PREFIX_LENGTH);
			// Removing "Efp" prefix from name of value type.
			// example: EfpNumberInterval -> NumberInterval
		}
		cap.setProperty("value-type", valueType);
		// --- VALUE-TYPE code TAIL

		cap.setProperty("gr-name", efp.getGr().getName());
		cap.setProperty("gr-id", efp.getGr().getId());
		cap.setProperty("type", efp.getType().name());

		if (efp.getGamma() != null) {
			cap.setProperty("gamma", efp.getGamma().toString());
		}
	}

}
