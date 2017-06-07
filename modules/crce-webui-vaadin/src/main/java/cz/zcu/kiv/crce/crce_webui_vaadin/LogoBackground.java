package cz.zcu.kiv.crce.crce_webui_vaadin;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class LogoBackground extends FormLayout{
	public LogoBackground(){
		Label logoBackground = new Label("<p style=\"font-size:200px;font-family:Verdana;margin-top:120px;"
				+ "margin-bottom:120px;text-align:center;color:rgb(224,224,224)\">CRCE</p>",ContentMode.HTML);
		addComponent(logoBackground);
	}
}
