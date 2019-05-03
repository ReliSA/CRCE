package cz.zcu.kiv.crce.crce_webui_v2.outer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.themes.ValoTheme;

import cz.zcu.kiv.crce.crce_external_repository.api.ArtifactTree;
import cz.zcu.kiv.crce.crce_external_repository.api.CentralMaven;
import cz.zcu.kiv.crce.crce_external_repository.api.ResultSearchArtifactTree;
import cz.zcu.kiv.crce.crce_external_repository.api.SettingsUrl;
import cz.zcu.kiv.crce.crce_webui_v2.internal.Activator;
import cz.zcu.kiv.crce.crce_webui_v2.repository.classes.ResourceBean;
import cz.zcu.kiv.crce.crce_webui_v2.repository.services.ResourceService;
import cz.zcu.kiv.crce.crce_webui_v2.webui.MyUI;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;

/**
 * User dialog for operations over the central Maven repository.
 * <p/>
 * Date: 02.05.19
 *
 * @author Roman Pesek
 */
@SuppressWarnings("serial")
public class CentralMavenForm extends FormLayout {
	private Label caption = new Label("Central Maven repository");
	private Panel formPanel = new Panel("Content");
	private TextField group = new TextField("Group Id");
	private TextField artifact = new TextField("Artifact Id");
	private TextField version = new TextField("Version");
	private TextField packaging = new TextField("Packaging");
	private OptionGroup directIndexOption = new OptionGroup("Direct or search index");
	private NativeSelect rangeOption = new NativeSelect("LimitRange");
	private Button searchButton = new Button("Search");
	private Button clearButton = new Button("Clear");
	private Label notFound = new Label("No artifact found");
	private Panel treePanel = new Panel("Result list");
	private Button uploadButton = new Button("Upload");
	private Tree tree = new Tree();
	private VerticalLayout bufferLayout = new VerticalLayout();
	private Panel bufferPanel = new Panel("Buffer");
	private Grid bufferGrid = new Grid();
	private ResourceService resourceService;

	public CentralMavenForm() {
		HorizontalLayout content = new HorizontalLayout();
		addComponent(content);
	}

	public CentralMavenForm(MyUI myUI) {
		VerticalLayout userForm = new VerticalLayout();
		HorizontalLayout versionLayout = new HorizontalLayout();
		VerticalLayout formPanelButtonLayout = new VerticalLayout();
		HorizontalLayout contentForm = new HorizontalLayout();
		VerticalLayout content = new VerticalLayout();

		caption.addStyleName(ValoTheme.LABEL_BOLD);

		group.setWidth("250px");
		artifact.setWidth("250px");

		directIndexOption.addItems("Direct", "Index");
		directIndexOption.setValue("Direct");

		if (myUI.getSession().getAttribute("mavenIndex") == null
				|| myUI.getSession().getAttribute("settingsUrl") != null
						&& ((SettingsUrl) myUI.getSession().getAttribute("settingsUrl")).isEnableGroupSearch()) {
			directIndexOption.setItemEnabled("Index", false);
			directIndexOption.setDescription(
					"The central maven repository index is not created or it is allowed to search only by group. Check the settings menu");
		}

		group.setRequired(true);
		group.setRequiredError("The item can not be empty!");

		if (myUI.getSession().getAttribute("settingsUrl") == null
				|| !((SettingsUrl) myUI.getSession().getAttribute("settingsUrl")).isEnableGroupSearch()) {
			artifact.setRequired(true);
			artifact.setRequiredError("The item can not be empty!");
			version.setRequired(true);
			version.setRequiredError("The item can not be empty!");
			packaging.setRequired(true);
			packaging.setRequiredError("The item can not be empty!");
		}

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
		userForm.addComponents(group, artifact, versionLayout, packaging, directIndexOption, buttons);
		userForm.setSpacing(true);

		treePanel.setWidth("600px");
		treePanel.setHeight("380px");

		contentForm.addComponent(userForm);
		contentForm.setMargin(true);
		formPanel.setContent(contentForm);
		formPanel.setHeight("570px");
		
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

		// Add tree
		searchButton.addClickListener(e -> {
			// clear tree
			tree.removeAllItems(); // no function tree.clear();
			// erasing any previous components shown
			if (contentForm.getComponentIndex(formPanelButtonLayout) != -1) {
				contentForm.removeComponent(formPanelButtonLayout);
			}
			// check filling form
			if (myUI.getSession().getAttribute("settingsUrl") == null
					&& (group.getValue().trim().isEmpty() || artifact.getValue().trim().isEmpty()
							|| version.getValue().trim().isEmpty() || packaging.getValue().trim().isEmpty())) {
				Notification notif = new Notification("Incomplete assignment!", Notification.Type.WARNING_MESSAGE);
				notif.setDelayMsec(5000);
				notif.show(Page.getCurrent());
			} else if (myUI.getSession().getAttribute("settingsUrl") != null
					&& !((SettingsUrl) myUI.getSession().getAttribute("settingsUrl")).isEnableGroupSearch()
					&& (group.getValue().trim().isEmpty() || artifact.getValue().trim().isEmpty()
							|| version.getValue().trim().isEmpty() || packaging.getValue().trim().isEmpty())) {
				Notification notif = new Notification("Incomplete assignment!", Notification.Type.WARNING_MESSAGE);
				notif.setDelayMsec(5000);
				notif.show(Page.getCurrent());
			} else if (myUI.getSession().getAttribute("settingsUrl") != null
					&& ((SettingsUrl) myUI.getSession().getAttribute("settingsUrl")).isEnableGroupSearch()
					&& group.getValue().trim().isEmpty()) {
				Notification notif = new Notification("Incomplete assignment!", Notification.Type.WARNING_MESSAGE);
				notif.setDelayMsec(5000);
				notif.show(Page.getCurrent());
			} else {
				// check exist component from central Maven repository
				if (directIndexOption.getValue().equals("Direct")) {
					ResultSearchArtifactTree artifactList = new CentralMaven(
							(SettingsUrl) myUI.getSession().getAttribute("settingsUrl")).getArtifactTree(
									group.getValue(), artifact.getValue(), version.getValue(), packaging.getValue(),
									directIndexOption.getValue(), null);
					if (artifactList.getArtifactTreeList() != null && artifactList.getStatus().equals("direct")) {
						ArtifactTree artifactTree = artifactList.getArtifactTreeList().get(0);
						String[] pom = artifactTree.getGroupId().split("\\.");
						tree.addItem(pom[0]);
						for (int i = 1; i < pom.length; i++) {
							tree.addItem(pom[i]);
							tree.setParent(pom[i], pom[i - 1]);
						}
						// addArtifactToTree(artifactTree, pom[pom.length - 1]);
						addArtifactToTree(artifactTree.getUrl(), artifactTree.getGroupId(),
								artifactTree.getArtefactId(), artifactTree.getVersions().get(0),
								artifactTree.getPackaging(), pom[pom.length - 1]);

						// for margin
						HorizontalLayout treePanelLayout = new HorizontalLayout();
						treePanelLayout.addComponent(tree);
						treePanelLayout.setMargin(true);
						treePanel.setContent(treePanelLayout);
						formPanelButtonLayout.addComponents(treePanel, uploadButton);
						formPanelButtonLayout.setMargin(new MarginInfo(false, true));
						formPanelButtonLayout.setSpacing(true);
						formPanelButtonLayout.setComponentAlignment(uploadButton, Alignment.BOTTOM_CENTER);
						uploadButton.setVisible(false);
					} else if (artifactList.getArtifactTreeList() != null && artifactList.getStatus().equals("group")) {
						for (ArtifactTree artifactTree : artifactList.getArtifactTreeList()) {
							String[] pom = artifactTree.getGroupId().split("/");
							String uniqueItemChildren, uniqueItemParent = "/";

							/*
							 * for central maven2 url - the correct is the listing over all items, but in
							 * this case the first is empty and the second is maven2
							 */
							for (int i = 2; i < pom.length; i++) {
								uniqueItemChildren = uniqueItemParent + pom[i] + "/";
								if (tree.getItem(uniqueItemChildren) == null) {
									tree.addItem(uniqueItemChildren);
									tree.setItemCaption(uniqueItemChildren, pom[i]);
									tree.setParent(uniqueItemChildren, uniqueItemParent);
								}
								uniqueItemParent = uniqueItemChildren;
							}
							addArtefactToTreeGroup(artifactTree, uniqueItemParent);
						}
						// for margin
						HorizontalLayout treePanelLayout = new HorizontalLayout();
						treePanelLayout.addComponent(tree);
						treePanelLayout.setMargin(true);
						treePanel.setContent(treePanelLayout);
						formPanelButtonLayout.addComponents(treePanel, uploadButton);
						//formPanelButtonLayout.setMargin(true);
						formPanelButtonLayout.setSpacing(true);
						formPanelButtonLayout.setComponentAlignment(uploadButton, Alignment.BOTTOM_CENTER);
						uploadButton.setVisible(false);
					} else {
						// for margin
						HorizontalLayout treePanelLayout = new HorizontalLayout();
						treePanelLayout.addComponent(notFound);
						treePanelLayout.setMargin(true);
						treePanel.setContent(treePanelLayout);
						formPanelButtonLayout.addComponents(treePanel);
						formPanelButtonLayout.setMargin(new MarginInfo(false, true));
					}
				}
				// index search
				else if (group.getValue().charAt(0) == '*' || group.getValue().charAt(0) == '?'
						|| artifact.getValue().charAt(0) == '*' || artifact.getValue().charAt(0) == '?') {
					Notification notif = new Notification("Can not start a search term with '*' or '?'",
							Notification.Type.WARNING_MESSAGE);
					notif.setDelayMsec(5000);
					notif.show(Page.getCurrent());
				} else {
					ResultSearchArtifactTree artifactList = new CentralMaven(
							(SettingsUrl) myUI.getSession().getAttribute("settingsUrl")).getArtifactTree(
									group.getValue(), artifact.getValue(), version.getValue(), packaging.getValue(),
									directIndexOption.getValue(), rangeOption.getValue().toString());
					if (artifactList.getStatus().equals("found")) {
						for (ArtifactTree artifactTree : artifactList.getArtifactTreeList()) {
							String[] pom = artifactTree.getGroupId().split("\\.");
							tree.addItem(pom[0]);
							for (int i = 1; i < pom.length; i++) {
								tree.addItem(pom[i]);
								tree.setParent(pom[i], pom[i - 1]);
							}
							for (String s : artifactTree.getVersions()) {
								addArtifactToTree(artifactTree.getUrl(), artifactTree.getGroupId(),
										artifactTree.getArtefactId(), s, artifactTree.getPackaging(),
										pom[pom.length - 1]);
							}
						}
						// for margin
						HorizontalLayout treePanelLayout = new HorizontalLayout();
						treePanelLayout.addComponent(tree);
						treePanelLayout.setMargin(true);
						treePanel.setContent(treePanelLayout);
						formPanelButtonLayout.addComponents(treePanel, uploadButton);
						formPanelButtonLayout.setMargin(new MarginInfo(false, true));
						formPanelButtonLayout.setSpacing(true);
						formPanelButtonLayout.setComponentAlignment(uploadButton, Alignment.BOTTOM_CENTER);
						uploadButton.setVisible(false);
					} else if (artifactList.getStatus().equals("notfound")) {
						HorizontalLayout treePanelLayout = new HorizontalLayout();
						treePanelLayout.addComponent(notFound);
						treePanelLayout.setMargin(true);
						treePanel.setContent(treePanelLayout);
						formPanelButtonLayout.addComponents(treePanel);
						formPanelButtonLayout.setMargin(new MarginInfo(false, true));
					}
				}
			}
			contentForm.addComponent(formPanelButtonLayout);
			contentForm.setSpacing(true);
		});

		tree.addCollapseListener(e -> {
			uploadButton.setVisible(false);
		});

		tree.addExpandListener(e -> {
			uploadButton.setVisible(true);
		});

		uploadButton.addClickListener(e -> {
			if (tree.getValue() != null && !(tree.areChildrenAllowed((Object) tree.getValue()))) {
				File file;
				try {
					URL url = new URL(tree.getValue().toString());
					file = new File(url.toString());
					InputStream input = url.openStream();
					Activator.instance().getBuffer(myUI.getSession().getSession()).put(file.getName(), input);
					bufferLayout.removeComponent(bufferGrid);
					bufferGrid.setContainerDataSource(new BeanItemContainer<>(ResourceBean.class, 
							resourceService.getAllResourceBeanFromBuffer(myUI.getSession().getSession())));
					bufferLayout.addComponent(bufferGrid);
					bufferPanel.setVisible(true);
					Notification notif = new Notification("Info", "Artefact from central maven upload sucess",
							Notification.Type.ASSISTIVE_NOTIFICATION);
					notif.setPosition(Position.TOP_RIGHT);
					notif.show(Page.getCurrent());
				} catch (IOException | RefusedArtifactException ex) {
					new Notification("Could not open or load file from url", ex.getMessage(),
							Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
				}
			} else {
				new Notification("No artefact selected", Notification.Type.WARNING_MESSAGE).show(Page.getCurrent());
			}
		});

		directIndexOption.addValueChangeListener(e -> {
			if (directIndexOption.getValue().equals("Direct")) {
				rangeOption.setEnabled(false);
			} else {
				rangeOption.setEnabled(true);
			}
		});

		// Clear user form
		clearButton.addClickListener(e -> {
			formPanelButtonLayout.removeAllComponents();
			contentForm.removeAllComponents();
			group.clear();
			artifact.clear();
			version.clear();
			packaging.clear();
			tree.removeAllItems();
			contentForm.addComponent(userForm);
		});

		
		content.addComponents(caption, formPanel, bufferPanel);
		content.setSpacing(true);
		content.setMargin(new MarginInfo(false, true));
		
		addComponent(content);
	}

	private void addArtifactToTree(String url, String group, String artifact, String version, String packaging,
			String parent) {
		String uniqueVersion = artifact + version;

		tree.addItem(artifact);
		tree.setParent(artifact, parent);
		tree.addItem(uniqueVersion);
		tree.setItemCaption(uniqueVersion, version);
		tree.setParent(uniqueVersion, artifact);

		// ultimate artefact is a complete url link eg for wget - EDIT BY NEEDS
		String artifactText = url + group.replace('.', '/') + "/" + artifact + "/" + version + "/" + artifact + "-"
				+ version + "." + packaging;

		tree.addItem(artifactText);
		tree.setParent(artifactText, uniqueVersion);
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

	private void addArtefactToTreeGroup(ArtifactTree artifactTree, String parent) {
		// ultimate artefact is a complete url link eg for wget - EDIT BY NEEDS
		String artifactText = artifactTree.getArtefactId();
		String artifactUrl = artifactTree.getUrl();

		tree.addItem(artifactUrl);
		tree.setParent(artifactUrl, parent);
		tree.setItemCaption(artifactUrl, artifactText);
		tree.setChildrenAllowed(artifactUrl, false);
		if (artifactTree.getPackaging().equals("jar") || artifactTree.getPackaging().equals("war")) {
			tree.setItemIcon(artifactUrl, FontAwesome.GIFT);
		} else if (artifactTree.getPackaging().equals("xml") || artifactTree.getPackaging().equals("pom")) {
			tree.setItemIcon(artifactUrl, FontAwesome.CODE);
		} else {
			tree.setItemIcon(artifactUrl, FontAwesome.FILE);
		}
	}
}
