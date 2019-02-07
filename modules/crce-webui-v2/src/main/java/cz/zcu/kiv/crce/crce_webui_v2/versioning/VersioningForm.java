package cz.zcu.kiv.crce.crce_webui_v2.versioning;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.zcu.kiv.crce.crce_webui_v2.webui.MyUI;

public class VersioningForm extends FormLayout {
    private static final long serialVersionUID = 7439970261502810719L;
    private Label labelForm = new Label("Composite components");
    private TextField idText = new TextField();
    private Grid gridComponents = new Grid();
    private PopupView popup;

    public VersioningForm(MyUI myUI){
        VerticalLayout content = new VerticalLayout();
        VerticalLayout fieldLayout = new VerticalLayout();
        HorizontalLayout buttonLayout = new HorizontalLayout();

        Button buttonDetail = new Button("Detail");
        buttonDetail.setWidth("100px");
        Button buttonRemove = new Button("Remove");
        buttonRemove.setWidth("100px");

        buttonDetail.setStyleName(ValoTheme.BUTTON_PRIMARY);
        buttonDetail.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        labelForm.addStyleName(ValoTheme.LABEL_BOLD);

        idText.setInputPrompt("search by name...");
        idText.setWidth("270px");

        Button findButton = new Button(FontAwesome.CHECK);
        findButton.addClickListener(e ->{
            //TODO
        });

        Button clearButton = new Button(FontAwesome.TIMES);
        clearButton.addClickListener(e -> {
            myUI.setContentBodyVersioning();
        });

        CssLayout filtering = new CssLayout();
        filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        filtering.addComponents(idText, findButton, clearButton);

        fieldLayout.addComponents(labelForm, filtering, gridComponents);
        fieldLayout.setSpacing(true);

        HorizontalLayout formLayout = new HorizontalLayout(fieldLayout);
        formLayout.setSizeFull();
        gridComponents.setSizeFull();
        formLayout.setExpandRatio(fieldLayout, 1);

        // Popup verification of artifact remove
        VerticalLayout buttonPopupLayout = new VerticalLayout();
        Panel panel = new Panel("Really remove the artifact?");
        Button yesRemoveButton = new Button("Confirm");
        yesRemoveButton.setStyleName(ValoTheme.BUTTON_DANGER);
        buttonPopupLayout.addComponents(yesRemoveButton);
        buttonPopupLayout.setMargin(true);
        buttonPopupLayout.setSizeFull();
        buttonPopupLayout.setComponentAlignment(yesRemoveButton, Alignment.BOTTOM_CENTER);
        panel.setContent(buttonPopupLayout);

        popup = new PopupView(null, panel);
        popup.setWidth("150px");

        buttonLayout.addComponents(buttonDetail, buttonRemove);
        buttonLayout.setSpacing(true);
        buttonLayout.setVisible(false);

        content.addComponents(formLayout, buttonLayout, popup);
        content.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
        content.setComponentAlignment(popup, Alignment.MIDDLE_CENTER);
        content.setMargin(new MarginInfo(false, true));
        content.setSpacing(true);

        addComponent(content);
    }
}
