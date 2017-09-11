package cz.zcu.kiv.crce.crce_webui_vaadin.webui;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

@SuppressWarnings("serial")
@StyleSheet("https://fonts.googleapis.com/css?family=Fredoka+One")
public class MenuForm extends FormLayout{
	public MenuForm(MyUI myUI) {
		HorizontalLayout headHL = new HorizontalLayout();
		HorizontalLayout menuHL = new HorizontalLayout();
		Label logo = new Label("<p style=\"font-size:50px;font-family:'Fredoka One';padding-left:10px;"
				+ "margin:0px 0px 0px 0px;color:rgb(23,116,216)\">CRCE UI</p>",ContentMode.HTML);
		MenuBar menu = new MenuBar();
		MenuItem upload = menu.addItem("Upload", null);
		MenuItem repository = menu.addItem("Repository", null);
		MenuItem settings = menu.addItem("Settings", null);
		
		// submenu upload
		upload.addItem("Local", e ->{myUI.setContentBodyLocalMaven();});
		upload.addItem("Central", e ->{myUI.setContentBodyCentralMaven();});
		upload.addItem("Defined", e ->{myUI.setContentBodyDefinedMaven();});
		upload.addItem("File/url", e ->{myUI.setContentBodyLoadFile();});
		
		// submenu repository
		repository.addItem("Buffer", e ->{myUI.setContentBodyBuffer();});
		repository.addItem("Store", null);
		repository.addItem("Plugins", e ->{myUI.setContentBodyPlugins();});
		
		// submenu settings
		settings.addItem("Index", e ->{myUI.setContentBodyCheckMavenIndexForm();});
		settings.addItem("Paths", e ->{myUI.setContentSettings();});
		
		// Menu login
		MenuBar menuLogin = new MenuBar();
		MenuItem menuItemLogin = menuLogin.addItem(myUI.getSession().getAttribute("singed").toString(), null);
		menuItemLogin.addItem("Logout", e ->{
			myUI.getSession().setAttribute("singed", null);
			myUI.logout();
		});
		
		menuHL.addComponents(logo, menu);
		menuHL.setComponentAlignment(menu, Alignment.MIDDLE_LEFT);
		menuHL.setSpacing(true);
		
		headHL.addComponents(menuHL, menuLogin);
		//Alignment.BOTTOM_RIGHT
		headHL.setComponentAlignment(menuHL,Alignment.MIDDLE_LEFT);
		headHL.setComponentAlignment(menuLogin,Alignment.MIDDLE_RIGHT);
		headHL.setSizeFull();
		
		//headHL.setSpacing(true);
		headHL.setMargin(new MarginInfo(false,true,false,false));
		headHL.setSpacing(true);
		addComponent(headHL);
	}
}
