package cz.zcu.kiv.crce.efp.indexer.internal;

import cz.zcu.kiv.crce.efp.indexer.internal.Indexer;

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

	private boolean debugInfo=true;
	
	@Override
	public Resource afterUploadToBuffer(Resource resource, Buffer buffer,
			String name) throws RevokedArtifactException {
		
		/*
		System.out.println(name);
		System.out.println(buffer.toString());
		Resource [] pole=buffer.getRepository().getResources();
		for(Resource res : pole ){
			System.out.println(res.getSymbolicName());
			System.out.println(res.getId());
			System.out.println(res.getPresentationName());
			System.out.println(res.getUri());
			System.out.println("********");
		}*/
		
		if(debugInfo)
		System.out.println("afterUploadToBuffer");
		
		String path=resource.getUri().getPath();
		if(debugInfo)
		System.out.println("Resource path: "+path);

		System.out.println("+-+");
		System.out.println(resource.getSymbolicName());
		System.out.println(resource.getId());
		System.out.println(resource.getPresentationName());
		System.out.println(resource.getUri());
		System.out.println("+-+");
		
		
		Indexer indexer = new Indexer(path, true);
		
		indexer.loadEFPs();
		indexer.loadResource(resource);
		indexer.assignEFPs();
		resource = indexer.getResource();
		
		try{
		saveResourceOBR(resource);
		}
		catch(IllegalStateException e){
			System.out.println("IllegalStateException!!!");
			e.printStackTrace();
		}
		catch(NullPointerException e){
			System.out.println("NullPointerException!!!");
			e.printStackTrace();
		}
		
		indexer.setInstancesToNull();
		
		/*System.out.println("+-+");
		System.out.println(resource.getSymbolicName());
		System.out.println(resource.getId());
		System.out.println(resource.getPresentationName());
		System.out.println(resource.getUri());
		System.out.println("+-+");*/
		
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
			System.out.println("-- resource saved");
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
