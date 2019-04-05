package cz.zcu.kiv.crce.crce_webui_v2.webui;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.zcu.kiv.crce.crce_component_collection.api.impl.LimitRange;
import cz.zcu.kiv.crce.crce_component_collection.api.settings.SettingsLimitRange;

public class SettingRangeForm extends FormLayout {
    public SettingRangeForm(){
        HorizontalLayout content = new HorizontalLayout();
        addComponent(content);
    }

    public SettingRangeForm(VaadinSession session){
        TextField exportPath = new TextField("Export path");
        exportPath.setWidth("300px");
        OptionGroup exportRangeArtifact = new OptionGroup("Select to export artifacts from a defined range");
        exportRangeArtifact.addItems(LimitRange.MAX, LimitRange.MIN);
        exportRangeArtifact.setItemCaption(LimitRange.MAX, "Only max");
        exportRangeArtifact.setItemCaption(LimitRange.MIN, "Only min");
        CheckBox exportWithMetadata = new CheckBox("Export artifact whit metadata");

        if(session.getAttribute("exportArtifactRange") == null){
            exportPath.setValue("tmp");
            exportRangeArtifact.select(LimitRange.MAX);
            exportWithMetadata.setValue(false);
        }
        else{
            exportPath.setValue(((SettingsLimitRange) session.getAttribute("exportArtifactRange")).getExportPath());
            exportRangeArtifact.select(((SettingsLimitRange) session.getAttribute("exportArtifactRange")).getExportArtifactRange());
            exportWithMetadata.setValue(((SettingsLimitRange) session.getAttribute("exportArtifactRange")).isExportArtifactWithMetadata());
        }

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        Button saveButton = new Button("Save");
        Button defaultButton = new Button("Default");
        buttonsLayout.addComponents(saveButton,defaultButton);
        buttonsLayout.setSpacing(true);

        saveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        VerticalLayout content = new VerticalLayout();
        content.addComponents(exportPath ,exportRangeArtifact, exportWithMetadata, buttonsLayout);

        content.setSpacing(true);

        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.addComponent(content);
        formLayout.setMargin(new MarginInfo(false, true));

        saveButton.addClickListener(e -> {
            SettingsLimitRange settingsRange;
            if(getSession().getAttribute("exportArtifactRange") == null){
                settingsRange = new SettingsLimitRange();
            }
            else{
                settingsRange = (SettingsLimitRange) getSession().getAttribute("exportArtifactRange");
            }
            settingsRange.setExportPath(exportPath.getValue());
            settingsRange.setExportArtifactRange((LimitRange) exportRangeArtifact.getValue());
            settingsRange.setExportArtifactWithMetadata(exportWithMetadata.getValue());
            getSession().setAttribute("exportArtifactRange", settingsRange);
            Notification notif = new Notification("Info", "Settings saved successfully ",
                    Notification.Type.ASSISTIVE_NOTIFICATION);
            notif.setPosition(Position.TOP_RIGHT);
            notif.show(Page.getCurrent());
        });

        defaultButton.addClickListener( e ->{
            SettingsLimitRange settingsRange = new SettingsLimitRange();
            getSession().setAttribute("exportArtifactRange", settingsRange);
            exportPath.setValue("tmp");
            exportRangeArtifact.select(LimitRange.MAX);
            exportWithMetadata.setValue(false);
        });

        addComponent(formLayout);
    }
}
