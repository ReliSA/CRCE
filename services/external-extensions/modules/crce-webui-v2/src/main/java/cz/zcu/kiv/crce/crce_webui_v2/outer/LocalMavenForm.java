package cz.zcu.kiv.crce.crce_webui_v2.outer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Tree;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.VerticalLayout;

import cz.zcu.kiv.crce.crce_external_repository.api.impl.SettingsUrl;
import cz.zcu.kiv.crce.crce_webui_v2.internal.Activator;
import cz.zcu.kiv.crce.crce_webui_v2.outer.classes.LocalMaven;
import cz.zcu.kiv.crce.crce_webui_v2.repository.classes.ResourceBean;
import cz.zcu.kiv.crce.crce_webui_v2.repository.services.ResourceService;
import cz.zcu.kiv.crce.crce_webui_v2.webui.MyUI;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;

/**
 * User dialog for operations over the local Maven repository.
 * <p/>
 * Date: 02.05.19
 *
 * @author Roman Pesek
 */
@SuppressWarnings("serial")
public class LocalMavenForm extends FormLayout {
	private LocalMaven localMaven = new LocalMaven();
	private Label caption = new Label("Local Maven repository");
	private Panel formPanel = new Panel("Content");
	private VerticalLayout bufferLayout = new VerticalLayout();
	private Panel bufferPanel = new Panel("Buffer");
	private Grid bufferGrid = new Grid();
	private ResourceService resourceService;
	private Button uploadButton = new Button("Upload");
	private Button removeButton = new Button("Remove");

	public LocalMavenForm() {
		HorizontalLayout content = new HorizontalLayout();
		addComponent(content);
	}

	public LocalMavenForm(MyUI myUI) {
		VerticalLayout content = new VerticalLayout();
		caption.addStyleName(ValoTheme.LABEL_BOLD);

		VerticalLayout formLayout = new VerticalLayout();
		HorizontalLayout buttons = new HorizontalLayout();

		removeButton.setStyleName(ValoTheme.BUTTON_DANGER);

		if (myUI.getSession().getAttribute("settingsUrl") == null
				|| !((SettingsUrl) myUI.getSession().getAttribute("settingsUrl")).isEnableDeleteLocalMaven()) {
			buttons.addComponent(uploadButton);
		} else {
			buttons.addComponents(uploadButton, removeButton);
		}

		buttons.setSpacing(true);
		buttons.setVisible(false);

		// Tree of Maven local repository
		Tree localMavenTree = localMaven.getTree(myUI.getSession());
		content.setMargin(new MarginInfo(false, true));
		if (localMavenTree == null) {
			content.addComponent(new Label("Local Maven repository not found"));
		} else {
			HorizontalLayout treeLayout = new HorizontalLayout();
			treeLayout.addComponent(localMavenTree);
			treeLayout.setMargin(true);
			formPanel.setContent(treeLayout);
			formPanel.setHeight("500px");
			
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
			
			formLayout.addComponents(caption, formPanel, buttons, bufferPanel);
			
			if(!resourceBeanList.isEmpty()) {
				bufferPanel.setVisible(true);
			}
			else {
				bufferPanel.setVisible(false);
			}
			
			formLayout.setSpacing(true);
			formLayout.setComponentAlignment(buttons, Alignment.BOTTOM_CENTER);
			content.addComponents(formLayout);
			content.setSpacing(true);

			localMavenTree.addItemClickListener(e -> {
				buttons.setVisible(true);
			});

			removeButton.addClickListener(e -> {
				if (localMavenTree.getValue() != null) {
					File file = new File(localMavenTree.getValue().toString());
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
					} finally {
						myUI.setContentBodyLocalMaven();
					}
				}
			});

			uploadButton.addClickListener(e -> {
				if (localMavenTree.getValue() != null) {
					if (!localMavenTree.areChildrenAllowed((Object) localMavenTree.getValue())) {
						try {
							File file = new File(localMavenTree.getValue().toString());
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
						new Notification("No artifact is selected for upload!", Notification.Type.WARNING_MESSAGE)
								.show(Page.getCurrent());
					}
				}
			});
		}
		addComponent(content);
	}
}
