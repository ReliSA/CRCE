package cz.zcu.kiv.crce.crce_webui_v2.versioning;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.zcu.kiv.crce.crce_component_versioning.api.bean.ComponentBean;
import cz.zcu.kiv.crce.crce_component_versioning.api.impl.VersioningService;
import cz.zcu.kiv.crce.crce_webui_v2.internal.Activator;
import cz.zcu.kiv.crce.crce_webui_v2.repository.classes.ResourceBean;
import cz.zcu.kiv.crce.crce_webui_v2.repository.services.ResourceService;
import cz.zcu.kiv.crce.crce_webui_v2.versioning.services.FindCompositeService;
import cz.zcu.kiv.crce.crce_webui_v2.webui.MyUI;

import java.util.ArrayList;
import java.util.List;

public class VersioningNewForm extends FormLayout{
    private static final long serialVersionUID = 3539728975121021002L;
    private Label labelDescription = new Label("Add a new collection");
    private TextField name = new TextField("name");
    private TextField version = new TextField("version");
    private Label labelStore = new Label("Artifact in Store");
    private Label labelVersioning = new Label("Composite component");
    private TextField idTextStore = new TextField();
    private TextField idTextVersioning = new TextField();
    private Grid gridStore = new Grid();
    private Grid gridVersioning = new Grid();
    private Grid newCompositeGrid = new Grid();
    private ResourceService resourceService;
    private transient VersioningService versioningService;
    private transient FindCompositeService findCompositeService;
    private transient ResourceBean resourceBeanSelect;
    private transient ComponentBean componentBeanSelect;
    private transient ComponentBean newComponentBeanSelect;

    public VersioningNewForm(MyUI myUI){
        VerticalLayout content = new VerticalLayout();
        VerticalLayout gridLeftLayout = new VerticalLayout();
        VerticalLayout gridRightLayout = new VerticalLayout();
        HorizontalLayout gridLayout = new HorizontalLayout();

        resourceService = new ResourceService(Activator.instance().getMetadataService());
        findCompositeService = new FindCompositeService();

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
        gridLeftLayout.addComponents(labelStore, filteringStore, gridStore, addFromStoreButton);
        gridLeftLayout.setComponentAlignment(addFromStoreButton, Alignment.BOTTOM_CENTER);
        gridLeftLayout.setSpacing(true);

        versioningService = new VersioningService();
        gridVersioning.setContainerDataSource(new BeanItemContainer<>(ComponentBean.class,
                versioningService.getCompositeComponentAll()));
        gridVersioning.getColumn("id").setHidden(true);
        gridVersioning.getColumn("composite").setHidden(true);
        gridVersioning.setColumnOrder("name", "version");
        gridVersioning.addStyleName("my-style");

        idTextVersioning.setInputPrompt("search by name...");
        idTextVersioning.setWidth("270px");
        Button findVersioningButton = new Button(FontAwesome.CHECK);
        findVersioningButton.addClickListener(e ->{
            gridVersioning.setContainerDataSource(new BeanItemContainer<>(ComponentBean.class,
                    findCompositeService.getFindComponentBean(idTextVersioning.getValue())));
        });
        Button clearVersioningButton = new Button(FontAwesome.TIMES);
        clearVersioningButton.addClickListener(e -> {
            idTextVersioning.clear();
            gridVersioning.setContainerDataSource(new BeanItemContainer<>(ComponentBean.class,
                    versioningService.getCompositeComponentAll()));
        });

        CssLayout filteringVersioning = new CssLayout();
        filteringVersioning.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        filteringVersioning.addComponents(idTextVersioning, findVersioningButton, clearVersioningButton);

        Button addFromVersioningButton = new Button("Add composite");
        addFromVersioningButton.setEnabled(false);
        gridRightLayout.addComponents(labelVersioning, filteringVersioning, gridVersioning, addFromVersioningButton);
        gridRightLayout.setComponentAlignment(addFromVersioningButton, Alignment.BOTTOM_CENTER);
        gridRightLayout.setSpacing(true);

        gridLayout.addComponents(gridLeftLayout,gridRightLayout);
        gridLayout.setSpacing(true);
        gridLayout.setSizeFull();
        gridStore.setSizeFull();
        gridVersioning.setSizeFull();
        gridLayout.setExpandRatio(gridLeftLayout, 1);
        gridLayout.setExpandRatio(gridRightLayout, 1);

        content.setMargin(new MarginInfo(false,true));
        content.setSpacing(true);

        List<ComponentBean> newComponentList = new ArrayList<>();
        newCompositeGrid.setContainerDataSource(new BeanItemContainer<>(ComponentBean.class, newComponentList));
        newCompositeGrid.getColumn("id").setHidden(true);
        newCompositeGrid.setColumnOrder("name", "version", "composite");
        newCompositeGrid.addStyleName("my-style");
        newCompositeGrid.setSizeFull();

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        Button saveButton = new Button("Save");
        saveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.setWidth("100px");
        saveButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        Button removeButton = new Button("Remove");
        removeButton.setEnabled(false);
        buttonLayout.addComponents(saveButton, removeButton);
        name.setWidth("270px");
        version.setWidth("270px");
        content.addComponents(labelDescription, name, version, gridLayout, newCompositeGrid, buttonLayout);
        content.setComponentAlignment(buttonLayout,Alignment.BOTTOM_CENTER);

        gridStore.addSelectionListener(e ->{
            if(!e.getSelected().isEmpty()){
                resourceBeanSelect = (ResourceBean)e.getSelected().iterator().next();
                addFromStoreButton.setEnabled(true);
            }
            else{
                resourceBeanSelect = null;
            }
        });

        gridVersioning.addSelectionListener(e ->{
            if(!e.getSelected().isEmpty()){
                componentBeanSelect = (ComponentBean)e.getSelected().iterator().next();
                addFromVersioningButton.setEnabled(true);
            }
            else{
                componentBeanSelect = null;
            }
        });

        newCompositeGrid.addSelectionListener(e ->{
            if(!e.getSelected().isEmpty()){
                newComponentBeanSelect = (ComponentBean)e.getSelected().iterator().next();
                removeButton.setEnabled(true);
            }
            else{
                newComponentBeanSelect = null;
            }
        });


        addFromStoreButton.addClickListener(e ->{
            if(resourceBeanSelect != null){
                boolean contains = false;
                for(ComponentBean cb : newComponentList){
                    if(resourceBeanSelect.getResource().getId() == cb.getId()){
                        contains = true;
                    }
                }
                if(!contains){
                    newComponentList.add(new ComponentBean(resourceBeanSelect.getResource().getId(),
                            resourceBeanSelect.getPresentationName(), resourceBeanSelect.getVersion(), false));

                    newCompositeGrid.setContainerDataSource(new BeanItemContainer<>(ComponentBean.class, newComponentList));
                    newCompositeGrid.clearSortOrder();
                }
            }
        });

        addFromVersioningButton.addClickListener(e ->{
            if(componentBeanSelect != null){
                newComponentList.add(componentBeanSelect);

                newCompositeGrid.setContainerDataSource(new BeanItemContainer<>(ComponentBean.class, newComponentList));
                newCompositeGrid.clearSortOrder();
            }
        });

        removeButton.addClickListener(e ->{
            if(newComponentBeanSelect != null){
                newComponentList.remove(newComponentBeanSelect);

                newCompositeGrid.setContainerDataSource(new BeanItemContainer<>(ComponentBean.class, newComponentList));
                newCompositeGrid.clearSortOrder();
            }
        });

        saveButton.addClickListener(e ->{
            List<String> idList = new ArrayList<>();
            for(ComponentBean cb: newComponentList){
                idList.add(cb.getId());
            }
            boolean status = versioningService.setCompositeComponent(name.getValue(), version.getValue(), idList);
            if(status){
                Notification notif = new Notification("Info", "The new composite component has been " +
                        "successfully saved.", Notification.Type.ASSISTIVE_NOTIFICATION);
                notif.setPosition(Position.TOP_RIGHT);
                notif.show(Page.getCurrent());

            }
            else{
                new Notification("Error saving new composite component.", Notification.Type.WARNING_MESSAGE)
                        .show(Page.getCurrent());
            }
            myUI.setContentBodyVersioningNew();
        });

        addComponent(content);
    }
}
