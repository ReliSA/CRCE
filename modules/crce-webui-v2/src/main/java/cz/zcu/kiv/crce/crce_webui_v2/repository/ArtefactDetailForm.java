package cz.zcu.kiv.crce.crce_webui_v2.repository;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cz.zcu.kiv.crce.crce_webui_v2.repository.classes.CapabilityAttributeBean;
import cz.zcu.kiv.crce.crce_webui_v2.repository.classes.RequirementAttributeBean;
import cz.zcu.kiv.crce.crce_webui_v2.repository.classes.ResourceBean;
import cz.zcu.kiv.crce.crce_webui_v2.webui.MyUI;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;

@SuppressWarnings("serial")
public class ArtefactDetailForm extends FormLayout{
	private Label resourceLabel = new Label();
	private TabSheet tabsheet = new TabSheet();
	private Button buttonBackProperties = new Button("Back");
	private Button buttonBackCapabilities = new Button("Back");
	private Button buttonBackRequirements = new Button("Back");
	private Button buttonAddCapability = new Button("Add capability");
	private Button buttonAddRequirements = new Button("Add requirement");
	private Grid gridCapabilities = new Grid();
	private Grid gridRequirements = new Grid();
	
	public ArtefactDetailForm(MyUI myUI, ResourceBean resourceBean, boolean isFromStore){
		VerticalLayout content = new VerticalLayout();
		
		resourceLabel.setValue("Details of artefact: " + resourceBean.getPresentationName());
		resourceLabel.addStyleName(ValoTheme.LABEL_BOLD);
		
		// display property
		VerticalLayout propertyContentLayout = new VerticalLayout();
		HorizontalLayout buttonPropertyLayout = new HorizontalLayout();
		
		Label propertiesValues = new Label("<ul>"+
			    "  <li><b>Id: </b>" + resourceBean.getResource().getId() + "</li>" +
			    "  <li><b>Symbolic name: </b>"+ resourceBean.getSymbolicName() + "</li>" +
			    "  <li><b>Size: </b>" + resourceBean.getSize() + "</li>" +
			    "</ul> ", ContentMode.HTML);
		
		buttonBackProperties.setWidth("130px");
		buttonPropertyLayout.addComponent(buttonBackProperties);
		
		propertyContentLayout.addComponents(propertiesValues, buttonPropertyLayout);
		propertyContentLayout.setComponentAlignment(buttonPropertyLayout, Alignment.BOTTOM_CENTER);
		propertyContentLayout.setSpacing(true);
		propertyContentLayout.setMargin(new MarginInfo(true, false));
		propertyContentLayout.setHeight("450px");
		
		// display capability
		VerticalLayout capabilityContentLayout = new VerticalLayout();
		VerticalLayout capabilityTabLayout = new VerticalLayout();
		HorizontalLayout buttonCapabilityLayout = new HorizontalLayout();
		buttonCapabilityLayout.addComponents(buttonAddCapability, buttonBackCapabilities);
		buttonCapabilityLayout.setSpacing(true);
		
		buttonBackCapabilities.setWidth("130px");
		
		List<CapabilityAttributeBean> capabilityAttributeBeanList = new ArrayList<CapabilityAttributeBean>();
		for(Capability capability : resourceBean.getResource().getCapabilities()){
			for(Attribute<?> a : capability.getAttributes()){
				CapabilityAttributeBean capabilityAttributeBean = new CapabilityAttributeBean();
				capabilityAttributeBean.setNameSpace(capability.getNamespace());
				capabilityAttributeBean.setDesignation(a.getName());
				capabilityAttributeBean.setType(a.getType().toString());
				capabilityAttributeBean.setValue(a.getStringValue());
				capabilityAttributeBeanList.add(capabilityAttributeBean);
			}
		}
		
		gridCapabilities.setContainerDataSource(new BeanItemContainer<>(CapabilityAttributeBean.class, capabilityAttributeBeanList));
		gridCapabilities.setColumnOrder("nameSpace", "designation", "value");
		gridCapabilities.addStyleName("my-style");
		HorizontalLayout capabilityFormLayout = new HorizontalLayout(capabilityTabLayout);
		capabilityFormLayout.setSizeFull();
		gridCapabilities.setSizeFull();
		capabilityTabLayout.addComponents(gridCapabilities);
		capabilityFormLayout.setExpandRatio(capabilityTabLayout, 1);
		
		capabilityContentLayout.addComponents(capabilityFormLayout, buttonCapabilityLayout);
		capabilityContentLayout.setComponentAlignment(buttonCapabilityLayout, Alignment.BOTTOM_CENTER);
		capabilityContentLayout.setSpacing(true);
		capabilityContentLayout.setMargin(new MarginInfo(true, false));
		
		buttonAddCapability.addClickListener(e ->{
			myUI.setContentNewCapabilityForm(resourceBean, isFromStore);
		});
		
		// display requirement
		VerticalLayout requirementContentLayout = new VerticalLayout();
		VerticalLayout requirementTabLayout = new VerticalLayout();
		HorizontalLayout buttonRequirementLayout = new HorizontalLayout();
		buttonRequirementLayout.addComponents(buttonAddRequirements, buttonBackRequirements);
		buttonRequirementLayout.setSpacing(true);
		
		buttonBackRequirements.setWidth("130px");
		
		List<RequirementAttributeBean> requirementAttributeBeanList = new ArrayList<RequirementAttributeBean>();
		for(Requirement requirement : resourceBean.getResource().getRequirements()){
			for(Attribute<?> a : requirement.getAttributes()){
				RequirementAttributeBean requirementAttributeBean = new RequirementAttributeBean();
				requirementAttributeBean.setNameSpace(requirement.getNamespace());
				requirementAttributeBean.setDesignation(a.getName());
				requirementAttributeBean.setType(a.getType().toString());
				requirementAttributeBean.setValue(a.getStringValue());
				requirementAttributeBeanList.add(requirementAttributeBean);
			}
		}
		
		gridRequirements.setContainerDataSource(new BeanItemContainer<>(RequirementAttributeBean.class, requirementAttributeBeanList));
		gridRequirements.setColumnOrder("nameSpace", "designation", "value");
		gridRequirements.addStyleName("my-style");
		HorizontalLayout requirementFormLayout = new HorizontalLayout(requirementTabLayout);
		requirementFormLayout.setSizeFull();
		gridRequirements.setSizeFull();
		requirementTabLayout.addComponents(gridRequirements);
		requirementFormLayout.setExpandRatio(requirementTabLayout, 1);
		
		requirementContentLayout.addComponents(requirementFormLayout, buttonRequirementLayout);
		requirementContentLayout.setComponentAlignment(buttonRequirementLayout, Alignment.BOTTOM_CENTER);
		requirementContentLayout.setSpacing(true);
		requirementContentLayout.setMargin(new MarginInfo(true, false));
		
		
	
		tabsheet.addTab(propertyContentLayout).setCaption("Properties");
		tabsheet.addTab(capabilityContentLayout).setCaption("Capability");
		tabsheet.addTab(requirementContentLayout).setCaption("Requirement");
		
		buttonBackProperties.addClickListener(e ->{
			if(isFromStore){
				myUI.setContentBodyStore();
			}
			else{
				myUI.setContentBodyBuffer();
			}
		});
		
		buttonBackCapabilities.addClickListener(e ->{
			if(isFromStore){
				myUI.setContentBodyStore();
			}
			else{
				myUI.setContentBodyBuffer();
			}
		});
		
		buttonBackRequirements.addClickListener(e ->{
			if(isFromStore){
				myUI.setContentBodyStore();
			}
			else{
				myUI.setContentBodyBuffer();
			}
		});
		
		content.addComponents(resourceLabel, tabsheet);
		content.setSpacing(true);
		content.setMargin(new MarginInfo(false, true));
		
		addComponent(content);
	}
}
