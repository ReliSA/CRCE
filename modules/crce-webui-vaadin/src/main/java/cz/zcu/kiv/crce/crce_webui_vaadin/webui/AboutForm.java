package cz.zcu.kiv.crce.crce_webui_vaadin.webui;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import cz.zcu.kiv.crce.crce_webui_vaadin.other.VersionInfo;

@SuppressWarnings("serial")
public class AboutForm extends FormLayout{ 
	public AboutForm(){
		VerticalLayout content = new VerticalLayout();
		VersionInfo versionInfo = VersionInfo.getVersionInfo();
		
		Label about = new Label("<p style=\"font-size:16px;font-family:Verdana;"
				+ "text-align:center;color:rgb(128,128,128)\">University of West Bohemia, "
				+ "ReliSA research group, Version: " + versionInfo.getProductVersion()
				+ ", Build revision: " + versionInfo.getBuildRevision() + "</p>", ContentMode.HTML);
		
		content.setSpacing(false);
		content.setMargin(false);
		content.addComponent(about);
		addComponent(content);
	}
}
