package cz.zcu.kiv.crce.efp.indexer.internal;

import java.util.List;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.efps.assignment.types.Feature;
import cz.zcu.kiv.efps.types.lr.LR;
import cz.zcu.kiv.efps.types.properties.EFP;

/**
 * OBRTranscriptFormat class provides methods 
 * for indexing EFP properties into resource OBR metadata. 
 */
public class OBRTranscriptFormat {

	EFPIndexer efpIndexer;

	/**
	 * OBRTranscriptFormat constructor.
	 * 
	 * @param efpIndexer - instance of EFPIndexer for access to another data instances used during processing.
	 */
	public OBRTranscriptFormat(EFPIndexer efpIndexer) {
		this.efpIndexer=efpIndexer;
	}

	/**
	 * Method that directly indexing required EFP of feature into OBR metadata of resource.
	 * 
	 * @param listEfp - List of EFP, which belongs to feature.
	 * @param feature - Feature entering for processing.
	 */
	void featureWithEfpRequired(List<EFP> listEfp,Feature feature){

		for(EFP efp : listEfp ){
			Requirement req=efpIndexer.getResource().createRequirement("EFP");
			req.setFilter("(&(package="+feature.getName()+")(name="+efp.getName()+"))");
		}
	}

	/**
	 * Method that directly indexing provided EFP of feature into OBR metadata of resource.
	 * 
	 * @param listEfp - List of EFP, which belongs to feature.
	 * @param feature - Feature entering for processing.
	 */
	void featureWithEfpProvided(List<EFP> listEfp,Feature feature){
		for(EFP efp : listEfp ){

			Capability cap=efpIndexer.getResource().createCapability("EFP");

			cap.setProperty("parent-type", feature.getRepresentElement());
			cap.setProperty("parent-name", feature.getName());

			cap.setProperty("efp-name", efp.getName());
			cap.setProperty("efp-id", efp.getId());

			for(LR val : efpIndexer.getArrayLR()){
				try{
					cap.setProperty("value", efpIndexer.getAccessor().getAssignedValue(feature, efp, val).computeValue().getLabel());
					cap.setProperty("lr-name", val.getName());
				}catch (NullPointerException e){
					//There was no-matching computeValue() result from given getAssignedValue(feature, efp, val).
				}
			}

			cap.setProperty("meta-toString", efp.getMeta().toString());
			cap.setProperty("gr-name", efp.getGr().getName());
			cap.setProperty("gr-id", efp.getGr().getId());
			cap.setProperty("type", efp.getType().name());

			if(efp.getGamma()!=null)
				cap.setProperty("gamma", efp.getGamma().toString());
		}
	}
}
