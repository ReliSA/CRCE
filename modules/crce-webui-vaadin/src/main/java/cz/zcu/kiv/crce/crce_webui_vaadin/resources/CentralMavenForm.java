package cz.zcu.kiv.crce.crce_webui_vaadin.resources;

import java.util.EnumSet;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cz.zcu.kiv.crce.crce_webui_vaadin.other.TypePackaging;
import cz.zcu.kiv.crce.crce_webui_vaadin.resources.classes.CentralMaven;

@SuppressWarnings("serial")
public class CentralMavenForm extends FormLayout {
	private Label caption = new Label("Central Maven repository");
	private TextField group = new TextField("Group Id");
	private TextField artifact = new TextField("Artifact Id");
	private TextField version = new TextField("Version");
	private NativeSelect packaging = new NativeSelect("Packaging");
	private Button searchButton = new Button("Search");
	private Button clearButton = new Button("Clear");
	private Label notFound = new Label("No artifact found");
	private Tree tree;
	
	public CentralMavenForm() {
		HorizontalLayout content = new HorizontalLayout();
		addComponent(content);
	}

	public CentralMavenForm(VaadinSession session){
		VerticalLayout userForm = new VerticalLayout();
		HorizontalLayout content = new HorizontalLayout();
		packaging.addItems(EnumSet.allOf(TypePackaging.class));
		packaging.select(TypePackaging.jar);
		packaging.setNullSelectionAllowed(false);
		
		searchButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		searchButton.setClickShortcut(KeyCode.ENTER);

		HorizontalLayout buttons = new HorizontalLayout(searchButton, clearButton);
		buttons.setSpacing(true);
		userForm.addComponents(caption, group, artifact, version, packaging, buttons);
		userForm.setSpacing(true);
		userForm.setMargin(new MarginInfo(false, true));
		content.addComponent(userForm);
		
		// Add tree
		searchButton.addClickListener(e ->{
			// erasing any previous components shown
			if(content.getComponentIndex(notFound) != -1){
				content.removeComponent(notFound);
			}
			if(content.getComponentIndex(tree) != -1){
				content.removeComponent(tree);
			}
			// check exist component from central Maven repository 
			tree = new CentralMaven(session).getTree(group.getValue(),artifact.getValue(),
					version.getValue(),packaging.getValue());
			if(tree == null){
				content.addComponent(notFound);
			}
			else{
				content.addComponent(tree);
			}
		});
		

		// Clear user form
		clearButton.addClickListener(e ->{
			content.removeAllComponents();
			group.clear();
			artifact.clear();
			version.clear();
			packaging.select(TypePackaging.jar);
			content.addComponent(userForm);
		});
		
		content.setSpacing(true);
		addComponent(content);
	}
	
	
}
