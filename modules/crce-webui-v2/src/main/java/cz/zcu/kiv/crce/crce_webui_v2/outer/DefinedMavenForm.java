package cz.zcu.kiv.crce.crce_webui_v2.outer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.themes.ValoTheme;

import cz.zcu.kiv.crce.crce_external_repository.api.ArtifactTree;
import cz.zcu.kiv.crce.crce_external_repository.api.DefinedMaven;
import cz.zcu.kiv.crce.crce_external_repository.api.SettingsUrl;
import cz.zcu.kiv.crce.crce_webui_v2.internal.Activator;
import cz.zcu.kiv.crce.crce_webui_v2.repository.classes.ResourceBean;
import cz.zcu.kiv.crce.crce_webui_v2.repository.services.ResourceService;
import cz.zcu.kiv.crce.crce_webui_v2.webui.MyUI;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;

public class DefinedMavenForm extends FormLayout{
	private static final long serialVersionUID = 4172878715304331198L;
	private transient DefinedMaven definedMaven;
	private Panel formPanel = new Panel("Content");
	private TextField definedUrl = new TextField();
	private Label caption = new Label("Defined Maven repository");
	private TextField group = new TextField("Group Id");
	private TextField artifact = new TextField("Artifact Id");
	private TextField version = new TextField("Version");
	private TextField packaging = new TextField("Packaging");
	private Button searchButton = new Button("Search");
	private Button clearButton = new Button("Clear");
	private Button uploadButton = new Button("Upload");
	private Button resolveButton = new Button("Resolve");
	private Panel treePanel = new Panel("Result list");
	private Label notFound = new Label("No artifact found");
	private Tree tree;
	private VerticalLayout bufferLayout = new VerticalLayout();
	private Panel bufferPanel = new Panel("Buffer");
	private Grid bufferGrid = new Grid();
	private ResourceService resourceService;
	
	public DefinedMavenForm() {
		HorizontalLayout content = new HorizontalLayout();
		addComponent(content);
	}

	public DefinedMavenForm(MyUI myUI) {
		if (myUI.getSession().getAttribute("settingsUrl") == null) {
			SettingsUrl settings = new SettingsUrl();
			definedUrl.setValue(settings.getExternalAetherUrl());
		} else {
			definedUrl.setValue(((SettingsUrl) myUI.getSession().getAttribute("settingsUrl")).getExternalAetherUrl());
		}

		VerticalLayout fieldLayout = new VerticalLayout();
		HorizontalLayout treePanelLayout = new HorizontalLayout();
		VerticalLayout treePanelButtonLayout = new VerticalLayout();
		HorizontalLayout buttonUploadResolveLayout = new HorizontalLayout();
		HorizontalLayout treeLayout = new HorizontalLayout();
		VerticalLayout formLayout = new VerticalLayout();
		HorizontalLayout contentForm = new HorizontalLayout();
		VerticalLayout content = new VerticalLayout();

		caption.addStyleName(ValoTheme.LABEL_BOLD);

		definedUrl.setWidth("450px");

		group.setRequired(true);
		group.setRequiredError("The item can not be empty!");
		artifact.setRequired(true);
		artifact.setRequiredError("The item can not be empty!");
		packaging.setRequired(true);
		packaging.setRequiredError("The item can not be empty!");

		searchButton.setStyleName(ValoTheme.BUTTON_PRIMARY);

		HorizontalLayout buttonsSearchClearLayout = new HorizontalLayout(searchButton, clearButton);
		buttonsSearchClearLayout.setSpacing(true);

		Button setUrl = new Button("Set");
		CssLayout definedCss = new CssLayout();
		definedCss.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		definedCss.addComponents(definedUrl, setUrl);

		fieldLayout.addComponents(group, artifact, version, packaging, buttonsSearchClearLayout);
		fieldLayout.setSpacing(true);
		fieldLayout.setMargin(new MarginInfo(false, true, false, false));

		treeLayout.setMargin(true);
		treePanel.setContent(treeLayout);
		treePanel.setWidth("600px");
		treePanel.setHeight("283px");
		treePanel.setVisible(false);
		buttonUploadResolveLayout.setVisible(false);

		buttonUploadResolveLayout.addComponents(resolveButton, uploadButton);
		buttonUploadResolveLayout.setSpacing(true);

		treePanelButtonLayout.addComponents(treePanel, buttonUploadResolveLayout);
		treePanelButtonLayout.setSpacing(true);
		treePanelButtonLayout.setComponentAlignment(buttonUploadResolveLayout, Alignment.BOTTOM_CENTER);

		treePanelLayout.addComponents(fieldLayout, treePanelButtonLayout);
		treePanelLayout.setSpacing(true);
		
		formLayout.addComponents(definedCss, treePanelLayout);
		formLayout.setSpacing(true);
		formLayout.setMargin(true);
		formLayout.addComponents(definedCss, treePanelLayout);
		
		formPanel.setContent(formLayout);
		formPanel.setHeight("500px");

		contentForm.addComponents(formPanel);
		
		resourceService = new ResourceService(Activator.instance().getMetadataService());
		List<ResourceBean> resourceBeanList = resourceService.getAllResourceBeanFromBuffer(myUI.getSession().getSession());
		
		HorizontalLayout bufferPanelLayout = new HorizontalLayout();
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
		
		if(!resourceBeanList.isEmpty()) {
			bufferPanel.setVisible(true);
		}
		else {
			bufferPanel.setVisible(false);
		}
		
		content.addComponents(caption, contentForm, bufferPanel);
		
		contentForm.setSpacing(true);
		contentForm.setSizeFull();
		content.setSpacing(true);
		content.setMargin(new MarginInfo(false, true));

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
			packaging.clear();
			// packaging.select(TypePackaging.jar);
			// erasing any previous components shown
			if (treeLayout.getComponentIndex(notFound) != -1) {
				treeLayout.removeComponent(notFound);
			}
			if (treeLayout.getComponentIndex(tree) != -1) {
				treeLayout.removeComponent(tree);
			}
			treePanel.setVisible(false);
			buttonUploadResolveLayout.setVisible(false);
		});

		// search artefact from defined repository
		searchButton.addClickListener(e1 -> {
			if (group.getValue().trim().isEmpty() || artifact.getValue().trim().isEmpty()
					|| packaging.getValue().trim().isEmpty()) {
				Notification notif = new Notification("Incomplete assignment!", Notification.Type.WARNING_MESSAGE);
				notif.setDelayMsec(5000);
				notif.show(Page.getCurrent());
			} else {
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

				// předání hodnot
				definedMaven = new DefinedMaven(settings);
				ArtifactTree definedArtefact = definedMaven.getArtifact(group.getValue(), artifact.getValue(),
						version.getValue(), packaging.getValue());
				if (definedArtefact == null) {
					treeLayout.addComponent(notFound);
				} else {
					tree = new Tree();
					String[] pom = definedArtefact.getGroupId().split("\\.");
					tree.addItem(pom[0]);
					for (int i = 1; i < pom.length; i++) {
						tree.addItem(pom[i]);
						tree.setParent(pom[i], pom[i - 1]);
					}

					if (definedArtefact.getVersions().size() > 1) {
						for (String s : definedArtefact.getVersions()) {
							addArtefactToTree(definedArtefact.getGroupId(), definedArtefact.getArtefactId(), s,
									definedArtefact.getPackaging(), pom[pom.length - 1]);
						}
					} else {
						addArtefactToTree(definedArtefact.getGroupId(), definedArtefact.getArtefactId(),
								definedArtefact.getVersions().get(0), definedArtefact.getPackaging(),
								pom[pom.length - 1]);
					}

					tree.addExpandListener(e2 -> {
						buttonUploadResolveLayout.setVisible(true);
					});

					tree.addCollapseListener(e3 -> {
						buttonUploadResolveLayout.setVisible(false);
					});

					treeLayout.addComponent(tree);
				}
				treePanel.setVisible(true);
			}
		});

		uploadButton.addClickListener(e -> {
			SettingsUrl settings;
			if (myUI.getSession().getAttribute("settingsUrl") == null) {
				settings = new SettingsUrl();
			} else {
				settings = (SettingsUrl) myUI.getSession().getAttribute("settingsUrl");
			}
			// First check the artifact storage in the local repository
			if (tree.getValue() != null && tree.getValue().toString().contains(":")) {
				String artifactText = tree.getValue().toString();
				if (DefinedMaven.resolveArtifact(artifactText, settings)) {
					File file = new File(settings.getLocalAetherUrl() + "/"
							+ artifactText.split(":")[0].replace('.', '/') + "/" + artifactText.split(":")[1] + "/"
							+ artifactText.split(":")[2] + "/" + artifactText.split(":")[1] + "-"
							+ artifactText.split(":")[2] + "." + artifactText.split(":")[3]);
					try {
						InputStream is = Files.newInputStream(file.toPath(), StandardOpenOption.READ);
						try {
							Activator.instance().getBuffer(myUI.getSession().getSession()).put(file.getName(), is);
							bufferLayout.removeComponent(bufferGrid);
							bufferGrid.setContainerDataSource(new BeanItemContainer<>(ResourceBean.class, 
									resourceService.getAllResourceBeanFromBuffer(myUI.getSession().getSession())));
							bufferLayout.addComponent(bufferGrid);
							bufferPanel.setVisible(true);
							Notification notif = new Notification("Info", "Artifact to buffer upload sucess",
									Notification.Type.ASSISTIVE_NOTIFICATION);
							notif.setPosition(Position.TOP_RIGHT);
							notif.show(Page.getCurrent());
						} finally {
							is.close();
						}
					} catch (IOException | RefusedArtifactException ex) {
						new Notification("Could not open file", ex.getMessage(), Notification.Type.ERROR_MESSAGE)
								.show(Page.getCurrent());
					}
				} else {
					new Notification("Problem with resolving artifact", Notification.Type.WARNING_MESSAGE)
							.show(Page.getCurrent());
				}
			} else {
				new Notification("No artefact selected", Notification.Type.WARNING_MESSAGE).show(Page.getCurrent());
			}
		});

		resolveButton.addClickListener(e -> {
			SettingsUrl settings;
			if (myUI.getSession().getAttribute("settingsUrl") == null) {
				settings = new SettingsUrl();
			} else {
				settings = (SettingsUrl) myUI.getSession().getAttribute("settingsUrl");
			}
			// First check the artifact storage in the local repository
			if (tree.getValue() != null && tree.getValue().toString().contains(":")) {
				String artifactText = tree.getValue().toString();
				if (DefinedMaven.resolveArtifact(artifactText, settings)) {
					Notification notif = new Notification("Info", "Artefact sucess resolved to Maven local repository.",
							Notification.Type.ASSISTIVE_NOTIFICATION);
					notif.setPosition(Position.TOP_RIGHT);
					notif.show(Page.getCurrent());
				} else {
					new Notification("Problem with resolving artifact", Notification.Type.WARNING_MESSAGE)
							.show(Page.getCurrent());
				}
			} else {
				new Notification("No artefact selected", Notification.Type.WARNING_MESSAGE).show(Page.getCurrent());
			}
		});

		addComponent(content);
	}

	private void addArtefactToTree(String group, String artifact, String version, String packaging, String parent) {
		tree.addItem(artifact);
		tree.setParent(artifact, parent);
		tree.addItem(version);
		tree.setParent(version, artifact);

		// konečný artefact je komplet url link např. pro wget - UPRAVIT DLE
		// POTŘEBY
		/*
		 * String artifactText = settings.getExternalAetherUrl() + "/" + groupText + "/"
		 * + idText + "/" + version + "." + packagingText;
		 */

		String artifactText = group + ":" + artifact + ":" + version + ":" + packaging;

		tree.addItem(artifactText);
		tree.setParent(artifactText, version.toString());
		tree.setItemCaption(artifactText, artifact + "-" + version + "." + packaging);
		tree.setChildrenAllowed(artifactText, false);
		if (packaging.equals("jar") || packaging.equals("war")) {
			tree.setItemIcon(artifactText, FontAwesome.GIFT);
		} else if (packaging.equals("xml") || packaging.equals("pom")) {
			tree.setItemIcon(artifactText, FontAwesome.CODE);
		} else {
			tree.setItemIcon(artifactText, FontAwesome.FILE);
		}
	}
}