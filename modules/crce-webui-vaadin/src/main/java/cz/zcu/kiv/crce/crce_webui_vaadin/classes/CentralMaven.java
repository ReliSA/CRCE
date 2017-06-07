package cz.zcu.kiv.crce.crce_webui_vaadin.classes;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Tree;
import cz.zcu.kiv.crce.external.web.impl.SettingsUrl;

public class CentralMaven {
	private static String centralMavenUrl;
	private Tree resultSearchTree = new Tree("Result Search");
	private VaadinSession session;
	
	public CentralMaven(VaadinSession session){
		this.session = session;
	}
	
	public Tree getTree(String group, String artifact, String version, Object packaging) {
		// reset tree
		resultSearchTree.removeAllItems();
		directSearch(group, artifact, version, packaging.toString());
		// is tree empty?
		if(resultSearchTree.size() == 0){
			return null;
		}
		else{
			return resultSearchTree;
		}
	}
	
	private void directSearch(String group, String artifact, String version, String packaging){
		URL website;
		if(session.getAttribute("settingsUrl") == null){
			SettingsUrl settings = new SettingsUrl();
			centralMavenUrl = settings.getCentralMavenUrl();
		}
		else{
			centralMavenUrl = ((SettingsUrl)session.getAttribute("settingsUrl")).getCentralMavenUrl();
		}
		try {
			website = new URL(centralMavenUrl + group.replace('.', '/') + "/" + artifact + "/" + version
					+ "/" + artifact + "-" + version  + "." + packaging);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			if(rbc.isOpen()){
				String[] pom = group.split("\\.");
				// adding group
				resultSearchTree.addItem(pom[0]);
				for(int i=1; i< pom.length; i++){
					resultSearchTree.addItem(pom[i]);
					resultSearchTree.setParent(pom[i], pom[i-1]);
				}
				resultSearchTree.addItem(artifact);
				resultSearchTree.setParent(artifact, pom[pom.length-1]);
				resultSearchTree.addItem(version);
				resultSearchTree.setParent(version,artifact);
				//end artifact
				resultSearchTree.addItem(website.toString());
				resultSearchTree.setParent(website.toString(),version);
				resultSearchTree.setItemCaption(website.toString(), artifact + "-" + version + "." + packaging);
				resultSearchTree.setChildrenAllowed(website.toString(),false);
				if(packaging.equals("jar") || packaging.equals("war")){
            		resultSearchTree.setItemIcon(website.toString(), FontAwesome.GIFT);
            	}
            	else if(packaging.equals("xml") || packaging.equals("pom")){
            		resultSearchTree.setItemIcon(website.toString(), FontAwesome.CODE);
            	}
            	else{
            		resultSearchTree.setItemIcon(website.toString(), FontAwesome.FILE);
            	}
				
				rbc.close();
				
				// real download from url
				/*FileOutputStream fos = new FileOutputStream("information.html");
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);*/
				
			}
		} catch (IOException e) {
			resultSearchTree.clear();
		}
	}
}
