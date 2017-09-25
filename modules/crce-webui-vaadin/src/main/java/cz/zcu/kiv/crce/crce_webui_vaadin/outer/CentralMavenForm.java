package cz.zcu.kiv.crce.crce_webui_vaadin.outer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.EnumSet;

import com.vaadin.event.ShortcutListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import cz.zcu.kiv.crce.crce_webui_vaadin.internal.Activator;
import cz.zcu.kiv.crce.crce_webui_vaadin.other.TypePackaging;
import cz.zcu.kiv.crce.crce_webui_vaadin.outer.classes.CentralMaven;
import cz.zcu.kiv.crce.crce_webui_vaadin.webui.MyUI;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;

@SuppressWarnings("serial")
public class CentralMavenForm extends FormLayout {
	private Label caption = new Label("Central Maven repository");
	private TextField group = new TextField("Group Id");
	private TextField artifact = new TextField("Artifact Id");
	private TextField version = new TextField("Version");
	private NativeSelect packaging = new NativeSelect("Packaging");
	private OptionGroup directIndexOption = new OptionGroup("Direct or search index");
	private NativeSelect rangeOption = new NativeSelect("Range");
	private Button searchButton = new Button("Search");
	private Button clearButton = new Button("Clear");
	private Label notFound = new Label("No artifact found");
	private Tree tree;

	public CentralMavenForm() {
		HorizontalLayout content = new HorizontalLayout();
		addComponent(content);
	}

	public CentralMavenForm(MyUI myUI) {
		VerticalLayout userForm = new VerticalLayout();
		HorizontalLayout content = new HorizontalLayout();
		HorizontalLayout versionLayout = new HorizontalLayout();
		packaging.addItems(EnumSet.allOf(TypePackaging.class));
		packaging.select(TypePackaging.jar);
		packaging.setNullSelectionAllowed(false);
		
		caption.addStyleName(ValoTheme.LABEL_BOLD);
		
		group.setWidth("250px");
		artifact.setWidth("250px");
		
		directIndexOption.addItems("Direct", "Index");
		directIndexOption.setValue("Direct");
		
		rangeOption.addItem("<=");
		rangeOption.addItem("=");
		rangeOption.addItem(">=");
		rangeOption.select("=");
		rangeOption.setNullSelectionAllowed(false);
		rangeOption.setEnabled(false);
		
		versionLayout.addComponents(version, rangeOption);
		versionLayout.setSpacing(true);

		searchButton.setStyleName(ValoTheme.BUTTON_PRIMARY);

		HorizontalLayout buttons = new HorizontalLayout(searchButton, clearButton);
		buttons.setSpacing(true);
		userForm.addComponents(caption, group, artifact, versionLayout, packaging, directIndexOption, buttons);
		userForm.setSpacing(true);
		userForm.setMargin(new MarginInfo(false, true));
		content.addComponent(userForm);

		// Add tree
		searchButton.addClickListener(e -> {
			// erasing any previous components shown
			if (content.getComponentIndex(notFound) != -1) {
				content.removeComponent(notFound);
			}
			if (content.getComponentIndex(tree) != -1) {
				content.removeComponent(tree);
			}
			// check exist component from central Maven repository
			if(directIndexOption.getValue().equals("Direct")){
				tree = new CentralMaven(myUI.getSession()).getTree(group.getValue(), artifact.getValue(), version.getValue(),
						packaging.getValue(), directIndexOption.getValue(), null);
			}
			else{
				if(group.getValue().trim().isEmpty() || artifact.getValue().trim().isEmpty()){
					Notification notif = new Notification("Incomplete assignment!",
							Notification.Type.WARNING_MESSAGE);
					notif.setDelayMsec(5000);
					notif.show(Page.getCurrent());
				}
				else if(group.getValue().charAt(0) == '*' || group.getValue().charAt(0) == '?' || artifact.getValue().charAt(0) == '*'
						|| artifact.getValue().charAt(0) == '?'){
					Notification notif = new Notification("Can not start a search term with '*' or '?'",
							Notification.Type.WARNING_MESSAGE);
					notif.setDelayMsec(5000);
					notif.show(Page.getCurrent());
				}
				else{
					tree = new CentralMaven(myUI.getSession()).getTree(group.getValue(), artifact.getValue(), version.getValue(),
							packaging.getValue(), directIndexOption.getValue(), rangeOption.getValue().toString());
				}
			}
			if (tree == null) {
				content.addComponent(notFound);
			} else {
				tree.addShortcutListener(new ShortcutListener("", KeyCode.ENTER, null) {
					@Override
					public void handleAction(Object sender, Object target) {
						if (tree.getValue() != null && !(tree.areChildrenAllowed((Object) tree.getValue()))
								&& myUI.getWindows().isEmpty()) {
							myUI.addWindow(new CheckUploadModal(tree.getValue().toString(), myUI.getSession()));
						}
					}
				});
				content.addComponent(tree);
			}
		});
		
		directIndexOption.addValueChangeListener(e ->{
			if(directIndexOption.getValue().equals("Direct")){
				rangeOption.setEnabled(false);
				group.setRequired(false);
				artifact.setRequired(false);
			}
			else{
				rangeOption.setEnabled(true);
				group.setRequired(true);
				group.setRequiredError("The item can not be empty!");
				artifact.setRequired(true);
				artifact.setRequiredError("The item can not be empty!");
			}
		});

		// Clear user form
		clearButton.addClickListener(e -> {
			content.removeAllComponents();
			group.clear();
			artifact.clear();
			version.clear();
			packaging.select(TypePackaging.jar);
			content.addComponent(userForm);
		});

		content.setSpacing(true);
		addComponent(content);
	}

	private static class CheckUploadModal extends Window {
		public CheckUploadModal(String urlText, VaadinSession session) {
			super("Upload " + urlText.substring(urlText.lastIndexOf('/') + 1) + " to buffer?");
			VerticalLayout content = new VerticalLayout();
			content.setWidth("500px");
			content.setHeight("100px");

			HorizontalLayout buttonLayout = new HorizontalLayout();

			Button uploadButton = new Button("Upload");
			uploadButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
			Button cancelButton = new Button("Cancel");

			buttonLayout.addComponents(uploadButton, cancelButton);

			buttonLayout.setSpacing(true);
			buttonLayout.setMargin(true);

			content.addComponent(buttonLayout);
			content.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);

			center();
			setClosable(false);
			setResizable(false);

			uploadButton.addClickListener(e -> {
				File file;
				try {
					URL url = new URL(urlText);
					file = new File(url.toString());
					InputStream input = url.openStream();
					Activator.instance().getBuffer(session.getSession()).put(file.getName(), input);
					Notification notif = new Notification("Info", "Artefact from central maven upload sucess",
							Notification.Type.ASSISTIVE_NOTIFICATION);
					notif.setPosition(Position.TOP_RIGHT);
					notif.show(Page.getCurrent());
				} catch (IOException | RefusedArtifactException ex) {
					new Notification("Could not open or load file from url", ex.getMessage(), Notification.Type.ERROR_MESSAGE)
					.show(Page.getCurrent());
				}
				close();
			});

			cancelButton.addClickListener(e -> {
				close();
			});

			setContent(content);
		}
	}
}
