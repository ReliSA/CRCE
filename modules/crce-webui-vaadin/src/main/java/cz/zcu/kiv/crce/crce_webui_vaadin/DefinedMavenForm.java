package cz.zcu.kiv.crce.crce_webui_vaadin;

import java.io.Serializable;
import java.util.EnumSet;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import cz.zcu.kiv.crce.crce_webui_vaadin.classes.DefinedMaven;
import cz.zcu.kiv.crce.crce_webui_vaadin.other.TypePackaging;
import cz.zcu.kiv.crce.external.web.impl.SettingsUrl;

public class DefinedMavenForm extends FormLayout implements Serializable{
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

	public DefinedMavenForm(VaadinSession session) {
		// settings url
		if (session.getAttribute("settingsUrl") == null) {
			SettingsUrl settings = new SettingsUrl();
			definedUrl.setValue(settings.getExternalAetherUrl());
		} else {
			definedUrl.setValue(((SettingsUrl) session.getAttribute("settingsUrl")).getExternalAetherUrl());
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
		searchButton.setClickShortcut(KeyCode.ENTER);

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
			if (session.getAttribute("settingsUrl") == null) {
				settings = new SettingsUrl();
				settings.setExternalAetherUrl(definedUrl.getValue());
				getSession().setAttribute("settingsUrl", settings);
			} else {
				settings = (SettingsUrl) session.getAttribute("settingsUrl");
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
			if (session.getAttribute("settingsUrl") == null) {
				settings = new SettingsUrl();
			} else {
				settings = (SettingsUrl) session.getAttribute("settingsUrl");
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
			}
		});

		content.setSpacing(true);
		addComponent(content);
	}
}
