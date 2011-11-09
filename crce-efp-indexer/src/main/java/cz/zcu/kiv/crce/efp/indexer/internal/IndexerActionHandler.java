package cz.zcu.kiv.crce.efp.indexer.internal;

import cz.zcu.kiv.crce.efp.indexer.internal.Indexer;

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
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.efps.assignment.types.Feature;


public class IndexerActionHandler extends AbstractActionHandler implements ActionHandler {

	private volatile LogService m_log;
	private volatile PluginManager m_pluginManager;     /* injected by dependency manager */

	private boolean debugInfo=true,test=true;
	private boolean jarFile;

	@Override
	public Resource afterUploadToBuffer(Resource resource, Buffer buffer,
			String name) throws RevokedArtifactException {

		resource=handleNewResource(resource,name);
		
		try{
			if(test)
				wrongIndexerResultTest(buffer);
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return resource;

	}

	// Metoda testuje, zda pro ruzne resources v bufferu se vrati stejne, 
	// nebo ruzne features v metode loadEFPs().
	void wrongIndexerResultTest(Buffer buffer){
		Resource[] resArray=buffer.getRepository().getResources();
		for(Resource testingRes : resArray ){
			System.out.println("\n========== Resource "+testingRes.getSymbolicName()+" test. ==========");
			Indexer indexer = new Indexer(testingRes.getUri().getPath(), true);

			if(!indexer.loadEFPs()){
				System.out.println("-- Resource is not OSGi bundle. --");
				continue;
			}
			sysoInfos(testingRes,null);
			System.out.println("-----------------");
		}
	}

	public Resource handleNewResource(Resource resource, String name){
		if(debugInfo)
			System.out.println("afterUploadToBuffer");

		jarFile=false;
		if(name.endsWith(".jar")){
			jarFile=true;
			System.out.println("-- Resource is jar file. --");
		}

		if(!jarFile)
			return resource;

		String path=resource.getUri().getPath(); // cesta k artefaktu
		if(debugInfo)
			System.out.println("Resource path: "+path);

		Indexer indexer = new Indexer(path, true);

		if(!indexer.loadEFPs()){
			System.out.println("-- Resource is not OSGi bundle. --");
			return resource;
		}
		indexer.loadResource(resource);
		indexer.assignEFPsOBR();
		resource = indexer.getResource();

		try{
			saveResourceOBR(resource);
		}
		catch(IllegalStateException e){
			e.printStackTrace();
			System.out.println("IllegalStateException!!!");
		}
		catch(NullPointerException e){
			e.printStackTrace();
			System.out.println("NullPointerException!!!");
		}

		sysoInfos(resource,name);

		return resource;
	}

	public void saveResourceOBR(Resource resource){
		ResourceDAO rd =m_pluginManager.getPlugin(ResourceDAO.class);
		try {
			rd.save(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(debugInfo)
			System.out.println("-- resource saved --");
	}

	void sysoInfos(Resource resource,String name){
		System.out.println("+-+");
		System.out.println(resource.getSymbolicName());
		System.out.println(resource.getId());
		System.out.println(resource.getPresentationName());
		System.out.println(resource.getUri());
		System.out.println(name);
		System.out.println("+-+");
	}


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




}
