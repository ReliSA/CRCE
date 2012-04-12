package cz.zcu.kiv.crce.efp.indexer.internal;

import java.util.List;

import org.osgi.service.log.LogService;

import cz.zcu.kiv.efps.assignment.types.Feature;
import cz.zcu.kiv.efps.assignment.types.Feature.AssignmentSide;
import cz.zcu.kiv.efps.assignment.values.EfpAssignedValue;
import cz.zcu.kiv.efps.assignment.values.EfpAssignedValue.AssignmentType;
import cz.zcu.kiv.efps.assignment.values.EfpFormulaValue;
import cz.zcu.kiv.efps.assignment.values.EfpNamedValue;
import cz.zcu.kiv.efps.types.datatypes.EfpValueType;
import cz.zcu.kiv.efps.types.evaluator.LrConstraintEvaluator;
import cz.zcu.kiv.efps.types.evaluator.LrDerivedValueEvaluator;
import cz.zcu.kiv.efps.types.lr.LR;
import cz.zcu.kiv.efps.types.lr.LrAssignment;
import cz.zcu.kiv.efps.types.lr.LrAssignment.LrAssignmentType;
import cz.zcu.kiv.efps.types.lr.LrDerivedAssignment;
import cz.zcu.kiv.efps.types.properties.EFP;

/**
 * EFPAssignmentTypeResolver class provides method for resolving
 * AssignmentType attribute of each EFP. Class also contains methods
 * for handling with given extra-functional properties with regard to resolved AssignmentType.
 */
public class EFPAssignmentTypeResolver {

	/**
	 * IndexerDataContainer encapsulates some instances (componentEfpAccessor, featureList, list of LR),
	 * which are needed for transcription of EFP to OBR.
	 */
	private IndexerDataContainer container;

	/**
	 * EFPAssignmentTypeResolver constructor.
	 *
	 * @param container2 - IndexerDataContainer instance for access to another data instances,
	 * which are used during processing.
	 */
	public EFPAssignmentTypeResolver(final IndexerDataContainer container2) {
		this.container = container2;
	}


	/**
	 * Method processes all EFP of given feature. Every EFP is resolved by AssignmentType attribute
	 * and next is called appropriate handling method for this AssignmentType.
	 *
	 * @param listEfp - List of EFP, which belongs to feature.
	 * @param feature - Feature which enters in processing. Feature contains some metadata for indexing.
	 */
	public final void assignmentTypeResolving(final List<EFP> listEfp, final Feature feature) {
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

			if (!assignmentTypeMatch) {
				new EFPToOBRTranscription(container.getResource(), feature, efp, AssignmentType.named);
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
	 * Method ensures handling with EFP AssignmentType.direct.
	 *
	 * @param feature - Feature contains some metadata for indexing. EFP belongs to this feature.
	 * @param efp - This is actual processed EFP, which will be indexed.
	 * @param eav - Instance of EfpAssignedValue interface provides access to assigned value.
	 */
	public final void directValue(final Feature feature, final EFP efp, final EfpAssignedValue eav) {
		EFPToOBRTranscription writer =
				new EFPToOBRTranscription(container.getResource(), feature, efp, AssignmentType.direct);

		EfpValueType efpValue = eav.computeValue();

		if (efpValue != null) {
			writer.addProperty("value", efpValue.toString());
		}
	}

	/**
	 * Method ensures handling with EFP AssignmentType.formula.
	 *
	 * @param feature - Feature contains some metadata for indexing. EFP belongs to this feature.
	 * @param efp - This is actual processed EFP, which will be indexed.
	 * @param eav - Instance of EfpAssignedValue interface provides access to assigned value.
	 */
	public final void formulaValue(final Feature feature, final EFP efp, final EfpAssignedValue eav) {
		EFPToOBRTranscription writer =
				new EFPToOBRTranscription(container.getResource(), feature, efp, AssignmentType.formula);

		String formula = ((EfpFormulaValue) eav).getFormula();

		if (formula != null) {
			writer.addProperty("formula", formula);
		}
	}

	/**
	 * Method ensures handling with EFP AssignmentType.named.
	 *
	 * @param feature - Feature contains some metadata for indexing. EFP belongs to this feature.
	 * @param efp - This is actual processed EFP, which will be indexed.
	 * @param eav - Instance of EfpAssignedValue interface provides access to assigned value.
	 * @param lr - Given EFP was assigned to feature with this local register.
	 */
	public final void namedValue(final Feature feature, final EFP efp, final EfpAssignedValue eav, final LR lr) {
		EFPToOBRTranscription writer =
				new EFPToOBRTranscription(container.getResource(), feature, efp, AssignmentType.named);

		String valueName = ((EfpNamedValue) eav).getValueName();

		if (valueName != null) {
			writer.addProperty("value-name", valueName);
		}

		if (feature.getSide() == AssignmentSide.PROVIDED) {
			writer.addProperty("lr-name", lr.getName());
			writer.addProperty("lr-id", lr.getId());
		}

		LrAssignmentType lat = ((EfpNamedValue) eav).getLrAssignment().getAssignmentType();
		
		if (lat == LrAssignmentType.DERIVED){
			LrAssignment lrAssignment = ((EfpNamedValue) eav).getLrAssignment();
			LrDerivedValueEvaluator evaluator = ((LrDerivedAssignment) lrAssignment).getEvaluator();
			String logicalRule = ((LrConstraintEvaluator) evaluator).getLogicalRule();

			writer.addProperty("deriving-formula", logicalRule);
		}

		if (efp.getType() == EFP.Type.DERIVED) {
			return;
		}

		EfpValueType efpValueType = eav.computeValue();

		if (efpValueType == null) {
			return;
		}

		String value = efpValueType.getLabel();

		if (value != null) {
			writer.addProperty("value", value);
		}
	}

}


