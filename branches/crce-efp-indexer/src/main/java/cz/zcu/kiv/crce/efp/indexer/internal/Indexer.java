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
	private MirroredDataManipulator mirroredDataManipulator;

	private List<Feature> featureList;
	private LR []arrayLR;

	private Resource resource;

	public Indexer(String sourceFileName,boolean debugInfo) {
		this.sourceFileName=sourceFileName;
		this.debugInfo=debugInfo;
	}

	public void setInstancesToNull(){
		loader=null;
		accessor=null;
		featureList=null;
	}
	
	public void loadEFPs(){

		//loader = new OSGiAssignmentImpl();
		loader = EfpAssignmentClient.initialiseComponentLoader("cz.zcu.kiv.efps.assignment.osgi.OSGiAssignmentImpl");
		
		if(debugInfo)
			System.out.println("EfpAwareComponentLoader ok");
		accessor=loader.loadForRead(sourceFileName);
		if(debugInfo)
			System.out.println("ComponentEfpAccessor ok");

		featureList=accessor.getAllFeatures();
		
		for(Feature ono : featureList ){
			System.out.println(ono.getSide().name());
			System.out.println(""+ ono.getRepresentElement());
			System.out.println(""+ ono.getName());
			System.out.println("......");
		}
		
		Set<LR> LRset=accessor.getLRs();
		arrayLR=LRset.toArray(new LR[0]);

		EfpDataLocation bundleManager = new CommonBundleFilesManagerImpl(sourceFileName);
		if(debugInfo)
			System.out.println(bundleManager.locateEfpsDataMirror());
		if(debugInfo)
			System.out.println(bundleManager.locateEfpsDataStore());

		mirroredDataManipulator = new XMLDataManipulator(bundleManager);
	}	

	public void loadResource(Resource resource){
		this.resource = resource;
	}

	public Resource getResource(){
		return resource;
	}

	public void assignEFPs() {

		for(Feature ono : featureList ){
			System.out.println(ono.getSide().name());
			System.out.println(""+ ono.getRepresentElement());
			System.out.println(""+ ono.getName());
			System.out.println("......");
		}
		
		for(Feature ono : featureList ){

			if(debugInfo){
				System.out.println(ono.getSide().name());
				System.out.println(""+ ono.getRepresentElement());
				System.out.println(""+ ono.getName());
			}
			debugTrash1(ono);

			//obecne všechna efp v repository.xml
			List<EFP> listEfp=accessor.getEfps(ono);

			if(listEfp.size()==0){
				if(ono.getSide()==Feature.AssignmentSide.PROVIDED){
					Capability capProvided=resource.createCapability("PROVIDED");
					capProvided.setProperty("feature type", ono.getRepresentElement());
					capProvided.setProperty("feature", ono.getName());
				}

				if(ono.getSide()==Feature.AssignmentSide.REQUIRED){
					Requirement req=resource.createRequirement("REQUIRED");
					req.setComment("feature type - "+ ono.getRepresentElement());
					req.setComment("feature - "+ ono.getName());
				}
			}
			else
				for(EFP efp : listEfp ){
					if(debugInfo)
						System.out.println("<"+type+" name=efps>");

					//if(ono.getSide()==Feature.AssignmentSide.PROVIDED);
					Capability cap=resource.createCapability("EFP");

					cap.setProperty("parent feature type", ono.getRepresentElement());
					cap.setProperty("parent feature", ono.getName());
					if(debugInfo){
						System.out.println("	parent feature type  - "+ono.getRepresentElement());
						System.out.println("	parent feature - "+ono.getName());
					}

					cap.setProperty("name", efp.getName());
					if(debugInfo)
						System.out.println("	name - "+efp.getName());

					for(LR val : arrayLR ){
						try{
							System.out.println("	value - "+accessor.getAssignedValue(ono, efp, val).toString());
							cap.setProperty("value", accessor.getAssignedValue(ono, efp, val).toString());

							/* 
							 * //System.out.println(mirroredDataManipulator.readLrAssignment(efp, val.getId(), val.getName()).toString());
							 * //System.out.println(mirroredDataManipulator.readEFP(efp.getName(), efp.getGr().getId()));
							 * 
							 * ZATIM KONCI CHYBOU 
							 * java.lang.IllegalArgumentException: No SchemaFactory that implements the schema language specified by: http://www.w3.org/2001/XMLSchema could be loaded
							 * at javax.xml.validation.SchemaFactory.newInstance(SchemaFactory.java:204)
							 * at cz.zcu.kiv.efps.assignment.repomirror.impl.XMLDataManipulator.loadRepository(XMLDataManipulator.java:81)
							 */

							
							/*
							List<String> metaNames=efp.getMeta().getNames();
							for(String name : metaNames ){
								try{
									//System.out.println("ziskano: "+mirroredDataManipulator.readLrAssignment(efp, val.getId(), name).toString());	
									LrAssignment lrAssignment=  mirroredDataManipulator.readLrAssignment(efp, val.getId(), name);

									cap.setProperty("value name", lrAssignment.getValueName());
									cap.setProperty("value label", mirroredDataManipulator.readLrAssignment(efp, val.getId(), name).getLabel());
									cap.setProperty("value from local registry", val.getName());
									if(debugInfo){
										System.out.println("	value name - "+lrAssignment.getValueName());
										System.out.println("	value label - "+mirroredDataManipulator.readLrAssignment(efp, val.getId(), name).getLabel());
										System.out.println("	value from local registry - "+val.getName());
									}

								}catch (AssignmentRTException e){
									//System.out.println("err AssignmentRTException");
								}
							}*/


						}catch (NullPointerException e){
							//System.out.println("err");
						}
					}

					cap.setProperty("meta-list", efp.getMeta().toString());
					if(debugInfo)
						System.out.println("	meta-list - "+efp.getMeta().toString());

					cap.setProperty("type", efp.getType().name());
					if(debugInfo)
						System.out.println("	type - "+efp.getType().name());

					cap.setProperty("gr name", efp.getGr().getName());
					if(debugInfo)
						System.out.println("	gr name - "+efp.getGr().getName());

					if(efp.getGamma()!=null){
						cap.setProperty("gamma", efp.getGamma().toString());
						if(debugInfo)
							System.out.println("	gamma - "+efp.getGamma().toString());
					}

					cap.setProperty("text_summary", efp.toString());
					if(debugInfo)
						System.out.println("	text_summary - "+efp.toString());

					if(debugInfo)
						System.out.println("</"+type+">");
				}

			debugTrash2(ono);
		}
	}



	//------------------------------------------

	// informativni vypisy pro zacatek
	private String type;
	public void debugTrash1(Feature ono){
		System.out.println("-------");
		System.out.println(ono.getSide().name());
		if(ono.getSide()==Feature.AssignmentSide.PROVIDED){
			System.out.println("<capabilities>");
			type="capability";
		}
		else if(ono.getSide()==Feature.AssignmentSide.REQUIRED){
			System.out.println("<requirements>");
			type="requirement";
		}
	}

	public void debugTrash2(Feature ono){
		if(ono.getSide()==Feature.AssignmentSide.PROVIDED)
			System.out.println("</capabilities>");
		else if(ono.getSide()==Feature.AssignmentSide.REQUIRED)
			System.out.println("</requirements>");
		System.out.println("-------");
	}

}
