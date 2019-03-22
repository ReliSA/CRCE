package cz.zcu.kiv.crce.crce_webui_v2.collection;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.zcu.kiv.crce.crce_component_collection.api.bean.CollectionBean;
import cz.zcu.kiv.crce.crce_component_collection.api.impl.CollectionService;
import cz.zcu.kiv.crce.crce_webui_v2.collection.classes.ArtifactRangeBean;
import cz.zcu.kiv.crce.crce_webui_v2.collection.classes.ParameterBean;
import cz.zcu.kiv.crce.crce_webui_v2.internal.Activator;
import cz.zcu.kiv.crce.crce_webui_v2.repository.classes.ResourceBean;
import cz.zcu.kiv.crce.crce_webui_v2.repository.services.ResourceService;
import cz.zcu.kiv.crce.crce_webui_v2.collection.services.FindCollectionService;
import cz.zcu.kiv.crce.crce_webui_v2.webui.MyUI;

import java.util.ArrayList;
import java.util.List;

public class CollectionNewForm extends FormLayout{
    private static final long serialVersionUID = 3539728975121021002L;
    private Label labelDescription = new Label("Add a new collection");
    private TextField name = new TextField("name");
    private TextField version = new TextField("version");
    private Label labelStore = new Label("Artifact in Store");
    private Label labelCollection = new Label("Saved collection");
    private TextField idTextStore = new TextField();
    private TextField idTextCollection = new TextField();
    private Grid gridStore = new Grid();
    private Grid gridCollection = new Grid();
    private Grid newCollectionGrid = new Grid();
    private TextField nameParameter = new TextField("Name parameter");
    private TextField valueParameter = new TextField("Value parameter");
    private Grid newParameterGrid = new Grid();
    private TextField nameRange = new TextField("Name artifact");
    private TextField valueRange = new TextField("Version range");
    private Grid newRangeGrid = new Grid();
    private ResourceService resourceService;
    private transient CollectionService collectionService;
    private transient FindCollectionService findCollectionService;
    private transient ResourceBean resourceBeanSelect;
    private transient CollectionBean collectionBeanSelect;
    private transient CollectionBean newCollectionBeanSelect;
    private transient ParameterBean newParameterBeanSelect;
    private transient ArtifactRangeBean newArtifactRangeBeanSelect;

    public CollectionNewForm(MyUI myUI){
        VerticalLayout content = new VerticalLayout();
        VerticalLayout gridArtifactLayout = new VerticalLayout();
        VerticalLayout gridCollectionLayout = new VerticalLayout();
        VerticalLayout panelParameterLayout = new VerticalLayout();
        VerticalLayout panelRangeLayout = new VerticalLayout();
        HorizontalLayout gridArtifactCollectionLayout = new HorizontalLayout();
        HorizontalLayout panelParameterRangeLayout = new HorizontalLayout();

        name.setWidth("270px");
        name.setRequiredError("The item can not be empty!");
        name.setRequired(true);
        version.setWidth("270px");
        version.setRequiredError("The item can not be empty!");
        version.setRequired(true);

        // grid artifact from store
        resourceService = new ResourceService(Activator.instance().getMetadataService());
        findCollectionService = new FindCollectionService();

        gridStore.setContainerDataSource(new BeanItemContainer<>(ResourceBean.class,
                resourceService.getAllResourceBeanFromStore(myUI.getSession())));
        gridStore.getColumn("resource").setHidden(true);
        gridStore.getColumn("size").setHidden(true);
        gridStore.getColumn("symbolicName").setHidden(true);
        gridStore.getColumn("categories").setHidden(true);
        gridStore.setColumnOrder("presentationName", "version");
        gridStore.addStyleName("my-style");

        idTextStore.setInputPrompt("search by presentation name...");
        idTextStore.setWidth("270px");
        Button findStoreButton = new Button(FontAwesome.CHECK);
        findStoreButton.addClickListener(e ->{
            gridStore.setContainerDataSource(new BeanItemContainer<>(ResourceBean.class,
                    resourceService.getFindResourceBeanFromStore(myUI.getSession(), idTextStore.getValue())));
        });
        Button clearStoreButton = new Button(FontAwesome.TIMES);
        clearStoreButton.addClickListener(e -> {
            idTextStore.clear();
            gridStore.setContainerDataSource(new BeanItemContainer<>(ResourceBean.class,
                    resourceService.getAllResourceBeanFromStore(myUI.getSession())));
        });

        CssLayout filteringStore = new CssLayout();
        filteringStore.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        filteringStore.addComponents(idTextStore, findStoreButton, clearStoreButton);

        Button addFromStoreButton = new Button("Add artifact");
        addFromStoreButton.setEnabled(false);
        gridArtifactLayout.addComponents(labelStore, filteringStore, gridStore, addFromStoreButton);
        gridArtifactLayout.setComponentAlignment(addFromStoreButton, Alignment.BOTTOM_CENTER);
        gridArtifactLayout.setSpacing(true);

        //grid collection
        collectionService = new CollectionService();
        gridCollection.setContainerDataSource(new BeanItemContainer<>(CollectionBean.class,
                collectionService.getCollectionComponentAll()));
        gridCollection.getColumn("id").setHidden(true);
        gridCollection.getColumn("collection").setHidden(true);
        gridCollection.setColumnOrder("name", "version");
        gridCollection.addStyleName("my-style");

        idTextCollection.setInputPrompt("search by name...");
        idTextCollection.setWidth("270px");
        Button findCollectionButton = new Button(FontAwesome.CHECK);
        findCollectionButton.addClickListener(e ->{
            gridCollection.setContainerDataSource(new BeanItemContainer<>(CollectionBean.class,
                    findCollectionService.getFindCollectionBean(idTextCollection.getValue())));
        });
        Button clearCollectionButton = new Button(FontAwesome.TIMES);
        clearCollectionButton.addClickListener(e -> {
            idTextCollection.clear();
            gridCollection.setContainerDataSource(new BeanItemContainer<>(CollectionBean.class,
                    collectionService.getCollectionComponentAll()));
        });

        CssLayout filteringCollection = new CssLayout();
        filteringCollection.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        filteringCollection.addComponents(idTextCollection, findCollectionButton, clearCollectionButton);

        Button addFromCollectionButton = new Button("Add collection");
        addFromCollectionButton.setEnabled(false);
        gridCollectionLayout.addComponents(labelCollection, filteringCollection, gridCollection, addFromCollectionButton);
        gridCollectionLayout.setComponentAlignment(addFromCollectionButton, Alignment.BOTTOM_CENTER);
        gridCollectionLayout.setSpacing(true);

        gridArtifactCollectionLayout.addComponents(gridArtifactLayout,gridCollectionLayout);
        gridArtifactCollectionLayout.setSpacing(true);
        gridArtifactCollectionLayout.setSizeFull();
        gridStore.setSizeFull();
        gridCollection.setSizeFull();
        gridArtifactCollectionLayout.setExpandRatio(gridArtifactLayout, 1);
        gridArtifactCollectionLayout.setExpandRatio(gridCollectionLayout, 1);

        // panel parameter
        Panel parameterPanel = new Panel("Parameter");
        VerticalLayout parameterTextFieldLayout = new VerticalLayout();
        nameParameter.setWidth("310px");
        valueParameter.setWidth("310px");
        parameterTextFieldLayout.addComponents(nameParameter, valueParameter);
        parameterTextFieldLayout.setSpacing(true);
        parameterTextFieldLayout.setMargin(true);
        parameterPanel.setContent(parameterTextFieldLayout);
        Button addParameterButton = new Button("Add parameter");
        panelParameterLayout.addComponents(parameterPanel,addParameterButton);
        panelParameterLayout.setSpacing(true);
        panelParameterLayout.setComponentAlignment(addParameterButton, Alignment.BOTTOM_CENTER);

        // panel range version component
        Panel rangePanel = new Panel("Artifact with a defined version range");
        VerticalLayout rangeTextFieldLayout = new VerticalLayout();
        nameRange.setWidth("310px");
        valueRange.setWidth("310px");
        rangeTextFieldLayout.addComponents(nameRange, valueRange);
        rangeTextFieldLayout.setSpacing(true);
        rangeTextFieldLayout.setMargin(true);
        rangePanel.setContent(rangeTextFieldLayout);
        Button addRangeButton = new Button("Add with range");
        panelRangeLayout.addComponents(rangePanel, addRangeButton);
        panelRangeLayout.setSpacing(true);
        panelRangeLayout.setComponentAlignment(addRangeButton, Alignment.BOTTOM_CENTER);

        panelParameterRangeLayout.addComponents(panelParameterLayout, panelRangeLayout);
        panelParameterRangeLayout.setSpacing(true);
        panelParameterRangeLayout.setSizeFull();
        panelParameterLayout.setSizeFull();
        panelRangeLayout.setSizeFull();
        panelParameterRangeLayout.setExpandRatio(panelParameterLayout,1);
        panelParameterRangeLayout.setExpandRatio(panelRangeLayout, 1);

        List<CollectionBean> newCollectionList = new ArrayList<>();
        newCollectionGrid.setContainerDataSource(new BeanItemContainer<>(CollectionBean.class, newCollectionList));
        newCollectionGrid.getColumn("id").setHidden(true);
        newCollectionGrid.setColumnOrder("name", "version", "collection");
        newCollectionGrid.addStyleName("my-style");
        newCollectionGrid.setSizeFull();

        HorizontalLayout newParameterRangeGridLayout = new HorizontalLayout();
        List<ParameterBean> newParameterList = new ArrayList<>();
        newParameterGrid.setContainerDataSource(new BeanItemContainer<>(ParameterBean.class, newParameterList));
        newParameterGrid.setColumnOrder("name", "value");
        newParameterGrid.addStyleName("my-style");
        newParameterGrid.setSizeFull();

        List<ArtifactRangeBean> newRangeList = new ArrayList<>();
        newRangeGrid.setContainerDataSource(new BeanItemContainer<>(ArtifactRangeBean.class, newRangeList));
        newRangeGrid.setColumnOrder("name", "range");
        newRangeGrid.addStyleName("my-style");
        newRangeGrid.setSizeFull();

        Button removeArtifactButton = new Button("Remove from list");
        removeArtifactButton.setEnabled(false);
        Button removeParameterButton = new Button("Remove from list");
        removeParameterButton.setEnabled(false);
        Button removeRangeButton = new Button("Remove from list");
        removeRangeButton.setEnabled(false);

        VerticalLayout newParameterLayout = new VerticalLayout();
        VerticalLayout newRangeLayout = new VerticalLayout();
        newParameterLayout.addComponents(newParameterGrid, removeParameterButton);
        newParameterLayout.setSpacing(true);
        newParameterLayout.setSizeFull();
        newParameterLayout.setComponentAlignment(removeParameterButton, Alignment.BOTTOM_CENTER);
        newRangeLayout.addComponents(newRangeGrid, removeRangeButton);
        newRangeLayout.setSpacing(true);
        newRangeLayout.setSizeFull();
        newRangeLayout.setComponentAlignment(removeRangeButton, Alignment.BOTTOM_CENTER);

        newParameterRangeGridLayout.addComponents(newParameterLayout, newRangeLayout);
        newParameterRangeGridLayout.setSpacing(true);
        newParameterRangeGridLayout.setSizeFull();
        newParameterRangeGridLayout.setExpandRatio(newParameterLayout,1);
        newParameterRangeGridLayout.setExpandRatio(newRangeLayout, 1);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button saveButton = new Button("Save");
        saveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.setWidth("100px");
        saveButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        Button clearButton = new Button("Clear");
        clearButton.setWidth("100px");
        buttonLayout.addComponents(saveButton, clearButton);
        buttonLayout.setSpacing(true);

        content.setMargin(new MarginInfo(false,true));
        content.setSpacing(true);
        content.addComponents(labelDescription, name, version, gridArtifactCollectionLayout, newCollectionGrid,
                removeArtifactButton, panelParameterRangeLayout, newParameterRangeGridLayout, buttonLayout);
        content.setComponentAlignment(removeArtifactButton, Alignment.BOTTOM_CENTER);

        gridStore.addSelectionListener(e ->{
            if(!e.getSelected().isEmpty()){
                resourceBeanSelect = (ResourceBean)e.getSelected().iterator().next();
                addFromStoreButton.setEnabled(true);
            }
            else{
                resourceBeanSelect = null;
            }
        });

        gridCollection.addSelectionListener(e ->{
            if(!e.getSelected().isEmpty()){
                collectionBeanSelect = (CollectionBean)e.getSelected().iterator().next();
                addFromCollectionButton.setEnabled(true);
            }
            else{
                collectionBeanSelect = null;
            }
        });

        newCollectionGrid.addSelectionListener(e ->{
            if(!e.getSelected().isEmpty()){
                newCollectionBeanSelect = (CollectionBean)e.getSelected().iterator().next();
                removeArtifactButton.setEnabled(true);
            }
            else{
                newCollectionBeanSelect = null;
            }
        });

        newParameterGrid.addSelectionListener(e ->{
            if(!e.getSelected().isEmpty()){
                newParameterBeanSelect = (ParameterBean)e.getSelected().iterator().next();
                removeParameterButton.setEnabled(true);
            }
            else{
                newParameterBeanSelect = null;
            }
        });

        newRangeGrid.addSelectionListener(e ->{
            if(!e.getSelected().isEmpty()){
                newArtifactRangeBeanSelect = (ArtifactRangeBean)e.getSelected().iterator().next();
                removeRangeButton.setEnabled(true);
            }
            else{
                newArtifactRangeBeanSelect = null;
            }
        });

        addFromStoreButton.addClickListener(e ->{
            if(resourceBeanSelect != null){
                boolean contains = false;
                for(CollectionBean cb : newCollectionList){
                    if(resourceBeanSelect.getResource().getId() == cb.getId()){
                        contains = true;
                    }
                }
                if(!contains){
                    newCollectionList.add(new CollectionBean(resourceBeanSelect.getResource().getId(),
                            resourceBeanSelect.getPresentationName(), resourceBeanSelect.getVersion(), false));

                    newCollectionGrid.setContainerDataSource(new BeanItemContainer<>(CollectionBean.class, newCollectionList));
                    newCollectionGrid.clearSortOrder();
                }
            }
        });

        addFromCollectionButton.addClickListener(e ->{
            if(collectionBeanSelect != null){
                newCollectionList.add(collectionBeanSelect);

                newCollectionGrid.setContainerDataSource(new BeanItemContainer<>(CollectionBean.class, newCollectionList));
                newCollectionGrid.clearSortOrder();
            }
        });

        removeArtifactButton.addClickListener(e ->{
            if(newCollectionBeanSelect != null){
                newCollectionList.remove(newCollectionBeanSelect);

                newCollectionGrid.setContainerDataSource(new BeanItemContainer<>(CollectionBean.class, newCollectionList));
                newCollectionGrid.clearSortOrder();
            }
        });

        addParameterButton.addClickListener(e ->{
            if(nameParameter.isEmpty() || valueParameter.isEmpty()){
                Notification notif = new Notification("Items are not filled!", Notification.Type.WARNING_MESSAGE);
                notif.setDelayMsec(5000);
                notif.show(Page.getCurrent());
            }
            else{
                newParameterList.add(new ParameterBean(nameParameter.getValue(),valueParameter.getValue()));
                newParameterGrid.setContainerDataSource(new BeanItemContainer<>(ParameterBean.class, newParameterList));
                newParameterGrid.clearSortOrder();
            }
        });

        addRangeButton.addClickListener(e ->{
            if(nameRange.isEmpty() || valueRange.isEmpty()){
                Notification notif = new Notification("Items are not filled!", Notification.Type.WARNING_MESSAGE);
                notif.setDelayMsec(5000);
                notif.show(Page.getCurrent());
            }
            else{
                newRangeList.add(new ArtifactRangeBean(nameRange.getValue(),valueRange.getValue()));
                newRangeGrid.setContainerDataSource(new BeanItemContainer<>(ArtifactRangeBean.class, newRangeList));
                newRangeGrid.clearSortOrder();
            }
        });

        removeParameterButton.addClickListener(e ->{
            if(newParameterBeanSelect != null){
                newParameterList.remove(newParameterBeanSelect);

                newParameterGrid.setContainerDataSource(new BeanItemContainer<>(ParameterBean.class, newParameterList));
                newParameterGrid.clearSortOrder();
            }
        });

        removeRangeButton.addClickListener(e ->{
            if(newArtifactRangeBeanSelect != null){
                newRangeList.remove(newArtifactRangeBeanSelect);

                newRangeGrid.setContainerDataSource(new BeanItemContainer<>(ArtifactRangeBean.class, newRangeList));
                newRangeGrid.clearSortOrder();
            }
        });

        clearButton.addClickListener(e ->{
            name.clear();
            version.clear();

            newCollectionList.clear();
            newCollectionGrid.setContainerDataSource(new BeanItemContainer<>(CollectionBean.class, newCollectionList));
            newCollectionGrid.clearSortOrder();

            nameParameter.clear();
            valueParameter.clear();
            nameRange.clear();
            valueRange.clear();

            newParameterList.clear();
            newParameterGrid.setContainerDataSource(new BeanItemContainer<>(ParameterBean.class, newParameterList));
            newParameterGrid.clearSortOrder();

            newRangeList.clear();
            newRangeGrid.setContainerDataSource(new BeanItemContainer<>(ArtifactRangeBean.class, newRangeList));
            newRangeGrid.clearSortOrder();
        });

        saveButton.addClickListener(e ->{
            if(name.isEmpty() || version.isEmpty()){
                Notification notif = new Notification("Incomplete assignment!", Notification.Type.WARNING_MESSAGE);
                notif.setDelayMsec(5000);
                notif.show(Page.getCurrent());
            }
            else{
                List<String> ids = new ArrayList<>();
                List<String> parameters = new ArrayList<>();
                List<String> artifactsWithRange = new ArrayList<>();
                for(CollectionBean cb: newCollectionList){
                    ids.add(cb.getId());
                }
                for(ParameterBean pb : newParameterList){
                    parameters.add(pb.getName() + "=" + pb.getValue());
                }
                for(ArtifactRangeBean arb : newRangeList){
                    artifactsWithRange.add(arb.getName() + "=[" + arb.getRange() + "]");
                }

                boolean status = collectionService.setCollectionComponent(name.getValue(), version.getValue(), ids,
                        parameters, artifactsWithRange);
                if(status){
                    Notification notif = new Notification("Info", "The new collection has been " +
                            "successfully saved.", Notification.Type.ASSISTIVE_NOTIFICATION);
                    notif.setPosition(Position.TOP_RIGHT);
                    notif.show(Page.getCurrent());

                }
                else{
                    new Notification("Error saving new collection.", Notification.Type.WARNING_MESSAGE)
                            .show(Page.getCurrent());
                }
                myUI.setContentBodyCollectionNew();
            }
        });

        addComponent(content);
    }
}
