package cz.zcu.kiv.crce.crce_webui_v2.webui;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.zcu.kiv.crce.crce_external_repository.api.SettingsUrl;

@SuppressWarnings("serial")
public class SettingsUrlForm extends FormLayout {
	
	public SettingsUrlForm() {
		HorizontalLayout content = new HorizontalLayout();
		addComponent(content);
	}

	public SettingsUrlForm(VaadinSession session) {
		TextField centralMavenUrl = new TextField("Central Maven url");
		TextField localAetherRepo = new TextField("Local Maven repository");
		CheckBox enableDeleteLocalMaven = new CheckBox("Enable delete local Maven repo");
		CheckBox enableGroupSearch = new CheckBox("Enable only group search");

		centralMavenUrl.setWidth("400px");
		localAetherRepo.setWidth("400px");

		// default value

		if (session.getAttribute("settingsUrl") == null) {
			SettingsUrl settingsUrl = new SettingsUrl();
			centralMavenUrl.setValue(settingsUrl.getCentralMavenUrl());
			localAetherRepo.setValue(settingsUrl.getLocalAetherUrl());
			enableDeleteLocalMaven.setValue(false);
			enableGroupSearch.setValue(false);
		} else {
			centralMavenUrl.setValue(((SettingsUrl) session.getAttribute("settingsUrl")).getCentralMavenUrl());
			localAetherRepo.setValue(((SettingsUrl) session.getAttribute("settingsUrl")).getLocalAetherUrl());
			enableDeleteLocalMaven.setValue(((SettingsUrl) session.getAttribute("settingsUrl")).isEnableDeleteLocalMaven());
			enableGroupSearch.setValue(((SettingsUrl) session.getAttribute("settingsUrl")).isEnableGroupSearch());
		}

		HorizontalLayout buttonsLayout = new HorizontalLayout();
		Button saveButton = new Button("Save");
		Button defaultButton = new Button("Default");
		buttonsLayout.addComponents(saveButton,defaultButton);
		buttonsLayout.setSpacing(true);

		saveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		saveButton.setClickShortcut(KeyCode.ENTER);
		
		VerticalLayout content = new VerticalLayout();
		content.addComponents(centralMavenUrl, localAetherRepo, enableDeleteLocalMaven, enableGroupSearch, buttonsLayout);
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
			settingsUrl.setCentralMavenUrl(centralMavenUrl.getValue());
			settingsUrl.setLocalAetherUrl(localAetherRepo.getValue());
			settingsUrl.setEnableDeleteLocalMaven(enableDeleteLocalMaven.getValue());
			settingsUrl.setEnableGroupSearch(enableGroupSearch.getValue());

			getSession().setAttribute("settingsUrl", settingsUrl);
			Notification notif = new Notification("Info", "Settings saved successfully" ,
					Notification.Type.ASSISTIVE_NOTIFICATION);
			notif.setPosition(Position.TOP_RIGHT);
			notif.show(Page.getCurrent());
		});
		
		defaultButton.addClickListener(e ->{
			SettingsUrl settingsUrl = new SettingsUrl();
			getSession().setAttribute("settingsUrl", settingsUrl);
			centralMavenUrl.setValue(settingsUrl.getCentralMavenUrl());
			localAetherRepo.setValue(settingsUrl.getLocalAetherUrl());
			enableDeleteLocalMaven.setValue(settingsUrl.isEnableDeleteLocalMaven());
			enableGroupSearch.setValue(settingsUrl.isEnableGroupSearch());
		});

		addComponent(formLayout);
	}
}
