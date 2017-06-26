package cz.zcu.kiv.crce.crce_webui_vaadin.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class LoadFileForm extends FormLayout {
	final TextArea area = new TextArea();
	public LoadFileForm() {
		FileUploader receiver = new FileUploader();
		VerticalLayout content = new VerticalLayout();
		Upload upload = new Upload("Directly Upload File:", receiver);
		upload.setButtonCaption("Browse Files");
		upload.addSucceededListener(receiver);
		upload.setImmediate(true);
		area.setVisible(false);
		area.setSizeFull();
		content.addComponents(upload, area);
		content.setMargin(new MarginInfo(false, true, false, false));
		content.setSpacing(true);
		addComponent(content);
	}
	
	class FileUploader implements Receiver, SucceededListener {
		public File file;

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
			Notification notif = new Notification("Info", "File upload sucess",
					Notification.Type.ASSISTIVE_NOTIFICATION);
			notif.setPosition(Position.TOP_RIGHT);
			notif.show(Page.getCurrent());
			try {
				area.setCaption("Content of File " + file.getName());
				area.setValue(new String(Files.readAllBytes(file.toPath()),Charset.forName("UTF-8")));
				area.setVisible(true);
				area.setHeight("600px");
			} catch (IOException e) {
				new Notification("Could not open file", e.getMessage(), Notification.Type.ERROR_MESSAGE)
				.show(Page.getCurrent());
			}
		}
	}
}
