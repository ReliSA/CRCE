package cz.zcu.kiv.crce.crce_webui_vaadin.repository;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cz.zcu.kiv.crce.crce_webui_vaadin.internal.Activator;
import cz.zcu.kiv.crce.plugin.Plugin;

@SuppressWarnings("serial")
public class PluginsForm extends FormLayout{
	private Label labelForm = new Label("Plugins");
	private Grid gridPlugins = new Grid();
	private HorizontalLayout formLayout;
	
	public PluginsForm(){
		VerticalLayout content = new VerticalLayout();
		VerticalLayout fieldLayout = new VerticalLayout();
		
		labelForm.addStyleName(ValoTheme.LABEL_BOLD);
		
		gridPlugins.setContainerDataSource(new BeanItemContainer<>(Plugin.class, Activator.instance().getPluginManager().getPlugins()));
		gridPlugins.getColumn("pluginKeywords").setHidden(true);
		gridPlugins.getColumn("pluginId").setHidable(true);
		gridPlugins.getColumn("pluginDescription").setHidable(true);
		gridPlugins.getColumn("pluginPriority").setHidable(true);
		gridPlugins.getColumn("pluginVersion").setHidable(true);
		gridPlugins.setColumnOrder("pluginId", "pluginDescription");
		gridPlugins.addStyleName("my-style");
		
		fieldLayout.addComponents(labelForm, gridPlugins);
		fieldLayout.setSpacing(true);
		
		formLayout = new HorizontalLayout(fieldLayout);
		formLayout.setSizeFull();
		gridPlugins.setSizeFull();
		formLayout.setExpandRatio(fieldLayout, 1);
		
		content.addComponent(formLayout);
		content.setMargin(new MarginInfo(false, true));
		content.setSpacing(true);
		addComponent(content);
	}
}
