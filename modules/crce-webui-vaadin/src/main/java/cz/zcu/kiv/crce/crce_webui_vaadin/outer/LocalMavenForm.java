package cz.zcu.kiv.crce.crce_webui_vaadin.outer;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Tree;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.zcu.kiv.crce.crce_webui_vaadin.internal.Activator;
import cz.zcu.kiv.crce.crce_webui_vaadin.outer.classes.LocalMaven;
import cz.zcu.kiv.crce.crce_webui_vaadin.webui.MyUI;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;

@SuppressWarnings("serial")
public class LocalMavenForm extends FormLayout {
	private LocalMaven localMaven = new LocalMaven();
	private Label caption = new Label("Local Maven repository");

	public LocalMavenForm() {
		HorizontalLayout content = new HorizontalLayout();
		addComponent(content);
	}

	public LocalMavenForm(MyUI myUI) {
		VerticalLayout content = new VerticalLayout();
		// Tree of Maven local repository
		Tree localMavenTree = localMaven.getTree(myUI.getSession());
		content.setMargin(new MarginInfo(false, true));
		if (localMavenTree == null) {
			content.addComponent(new Label("Local Maven repository not found"));
		} else {
			content.addComponents(caption, localMavenTree);
			content.setSpacing(true);

			localMavenTree.addShortcutListener(new ShortcutListener("", KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (localMavenTree.getValue() != null && myUI.getWindows().isEmpty()){
						if(localMavenTree.areChildrenAllowed((Object) localMavenTree.getValue())){
							myUI.addWindow(new RemovePathModal(new File(localMavenTree.getValue().toString()), myUI));
						}
						else{
							myUI.addWindow(new CheckUploadModal(new File(localMavenTree.getValue().toString()), myUI.getSession()));
						}
					}
				}
			});
		}
		addComponent(content);
	}
	
	private static class CheckUploadModal extends Window {
		public CheckUploadModal(File file, VaadinSession session) {
			super("Upload " + file.getName() + " to buffer?");
			VerticalLayout content = new VerticalLayout();
			content.setWidth("400px");
			content.setHeight("100px");

			HorizontalLayout buttonLayout = new HorizontalLayout();
			
			Button acceptButton = new Button("OK");
			acceptButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
			Button cancelButton = new Button("Cancel");

			buttonLayout.addComponents(acceptButton, cancelButton);

			buttonLayout.setSpacing(true);
			buttonLayout.setMargin(true);

			content.addComponent(buttonLayout);
			content.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);

			center();
			setClosable(false);
			setResizable(false);
			
			acceptButton.addClickListener(e ->{
				try {
					Activator.instance().getBuffer(session.getSession()).put(file.getName(), Files.newInputStream(file.toPath(), StandardOpenOption.READ));
					Notification notif = new Notification("Info", "Artifact to buffer upload sucess", 
							Notification.Type.ASSISTIVE_NOTIFICATION);
					notif.setPosition(Position.TOP_RIGHT);
					notif.show(Page.getCurrent());
				} catch (IOException | RefusedArtifactException ex) {
					new Notification("Could not open file", ex.getMessage(), Notification.Type.ERROR_MESSAGE)
					.show(Page.getCurrent());
				} finally {
					close();
				}
			});

			cancelButton.addClickListener(e -> {
				close();
			});

			setContent(content);
		}
	}
	
	private static class RemovePathModal extends Window {
		public RemovePathModal(File file, MyUI myUI){
			super("Remove path " + file.getName() + "?");
			VerticalLayout content = new VerticalLayout();
			content.setWidth("400px");
			content.setHeight("100px");

			HorizontalLayout buttonLayout = new HorizontalLayout();
			
			Button removeButton = new Button("Remove");
			removeButton.setStyleName(ValoTheme.BUTTON_DANGER);
			Button cancelButton = new Button("Cancel");

			buttonLayout.addComponents(removeButton, cancelButton);

			buttonLayout.setSpacing(true);
			buttonLayout.setMargin(true);

			content.addComponent(buttonLayout);
			content.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);

			center();
			setClosable(false);
			setResizable(false);
			
			removeButton.addClickListener(e ->{
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
					new Notification("Could not remove path!", ex.getMessage(), Notification.Type.ERROR_MESSAGE)
					.show(Page.getCurrent());
				} finally{
					close();
					myUI.setContentBodyLocalMaven();
				}
			});
			
			cancelButton.addClickListener(e -> {
				close();
			});

			setContent(content);
		}
	}
}
