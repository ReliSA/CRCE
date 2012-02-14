package cz.zcu.kiv.crce.efp.indexer.internal;

import cz.zcu.kiv.crce.efp.indexer.internal.EFPIndexer;

import java.io.File;
import java.io.IOException;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;

import org.osgi.service.log.LogService;

import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;

import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;

/**
 * IndexerActionHandler class ensures general tasks about efp-indexing process. 
 * Verification OSGi bundle, initialization indexing process 
 * and saving modified OBR metadata of resource.   
 */
public class IndexerActionHandler extends AbstractActionHandler implements ActionHandler {

	/** LogService injected by dependency manager. */
	private volatile LogService m_log;
	
	/** PluginManager injected by dependency manager. */
	private volatile PluginManager m_pluginManager;
	
	@Override
	public Resource afterUploadToBuffer(Resource resource, Buffer buffer,
			String name) throws RevokedArtifactException {

		m_log.log(LogService.LOG_INFO,"afterUploadToBuffer");
		
		// Indexing process starts in afterUploadToBuffer trigger.
		try{
		resource=handleNewResource(resource,name);
		}catch(Exception e){
			e.printStackTrace();
			m_log.log(LogService.LOG_WARNING,"Unexpected error during handling with resource!");
			m_log.log(LogService.LOG_WARNING,"Maybe there was a resource with old EFP format verison!");
		}
		
		return resource;
	}

	/**
	 * In case that input resource file is JAR file and OSGi bundle, 
	 * there is started indexing process.
	 * 
	 * @param resource - Resource uploaded to buffer, which enters into indexing process.
	 * @param name - Name of resource file.
	 * @return resource - Modified resource with indexed EFP data in OBR format 
	 * or original resource in case of indexing fault.
	 */
	public Resource handleNewResource(Resource resource, String name){

		boolean jarFile=false;
		if(name.endsWith(".jar")){	// Test of input file, whether it is JAF file.
			jarFile=true;
			m_log.log(LogService.LOG_INFO, "-- Resource is jar file. --");
		}

		if(!jarFile)				// To indexing process continues only JAR files.
			return resource;

		String path=resource.getUri().getPath(); // Path of resource artifact.
		
		m_log.log(LogService.LOG_INFO,"Resource path: "+path);

		EFPIndexer indexer = new EFPIndexer(path, m_log);		// Main indexing class.

		if(!indexer.loadFeatures()){
			m_log.log(LogService.LOG_WARNING,"-- Resource is not OSGi bundle. --");
			// In case that resource is not OSGi bundle, indexing process fails.
			return resource;
		}
		
		indexer.setResource(resource);		// Setting of resource into indexer instance.
		indexer.InitAssignmentEFPtoOBR();	// Method initializes indexing process.
		resource = indexer.getResource();	// Getting modified resource from indexer instance.

		try{
			saveResourceOBR(resource);		// Saving modified OBR metadata.
		}
		catch(IllegalStateException e){		// Info about error during saving process.
			e.printStackTrace();
			m_log.log(LogService.LOG_ERROR,"Error during saving process!\nIllegalStateException!!!");
		}
		catch(NullPointerException e){		// Info about error during saving process.
			e.printStackTrace();
			m_log.log(LogService.LOG_ERROR,"Error during saving process!\nNullPointerException!!!");
		}

		return resource;
	}

	/**
	 * Method saves indexed EFP data. Without saving would be modified OBR metadata lost.
	 * 
	 * @param resource - Modified instance with indexed EFP data.
	 */
	public void saveResourceOBR(Resource resource){
		ResourceDAO rd =m_pluginManager.getPlugin(ResourceDAO.class);
		try {
			rd.save(resource);		 
		} catch (IOException e) {
			e.printStackTrace();
		}

		m_log.log(LogService.LOG_INFO,"-- Resource was saved. --");
	}

/*
	@Override
	public Resource beforePutToStore(Resource resource, Store store) throws RevokedArtifactException {

		if(debugInfo)
			System.out.println("beforePutToStore");

		return resource;
	}

	@Override
	public Resource onUploadToBuffer(Resource resource, Buffer buffer, String name) {

		if(debugInfo)
			System.out.println("onUploadToBuffer");

		return resource;
	}
*/	
}