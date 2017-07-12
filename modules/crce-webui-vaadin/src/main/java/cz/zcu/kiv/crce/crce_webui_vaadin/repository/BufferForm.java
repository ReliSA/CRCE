package cz.zcu.kiv.crce.crce_webui_vaadin.repository;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cz.zcu.kiv.crce.crce_webui_vaadin.internal.Activator;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Buffer;

@SuppressWarnings("serial")
public class BufferForm extends FormLayout{
	private Label labelForm = new Label("Artefact in Buffer");
	private Grid gridBuffer = new Grid();
	private HorizontalLayout formLayout;
	
	public BufferForm(VaadinSession session){
		VerticalLayout content = new VerticalLayout();
		VerticalLayout fieldLayout = new VerticalLayout();
		
		labelForm.addStyleName(ValoTheme.LABEL_BOLD);
		
		Buffer buffer = Activator.instance().getBuffer(session.getSession());
		
		gridBuffer.setContainerDataSource(new BeanItemContainer<>(Resource.class, buffer.getResources()));
		gridBuffer.addStyleName("my-style");
		
		fieldLayout.addComponents(labelForm, gridBuffer);
		fieldLayout.setSpacing(true);
		
		formLayout = new HorizontalLayout(fieldLayout);
		formLayout.setSizeFull();
		gridBuffer.setSizeFull();
		formLayout.setExpandRatio(fieldLayout, 1);
		
		content.addComponent(formLayout);
		content.setMargin(new MarginInfo(false, true));
		content.setSpacing(true);
		addComponent(content);
	}
}
