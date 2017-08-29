package cz.zcu.kiv.crce.crce_webui_vaadin.outer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import cz.zcu.kiv.crce.crce_webui_vaadin.internal.Activator;
import cz.zcu.kiv.crce.crce_webui_vaadin.other.TypePackaging;
import cz.zcu.kiv.crce.crce_webui_vaadin.outer.classes.DefinedMaven;
import cz.zcu.kiv.crce.crce_webui_vaadin.outer.classes.SettingsUrl;
import cz.zcu.kiv.crce.crce_webui_vaadin.webui.MyUI;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;

public class DefinedMavenForm extends FormLayout implements Serializable {
	private static final long serialVersionUID = 4172878715304331198L;
	private transient DefinedMaven definedMaven;
	private TextField definedUrl = new TextField();
	private Label caption = new Label("Defined Maven repository");
	private TextField group = new TextField("Group Id");
	private TextField artifact = new TextField("Artifact Id");
	private TextField version = new TextField("Version");
	private NativeSelect packaging = new NativeSelect("Packaging");
	private Button searchButton = new Button("Search");
	private Button clearButton = new Button("Clear");
	private Label notFound = new Label("No artifact found");
	private Tree tree;

	public DefinedMavenForm() {
		HorizontalLayout content = new HorizontalLayout();
		addComponent(content);
	}

	@SuppressWarnings("serial")
	public DefinedMavenForm(MyUI myUI) {
		// settings url
		if (myUI.getSession().getAttribute("settingsUrl") == null) {
			SettingsUrl settings = new SettingsUrl();
			definedUrl.setValue(settings.getExternalAetherUrl());
		} else {
			definedUrl.setValue(((SettingsUrl) myUI.getSession().getAttribute("settingsUrl")).getExternalAetherUrl());
		}

		VerticalLayout fieldLayout = new VerticalLayout();
		HorizontalLayout treeLayout = new HorizontalLayout();
		VerticalLayout formLayout = new VerticalLayout();
		HorizontalLayout content = new HorizontalLayout();

		definedUrl.setWidth("450px");

		packaging.addItems(EnumSet.allOf(TypePackaging.class));
		packaging.select(TypePackaging.jar);
		packaging.setNullSelectionAllowed(false);

		searchButton.setStyleName(ValoTheme.BUTTON_PRIMARY);

		HorizontalLayout buttons = new HorizontalLayout(searchButton, clearButton);
		buttons.setSpacing(true);

		Button setUrl = new Button("Set");
		CssLayout definedCss = new CssLayout();
		definedCss.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		definedCss.addComponents(definedUrl, setUrl);

		fieldLayout.addComponents(group, artifact, version, packaging, buttons);
		fieldLayout.setSpacing(true);
		fieldLayout.setMargin(new MarginInfo(false, true, false, false));

		treeLayout.addComponent(fieldLayout);
		treeLayout.setSpacing(true);

		formLayout.addComponents(caption, definedCss, treeLayout);
		formLayout.setSpacing(true);
		formLayout.setMargin(new MarginInfo(false, true));

		content.addComponents(formLayout);

		// Setting url defined repository
		setUrl.addClickListener(e -> {
			SettingsUrl settings;
			if (myUI.getSession().getAttribute("settingsUrl") == null) {
				settings = new SettingsUrl();
				settings.setExternalAetherUrl(definedUrl.getValue());
				getSession().setAttribute("settingsUrl", settings);
			} else {
				settings = (SettingsUrl) myUI.getSession().getAttribute("settingsUrl");
				settings.setExternalAetherUrl(definedUrl.getValue());
				getSession().setAttribute("settingsUrl", settings);
			}
		});

		// clear form
		clearButton.addClickListener(e -> {
			group.clear();
			artifact.clear();
			version.clear();
			packaging.select(TypePackaging.jar);
			// erasing any previous components shown
			if (treeLayout.getComponentIndex(notFound) != -1) {
				treeLayout.removeComponent(notFound);
			}
			if (treeLayout.getComponentIndex(tree) != -1) {
				treeLayout.removeComponent(tree);
			}
		});

		// search artefact from defined repository
		searchButton.addClickListener(e -> {
			SettingsUrl settings;
			if (myUI.getSession().getAttribute("settingsUrl") == null) {
				settings = new SettingsUrl();
			} else {
				settings = (SettingsUrl) myUI.getSession().getAttribute("settingsUrl");
			}
			// erasing any previous components shown
			if (treeLayout.getComponentIndex(notFound) != -1) {
				treeLayout.removeComponent(notFound);
			}
			if (treeLayout.getComponentIndex(tree) != -1) {
				treeLayout.removeComponent(tree);
			}

			// předání hodnot - doplnit
			definedMaven = new DefinedMaven(settings);
			tree = definedMaven.getTree(group.getValue(), artifact.getValue(), version.getValue(),
					packaging.getValue());
			if (tree == null) {
				treeLayout.addComponent(notFound);
			} else {
				treeLayout.addComponent(tree);
				tree.addShortcutListener(new ShortcutListener("", KeyCode.ENTER, null) {
					@Override
					public void handleAction(Object sender, Object target) {
						if (tree.getValue() != null && !(tree.areChildrenAllowed((Object) tree.getValue()))
								&& myUI.getWindows().isEmpty()) {
							myUI.addWindow(new CheckUploadModal(tree.getValue().toString(), settings, myUI.getSession()));
						}
					}
				});
			}
		});

		content.setSpacing(true);
		addComponent(content);
	}

	@SuppressWarnings("serial")
	private static class CheckUploadModal extends Window {
		public CheckUploadModal(String artifactText, SettingsUrl settings, VaadinSession session) {
			super("What to do next with " + artifactText.split(":")[1] + "-" + artifactText.split(":")[2] + "."
					+ artifactText.split(":")[3] + " ?");
			VerticalLayout content = new VerticalLayout();
			content.setWidth("500px");
			content.setHeight("100px");

			HorizontalLayout buttonLayout = new HorizontalLayout();

			Button uploadButton = new Button("Upload");
			uploadButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
			Button resolveButton = new Button("Resolve");
			Button cancelButton = new Button("Cancel");

			buttonLayout.addComponents(uploadButton, resolveButton, cancelButton);

			buttonLayout.setSpacing(true);
			buttonLayout.setMargin(true);

			content.addComponent(buttonLayout);
			content.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);

			center();
			setClosable(false);
			setResizable(false);

			resolveButton.addClickListener(e -> {
				if (DefinedMaven.resolveArtefact(artifactText, settings)) {
					Notification notif = new Notification("Info", "Artefact sucess resolved to Maven local repository.",
							Notification.Type.ASSISTIVE_NOTIFICATION);
					notif.setPosition(Position.TOP_RIGHT);
					notif.show(Page.getCurrent());
				} else {
					new Notification("Problem with resolving artifact", Notification.Type.WARNING_MESSAGE)
							.show(Page.getCurrent());
				}
				close();
			});

			uploadButton.addClickListener(e -> {
				// First check the artifact storage in the local repository
				if (DefinedMaven.resolveArtefact(artifactText, settings)) {
					File file = new File(settings.getLocalAetherUrl() + "/" + artifactText.split(":")[0].replace('.', '/') + "/" +
							artifactText.split(":")[1] + "/" + artifactText.split(":")[2] + "/" + artifactText.split(":")[1] + 
							"-" + artifactText.split(":")[2] +"." + artifactText.split(":")[3]);
					try {
						Activator.instance().getBuffer(session.getSession()).put(file.getName(), Files.newInputStream(file.toPath(), StandardOpenOption.READ));
						Notification notif = new Notification("Info", "Artifact to buffer upload sucess", 
								Notification.Type.ASSISTIVE_NOTIFICATION);
						notif.setPosition(Position.TOP_RIGHT);
						notif.show(Page.getCurrent());
					} catch (IOException | RefusedArtifactException ex) {
						new Notification("Could not open file", ex.getMessage(), Notification.Type.ERROR_MESSAGE)
						.show(Page.getCurrent());
					}
				} else {
					new Notification("Problem with resolving artifact", Notification.Type.WARNING_MESSAGE)
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