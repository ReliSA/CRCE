package cz.zcu.kiv.crce.crce_webui_vaadin.repository;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cz.zcu.kiv.crce.crce_webui_vaadin.repository.classes.ResourceBean;
import cz.zcu.kiv.crce.crce_webui_vaadin.webui.MyUI;

@SuppressWarnings("serial")
public class NewCapabilityForm extends FormLayout{
	private Label labelForm = new Label("Add new capability");
	private Button buttonSave = new Button("Save");
	private Button buttonCancel = new Button("Cancel");
	public NewCapabilityForm(MyUI myUI, ResourceBean resourceBean, boolean isFromStore){
		VerticalLayout content = new VerticalLayout();
		HorizontalLayout buttonLayout = new HorizontalLayout();
		
		labelForm.addStyleName(ValoTheme.LABEL_BOLD);
		
		buttonSave.setStyleName(ValoTheme.BUTTON_PRIMARY);
		buttonSave.setClickShortcut(KeyCode.ENTER);
		buttonLayout.addComponents(buttonSave, buttonCancel);
		buttonLayout.setSpacing(true);
		
		buttonSave.addClickListener(e ->{
			
		});
		
		buttonCancel.addClickListener(e ->{
			myUI.setContentArtefactDetailForm(resourceBean, isFromStore);
		});
		
		content.addComponents(labelForm, buttonLayout);
		content.setSpacing(true);
		content.setMargin(new MarginInfo(true, false));
		addComponent(content);
	}
}
