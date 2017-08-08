package cz.zcu.kiv.crce.crce_webui_vaadin.webui;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cz.zcu.kiv.crce.crce_webui_vaadin.resources.classes.SettingsUrl;

@SuppressWarnings("serial")
public class SettingsForm extends FormLayout {
	
	public SettingsForm() {
		HorizontalLayout content = new HorizontalLayout();
		addComponent(content);
	}

	public SettingsForm(VaadinSession session) {
		TextField localMavenUrl = new TextField("Local Maven path");
		TextField centralMavenUrl = new TextField("Central Maven url");
		TextField localAetherRepo = new TextField("Local Aether repository");

		localMavenUrl.setWidth("400px");
		centralMavenUrl.setWidth("400px");
		localAetherRepo.setWidth("400px");

		// default value

		if (session.getAttribute("settingsUrl") == null) {
			SettingsUrl settingsUrl = new SettingsUrl();
			localMavenUrl.setValue(settingsUrl.getLocalMavenUrl());
			centralMavenUrl.setValue(settingsUrl.getCentralMavenUrl());
			localAetherRepo.setValue(settingsUrl.getLocalAetherUrl());
		} else {
			localMavenUrl.setValue(((SettingsUrl) session.getAttribute("settingsUrl")).getLocalMavenUrl());
			centralMavenUrl.setValue(((SettingsUrl) session.getAttribute("settingsUrl")).getCentralMavenUrl());
			localAetherRepo.setValue(((SettingsUrl) session.getAttribute("settingsUrl")).getLocalAetherUrl());
		}

		HorizontalLayout buttonsLayout = new HorizontalLayout();
		Button saveButton = new Button("Save");
		Button defaultButton = new Button("Default");
		buttonsLayout.addComponents(saveButton,defaultButton);
		buttonsLayout.setSpacing(true);

		saveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		saveButton.setClickShortcut(KeyCode.ENTER);
		
		VerticalLayout content = new VerticalLayout();
		content.addComponents(localMavenUrl, centralMavenUrl, localAetherRepo, buttonsLayout);
		content.setSpacing(true);

		HorizontalLayout formLayout = new HorizontalLayout();
		formLayout.addComponent(content);
		formLayout.setMargin(new MarginInfo(false, true));

		saveButton.addClickListener(e -> {
			SettingsUrl settingsUrl;
			if(getSession().getAttribute("settingsUrl") == null){
				settingsUrl = new SettingsUrl();
			}
			else{
				settingsUrl = (SettingsUrl) getSession().getAttribute("settingsUrl");
			}
			settingsUrl.setLocalMavenUrl(localMavenUrl.getValue());
			settingsUrl.setCentralMavenUrl(centralMavenUrl.getValue());
			settingsUrl.setLocalAetherUrl(localAetherRepo.getValue());
			getSession().setAttribute("settingsUrl", settingsUrl);
			Notification notif = new Notification("Info", "Settings saved successfully" ,
					Notification.Type.ASSISTIVE_NOTIFICATION);
			notif.setPosition(Position.TOP_RIGHT);
			notif.show(Page.getCurrent());
		});
		
		defaultButton.addClickListener(e ->{
			SettingsUrl settingsUrl = new SettingsUrl();
			getSession().setAttribute("settingsUrl", settingsUrl);
			localMavenUrl.setValue(settingsUrl.getLocalMavenUrl());
			centralMavenUrl.setValue(settingsUrl.getCentralMavenUrl());
			localAetherRepo.setValue(settingsUrl.getLocalAetherUrl());
		});

		addComponent(formLayout);
	}
}
