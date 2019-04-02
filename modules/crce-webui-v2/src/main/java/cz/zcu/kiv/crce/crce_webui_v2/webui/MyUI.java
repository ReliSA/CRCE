package cz.zcu.kiv.crce.crce_webui_v2.webui;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import cz.zcu.kiv.crce.crce_component_collection.api.bean.CollectionBean;
import cz.zcu.kiv.crce.crce_component_collection.api.bean.CollectionDetailBean;
import cz.zcu.kiv.crce.crce_component_collection.api.settings.SettingsLimitRange;
import cz.zcu.kiv.crce.crce_webui_v2.collection.CollectionEditForm;
import cz.zcu.kiv.crce.crce_webui_v2.collection.CollectionForm;
import cz.zcu.kiv.crce.crce_webui_v2.collection.CollectionNewForm;
import cz.zcu.kiv.crce.crce_webui_v2.collection.CollectionRangeParamDetailForm;
import cz.zcu.kiv.crce.crce_webui_v2.internal.Activator;
import cz.zcu.kiv.crce.crce_webui_v2.outer.*;
import cz.zcu.kiv.crce.crce_webui_v2.repository.ArtefactDetailForm;
import cz.zcu.kiv.crce.crce_webui_v2.repository.BufferForm;
import cz.zcu.kiv.crce.crce_webui_v2.repository.PluginsForm;
import cz.zcu.kiv.crce.crce_webui_v2.repository.StoreForm;
import cz.zcu.kiv.crce.crce_webui_v2.repository.classes.ResourceBean;
import cz.zcu.kiv.crce.crce_webui_v2.repository.services.ResourceService;

import javax.servlet.annotation.WebServlet;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;


/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@SuppressWarnings("serial")
@Theme("mytheme")
@Push
public class MyUI extends UI {
	private Panel head = new Panel();
	private Panel body = new Panel();
	private Panel footer = new Panel();
	private LocalMavenForm localMavenForm;
	private CentralMavenForm centralMavenForm;
	private CheckMavenIndexForm checkMavenIndexFrom;
	private DefinedMavenForm definedMavenForm;
	private LoadFileForm loadFileForm;
	private BufferForm bufferForm;
	private StoreForm storeForm;
	private CollectionForm collectionForm;
	private CollectionNewForm collectionNewForm;
	private CollectionEditForm collectionEditForm;
	private CollectionRangeParamDetailForm collectionRangeParamDetailForm;
	private ArtefactDetailForm artefactDetailForm;
	private PluginsForm pluginsForm;
	private SettingsUrlForm settingsUrlForm;
	private SettingRangeForm settingsRangeForm;
	private VerticalLayout content = new VerticalLayout();
	
	@Override
    protected void init(VaadinRequest vaadinRequest) {
		if (getSession().getAttribute("singed") != null) {
			loginExistSession();
		} else {
			loginNoSession();
		}
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
    
    public void loginExistSession() {
    	// menu components
		MenuForm menu = new MenuForm(this);
		menu.setMargin(false);
		head.setContent(menu);

		// body components
		LogoBackground logoBackground = new LogoBackground();
		body.setContent(logoBackground);

		// about component
		AboutForm about = new AboutForm();
		about.setMargin(false);
		footer.setContent(about);
		
		// content components
		content.setMargin(true);
		content.setSpacing(true);
		content.addComponents(head, body, footer);
		setContent(content);
	}
	
	public void loginNoSession(){
		LoginForm loginForm = new LoginForm(this);
		setContent(loginForm);
	}

	public void logout() {
		// clear component
		LoginForm loginForm = new LoginForm(this);
		cleanupAfterLogout();
		setContent(loginForm);
	}
	
	private void cleanupAfterLogout(){
		ResourceService resourceService = new ResourceService(Activator.instance().getMetadataService());
		resourceService.clearBuffer(getSession().getSession());
		getSession().setAttribute("settingsUrl", null);
		String exportPathText;
		// remove export path from collection
		if (getSession().getAttribute("exportArtifactRange") == null) {
			SettingsLimitRange settingsRange = new SettingsLimitRange();
			exportPathText = settingsRange.getExportPath();
		} else {
			exportPathText = ((SettingsLimitRange) getSession().getAttribute("exportArtifactRange")).getExportPath();
		}
		File file = new File(exportPathText + File.separator + getSession().getSession().getId());
		if(file.exists()){
			Path directory = file.toPath();
			try {
				Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Files.delete(file);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					}
				});
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		getSession().setAttribute("exportArtifactRange", null);
		getSession().getSession().invalidate();
		Page.getCurrent().setLocation(VaadinServlet.getCurrent().getServletContext().getContextPath());
		content.removeAllComponents();
	}
    
    public void setContentBodyLocalMaven(){
    	localMavenForm = new LocalMavenForm(this);
    	body.setContent(localMavenForm);
    }
    
    public void setContentBodyCentralMaven(){
    	centralMavenForm = new CentralMavenForm(this);
    	body.setContent(centralMavenForm);
    }
    
    public void setContentBodyCheckMavenIndexForm(){
    	checkMavenIndexFrom = new CheckMavenIndexForm(this);
    	body.setContent(checkMavenIndexFrom);
    }
    
    public void setContentBodyDefinedMaven(){
    	definedMavenForm = new DefinedMavenForm(this);
    	body.setContent(definedMavenForm);
    }
    
    public void setContentBodyLoadFile(){
    	loadFileForm = new LoadFileForm(this);
    	body.setContent(loadFileForm);
    }
    
    public void setContentBodyBuffer(){
    	bufferForm = new BufferForm(this);
    	body.setContent(bufferForm);
    }
    
    public void setContentBodyStore(){
    	storeForm = new StoreForm(this);
    	body.setContent(storeForm);
    }

    public void setContentBodyCollection(){
		collectionForm = new CollectionForm(this);
		body.setContent(collectionForm);
	}

	public void setContentBodyCollectionNew(){
		collectionNewForm = new CollectionNewForm(this);
		body.setContent(collectionNewForm);
	}

	public void setContentBodyCollectionEdit(CollectionBean collectionBean){
		collectionEditForm = new CollectionEditForm(this, collectionBean);
		body.setContent(collectionEditForm);
	}

	public void setContentBodyCollectionRangeParamDetailForm(CollectionDetailBean collectionDetailBean, FormLayout returnPage){
		collectionRangeParamDetailForm = new CollectionRangeParamDetailForm(this, collectionDetailBean, returnPage);
		body.setContent(collectionRangeParamDetailForm);
	}
    
    public void setContentArtefactDetailForm(ResourceBean resourceBean, FormLayout returnPage){
    	artefactDetailForm = new ArtefactDetailForm(this, resourceBean, returnPage);
    	body.setContent(artefactDetailForm);
    }
    
    public void setContentBodyPlugins(){
    	pluginsForm = new PluginsForm(this);
    	body.setContent(pluginsForm);
    }
    
    public void setContentSettingsUrl(){
    	settingsUrlForm = new SettingsUrlForm(this.getSession());
    	body.setContent(settingsUrlForm);
    }

	public void setContentSettingsRange(){
		settingsRangeForm = new SettingRangeForm(this.getSession());
		body.setContent(settingsRangeForm);
	}

    public void setContentExistingPage(FormLayout existingPage){
		body.setContent(existingPage);
	}
}

