package cz.zcu.kiv.crce.crce_webui_v2.outer;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;

import cz.zcu.kiv.crce.crce_external_repository.api.MavenIndex;
import cz.zcu.kiv.crce.crce_external_repository.api.SettingsUrl;
import cz.zcu.kiv.crce.crce_webui_v2.webui.MyUI;

@SuppressWarnings("serial")
public class CheckMavenIndexForm extends FormLayout {
	private Button checkButton = new Button("Check index");
	private ProgressBar bar = new ProgressBar();
	private Label statusLabel = new Label("This might take a while on first run, so please be patient!");
	private MyUI myUI;
	private String result;

	public CheckMavenIndexForm(MyUI myUI) {
		this.myUI = myUI;
		VerticalLayout content = new VerticalLayout();
		HorizontalLayout progressLayout = new HorizontalLayout();
		bar.setVisible(false);

		checkButton.addClickListener(e -> {
			statusLabel.setValue("Please waiting...");
			checkButton.setEnabled(false);
			bar.setIndeterminate(true);
			bar.setVisible(true);
			new HelperIndexingThread(myUI.getSession()).start();
		});

		progressLayout.addComponents(checkButton, bar);
		progressLayout.setSpacing(true);
		content.addComponents(statusLabel, progressLayout);
		content.setSpacing(true);
		addComponent(content);
	}

	class HelperIndexingThread extends Thread {
		VaadinSession session;
		public HelperIndexingThread(VaadinSession session){
			this.session = session;
		}
		public void run() {
			try {
				MavenIndex check = new MavenIndex();
				result = check.checkIndex((SettingsUrl)session.getAttribute("settingsUrl"));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				myUI.access(() -> checkButton.setEnabled(true));
				myUI.access(() -> bar.setVisible(false));
				myUI.access(() -> bar.setIndeterminate(false));
				if (result != null) {
					myUI.access(() -> statusLabel.setValue(result));
					session.setAttribute("mavenIndex", true);
				}
				else{
					myUI.access(() -> statusLabel.setValue("Error when creating index!"));
				}
			}
			// Inform that we have stopped running

			/*// Another way without using lambda expressions
			myUI.access(new Runnable() {
				@Override
				public void run() {
					statusLabel.setValue("Done!");
					checkButton.setEnabled(true);
					bar.setIndeterminate(false);
					bar.setVisible(false);
				}
			});*/

		}
	}
}
