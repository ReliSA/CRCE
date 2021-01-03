package cz.zcu.kiv.crce.crce_webui_v2.versioning;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.zcu.kiv.crce.crce_component_versioning.api.bean.ComponentBean;
import cz.zcu.kiv.crce.crce_component_versioning.api.bean.ComponentDetailBean;
import cz.zcu.kiv.crce.crce_component_versioning.api.impl.VersioningService;
import cz.zcu.kiv.crce.crce_webui_v2.internal.Activator;
import cz.zcu.kiv.crce.crce_webui_v2.repository.classes.ResourceBean;
import cz.zcu.kiv.crce.crce_webui_v2.repository.services.ResourceService;
import cz.zcu.kiv.crce.crce_webui_v2.versioning.services.FindCompositeService;
import cz.zcu.kiv.crce.crce_webui_v2.versioning.classes.RandomStringGenerator;
import cz.zcu.kiv.crce.crce_webui_v2.webui.MyUI;

public class VersioningForm extends FormLayout {
    private static final long serialVersionUID = 7439970261502810719L;
    private Label labelForm = new Label("Composite components");
    private TextField idText = new TextField();
    private Panel panelGridComponents = new Panel("Detail of the composite component");
    private Grid gridComponents = new Grid();
    private Tree treeDetailComponent = new Tree();
    private PopupView popupRemove;
    private PopupView popupCopy;
    private ResourceService resourceService;
    private transient VersioningService versioningService;
    private transient FindCompositeService findCompositeService;
    private transient ComponentBean componentBeanSelect;
    private transient RandomStringGenerator randomStringGenerator;

    public VersioningForm(MyUI myUI){
        VerticalLayout content = new VerticalLayout();
        VerticalLayout formLayout = new VerticalLayout();
        HorizontalLayout gridTreeLayout = new HorizontalLayout();
        HorizontalLayout buttonLayout = new HorizontalLayout();

        findCompositeService = new FindCompositeService();
        versioningService = new VersioningService();
        resourceService = new ResourceService(Activator.instance().getMetadataService());
        randomStringGenerator = new RandomStringGenerator();

        Button buttonEdit = new Button("Edit");
        buttonEdit.setWidth("100px");
        Button buttonCopy = new Button("Copy");
        buttonCopy.setWidth("100px");
        Button buttonRemove = new Button("Remove");

        buttonEdit.setStyleName(ValoTheme.BUTTON_PRIMARY);
        buttonEdit.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        labelForm.addStyleName(ValoTheme.LABEL_BOLD);

        idText.setInputPrompt("search by name...");
        idText.setWidth("270px");

        Button findButton = new Button(FontAwesome.CHECK);
        findButton.addClickListener(e ->{
            gridComponents.setContainerDataSource(new BeanItemContainer<>(ComponentBean.class,
                    findCompositeService.getFindComponentBean(idText.getValue())));
        });

        Button clearButton = new Button(FontAwesome.TIMES);
        clearButton.addClickListener(e -> {
            idText.clear();
            gridComponents.setContainerDataSource(new BeanItemContainer<>(ComponentBean.class,
                    versioningService.getCompositeComponentAll()));
        });

        CssLayout filtering = new CssLayout();
        filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        filtering.addComponents(idText, findButton, clearButton);

        panelGridComponents.setVisible(false);

        gridComponents.setContainerDataSource(new BeanItemContainer<>(ComponentBean.class,
                versioningService.getCompositeComponentAll()));
        gridComponents.getColumn("id").setHidden(true);
        gridComponents.getColumn("composite").setHidden(true);
        gridComponents.setColumnOrder("name", "version");
        gridComponents.addStyleName("my-style");

        panelGridComponents.setContent(treeDetailComponent);
        panelGridComponents.setHeight("400px");
        gridTreeLayout.addComponents(gridComponents, panelGridComponents);
        gridTreeLayout.setSpacing(true);
        gridTreeLayout.setSizeFull();

        gridComponents.setSizeFull();
        treeDetailComponent.setSizeFull();
        gridTreeLayout.setExpandRatio(gridComponents, 1);
        gridTreeLayout.setExpandRatio(panelGridComponents, 1);

        HorizontalLayout popupLayout = new HorizontalLayout();

        // Popup verification of component remove
        VerticalLayout buttonPopupLayoutRemove = new VerticalLayout();
        Panel panelRemove = new Panel("Really remove the artifact?");
        Button yesRemoveButton = new Button("Confirm");
        yesRemoveButton.setStyleName(ValoTheme.BUTTON_DANGER);
        buttonPopupLayoutRemove.addComponents(yesRemoveButton);
        buttonPopupLayoutRemove.setMargin(true);
        buttonPopupLayoutRemove.setSizeFull();
        buttonPopupLayoutRemove.setComponentAlignment(yesRemoveButton, Alignment.BOTTOM_CENTER);
        panelRemove.setContent(buttonPopupLayoutRemove);

        popupRemove = new PopupView(null, panelRemove);
        popupRemove.setWidth("150px");

        // Popup copy set artifact
        VerticalLayout buttonPopupLayoutCopy = new VerticalLayout();
        Panel panelCopy = new Panel("Input the version of the set.");
        TextField textVersionSet = new TextField();
        Button saveVersionButton = new Button("Save");
        saveVersionButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        buttonPopupLayoutCopy.addComponents(textVersionSet, saveVersionButton);
        buttonPopupLayoutCopy.setMargin(true);
        buttonPopupLayoutCopy.setSpacing(true);
        buttonPopupLayoutCopy.setSizeFull();
        buttonPopupLayoutCopy.setComponentAlignment(saveVersionButton, Alignment.BOTTOM_CENTER);
        panelCopy.setContent(buttonPopupLayoutCopy);

        popupCopy = new PopupView(null, panelCopy);
        popupCopy.setWidth("250px");

        buttonLayout.addComponents(buttonEdit, buttonCopy, buttonRemove);
        buttonLayout.setSpacing(true);
        buttonLayout.setVisible(false);

        formLayout.addComponents(labelForm, filtering, gridTreeLayout);
        formLayout.setSpacing(true);

        popupLayout.addComponents(popupCopy, popupRemove);
        popupLayout.addStyleName("margin-top: -10px");

        content.addComponents(formLayout, buttonLayout, popupLayout);
        //content.setComponentAlignment(buttonLayout, Alignment.BOTTOM_LEFT);
        content.setMargin(new MarginInfo(false, true));
        content.setSpacing(true);

        gridComponents.addSelectionListener(e ->{
            if(!e.getSelected().isEmpty()){
                componentBeanSelect = (ComponentBean)e.getSelected().iterator().next();
                treeDetailComponent = new Tree();
                fillTreeDetailComponent(componentBeanSelect.getId(), null, myUI.getSession());
                treeDetailComponent.setSelectable(false);
                panelGridComponents.setContent(treeDetailComponent);
                panelGridComponents.setVisible(true);
                buttonLayout.setVisible(true);
            }
            else{
                treeDetailComponent = new Tree();
                componentBeanSelect = null;
                panelGridComponents.setVisible(false);
                buttonLayout.setVisible(false);
            }
        });

        buttonRemove.addClickListener(e ->{
            popupRemove.setPopupVisible(true);
            yesRemoveButton.addClickListener(ev ->{
                boolean result = versioningService.removeCompositeComponent(componentBeanSelect.getId());
                if(result){
                    Notification notif = new Notification("Info", "Collection sucess removed.",
                            Notification.Type.ASSISTIVE_NOTIFICATION);
                    notif.setPosition(Position.TOP_RIGHT);
                    notif.show(Page.getCurrent());
                }
                else{
                    new Notification("Could not remove collection", Notification.Type.WARNING_MESSAGE)
                            .show(Page.getCurrent());
                }
                myUI.setContentBodyVersioning();
            });
        });

        buttonCopy.addClickListener(e ->{
            popupCopy.setPopupVisible(true);
            textVersionSet.setValue(componentBeanSelect.getVersion());
            saveVersionButton.addClickListener(ev ->{
                boolean result = versioningService.setCompositeComponent(componentBeanSelect.getName(),
                        textVersionSet.getValue(),
                        versioningService.getCompositeComponentDetail(componentBeanSelect.getId()).getContent());
                if(result){
                    Notification notif = new Notification("Info", "Copy collection is success saved.",
                            Notification.Type.ASSISTIVE_NOTIFICATION);
                    notif.setPosition(Position.TOP_RIGHT);
                    notif.show(Page.getCurrent());
                }
                else{
                    new Notification("Could not save new version of collection", Notification.Type.WARNING_MESSAGE)
                            .show(Page.getCurrent());
                }
                myUI.setContentBodyVersioning();
            });
        });

        buttonEdit.addClickListener(e -> {
            myUI.setContentBodyVersioningEdit(componentBeanSelect);
        });

        addComponent(content);
    }

    void fillTreeDetailComponent(String idComponent, String parent, VaadinSession session){
        ComponentDetailBean componentDetailBean = versioningService.getCompositeComponentDetail(idComponent);

        if(componentDetailBean != null) {
            String item = componentDetailBean.getName() + "_" + randomStringGenerator.getRandomString();
            treeDetailComponent.addItem(item);
            treeDetailComponent.setItemCaption(item, componentDetailBean.getName() +
                    " (" + componentDetailBean.getVersion() + ")");
            // off the root
            if(parent != null){
                treeDetailComponent.setParent(item, parent);
            }
            for(String s : componentDetailBean.getContent()){
                fillTreeDetailComponent(s, item, session);
            }
        }
        // artefact in store
        else{
            for (ResourceBean resourceBean : resourceService.getAllResourceBeanFromStore(session)) {
                if (resourceBean.getResource().getId().equals(idComponent)) {
                    String item = resourceBean.getPresentationName() + "_" + randomStringGenerator.getRandomString();
                    treeDetailComponent.addItem(item);
                    treeDetailComponent.setItemCaption(item,
                            resourceBean.getPresentationName() + " (" + resourceBean.getVersion() + ")");
                    treeDetailComponent.setParent(item, parent);
                    treeDetailComponent.setChildrenAllowed(item, false);
                }
            }
        }
    }
}
