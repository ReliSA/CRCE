package cz.zcu.kiv.crce.efp.indexer.internal;

import java.util.List;

import cz.zcu.kiv.efps.assignment.types.Feature;
import cz.zcu.kiv.efps.types.lr.LR;
import cz.zcu.kiv.efps.types.properties.EFP;

public class DebugInfo {

	private Indexer indx;
	private String assignmentType;

	public DebugInfo(Indexer indx) {
		this.indx=indx;
	}

	void getFeatureList(List<Feature> featureList){
		System.out.println("List of features:");
		for(Feature ono : featureList ){
			System.out.println(ono.getSide().name());
			System.out.println(""+ ono.getRepresentElement());
			System.out.println(""+ ono.getName());
			System.out.println("-----");
		}
		System.out.println("End of features list.");
	}

	void getFeatureInfos(Feature feature,List<EFP> listEfp){
		if(listEfp.size()==0)
			debugFeatureWithoutEfp(feature);
		else
			debugFeatureWithEfp(listEfp,feature);
	}
	
	void debugFeatureWithoutEfp(Feature feature){
		System.out.println("Feature EFP list is empty.");
		System.out.println(feature.getSide().name());
		System.out.println(feature.getRepresentElement());
		System.out.println(feature.getName());
		System.out.println();
		return;
	}

	void debugFeatureWithEfp(List<EFP> listEfp,Feature feature){
		tag1(feature);
		for(EFP efp : listEfp ){
			System.out.println("<"+assignmentType+" name=efps>");
			System.out.println("	parent feature type  - "+feature.getRepresentElement());
			System.out.println("	parent feature - "+feature.getName());
			System.out.println("	name - "+efp.getName());

			for(LR val : indx.getArrayLR()){
				try{
					System.out.println("	value - "+indx.getAccessor().getAssignedValue(feature, efp, val).computeValue().toString());
					System.out.println("	valueToString - "+indx.getAccessor().getAssignedValue(feature, efp, val).toString());
				}catch (NullPointerException e){
					//System.out.println("err");
				}
			}

			System.out.println("	meta-list - "+efp.getMeta().toString());
			System.out.println("	type - "+efp.getType().name());
			System.out.println("	gr name - "+efp.getGr().getName());
			if(efp.getGamma()!=null)
				System.out.println("	gamma - "+efp.getGamma().toString());
			System.out.println("	text_summary - "+efp.toString());

			System.out.println("</"+assignmentType+">");

		}
		tag2(feature);
	}
	
	public void tag1(Feature feature){
		System.out.println("-------");
		System.out.println(feature.getSide().name());
		if(feature.getSide()==Feature.AssignmentSide.PROVIDED){
			System.out.println("<capabilities>");
			assignmentType="capability";
		}
		else if(feature.getSide()==Feature.AssignmentSide.REQUIRED){
			System.out.println("<requirements>");
			assignmentType="requirement";
		}
	}

	public void tag2(Feature feature){
		if(feature.getSide()==Feature.AssignmentSide.PROVIDED)
			System.out.println("</capabilities>");
		else if(feature.getSide()==Feature.AssignmentSide.REQUIRED)
			System.out.println("</requirements>");
		System.out.println("-------");
	}

}
