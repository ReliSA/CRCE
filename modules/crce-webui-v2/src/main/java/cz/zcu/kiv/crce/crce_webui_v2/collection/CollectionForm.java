package cz.zcu.kiv.crce.crce_webui_v2.collection;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.zcu.kiv.crce.crce_component_collection.api.bean.CollectionBean;
import cz.zcu.kiv.crce.crce_component_collection.api.bean.CollectionDetailBean;
import cz.zcu.kiv.crce.crce_component_collection.api.impl.CollectionService;
import cz.zcu.kiv.crce.crce_webui_v2.internal.Activator;
import cz.zcu.kiv.crce.crce_webui_v2.repository.classes.ResourceBean;
import cz.zcu.kiv.crce.crce_webui_v2.repository.services.ResourceService;
import cz.zcu.kiv.crce.crce_webui_v2.collection.services.FindCollectionService;
import cz.zcu.kiv.crce.crce_webui_v2.collection.classes.RandomStringGenerator;
import cz.zcu.kiv.crce.crce_webui_v2.webui.MyUI;

public class CollectionForm extends FormLayout {
    private static final long serialVersionUID = 7439970261502810719L;
    private Label labelForm = new Label("Collection of components");
    private TextField idText = new TextField();
    private Panel panelTreeCollection = new Panel("Detail of the collection component");
    private Grid gridCollection = new Grid();
    private Tree treeDetailCollection = new Tree();
    private PopupView popupRemove;
    private PopupView popupCopy;
    private ResourceService resourceService;
    private transient CollectionService collectionService;
    private transient FindCollectionService findCollectionService;
    private transient CollectionBean collectionBeanSelect;
    private transient RandomStringGenerator randomStringGenerator;

    public CollectionForm(MyUI myUI){
        VerticalLayout content = new VerticalLayout();
        VerticalLayout formLayout = new VerticalLayout();
        HorizontalLayout gridTreeLayout = new HorizontalLayout();
        VerticalLayout gridButtonLayout = new VerticalLayout();
        HorizontalLayout buttonLayout = new HorizontalLayout();
        VerticalLayout treeDetailCollectionButtonLayout = new VerticalLayout();

        findCollectionService = new FindCollectionService();
        collectionService = new CollectionService();
        resourceService = new ResourceService(Activator.instance().getMetadataService());
        randomStringGenerator = new RandomStringGenerator();

        Button buttonEdit = new Button("Edit");
        buttonEdit.setWidth("100px");
        Button buttonCopy = new Button("Copy");
        buttonCopy.setWidth("100px");
        Button buttonRemove = new Button("Remove");
        Button downloadButton = new Button("Download");

        buttonEdit.setStyleName(ValoTheme.BUTTON_PRIMARY);
        buttonEdit.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        labelForm.addStyleName(ValoTheme.LABEL_BOLD);

        idText.setInputPrompt("search by name...");
        idText.setWidth("270px");

        Button findButton = new Button(FontAwesome.CHECK);
        findButton.addClickListener(e ->{
            gridCollection.setContainerDataSource(new BeanItemContainer<>(CollectionBean.class,
                    findCollectionService.getFindCollectionBean(idText.getValue())));
        });

        Button clearButton = new Button(FontAwesome.TIMES);
        clearButton.addClickListener(e -> {
            idText.clear();
            gridCollection.setContainerDataSource(new BeanItemContainer<>(CollectionBean.class,
                    collectionService.getCollectionComponentAll()));
            treeDetailCollectionButtonLayout.setVisible(false);
            gridTreeLayout.setExpandRatio(gridButtonLayout, 1);
        });

        CssLayout filtering = new CssLayout();
        filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        filtering.addComponents(idText, findButton, clearButton);

        gridCollection.setContainerDataSource(new BeanItemContainer<>(CollectionBean.class,
                collectionService.getCollectionComponentAll()));
        gridCollection.getColumn("id").setHidden(true);
        gridCollection.getColumn("collection").setHidden(true);
        gridCollection.setColumnOrder("name", "version");
        gridCollection.addStyleName("my-style");

        panelTreeCollection.setContent(treeDetailCollection);
        panelTreeCollection.setHeight("400px");

        Button buttonDetail = new Button("Detail");
        buttonDetail.setEnabled(false);
        buttonDetail.setWidth("100px");
        treeDetailCollectionButtonLayout.addComponents(panelTreeCollection, buttonDetail);
        treeDetailCollectionButtonLayout.setSpacing(true);
        treeDetailCollectionButtonLayout.setVisible(false);

        // Popup verification of component remove
        HorizontalLayout popupLayout = new HorizontalLayout();
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

        buttonLayout.addComponents(buttonEdit, buttonCopy, buttonRemove, downloadButton);
        buttonLayout.setSpacing(true);
        buttonLayout.setVisible(false);

        popupLayout.addComponents(popupCopy, popupRemove);
        popupLayout.addStyleName("margin-top: -10px");

        gridButtonLayout.addComponents(gridCollection, buttonLayout, popupLayout);
        gridButtonLayout.setSpacing(true);

        gridTreeLayout.addComponents(gridButtonLayout, treeDetailCollectionButtonLayout);
        gridTreeLayout.setSpacing(true);
        gridTreeLayout.setSizeFull();

        gridCollection.setSizeFull();
        treeDetailCollection.setSizeFull();

        gridTreeLayout.setExpandRatio(gridButtonLayout, 1);

        formLayout.addComponents(labelForm, filtering, gridTreeLayout);
        formLayout.setSpacing(true);

        content.addComponents(formLayout);
        content.setMargin(new MarginInfo(false, true));
        content.setSpacing(true);

        gridCollection.addSelectionListener(e ->{
            if(!e.getSelected().isEmpty()){
                collectionBeanSelect = (CollectionBean)e.getSelected().iterator().next();
                treeDetailCollection = new Tree();
                fillTreeDetailComponent(collectionBeanSelect.getId(), null, myUI.getSession());
                panelTreeCollection.setContent(treeDetailCollection);
                treeDetailCollectionButtonLayout.setVisible(true);
                gridTreeLayout.setExpandRatio(treeDetailCollectionButtonLayout, 1);
                treeDetailCollection.addItemClickListener(e1 ->{
                    buttonDetail.setEnabled(true);
                });
                buttonLayout.setVisible(true);
            }
            else{
                treeDetailCollection = new Tree();
                collectionBeanSelect = null;
                treeDetailCollectionButtonLayout.setVisible(false);
                buttonLayout.setVisible(false);
            }
        });

        buttonDetail.addClickListener(e ->{
            if(treeDetailCollection.getValue() != null){
                String[] pom = treeDetailCollection.getValue().toString().split("_");
                if(pom[0].equals("collection")){
                    //collection
                    myUI.setContentBodyCollectionRangeParamDetailForm(collectionService
                            .getCollectionComponentDetail(pom[1]), this);
                }
                else{
                    // artifact in store
                    for (ResourceBean resourceBean : resourceService.getAllResourceBeanFromStore(myUI.getSession())) {
                        if(resourceBean.getResource().getId().equals(pom[1])){
                            myUI.setContentArtefactDetailForm(resourceBean,this);
                            break;
                        }
                    }
                }
            }
        });

        buttonRemove.addClickListener(e ->{
            popupRemove.setPopupVisible(true);
            yesRemoveButton.addClickListener(ev ->{
                boolean result = collectionService.removeCollectionComponent(collectionBeanSelect.getId());
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
                myUI.setContentBodyCollection();
            });
        });

        buttonCopy.addClickListener(e ->{
            popupCopy.setPopupVisible(true);
            textVersionSet.setValue(collectionBeanSelect.getVersion());
            saveVersionButton.addClickListener(ev ->{
                boolean result = collectionService.setCollectionComponent(collectionBeanSelect.getName(),
                        textVersionSet.getValue(),
                        collectionService.getCollectionComponentDetail(collectionBeanSelect.getId()).getSpecificArtifacts(),
                        collectionService.getCollectionComponentDetail(collectionBeanSelect.getId()).getParameters(),
                        collectionService.getCollectionComponentDetail(collectionBeanSelect.getId()).getRangeArtifacts());
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
                myUI.setContentBodyCollection();
            });
        });

        buttonEdit.addClickListener(e -> {
            myUI.setContentBodyCollectionEdit(collectionBeanSelect);
        });

        addComponent(content);
    }

    void fillTreeDetailComponent(String idCollection, String parent, VaadinSession session){
        CollectionDetailBean collectionDetailBean = collectionService.getCollectionComponentDetail(idCollection);

        if(collectionDetailBean != null) {
            String item = "collection_" + collectionDetailBean.getId() + "_" + randomStringGenerator.getRandomString();
            treeDetailCollection.addItem(item);
            treeDetailCollection.setItemCaption(item, collectionDetailBean.getName() +
                    " (" + collectionDetailBean.getVersion() + ")");
            // off the root
            if(parent != null){
                treeDetailCollection.setParent(item, parent);
            }
            for(String s : collectionDetailBean.getSpecificArtifacts()){
                fillTreeDetailComponent(s, item, session);
            }
        }
        // artefact in store
        else{
            for (ResourceBean resourceBean : resourceService.getAllResourceBeanFromStore(session)) {
                if (resourceBean.getResource().getId().equals(idCollection)) {
                    String item = "artifact_" + resourceBean.getResource().getId() + "_" +
                            randomStringGenerator.getRandomString();
                    treeDetailCollection.addItem(item);
                    treeDetailCollection.setItemCaption(item,
                            resourceBean.getPresentationName() + " (" + resourceBean.getVersion() + ")");
                    treeDetailCollection.setParent(item, parent);
                    treeDetailCollection.setChildrenAllowed(item, false);
                }
            }
        }
    }
}
