package cz.zcu.kiv.crce.crce_webui_vaadin.repository;

import java.util.ArrayList;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cz.zcu.kiv.crce.crce_webui_vaadin.other.KeyWord;
import cz.zcu.kiv.crce.plugin.Plugin;

@SuppressWarnings("serial")
public class PluginEditForm extends FormLayout{
	private int nameMaxLenght = 45;
	private Label labelKeyWords = new Label("Plugin key words");
	private Grid gridKeyWords = new Grid();
	private TextField priority = new TextField("Priority");
	private TextField version = new TextField("Version");
	private Button editButton = new Button("Edit");
	private Plugin plugin;
	public PluginEditForm(PluginsForm pluginsForm){
		HorizontalLayout content = new HorizontalLayout();
		VerticalLayout editLayout = new VerticalLayout();
		VerticalLayout keyWordLayout = new VerticalLayout();
		
		setSizeUndefined();
		setMargin(false);
		
		labelKeyWords.addStyleName(ValoTheme.LABEL_BOLD);
		keyWordLayout.addComponents(labelKeyWords, gridKeyWords);
		keyWordLayout.setSpacing(true);
		keyWordLayout.setVisible(false);
		
		gridKeyWords.setWidth("200px");
		gridKeyWords.addStyleName("my-style");
		
		editButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		editButton.setClickShortcut(KeyCode.ENTER);
		
		priority.setWidth("200px");
		priority.setRequired(true);
		priority.setRequiredError("Item can not be empty!");
		priority.addValidator(new StringLengthValidator("Item must have " + nameMaxLenght + " characters!", 1,
				nameMaxLenght, false));
		version.setWidth("200px");
		version.setRequired(true);
		version.setRequiredError("Item can not be empty!");
		version.addValidator(new StringLengthValidator("Item must have " + nameMaxLenght + " characters!", 1,
				nameMaxLenght, false));
		
		editLayout.addComponents(priority, version, editButton);
		editLayout.setSpacing(true);
		
		content.addComponents(keyWordLayout, editLayout);
		content.setSpacing(true);
		
		editButton.addClickListener(e -> {
			//TODO doplnit
			
			pluginsForm.update();
		});
		
		addComponent(content);
	}
	
	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
		if(!this.plugin.getPluginKeywords().isEmpty()){
			labelKeyWords.setVisible(true);
			ArrayList<KeyWord> keyWordList = new ArrayList<KeyWord>();
			for(String s : this.plugin.getPluginKeywords()){
				keyWordList.add(new KeyWord(s));
			}
			gridKeyWords.setContainerDataSource(new BeanItemContainer<>(KeyWord.class, keyWordList));
		}
		priority.setValue(plugin.getPluginPriority() + "");
		version.setValue(plugin.getPluginVersion().toString());
		setVisible(true);
	}
}