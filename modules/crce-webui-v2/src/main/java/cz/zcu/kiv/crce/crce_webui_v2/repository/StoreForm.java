package cz.zcu.kiv.crce.crce_webui_v2.repository;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cz.zcu.kiv.crce.crce_webui_v2.internal.Activator;
import cz.zcu.kiv.crce.crce_webui_v2.repository.classes.ResourceBean;
import cz.zcu.kiv.crce.crce_webui_v2.repository.services.ResourceService;
import cz.zcu.kiv.crce.crce_webui_v2.webui.MyUI;

public class StoreForm extends FormLayout{
	private static final long serialVersionUID = 7439970261502810719L;
	private Label labelForm = new Label("Artefact in Store");
	private TextField idText = new TextField();
	private Grid gridStore = new Grid();
	private PopupView popup;
	private transient ResourceBean resourceBeanSelect;
	private ResourceService resourceService;
	
	public StoreForm(MyUI myUI){
		VerticalLayout content = new VerticalLayout();
		VerticalLayout fieldLayout = new VerticalLayout();
		HorizontalLayout buttonLayout = new HorizontalLayout();
		
		resourceService = new ResourceService(Activator.instance().getMetadataService());
		
		Button buttonDetail = new Button("Detail");
		buttonDetail.setWidth("100px");
		Button buttonRemove = new Button("Remove");
		buttonRemove.setWidth("100px");
		
		buttonDetail.setStyleName(ValoTheme.BUTTON_PRIMARY);
		buttonDetail.setClickShortcut(KeyCode.ENTER);
		
		labelForm.addStyleName(ValoTheme.LABEL_BOLD);
		
		gridStore.setContainerDataSource(new BeanItemContainer<>(ResourceBean.class, resourceService.getAllResourceBeanFromStore(myUI.getSession())));
		gridStore.getColumn("resource").setHidden(true);
		gridStore.getColumn("size").setHidden(true);
		gridStore.setColumnOrder("presentationName", "symbolicName", "version", "categories");
		gridStore.addStyleName("my-style");
		
		idText.setInputPrompt("search by presentation name...");
		idText.setWidth("270px");
		
		Button findButton = new Button(FontAwesome.CHECK);
		findButton.addClickListener(e ->{
			gridStore.setContainerDataSource(new BeanItemContainer<>(ResourceBean.class, 
					resourceService.getFindResourceBeanFromStore(myUI.getSession(), idText.getValue())));
		});
		Button clearButton = new Button(FontAwesome.TIMES);
		clearButton.addClickListener(e -> {
			myUI.setContentBodyStore();
		});
		
		CssLayout filtering = new CssLayout();
		filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		filtering.addComponents(idText, findButton, clearButton);
		
		fieldLayout.addComponents(labelForm, filtering, gridStore);
		fieldLayout.setSpacing(true);
		
		HorizontalLayout formLayout = new HorizontalLayout(fieldLayout);
		formLayout.setSizeFull();
		gridStore.setSizeFull();
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
		
		gridStore.addSelectionListener(e ->{
			buttonLayout.setVisible(true);
			resourceBeanSelect = (ResourceBean)e.getSelected().iterator().next();
		});
		
		buttonDetail.addClickListener(e ->{
			myUI.setContentArtefactDetailForm(resourceBeanSelect, true);
		});
		
		buttonRemove.addClickListener(e ->{
			popup.setPopupVisible(true);
			yesRemoveButton.addClickListener(ev -> {
				boolean result = resourceService.removeResorceFromStore(myUI.getSession(), resourceBeanSelect.getResource());
				if(result){
					Notification notif = new Notification("Info", "Artifact sucess removed",
							Notification.Type.ASSISTIVE_NOTIFICATION);
					notif.setPosition(Position.TOP_RIGHT);
					notif.show(Page.getCurrent());
				}
				else{
					new Notification("Could not remove artefact from store", Notification.Type.WARNING_MESSAGE)
					.show(Page.getCurrent());
				}
				myUI.setContentBodyStore();
			});
		});
		
		addComponent(content);
	}
}
