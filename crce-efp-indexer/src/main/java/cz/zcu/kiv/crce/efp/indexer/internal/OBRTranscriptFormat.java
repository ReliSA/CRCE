package cz.zcu.kiv.crce.efp.indexer.internal;

import java.util.List;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.efps.assignment.types.Feature;
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
	 * Method that directly indexing required EFP of feature into OBR metadata of resource.
	 *
	 * @param listEfp - List of EFP, which belongs to feature.
	 * @param feature - Feature entering for processing.
	 */
	public final void featureWithEfpRequired(final List<EFP> listEfp, final Feature feature) {

		for (EFP efp : listEfp) {
			Requirement req = container.getResource().createRequirement("EFP");

			String reqFilter = "(&(package=" + feature.getName() + ")(efp-name="
			+ efp.getName() + ")(assignment-type=NAMED))"; // default filter value

			EfpAssignedValue efpAssignedValue = null;
			efpAssignedValue = container.getAccessor().getAssignedValue(feature, efp, null);

			if (efpAssignedValue != null) {
				if (efpAssignedValue.getAssignmentType() == AssignmentType.direct) {
					// Testing AssignmentType.direct
					reqFilter = "(&(package=" + feature.getName()
					+ ")(efp-name=" + efp.getName() + ")(assignment-type=DIRECT))";

					EfpValueType efpValue = efpAssignedValue.computeValue();

					if (efpValue != null) {
						reqFilter = "(&(package=" + feature.getName() + ")(efp-name="
						+ efp.getName()	+ ")(assignment-type=DIRECT)(computed-value=" + efpValue + "))";
					}

				} else if (efpAssignedValue.getAssignmentType() == AssignmentType.formula) {
					// Testing AssignmentType.formula

					reqFilter = "(&(package=" + feature.getName()
					+ ")(efp-name=" + efp.getName()	+ ")(assignment-type=FORMULA))";

					String formula = ((EfpFormulaValue) efpAssignedValue).getFormula();

					if (formula != null) {
						reqFilter = "(&(package=" + feature.getName() + ")(efp-name="
						+ efp.getName() + ")(assignment-type=FORMULA)(formula=" + formula + "))";
					}
				}
			} else {
				// Testing AssignmentType.named
				for (LR lr : container.getArrayLR()) {
					
					EfpAssignedValue namedValue = container.getAccessor().getAssignedValue(feature, efp, lr);

					if (namedValue == null) {
						continue;
					}

					if (namedValue.getAssignmentType() == AssignmentType.named) {

						String valueName = ((EfpNamedValue) namedValue).getValueName();

						if (valueName != null) {
							reqFilter = "(&(package=" + feature.getName() + ")(efp-name="
							+ efp.getName()	+ ")(assignment-type=NAMED)(value-name=" + valueName + "))";
						}

						EfpValueType valueType;

						try {
							valueType = namedValue.computeValue();
							if (valueType == null) {
								break;
							}
						} catch (NullPointerException e) {
							//TODO To handle with values from several LRs on one EFP.
							break;
						}

						String value = valueType.getLabel();

						if (value != null) {
							reqFilter = "(&(package=" + feature.getName() + ")(efp-name=" + efp.getName()
							+ ")(assignment-type=NAMED)(value-name=" + valueName + ")(computed-value=" + value + "))";
						}
					}
				}
			}

			req.setFilter(reqFilter);
		}
	}

	/**
	 * Method that directly indexing provided EFP of feature into OBR metadata of resource.
	 *
	 * @param listEfp - List of EFP, which belongs to feature.
	 * @param feature - Feature entering for processing.
	 */
	public final void featureWithEfpProvided(final List<EFP> listEfp, final Feature feature) {
		for (EFP efp : listEfp) {

			Capability cap = container.getResource().createCapability("EFP");

			cap.setProperty("parent-type", feature.getRepresentElement().toLowerCase());
			cap.setProperty("parent-name", feature.getName());

			cap.setProperty("efp-name", efp.getName());
			cap.setProperty("efp-id", efp.getId());

			String wholeType = efp.getValueType().getName();
			// String wholeType gets for example value: cz.zcu.kiv.efps.types.datatypes.EfpNumberInterval
			String valueType;
			try {
				valueType = wholeType.substring(wholeType.lastIndexOf(".") + 1);
				// There is attempt to get only "EfpNumberInterval" value from example above.
			} catch (ArrayIndexOutOfBoundsException e) {
				valueType = null;
			}
			if (valueType != null) {
				cap.setProperty("value-type", valueType);
			}

			EfpAssignedValue efpAssignedValue = null;
			efpAssignedValue = container.getAccessor().getAssignedValue(feature, efp, null);

			if (efpAssignedValue != null) {
				// Testing AssignmentType.direct
				if (efpAssignedValue.getAssignmentType() == AssignmentType.direct) {
					cap.setProperty("assignment-type", "DIRECT");

					EfpValueType efpValue = efpAssignedValue.computeValue();
					if (efpValue != null) {
						cap.setProperty("computed-value", efpValue.toString());
					}

				} else if (efpAssignedValue.getAssignmentType() == AssignmentType.formula) {
					// Testing AssignmentType.formula
					cap.setProperty("assignment-type", "FORMULA");

					String formula = ((EfpFormulaValue) efpAssignedValue).getFormula();
					if (formula != null) {
						cap.setProperty("formula", formula);
					}
				}
			} else {
				// Testing AssignmentType.named
				for (LR lr : container.getArrayLR()) {

					EfpAssignedValue namedValue = container.getAccessor().getAssignedValue(feature, efp, lr);

					if (namedValue == null) {
						continue;
					}

					if (namedValue.getAssignmentType() == AssignmentType.named) {
						cap.setProperty("assignment-type", "NAMED");

						String valueName = ((EfpNamedValue) namedValue).getValueName();

						if (valueName != null) {
							cap.setProperty("value-name", valueName);
						}
						cap.setProperty("lr-name", lr.getName());
						cap.setProperty("lr-id", lr.getId());

						EfpValueType efpValueType;

						try {
							efpValueType = namedValue.computeValue();
							if (efpValueType == null) {
								break;
							}
						} catch (NullPointerException e) {
							//TODO To handle with values from several LRs on one EFP.
							break;
						}

						String value = efpValueType.getLabel();

						if (value != null) {
							cap.setProperty("computed-value", value);
						}
					}
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
