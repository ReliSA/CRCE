package cz.zcu.kiv.crce.crce_webui_vaadin.resources;

import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import cz.zcu.kiv.crce.crce_webui_vaadin.classes.LocalMaven;

@SuppressWarnings("serial")
public class LocalMavenForm extends FormLayout{
	private LocalMaven localMaven = new LocalMaven();
	private Label caption = new Label("Local Maven repository");
	
	public LocalMavenForm() {
		HorizontalLayout content = new HorizontalLayout();
		addComponent(content);
	}
	
	public LocalMavenForm(VaadinSession session){
		VerticalLayout content = new VerticalLayout();
		// Tree of Maven local repository
		Tree localMavenTree = localMaven.getTree(session);
		content.setMargin(new MarginInfo(false, true));
		if(localMavenTree == null){
			content.addComponent(new Label("Local Maven repository not found"));
		}
		else{
			content.addComponents(caption, localMavenTree);
			content.setSpacing(true);
		}
		addComponent(content);
	}
}
