package cz.zcu.kiv.crce.efp.indexer.internal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.osgi.service.log.LogService;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.efps.assignment.api.ComponentEfpAccessor;
import cz.zcu.kiv.efps.assignment.api.EfpAwareComponentLoader;
import cz.zcu.kiv.efps.assignment.client.EfpAssignmentClient;
import cz.zcu.kiv.efps.assignment.osgi.OSGiAssignmentRTException;
import cz.zcu.kiv.efps.assignment.types.Feature;
import cz.zcu.kiv.efps.types.lr.LR;
import cz.zcu.kiv.efps.types.properties.EFP;

/**
 * EFPIndexer class ensures loading all features of resource 
 * and initial steps of indexing EFP properties into resource OBR metadata. 
 */
public class EFPIndexer {

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
	
	/** LogService injected by dependency manager into IndexerActionHandler. */
	private LogService m_log;
	
	/**
	 * Indexer constructor.
	 * 
	 * @param sourceFilePath - Path of resource file entering indexing process.
	 * @param debugInfo - This boolean attribute controls debugging messages.
	 */
	public EFPIndexer(String sourceFilePath,LogService m_log) {
		this.sourceFilePath=sourceFilePath;
		this.m_log=m_log;
	}

	/**
	 * Method for loading all features into list and loading list of LRs into array.
	 * 
	 * @return boolean - Returns true in case that feature load process succeeded 
	 * or false in case that process failed.
	 */
	public boolean loadFeatures(){
		m_log.log(LogService.LOG_INFO,"Initialising EfpAwareComponentLoader ...");
		loader = EfpAssignmentClient.initialiseComponentLoader("cz.zcu.kiv.efps.assignment.osgi.OSGiAssignmentImpl");
		m_log.log(LogService.LOG_INFO,"EfpAwareComponentLoader ok.");

		m_log.log(LogService.LOG_INFO,"Initialising ComponentEfpAccessor ...");
		accessor=loader.loadForRead(sourceFilePath);
		m_log.log(LogService.LOG_INFO,"ComponentEfpAccessor ok.");

		try{
			featureList=accessor.getAllFeatures();
			m_log.log(LogService.LOG_INFO,"Feature list loaded.");
		}
		catch(OSGiAssignmentRTException e){
			m_log.log(LogService.LOG_WARNING,"OSGiAssignmentRTException in accessor.getAllFeatures()!");
			return false;
		}

		Set<LR> LRset=accessor.getLRs();
		arrayLR=LRset.toArray(new LR[0]);

		return true;
	}

	/**
	 * Method extracts list of EFP from each feature. Next there is called individual feature OBR processing.
	 */
	public void InitAssignmentEFPtoOBR() {

		OBRTranscriptFormat obrTF=new OBRTranscriptFormat(this);
		
		for(Feature feature : featureList ){

			List<EFP> listEfp=accessor.getEfps(feature);

			if(listEfp.size()!=0){
				if(feature.getSide()==Feature.AssignmentSide.PROVIDED){
					obrTF.featureWithEfpProvided(listEfp,feature);
				}
				else if(feature.getSide()==Feature.AssignmentSide.REQUIRED){
					obrTF.featureWithEfpRequired(listEfp,feature);
				}
			}
		}
	}

	
	//------------------------------------------
	// Getters and Setters

	public void setResource(Resource resource){
		this.resource = resource;
	}

	public Resource getResource(){
		return resource;
	}

	public ComponentEfpAccessor getAccessor() {
		return accessor;
	}

	/*public void setAccessor(ComponentEfpAccessor accessor) {
		this.accessor = accessor;
	}*/

	public LR[] getArrayLR() {
		return arrayLR;
	}

	/*public void setArrayLR(LR[] arrayLR) {
		this.arrayLR = arrayLR;
	}*/

}
