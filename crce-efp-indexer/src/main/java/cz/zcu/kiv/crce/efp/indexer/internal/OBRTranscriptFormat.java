package cz.zcu.kiv.crce.efp.indexer.internal;

import java.util.List;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.efps.assignment.types.Feature;
import cz.zcu.kiv.efps.assignment.values.EfpAssignedValue.AssignmentType;
import cz.zcu.kiv.efps.assignment.values.EfpFormulaValue;
import cz.zcu.kiv.efps.assignment.values.EfpNamedValue;
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
	 * Method that directly indexing required EFP of feature into OBR metadata of resource.
	 *
	 * @param listEfp - List of EFP, which belongs to feature.
	 * @param feature - Feature entering for processing.
	 */
	final void featureWithEfpRequired(final List<EFP> listEfp, final Feature feature){

		for (EFP efp : listEfp) {
			Requirement req = container.getResource().createRequirement("EFP");
			req.setFilter("(&(package=" + feature.getName() + ")(efp-name="
					+ efp.getName() + ")(type=NAMED))");

      // Testing AssignmentType.direct
			try {
				if (container.getAccessor()
						.getAssignedValue(feature, efp, null)
						.getAssignmentType() == AssignmentType.direct) {
					req.setFilter("(&(package=" + feature.getName()
							+ ")(efp-name=" + efp.getName() + ")(type=DIRECT))");
					String value = container.getAccessor()
							.getAssignedValue(feature, efp, null)
							.computeValue().toString();
					req.setFilter("(&(package=" + feature.getName()
							+ ")(efp-name=" + efp.getName()
							+ ")(type=DIRECT)(value=" + value + "))");
				}
			} catch (Exception e) {
				// NO direct AssignmentType
			}

			// Testing AssignmentType.formula
			try {
				if (container.getAccessor()
						.getAssignedValue(feature, efp, null)
						.getAssignmentType() == AssignmentType.formula) {
					req.setFilter("(&(package=" + feature.getName()
							+ ")(efp-name=" + efp.getName()
							+ ")(type=FORMULA))");
					String formula = ((EfpFormulaValue) container.getAccessor()
							.getAssignedValue(feature, efp, null)).getFormula();
					req.setFilter("(&(package=" + feature.getName()
							+ ")(efp-name=" + efp.getName()
							+ ")(type=FORMULA)(formula=" + formula + "))");
				}
			} catch (Exception e) {
				// NO formula AssignmentType
			}

			// Testing AssignmentType.named
			for (LR lr : container.getArrayLR()) {
				try {
					if (container.getAccessor()
							.getAssignedValue(feature, efp, lr)
							.getAssignmentType() == AssignmentType.named) {
						String valueName = ((EfpNamedValue) container
								.getAccessor().getAssignedValue(feature, efp,
										lr)).getValueName();
						req.setFilter("(&(package=" + feature.getName()
								+ ")(efp-name=" + efp.getName()
								+ ")(type=NAMED)(value-name=" + valueName
								+ "))");
						String value = container.getAccessor()
								.getAssignedValue(feature, efp, lr)
								.computeValue().getLabel();
						req.setFilter("(&(package=" + feature.getName()
								+ ")(efp-name=" + efp.getName()
								+ ")(type=NAMED)(value-name=" + valueName
								+ ")(value=" + value + "))");
					}
				} catch (NullPointerException e) {
					// There was no-matching computeValue() result from given getAssignedValue(feature, efp, val).
				}
			}
		}
	}

	/**
	 * Method that directly indexing provided EFP of feature into OBR metadata of resource.
	 *
	 * @param listEfp - List of EFP, which belongs to feature.
	 * @param feature - Feature entering for processing.
	 */
	final void featureWithEfpProvided(final List<EFP> listEfp, final Feature feature) {
		for (EFP efp : listEfp) {

			Capability cap = container.getResource().createCapability("EFP");

			cap.setProperty("parent-type", feature.getRepresentElement().toLowerCase());
			cap.setProperty("parent-name", feature.getName());

			cap.setProperty("efp-name", efp.getName());
			cap.setProperty("efp-id", efp.getId());

			try {
				String wholeType = efp.getValueType().getName();
				String valueType = wholeType.substring(wholeType.lastIndexOf(".") + 1);
				cap.setProperty("value-type", valueType);
			} catch (Exception e) {
				// Error during efp-value-type string processing.
			}

			// Testing AssignmentType.direct
			try {
				if (container.getAccessor()
						.getAssignedValue(feature, efp, null)
						.getAssignmentType() == AssignmentType.direct) {
					cap.setProperty("assignment-type", "DIRECT");
					cap.setProperty("computed-value", container.getAccessor()
							.getAssignedValue(feature, efp, null)
							.computeValue().toString());
				}
			} catch (Exception e) {
				// NO direct AssignmentType
			}

			// Testing AssignmentType.formula
			try {
				if (container.getAccessor()
						.getAssignedValue(feature, efp, null)
						.getAssignmentType() == AssignmentType.formula) {
					cap.setProperty("assignment-type", "FORMULA");
					cap.setProperty("formula",
							((EfpFormulaValue) container.getAccessor()
									.getAssignedValue(feature, efp, null))
									.getFormula());
				}
			} catch (Exception e) {
			// NO formula AssignmentType
			}

			// Testing AssignmentType.named
			for (LR lr : container.getArrayLR()) {
				try {
					if (container.getAccessor()
							.getAssignedValue(feature, efp, lr)
							.getAssignmentType() == AssignmentType.named) {
						cap.setProperty("assignment-type", "NAMED");
						cap.setProperty("value-name",
								((EfpNamedValue) container.getAccessor()
										.getAssignedValue(feature, efp, lr))
										.getValueName());
						cap.setProperty("lr-name", lr.getName());
					}
					cap.setProperty("computed-value", container.getAccessor().getAssignedValue(feature, efp, lr).computeValue().getLabel());
				} catch (NullPointerException e) {
					//There was no-matching computeValue() result from given getAssignedValue(feature, efp, lr).
				}
			}

			cap.setProperty("gr-name", efp.getGr().getName());
			cap.setProperty("gr-id", efp.getGr().getId());
			cap.setProperty("type", efp.getType().name());

			if (efp.getGamma() != null) {
				cap.setProperty("gamma", efp.getGamma().toString());
			}
		}
	}
}
