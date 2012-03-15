package cz.zcu.kiv.crce.efp.indexer.internal;

import java.util.List;

import org.osgi.service.log.LogService;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.efps.assignment.types.Feature;
import cz.zcu.kiv.efps.assignment.types.Feature.AssignmentSide;
import cz.zcu.kiv.efps.assignment.values.EfpAssignedValue;
import cz.zcu.kiv.efps.assignment.values.EfpAssignedValue.AssignmentType;
import cz.zcu.kiv.efps.assignment.values.EfpFormulaValue;
import cz.zcu.kiv.efps.assignment.values.EfpNamedValue;
import cz.zcu.kiv.efps.types.datatypes.EfpValueType;
import cz.zcu.kiv.efps.types.lr.LR;
import cz.zcu.kiv.efps.types.properties.EFP;

/**
 * OBRTranscriptFormat class provides separated methods
 * for indexing EFP properties into resource OBR metadata.
 * Therefore transcription format EFP -> OBR is separeted from other functional code
 * and person who change this transcription format doesn't need to care about
 * order/sequence of indexing methods.
 */
public class OBRTranscriptFormat {

	/**
	 * IndexerDataContainer encapsulates some instances (componentEfpAccessor, featureList, list of LR),
	 * which are needed for transcrition of EFP to OBR.
	 */
	private IndexerDataContainer container;

	/**
	 * OBRTranscriptFormat constructor.
	 *
	 * @param container - IndexerDataContainer instance for access to another data instances,
	 * which are used during processing.
	 */
	public OBRTranscriptFormat(final IndexerDataContainer container) {
		this.container = container;
	}


	/**
	 * Method that directly indexing provided EFP of feature into OBR metadata of resource.
	 *
	 * @param listEfp - List of EFP, which belongs to feature.
	 * @param feature - Feature entering for processing.
	 */
	public final void featureWithEfp(final List<EFP> listEfp, final Feature feature) {
		for (EFP efp : listEfp) {

			boolean assignmentTypeMatch = false;

			EfpAssignedValue efpAssignedValue = null;
			efpAssignedValue = container.getAccessor().getAssignedValue(feature, efp, null);

			if (efpAssignedValue != null) {
				if (efpAssignedValue.getAssignmentType() == AssignmentType.direct) {
					assignmentTypeMatch = true;
					directValue(feature, efp, efpAssignedValue);

				} else if (efpAssignedValue.getAssignmentType() == AssignmentType.formula) {
					assignmentTypeMatch = true;
					formulaValue(feature, efp, efpAssignedValue);
				}
			}

			for (LR lr : container.getArrayLR()) {

				EfpAssignedValue namedEfpValue = container.getAccessor().getAssignedValue(feature, efp, lr);

				if (namedEfpValue == null) {
					continue;
				}

				if (namedEfpValue.getAssignmentType() == AssignmentType.named) {
					assignmentTypeMatch = true;
					namedValue(feature, efp, namedEfpValue, lr);

				}
			}

			if (assignmentTypeMatch == false) {
				new OBRWriter(container.getResource(), feature, efp, AssignmentType.named);
				// When there is no previous AssignmentType match, so EFP is AssignmentType.named
				// without LR specification like in example below.
				// Example of described case:
				// Import package cz.zcu.kiv.osgi.example.inventory.inventorydata.persistenceif;efp:=462.response_time
				// By using new OBRWriter(with parameters...) there is indexed EFP into OBR
				// with all important available metadata for this case.
			}

		}
	}

	/**
	 *
	 * @param feature
	 * @param efp
	 * @param eav
	 */
	public final void directValue(final Feature feature, final EFP efp, final EfpAssignedValue eav) {
		OBRWriter writer = new OBRWriter(container.getResource(), feature, efp, AssignmentType.direct);

		EfpValueType efpValue = eav.computeValue();

		if (efpValue != null) {
			writer.addProperty("computed-value", efpValue.toString());
		}
	}

	/**
	 * 
	 * @param feature
	 * @param efp
	 * @param eav
	 */
	public final void formulaValue(final Feature feature, final EFP efp, final EfpAssignedValue eav) {
		OBRWriter writer = new OBRWriter(container.getResource(), feature, efp, AssignmentType.formula);

		String formula = ((EfpFormulaValue) eav).getFormula();

		if (formula != null) {
			writer.addProperty("formula", formula);
		}
	}

	/**
	 * 
	 * @param feature
	 * @param efp
	 * @param eav
	 * @param lr
	 */
	public final void namedValue(final Feature feature, final EFP efp, final EfpAssignedValue eav, final LR lr) {
		OBRWriter writer = new OBRWriter(container.getResource(), feature, efp, AssignmentType.named);

		String valueName = ((EfpNamedValue) eav).getValueName();

		if (valueName != null) {
			writer.addProperty("value-name", valueName);
		}

		if (feature.getSide() == AssignmentSide.PROVIDED) {
			writer.addProperty("lr-name", lr.getName());
			writer.addProperty("lr-id", lr.getId());
		}

		EfpValueType efpValueType;

		try {
			efpValueType = eav.computeValue();
			if (efpValueType == null) {
				return;
			}

		} catch (NullPointerException e) {
			String infoMessage =  "EFP " + efp.getName() + " in feature " + feature.getName()
					+ " has no computable result.";
			container.getLogger().log(LogService.LOG_INFO, infoMessage);
			return;
		}

		String value = efpValueType.getLabel();

		if (value != null) {
			writer.addProperty("computed-value", value);
		}

	}


	/**
	 * 
	 *
	 */
	class OBRWriter {

		/**	 */
		private Resource res;
		/**	 */
		private AssignmentSide side;

		/**	 */
		private Requirement req;
		/**	 */
		private Capability cap;

		/**	 */
		private String reqFilter;

		/**
		 * 
		 * @param res
		 * @param feature
		 * @param efp
		 * @param type
		 */
		public OBRWriter(final Resource res,final Feature feature, final EFP efp, final AssignmentType type) {
			this.res = res;
			this.side = feature.getSide();

			if (side == AssignmentSide.PROVIDED) {
				createCap(feature, efp, type);
			} else if (side == AssignmentSide.REQUIRED) {
				createReq(feature, efp, type);
			}
		}

		/**
		 * 
		 * @param feature
		 * @param efp
		 * @param type
		 */
		public void createCap(final Feature feature, final EFP efp, final AssignmentType type){
			cap = res.createCapability("EFP");
			cap.setProperty("parent-type", feature.getRepresentElement().toLowerCase());
			cap.setProperty("parent-name", feature.getName());
			cap.setProperty("efp-name", efp.getName());
			cap.setProperty("assignment-type", type.toString().toUpperCase());

			secondaryImportanceMetadataInCapability(cap, efp);
		}

		/**
		 * 
		 * @param feature
		 * @param efp
		 * @param type
		 */
		public void createReq(final Feature feature, final EFP efp, final AssignmentType type){
			req = res.createRequirement("EFP");
			reqFilter = "(parent-name=" + feature.getName()
					+ ")(parent-type=" + feature.getRepresentElement().toLowerCase()
					+ ")(efp-name=" + efp.getName()
					+ ")(assignment-type=" + type.toString().toUpperCase() + ")";
			req.setFilter("(&" + reqFilter + ")");
			// Using setFilter() method after every manipulation with reqFilter ensures more stable code.
		}

		/**
		 * 
		 * @param name
		 * @param value
		 */
		public void addProperty(final String name, final String value) {
			if (side == AssignmentSide.PROVIDED) {
				cap.setProperty(name, value);
			} else if (side == AssignmentSide.REQUIRED) {
				reqFilter += "(" + name + "=" + value + ")";
				req.setFilter("(&" + reqFilter + ")");
				// Using setFilter() method after every manipulation with reqFilter ensures more stable code.
			}
		}

		/**
		 * 
		 * @param name
		 * @param value
		 */
		public void addProperty(final String name, final int value) {
			if (side == AssignmentSide.PROVIDED) {
				cap.setProperty(name, value);
				// Code of method is same as addProperty(String name, String value).
				// Important difference is that into cap.setProperty() method 
				// enters Integer value and this has an impact on indexed metadata. 
			} else if (side == AssignmentSide.REQUIRED) {
				reqFilter += "(" + name + "=" + value + ")";
				req.setFilter("(&" + reqFilter + ")");
				// Using setFilter() method after every manipulation with reqFilter ensures more stable code.
			}
		}


	}

	/**
	 * Method provides indexing of some more EFP metadata into Capability.
	 * Method processes only extra properties which are not indexed
	 * into OBR Requirement format in case of EFP Feature.AssignmentSide.REQUIRED type.
	 *
	 * @param cap - New properties are added to this Capability.
	 * @param efp - This EFP instance contains metadata which will be indexed.
	 */
	public final void secondaryImportanceMetadataInCapability(final Capability cap, final EFP efp) {

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


