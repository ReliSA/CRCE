package cz.zcu.kiv.crce.crce_webui_vaadin;

import javax.servlet.annotation.WebServlet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.zcu.kiv.crce.crce_webui_vaadin.repository.PluginsForm;
import cz.zcu.kiv.crce.crce_webui_vaadin.resources.CentralMavenForm;
import cz.zcu.kiv.crce.crce_webui_vaadin.resources.DefinedMavenForm;
import cz.zcu.kiv.crce.crce_webui_vaadin.resources.LoadFileForm;
import cz.zcu.kiv.crce.crce_webui_vaadin.resources.LocalMavenForm;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@SuppressWarnings("serial")
@Theme("mytheme")
public class MyUI extends UI {
	private MenuForm menu = new MenuForm(this);
	private Panel head = new Panel();
	private Panel body = new Panel();
	private LocalMavenForm localMavenForm = new LocalMavenForm();
	private CentralMavenForm centralMavenForm = new CentralMavenForm();
	private DefinedMavenForm definedMavenForm = new DefinedMavenForm();
	private PluginsForm pluginsForm = new PluginsForm();
	private SettingsForm settingsForm = new SettingsForm();
	
	
	private LoadFileForm loadFileForm = new LoadFileForm();
	private VerticalLayout content = new VerticalLayout();
	
	@Override
    protected void init(VaadinRequest vaadinRequest) {
		// menu components
		menu.setMargin(false);
		head.setContent(menu);
		
		// body components
		LogoBackground logoBackgroung = new LogoBackground();
		body.setContent(logoBackgroung);
		
		// content components
		content.setMargin(true);
		content.setSpacing(true);
		content.addComponents(head,body);
    	setContent(content);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
    
    public void setContentBodyLocalMaven(){
    	localMavenForm = new LocalMavenForm(this.getSession());
    	body.setContent(localMavenForm);
    }
    
    public void setContentBodyCentralMaven(){
    	centralMavenForm = new CentralMavenForm(this.getSession());
    	body.setContent(centralMavenForm);
    }
    
    public void setContentBodyDefinedMaven(){
    	definedMavenForm = new DefinedMavenForm(this.getSession());
    	body.setContent(definedMavenForm);
    }
    
    public void setContentBodyLoadFile(){
    	body.setContent(loadFileForm);
    }
    
    public void setContentBodyPlugins(){
    	body.setContent(pluginsForm);
    }
    
    public void setContentSettings(){
    	settingsForm = new SettingsForm(this.getSession());
    	body.setContent(settingsForm);
    }
}

