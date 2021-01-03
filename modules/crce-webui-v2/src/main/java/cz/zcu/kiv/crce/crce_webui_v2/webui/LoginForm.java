package cz.zcu.kiv.crce.crce_webui_v2.webui;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class LoginForm extends FormLayout{
	private Panel panel = new Panel("CRCE - Signing in");
	private TextField login = new TextField("Name");
	private PasswordField password = new PasswordField("Password");
	private Button loginButton = new Button("Log in");
	private Button guestButton = new Button("Guest");
	private Label error = new Label();
	private HorizontalLayout loginFormLayout = new HorizontalLayout();
	private HorizontalLayout submitErrorLayout = new HorizontalLayout();
	private VerticalLayout panelLayout = new VerticalLayout();
	private VerticalLayout content = new VerticalLayout();

	public LoginForm(MyUI myUI) {

		loginButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		loginButton.setClickShortcut(KeyCode.ENTER);

		login.setWidth("220px");
		password.setWidth("220px");

		loginFormLayout.addComponents(login, password);
		loginFormLayout.setSpacing(true);
		
		submitErrorLayout.addComponents(loginButton, guestButton, error);
		submitErrorLayout.setSpacing(true);

		panelLayout.addComponents(loginFormLayout, submitErrorLayout);
		panelLayout.setMargin(new MarginInfo(true, true));
		panelLayout.setSpacing(true);
		panel.setContent(panelLayout);
		panel.setWidth("500px");
		panel.setHeight("200px");
		content.setSizeFull();
		content.addComponent(panel);
		content.setComponentAlignment(panel, Alignment.MIDDLE_CENTER);

		loginButton.addClickListener(e -> {
			
			// kontrola vstupu např. volání služby apod.
			// v případě neúspěchu doplnění error hlášky např:
			submitErrorLayout.removeComponent(error);
			error = new Label("<p style=\"font-family:'Arial';color:rgb(237,18,29)\">Incorrect login</p>",
					ContentMode.HTML);
			submitErrorLayout.addComponent(error);
			
			// v případě úspěchu:
			/*myUI.getSession().setAttribute("singed", login.getValue());
			myUI.loginExistSession();*/
		});
		
		guestButton.addClickListener(e -> {
			myUI.getSession().setAttribute("singed", "guest");
			myUI.loginExistSession();
		});
		
		addComponent(content);
	}
}
