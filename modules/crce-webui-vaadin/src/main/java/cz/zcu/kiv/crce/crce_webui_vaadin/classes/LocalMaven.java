package cz.zcu.kiv.crce.crce_webui_vaadin.classes;

import java.io.File;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Tree;

import cz.zcu.kiv.crce.external.web.impl.SettingsUrl;

public class LocalMaven {
	private Tree localMavenTree = new Tree();
	private String path;// = System.getProperty("user.home") + File.separator + ".m2" + File.separator + "repository";
	public Tree getTree(VaadinSession session) {
		// get local Maven path
		if(session.getAttribute("settingsUrl") == null){
			SettingsUrl settings = new SettingsUrl();
			path = settings.getLocalMavenUrl();
		}
		else{
			path = ((SettingsUrl)session.getAttribute("settingsUrl")).getLocalMavenUrl();
		}
			
		File f = new File(path);
		if(f.exists()){
			list(f);
			return localMavenTree;
		}
		else{
			return null;
		}
	}

	private void list(File file) {
		File[] children = file.listFiles();
		for (File child : children) {
			if ((child.isDirectory()) && (child.getName().charAt(0) != '.')) {
				localMavenTree.addItem(child.getAbsolutePath());
				localMavenTree.setItemCaption(child.getAbsolutePath(), child.getName());
				localMavenTree.setParent(child.getAbsolutePath(), child.getParentFile().getAbsolutePath());
				list(child);
			}
			else if(child.isFile() && !child.getParentFile().getAbsolutePath().equals(path)){
				localMavenTree.addItem(child.getAbsolutePath());
				localMavenTree.setItemCaption(child.getAbsolutePath(), child.getName());
				localMavenTree.setParent(child.getAbsolutePath(), child.getParentFile().getAbsolutePath());
				localMavenTree.setChildrenAllowed(child.getAbsolutePath(), false);
				if(child.getName().endsWith(".jar") || child.getName().endsWith(".war")){
					localMavenTree.setItemIcon(child.getAbsolutePath(), FontAwesome.GIFT);
				}
				else if(child.getName().endsWith(".xml") || child.getName().endsWith(".pom")){
					localMavenTree.setItemIcon(child.getAbsolutePath(), FontAwesome.CODE);
				}
				else{
					localMavenTree.setItemIcon(child.getAbsolutePath(), FontAwesome.FILE);
				}
			}
		}
	}
}
