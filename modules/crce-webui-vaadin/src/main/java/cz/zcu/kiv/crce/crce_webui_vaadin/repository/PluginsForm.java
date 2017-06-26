package cz.zcu.kiv.crce.crce_webui_vaadin.repository;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class PluginsForm extends FormLayout{
	private Label text = new Label(); 
	public PluginsForm(){
		VerticalLayout userForm = new VerticalLayout();
		HorizontalLayout content = new HorizontalLayout();
		text.setValue("Pokus");
		userForm.addComponent(text);
		
		userForm.setSpacing(true);
		userForm.setMargin(new MarginInfo(false, true));
		content.addComponent(userForm);
		content.setSpacing(true);
		addComponent(content);
	}
}
