package cz.zcu.kiv.crce.efp.indexer.internal;
import java.util.List;
import java.util.Set;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.efps.assignment.api.ComponentEfpAccessor;
import cz.zcu.kiv.efps.assignment.api.EfpAwareComponentLoader;
import cz.zcu.kiv.efps.assignment.client.EfpAssignmentClient;
import cz.zcu.kiv.efps.assignment.core.AssignmentRTException;
import cz.zcu.kiv.efps.assignment.cosi.CosiAssignmentImpl;
import cz.zcu.kiv.efps.assignment.extension.CommonBundleFilesManagerImpl;
import cz.zcu.kiv.efps.assignment.extension.api.EfpDataLocation;
import cz.zcu.kiv.efps.assignment.osgi.OSGiAssignmentImpl;
import cz.zcu.kiv.efps.assignment.osgi.OSGiAssignmentRTException;
import cz.zcu.kiv.efps.assignment.repomirror.api.MirroredDataManipulator;
import cz.zcu.kiv.efps.assignment.repomirror.impl.XMLDataManipulator;
import cz.zcu.kiv.efps.assignment.types.Feature;
import cz.zcu.kiv.efps.assignment.types.Feature.AssignmentSide;
import cz.zcu.kiv.efps.types.lr.LR;
import cz.zcu.kiv.efps.types.lr.LrAssignment;
import cz.zcu.kiv.efps.types.properties.EFP;
import cz.zcu.kiv.efps.assignment.repository.generated.ObjectFactory;


public class Indexer {

	private String sourceFileName;
	private boolean debugInfo;

	private EfpAwareComponentLoader loader;
	private ComponentEfpAccessor accessor;

	private List<Feature> featureList;
	private LR []arrayLR;

	private Resource resource;
	private DebugInfo dbg;

	public Indexer(String sourceFileName,boolean debugInfo) {
		this.sourceFileName=sourceFileName;
		this.debugInfo=debugInfo;
		this.dbg=new DebugInfo(this);
	}

	public boolean loadEFPs(){

		if(debugInfo)
			System.out.println("Initialising EfpAwareComponentLoader ...");
		loader = EfpAssignmentClient.initialiseComponentLoader("cz.zcu.kiv.efps.assignment.osgi.OSGiAssignmentImpl");
		if(debugInfo)
			System.out.println("EfpAwareComponentLoader ok.");

		if(debugInfo)
			System.out.println("Initialising ComponentEfpAccessor ...");
		accessor=loader.loadForRead(sourceFileName);
		if(debugInfo)
			System.out.println("ComponentEfpAccessor ok.");

		try{
		featureList=accessor.getAllFeatures();
		}
		catch(OSGiAssignmentRTException e){
			System.out.println("OSGiAssignmentRTException in accessor.getAllFeatures()!");
			return false;
		}
		
		
		if(debugInfo)
			System.out.println("Feature list loaded.");

		if(debugInfo){
			dbg.getFeatureList(featureList);
		}

		Set<LR> LRset=accessor.getLRs();
		arrayLR=LRset.toArray(new LR[0]);
		
		return true;
	}	

	public void assignEFPsOBR() {

		for(Feature feature : featureList ){

			//vsechna efp v repository.xml
			List<EFP> listEfp=accessor.getEfps(feature);
			
			if(debugInfo)
				dbg.getFeatureInfos(feature,listEfp);

			if(listEfp.size()==0)
				featureWithoutEfp(feature);
			else
				featureWithEfp(listEfp,feature);
		}
	}
	
	//------------------------------------------

	void featureWithoutEfp(Feature feature){
		if(feature.getSide()==Feature.AssignmentSide.PROVIDED){
			Capability capProvided=resource.createCapability("PROVIDED");
			capProvided.setProperty("feature type", feature.getRepresentElement());
			capProvided.setProperty("feature", feature.getName());
		}

		if(feature.getSide()==Feature.AssignmentSide.REQUIRED){
			Requirement req=resource.createRequirement("REQUIRED");
			req.setComment("feature type - "+ feature.getRepresentElement());
			req.setComment("feature - "+ feature.getName());
		}
	}

	void featureWithEfp(List<EFP> listEfp,Feature feature){
		for(EFP efp : listEfp ){
			
			//if(ono.getSide()==Feature.AssignmentSide.PROVIDED); !!!!!
			Capability cap=resource.createCapability("EFP");

			cap.setProperty("parent feature type", feature.getRepresentElement());
			cap.setProperty("parent feature", feature.getName());

			cap.setProperty("name", efp.getName());

			for(LR val : arrayLR ){
				try{
					cap.setProperty("value", accessor.getAssignedValue(feature, efp, val).computeValue().toString());
					cap.setProperty("valueToString", accessor.getAssignedValue(feature, efp, val).toString());
				}catch (NullPointerException e){
					//System.out.println("err");
				}
			}

			cap.setProperty("meta-list", efp.getMeta().toString());
			cap.setProperty("type", efp.getType().name());
			cap.setProperty("gr name", efp.getGr().getName());

			if(efp.getGamma()!=null)
				cap.setProperty("gamma", efp.getGamma().toString());
			
			cap.setProperty("efp-summary", efp.toString());
		}
	}

	//------------------------------------------
	
	public void loadResource(Resource resource){
		this.resource = resource;
	}

	public Resource getResource(){
		return resource;
	}
	
	public ComponentEfpAccessor getAccessor() {
		return accessor;
	}

	public void setAccessor(ComponentEfpAccessor accessor) {
		this.accessor = accessor;
	}

	public LR[] getArrayLR() {
		return arrayLR;
	}

	public void setArrayLR(LR[] arrayLR) {
		this.arrayLR = arrayLR;
	}

}
