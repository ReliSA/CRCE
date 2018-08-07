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

import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

import cz.zcu.kiv.crce.crce_webui_v2.internal.Activator;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;

@SuppressWarnings("serial")
public class LoadFileForm extends FormLayout {
	private VaadinSession session;
	private Panel filePanel = new Panel("Upload artefact from local file system");
	private Panel urlPanel = new Panel("Upload artefact from remote url");
	
	public LoadFileForm(VaadinSession session){
		this.session = session;
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
		
		content.addComponents(filePanel, urlPanel);
		content.setMargin(new MarginInfo(false, true, false, false));
		content.setSpacing(true);
		
		urlButton.addClickListener(e -> {
			File file;
			try {
				URL url = new URL(urlText.getValue());
				file = new File(url.toString());
				InputStream input = url.openStream();
				Activator.instance().getBuffer(session.getSession()).put(file.getName(), input);
				Notification notif = new Notification("Info", "Artefact from url upload sucess",
						Notification.Type.ASSISTIVE_NOTIFICATION);
				notif.setPosition(Position.TOP_RIGHT);
				notif.show(Page.getCurrent());
			} catch (IOException | RefusedArtifactException ex) {
				new Notification("Could not open or load file from url", ex.getMessage(), Notification.Type.ERROR_MESSAGE)
				.show(Page.getCurrent());
			}
		});
		
		addComponent(content);
	}
	
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
				Activator.instance().getBuffer(session.getSession())
					.put(file.getName(), Files.newInputStream(file.toPath(), StandardOpenOption.READ));
				Notification notif = new Notification("Info", "Artefact from file upload sucess",
						Notification.Type.ASSISTIVE_NOTIFICATION);
				notif.setPosition(Position.TOP_RIGHT);
				notif.show(Page.getCurrent());
			} catch (IOException | RefusedArtifactException e) {
				new Notification("Could not open or load file", e.getMessage(), Notification.Type.ERROR_MESSAGE)
				.show(Page.getCurrent());
			}
		}
	}
}
