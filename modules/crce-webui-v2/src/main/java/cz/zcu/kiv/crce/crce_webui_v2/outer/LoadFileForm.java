package cz.zcu.kiv.crce.crce_webui_v2.outer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.SelectionMode;

import cz.zcu.kiv.crce.crce_webui_v2.internal.Activator;
import cz.zcu.kiv.crce.crce_webui_v2.repository.classes.ResourceBean;
import cz.zcu.kiv.crce.crce_webui_v2.repository.services.ResourceService;
import cz.zcu.kiv.crce.crce_webui_v2.webui.MyUI;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;

/**
 * A form for uploading an artifact to a CRCE from a file system or url address.
 * <p/>
 * Date: 02.05.19
 *
 * @author Roman Pesek
 */
@SuppressWarnings("serial")
public class LoadFileForm extends FormLayout {
	private Panel filePanel = new Panel("Upload artefact from local file system");
	private Panel urlPanel = new Panel("Upload artefact from remote url");
	private Panel bufferPanel = new Panel("Buffer");
	private Grid bufferGrid = new Grid();
	private ResourceService resourceService;
	private MyUI myUI;
	
	public LoadFileForm(MyUI myUI){
		this.myUI = myUI;
		VerticalLayout content = new VerticalLayout();
		HorizontalLayout fileLayout = new HorizontalLayout();
		HorizontalLayout urlLayout = new HorizontalLayout();
		
		FileUploader receiver = new FileUploader();
		Upload upload = new Upload("Directly Upload File:", receiver);
		upload.setButtonCaption("Browse Files");
		upload.addSucceededListener(receiver);
		upload.setImmediate(true);
		
		TextField urlText = new TextField();
		urlText.setWidth("400px");
		Button urlButton = new Button("Load");
		urlLayout.addComponents(urlText, urlButton);
		urlLayout.setSpacing(true);
		urlLayout.setMargin(true);
		urlLayout.setHeight("100px");
		urlPanel.setContent(urlLayout);
		
		fileLayout.addComponent(upload);
		fileLayout.setMargin(true);
		fileLayout.setHeight("300px");
		filePanel.setContent(fileLayout);
		
		resourceService = new ResourceService(Activator.instance().getMetadataService());
		List<ResourceBean> resourceBeanList = resourceService.getAllResourceBeanFromBuffer(myUI.getSession().getSession());
		
		if(!resourceBeanList.isEmpty()) {
			HorizontalLayout bufferPanelLayout = new HorizontalLayout();
			VerticalLayout bufferLayout = new VerticalLayout();
			bufferGrid.setContainerDataSource(new BeanItemContainer<>(ResourceBean.class, resourceBeanList));
			bufferGrid.getColumn("resource").setHidden(true);
			bufferGrid.getColumn("size").setHidden(true);
			bufferGrid.setColumnOrder("presentationName", "symbolicName", "version", "categories");
			bufferGrid.addStyleName("my-style");
			bufferGrid.setSelectionMode(SelectionMode.NONE);
			bufferLayout.addComponent(bufferGrid);
			bufferGrid.setSizeFull();
			bufferPanelLayout.setSizeFull();
			bufferPanelLayout.addComponent(bufferLayout);
			bufferPanelLayout.setExpandRatio(bufferLayout, 1);
			bufferPanel.setContent(bufferPanelLayout);
			
			content.addComponents(filePanel, urlPanel, bufferPanel);
		}
		else {
			content.addComponents(filePanel, urlPanel);
		}
		
		content.setMargin(new MarginInfo(false, true, false, false));
		content.setSpacing(true);
		
		urlButton.addClickListener(e -> {
			File file;
			try {
				URL url = new URL(urlText.getValue());
				file = new File(url.toString());
				InputStream input = url.openStream();
				Activator.instance().getBuffer(myUI.getSession().getSession()).put(file.getName(), input);
				Notification notif = new Notification("Info", "Artefact from url upload sucess",
						Notification.Type.ASSISTIVE_NOTIFICATION);
				notif.setPosition(Position.TOP_RIGHT);
				notif.show(Page.getCurrent());
				myUI.setContentBodyLoadFile();
			} catch (IOException | RefusedArtifactException ex) {
				new Notification("Could not open or load file from url", ex.getMessage(), Notification.Type.ERROR_MESSAGE)
				.show(Page.getCurrent());
			}
		});
		
		addComponent(content);
	}

	/**
	 * Implementation of Vaadin framework components.
	 * <p/>
	 * Date: 02.05.19
	 * @see <a href="https://vaadin.com/components/vaadin-upload/java-examples">vaadin-upload-component-example</a>
	 *
	 * @author Roman Pesek
	 */
	class FileUploader implements Receiver, SucceededListener {
		private File file;
		public OutputStream receiveUpload(String filename, String mimeType) {
			FileOutputStream fos = null;
			try {
				file = new File(filename);
				fos = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				new Notification("Could not open file", e.getMessage(), Notification.Type.ERROR_MESSAGE)
						.show(Page.getCurrent());
				return null;
			}
			return fos;
		}

		public void uploadSucceeded(SucceededEvent event) {
			try {
				Activator.instance().getBuffer(myUI.getSession().getSession())
					.put(file.getName(), Files.newInputStream(file.toPath(), StandardOpenOption.READ));
				Notification notif = new Notification("Info", "Artefact from file upload sucess",
						Notification.Type.ASSISTIVE_NOTIFICATION);
				notif.setPosition(Position.TOP_RIGHT);
				notif.show(Page.getCurrent());
				myUI.setContentBodyLoadFile();
			} catch (IOException | RefusedArtifactException e) {
				new Notification("Could not open or load file", e.getMessage(), Notification.Type.ERROR_MESSAGE)
				.show(Page.getCurrent());
			}
		}
	}
}
