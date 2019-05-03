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
import cz.zcu.kiv.crce.crce_component_collection.api.bean.CollectionDetailBean;
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

/**
 * Collection change form. It is called from the collection summary form. The form is identical to the user dialog for
 * creating a new collection. As an attribute, it takes the id of the set set.
 * <p/>
 * Date: 02.05.19
 *
 * @author Roman Pesek
 */
public class CollectionEditForm extends FormLayout {
    public static final long serialVersionUID = -6571696339324055801L;
    private Label labelDescription = new Label("Add a new collection");
    private TextField name = new TextField("name");
    private TextField version = new TextField("version");
    private TabSheet tabSheetSource = new TabSheet();
    private TabSheet tabSheetTarget = new TabSheet();
    private Label labelSource = new Label("Saved artifacts and collections, creating new parameters, " +
            "and defining range-based artifacts:");
    private Label labelTarget = new Label("Result list artifacts, existing collections, parameters, " +
            "and artifacts with a defined version range in new collection:");
    private TextField idTextStore = new TextField();
    private TextField idTextCollection = new TextField();
    private Grid gridSourceArtifact = new Grid();
    private Grid gridSourceCollection = new Grid();
    private Grid gridTargetArtifact = new Grid();
    private Grid gridTargetCollection = new Grid();
    private TextField nameParameter = new TextField("Name parameter");
    private TextField valueParameter = new TextField("Value parameter");
    private TextField nameRange = new TextField("Symbolic name of artifact");
    private TextField valueRange = new TextField("Version range - example input: 1.0.0,2.0.0");
    private Grid gridTargetParameter = new Grid();
    private Grid gridTargetRange = new Grid();
    private ResourceService resourceService;
    private transient CollectionService collectionService;
    private transient FindCollectionService findCollectionService;
    private transient ResourceBean resourceBeanSelectSource;
    private transient ResourceBean resourceBeanSelectTarget;
    private transient CollectionBean collectionBeanSelectSource;
    private transient CollectionBean collectionBeanSelectTarget;
    private transient ParameterBean parameterBeanSelect;
    private transient ArtifactRangeBean artifactRangeBeanSelect;

    public CollectionEditForm(MyUI myUI, CollectionBean collectionBeanTrans){
        VerticalLayout content = new VerticalLayout();
        VerticalLayout artifactSourceContentLayout = new VerticalLayout();
        VerticalLayout collectionSourceContentLayout = new VerticalLayout();

        name.setWidth("270px");
        name.setRequiredError("The item can not be empty!");
        name.setRequired(true);
        name.setValue(collectionBeanTrans.getName());
        version.setWidth("270px");
        version.setRequiredError("The item can not be empty!");
        version.setRequired(true);
        version.setValue(collectionBeanTrans.getVersion());

        // existing artifact from store
        resourceService = new ResourceService(Activator.instance().getMetadataService());
        findCollectionService = new FindCollectionService();

        gridSourceArtifact.setContainerDataSource(new BeanItemContainer<>(ResourceBean.class,
                resourceService.getAllResourceBeanFromStore(myUI.getSession())));
        gridSourceArtifact.getColumn("resource").setHidden(true);
        gridSourceArtifact.getColumn("size").setHidden(true);
        gridSourceArtifact.getColumn("categories").setHidden(true);
        gridSourceArtifact.setColumnOrder("presentationName", "symbolicName", "version");
        gridSourceArtifact.addStyleName("my-style");

        idTextStore.setInputPrompt("search by presentation name...");
        idTextStore.setWidth("270px");
        Button findStoreButton = new Button(FontAwesome.CHECK);
        findStoreButton.addClickListener(e ->{
            gridSourceArtifact.setContainerDataSource(new BeanItemContainer<>(ResourceBean.class,
                    resourceService.getFindResourceBeanFromStore(myUI.getSession(), idTextStore.getValue())));
        });
        Button clearStoreButton = new Button(FontAwesome.TIMES);
        clearStoreButton.addClickListener(e -> {
            idTextStore.clear();
            gridSourceArtifact.setContainerDataSource(new BeanItemContainer<>(ResourceBean.class,
                    resourceService.getAllResourceBeanFromStore(myUI.getSession())));
        });

        CssLayout filteringStore = new CssLayout();
        filteringStore.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        filteringStore.addComponents(idTextStore, findStoreButton, clearStoreButton);

        Button addFromStoreButton = new Button("Add artifact");
        addFromStoreButton.setEnabled(false);
        artifactSourceContentLayout.addComponents(filteringStore, gridSourceArtifact, addFromStoreButton);
        artifactSourceContentLayout.setComponentAlignment(addFromStoreButton, Alignment.BOTTOM_CENTER);
        artifactSourceContentLayout.setSpacing(true);
        gridSourceArtifact.setSizeFull();
        artifactSourceContentLayout.setSizeFull();
        artifactSourceContentLayout.setMargin(true);

        // existing collections
        if (myUI.getSession().getAttribute("collectionService") == null) {
            collectionService = new CollectionService();
            myUI.getSession().setAttribute("collectionService", collectionService);
        } else {
            collectionService = (CollectionService) myUI.getSession().getAttribute("collectionService");
        }
        gridSourceCollection.setContainerDataSource(new BeanItemContainer<>(CollectionBean.class,
                collectionService.getCollectionComponentAll()));
        gridSourceCollection.getColumn("id").setHidden(true);
        gridSourceCollection.getColumn("collection").setHidden(true);
        gridSourceCollection.setColumnOrder("name", "version");
        gridSourceCollection.addStyleName("my-style");

        idTextCollection.setInputPrompt("search by name...");
        idTextCollection.setWidth("270px");
        Button findCollectionButton = new Button(FontAwesome.CHECK);
        findCollectionButton.addClickListener(e ->{
            gridSourceCollection.setContainerDataSource(new BeanItemContainer<>(CollectionBean.class,
                    findCollectionService.getFindCollectionBean(idTextCollection.getValue())));
        });
        Button clearCollectionButton = new Button(FontAwesome.TIMES);
        clearCollectionButton.addClickListener(e -> {
            idTextCollection.clear();
            gridSourceCollection.setContainerDataSource(new BeanItemContainer<>(CollectionBean.class,
                    collectionService.getCollectionComponentAll()));
        });

        CssLayout filteringCollection = new CssLayout();
        filteringCollection.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        filteringCollection.addComponents(idTextCollection, findCollectionButton, clearCollectionButton);

        Button addFromCollectionButton = new Button("Add collection");
        addFromCollectionButton.setEnabled(false);
        collectionSourceContentLayout.addComponents(filteringCollection, gridSourceCollection, addFromCollectionButton);
        collectionSourceContentLayout.setComponentAlignment(addFromCollectionButton, Alignment.BOTTOM_CENTER);
        collectionSourceContentLayout.setSpacing(true);
        gridSourceCollection.setSizeFull();
        collectionSourceContentLayout.setSizeFull();
        collectionSourceContentLayout.setMargin(true);

        // source parameters
        VerticalLayout parametersSourceContentLayout = new VerticalLayout();
        nameParameter.setWidth("310px");
        valueParameter.setWidth("310px");
        Button addParameterButton = new Button("Add parameter");
        parametersSourceContentLayout.addComponents(nameParameter, valueParameter, addParameterButton);
        parametersSourceContentLayout.setSpacing(true);
        parametersSourceContentLayout.setMargin(true);

        // source range version component
        VerticalLayout rangeSourceContentLayout = new VerticalLayout();
        nameRange.setWidth("310px");
        valueRange.setWidth("310px");
        Button addRangeButton = new Button("Add with range");
        rangeSourceContentLayout.addComponents(nameRange, valueRange ,addRangeButton);
        rangeSourceContentLayout.setSpacing(true);
        rangeSourceContentLayout.setMargin(true);

        tabSheetSource.addTab(artifactSourceContentLayout).setCaption("Artifact");
        tabSheetSource.addTab(collectionSourceContentLayout).setCaption("Collection");
        tabSheetSource.addTab(parametersSourceContentLayout).setCaption("Parameters");
        tabSheetSource.addTab(rangeSourceContentLayout).setCaption("List component");

        // fill list
        List<CollectionBean> targetCollectionList = new ArrayList<>();
        List<ResourceBean> targetArtifactList = new ArrayList<>();
        for(String s : collectionService.getCollectionComponentDetail(collectionBeanTrans.getId()).getSpecificArtifacts()){
            // is a composite component
            CollectionDetailBean collectionDetailBeanTrans =  collectionService.getCollectionComponentDetail(s);
            if(collectionDetailBeanTrans != null){
                targetCollectionList.add(new CollectionBean(collectionDetailBeanTrans.getId(), collectionDetailBeanTrans.getName(),
                        collectionDetailBeanTrans.getVersion(), true));
            }
            // is an artifact in the store
            else{
                for(ResourceBean resourceBean : resourceService.getAllResourceBeanFromStore(myUI.getSession())){
                    if(resourceBean.getResource().getId().equals(s)){
                        targetArtifactList.add(resourceBean);
                        break;
                    }
                }
            }
        }

        // copied artifact to grid
        gridTargetArtifact.setContainerDataSource(new BeanItemContainer<>(ResourceBean.class, targetArtifactList));
        gridTargetArtifact.getColumn("resource").setHidden(true);
        gridTargetArtifact.getColumn("size").setHidden(true);
        gridTargetArtifact.getColumn("categories").setHidden(true);
        gridTargetArtifact.setColumnOrder("presentationName", "symbolicName", "version");
        gridTargetArtifact.addStyleName("my-style");
        Button removeArtifactButton = new Button("Remove from list");
        removeArtifactButton.setEnabled(false);
        VerticalLayout artifactTargetContentLayout = new VerticalLayout();
        artifactTargetContentLayout.addComponents(gridTargetArtifact, removeArtifactButton);
        artifactTargetContentLayout.setComponentAlignment(removeArtifactButton, Alignment.BOTTOM_CENTER);
        gridTargetArtifact.setSizeFull();
        artifactTargetContentLayout.setSpacing(true);
        artifactTargetContentLayout.setSizeFull();
        artifactTargetContentLayout.setMargin(true);

        // copied collection to grid
        gridTargetCollection.setContainerDataSource(new BeanItemContainer<>(CollectionBean.class, targetCollectionList));
        gridTargetCollection.getColumn("id").setHidden(true);
        gridTargetCollection.setColumnOrder("name", "version", "collection");
        gridTargetCollection.addStyleName("my-style");
        Button removeCollectionButton = new Button("Remove from list");
        removeCollectionButton.setEnabled(false);
        VerticalLayout collectionTargetContentLayout = new VerticalLayout();
        collectionTargetContentLayout.addComponents(gridTargetCollection, removeCollectionButton);
        collectionTargetContentLayout.setComponentAlignment(removeCollectionButton, Alignment.BOTTOM_CENTER);
        gridTargetCollection.setSizeFull();
        collectionTargetContentLayout.setSpacing(true);
        collectionTargetContentLayout.setSizeFull();
        collectionTargetContentLayout.setMargin(true);

        // copied parameter to grid
        List<ParameterBean> targetParameterList = new ArrayList<>();
        //fill list
        for(String s : collectionService.getCollectionComponentDetail(collectionBeanTrans.getId()).getParameters()){
            String[] pom = s.split("=");
            targetParameterList.add(new ParameterBean(pom[0],pom[1]));
        }
        gridTargetParameter.setContainerDataSource(new BeanItemContainer<>(ParameterBean.class, targetParameterList));
        gridTargetParameter.setColumnOrder("name", "value");
        gridTargetParameter.addStyleName("my-style");
        Button removeParameterButton = new Button("Remove from list");
        removeParameterButton.setEnabled(false);
        VerticalLayout parameterTargetContentLayout = new VerticalLayout();
        parameterTargetContentLayout.addComponents(gridTargetParameter, removeParameterButton);
        parameterTargetContentLayout.setComponentAlignment(removeParameterButton, Alignment.BOTTOM_CENTER);
        gridTargetParameter.setSizeFull();
        parameterTargetContentLayout.setSpacing(true);
        parameterTargetContentLayout.setSizeFull();
        parameterTargetContentLayout.setMargin(true);



        // copied artifact whit defined range to grid
        List<ArtifactRangeBean> targetRangeList = new ArrayList<>();
        //fill list
        for(String s : collectionService.getCollectionComponentDetail(collectionBeanTrans.getId()).getRangeArtifacts()){
            String[] pom = s.split("=");
            // delete char [ and ]
            targetRangeList.add(new ArtifactRangeBean(pom[0], pom[1].replace("[","")
                    .replace("]","")));
        }
        gridTargetRange.setContainerDataSource(new BeanItemContainer<>(ArtifactRangeBean.class, targetRangeList));
        gridTargetRange.setColumnOrder("name", "range");
        gridTargetRange.addStyleName("my-style");
        Button removeRangeButton = new Button("Remove from list");
        removeRangeButton.setEnabled(false);
        VerticalLayout rangeTargetContentLayout = new VerticalLayout();
        rangeTargetContentLayout.addComponents(gridTargetRange, removeRangeButton);
        rangeTargetContentLayout.setComponentAlignment(removeRangeButton, Alignment.BOTTOM_CENTER);
        gridTargetRange.setSizeFull();
        rangeTargetContentLayout.setSpacing(true);
        rangeTargetContentLayout.setSizeFull();
        rangeTargetContentLayout.setMargin(true);

        tabSheetTarget.addTab(artifactTargetContentLayout).setCaption("Artifact");
        tabSheetTarget.addTab(collectionTargetContentLayout).setCaption("Collection");
        tabSheetTarget.addTab(parameterTargetContentLayout).setCaption("Parameters");
        tabSheetTarget.addTab(rangeTargetContentLayout).setCaption("List component");

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button saveButton = new Button("Save");
        saveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.setWidth("100px");
        saveButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        Button backButton = new Button("Back");
        backButton.setWidth("100px");
        buttonLayout.addComponents(saveButton, backButton);
        buttonLayout.setSpacing(true);

        // Line skip component (otherwise it doesn't make sense).
        Label firstVerticalSpace = new Label(" ");
        Label secondVerticalSpace = new Label(" ");

        content.addComponents(labelDescription, name, version, firstVerticalSpace, labelSource, tabSheetSource,
                secondVerticalSpace, labelTarget, tabSheetTarget, buttonLayout);
        content.setMargin(new MarginInfo(false,true));
        content.setSpacing(true);

        gridSourceArtifact.addSelectionListener(e ->{
            if(!e.getSelected().isEmpty()){
                resourceBeanSelectSource = (ResourceBean)e.getSelected().iterator().next();
                addFromStoreButton.setEnabled(true);
            }
            else{
                resourceBeanSelectSource = null;
            }
        });

        gridSourceCollection.addSelectionListener(e ->{
            if(!e.getSelected().isEmpty()){
                collectionBeanSelectSource = (CollectionBean)e.getSelected().iterator().next();
                addFromCollectionButton.setEnabled(true);
            }
            else{
                collectionBeanSelectSource = null;
            }
        });

        gridTargetArtifact.addSelectionListener(e ->{
            if(!e.getSelected().isEmpty()){
                resourceBeanSelectTarget = (ResourceBean)e.getSelected().iterator().next();
                removeArtifactButton.setEnabled(true);
            }
            else{
                resourceBeanSelectTarget = null;
            }
        });

        gridTargetCollection.addSelectionListener(e ->{
            if(!e.getSelected().isEmpty()){
                collectionBeanSelectTarget = (CollectionBean)e.getSelected().iterator().next();
                removeCollectionButton.setEnabled(true);
            }
            else{
                collectionBeanSelectTarget = null;
            }
        });

        gridTargetParameter.addSelectionListener(e ->{
            if(!e.getSelected().isEmpty()){
                parameterBeanSelect = (ParameterBean)e.getSelected().iterator().next();
                removeParameterButton.setEnabled(true);
            }
            else{
                parameterBeanSelect = null;
            }
        });

        gridTargetRange.addSelectionListener(e ->{
            if(!e.getSelected().isEmpty()){
                artifactRangeBeanSelect = (ArtifactRangeBean)e.getSelected().iterator().next();
                removeRangeButton.setEnabled(true);
            }
            else{
                artifactRangeBeanSelect = null;
            }
        });

        addFromStoreButton.addClickListener(e ->{
            if(resourceBeanSelectSource != null){
                targetArtifactList.add(resourceBeanSelectSource);

                gridTargetArtifact.setContainerDataSource(new BeanItemContainer<>(ResourceBean.class, targetArtifactList));
                gridTargetArtifact.clearSortOrder();
            }
        });

        addFromCollectionButton.addClickListener(e ->{
            if(collectionBeanSelectSource != null){
                targetCollectionList.add(collectionBeanSelectSource);

                gridTargetCollection.setContainerDataSource(new BeanItemContainer<>(CollectionBean.class, targetCollectionList));
                gridTargetCollection.clearSortOrder();
            }
        });

        addParameterButton.addClickListener(e ->{
            if(nameParameter.isEmpty() || valueParameter.isEmpty()){
                Notification notif = new Notification("Items are not filled!", Notification.Type.WARNING_MESSAGE);
                notif.setDelayMsec(5000);
                notif.show(Page.getCurrent());
            }
            else{
                targetParameterList.add(new ParameterBean(nameParameter.getValue(),valueParameter.getValue()));
                gridTargetParameter.setContainerDataSource(new BeanItemContainer<>(ParameterBean.class, targetParameterList));
                gridTargetParameter.clearSortOrder();
                nameParameter.clear();
                valueParameter.clear();
            }
        });

        addRangeButton.addClickListener(e ->{
            if(nameRange.isEmpty() || valueRange.isEmpty()){
                Notification notif = new Notification("Items are not filled!", Notification.Type.WARNING_MESSAGE);
                notif.setDelayMsec(5000);
                notif.show(Page.getCurrent());
            }
            else{
                targetRangeList.add(new ArtifactRangeBean(nameRange.getValue(),valueRange.getValue()));
                gridTargetRange.setContainerDataSource(new BeanItemContainer<>(ArtifactRangeBean.class, targetRangeList));
                gridTargetRange.clearSortOrder();
                nameRange.clear();
                valueRange.clear();
            }
        });

        removeArtifactButton.addClickListener(e ->{
            if(resourceBeanSelectTarget != null){
                targetArtifactList.remove(resourceBeanSelectTarget);

                gridTargetArtifact.setContainerDataSource(new BeanItemContainer<>(ResourceBean.class, targetArtifactList));
                gridTargetArtifact.clearSortOrder();
            }
        });

        removeCollectionButton.addClickListener(e ->{
            if(collectionBeanSelectTarget != null){
                targetCollectionList.remove(collectionBeanSelectTarget);

                gridTargetCollection.setContainerDataSource(new BeanItemContainer<>(CollectionBean.class, targetCollectionList));
                gridTargetCollection.clearSortOrder();
            }
        });

        removeParameterButton.addClickListener(e ->{
            if(parameterBeanSelect != null){
                targetParameterList.remove(parameterBeanSelect);

                gridTargetParameter.setContainerDataSource(new BeanItemContainer<>(ParameterBean.class, targetParameterList));
                gridTargetParameter.clearSortOrder();
            }
        });

        removeRangeButton.addClickListener(e ->{
            if(artifactRangeBeanSelect != null){
                targetRangeList.remove(artifactRangeBeanSelect);

                gridTargetRange.setContainerDataSource(new BeanItemContainer<>(ArtifactRangeBean.class, targetRangeList));
                gridTargetRange.clearSortOrder();
            }
        });

        backButton.addClickListener(e ->{
            myUI.setContentBodyCollection();
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
                for(ResourceBean rb : targetArtifactList){
                    ids.add(rb.getResource().getId());
                }
                for(CollectionBean cb: targetCollectionList){
                    ids.add(cb.getId());
                }
                for(ParameterBean pb : targetParameterList){
                    parameters.add(pb.getName() + "=" + pb.getValue());
                }
                for(ArtifactRangeBean arb : targetRangeList){
                    artifactsWithRange.add(arb.getName() + "=[" + arb.getRange() + "]");
                }

                boolean status = collectionService.updateCollectionComponent(collectionBeanTrans.getId(), name.getValue(),
                        version.getValue(), ids, parameters, artifactsWithRange);
                if(status){
                    Notification notif = new Notification("Info", "The collection has been updated " +
                            "successfully.", Notification.Type.ASSISTIVE_NOTIFICATION);
                    notif.setPosition(Position.TOP_RIGHT);
                    notif.show(Page.getCurrent());

                }
                else{
                    new Notification("Error updating collection.", Notification.Type.WARNING_MESSAGE)
                            .show(Page.getCurrent());
                }
                myUI.setContentBodyCollection();
            }
        });
        addComponent(content);
    }
}
