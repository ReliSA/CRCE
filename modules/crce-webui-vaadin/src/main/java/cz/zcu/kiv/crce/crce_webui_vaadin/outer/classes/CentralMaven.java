package cz.zcu.kiv.crce.crce_webui_vaadin.outer.classes;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Tree;

public class CentralMaven {
	private String centralMavenUrl;
	private boolean enableGroupSearch;
	private Tree resultSearchTree = new Tree("Result Search");
	private VaadinSession session;

	public CentralMaven(VaadinSession session) {
		this.session = session;
	}

	public Tree getTree(String group, String artifact, String version, Object packaging) {
		if (session.getAttribute("settingsUrl") == null) {
			SettingsUrl settings = new SettingsUrl();
			centralMavenUrl = settings.getCentralMavenUrl();
			enableGroupSearch = settings.isEnableGroupSearch();
		} else {
			centralMavenUrl = ((SettingsUrl) session.getAttribute("settingsUrl")).getCentralMavenUrl();
			enableGroupSearch = ((SettingsUrl) session.getAttribute("settingsUrl")).isEnableGroupSearch();
		}
		// reset tree
		resultSearchTree.removeAllItems();
		if (!artifact.isEmpty() || !version.isEmpty()) {
			directSearch(group, artifact, version, packaging.toString());
		} else if (enableGroupSearch && !group.isEmpty()) {
			onlyGroupSearch(group);
		}
		if (resultSearchTree.size() == 0) {
			return null;
		} else {
			return resultSearchTree;
		}
	}

	private void directSearch(String group, String artifact, String version, String packaging) {
		URL website;
		try {
			website = new URL(centralMavenUrl + group.replace('.', '/') + "/" + artifact + "/" + version + "/"
					+ artifact + "-" + version + "." + packaging);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			if (rbc.isOpen()) {
				String[] pom = group.split("\\.");
				// adding group
				resultSearchTree.addItem(pom[0]);
				for (int i = 1; i < pom.length; i++) {
					resultSearchTree.addItem(pom[i]);
					resultSearchTree.setParent(pom[i], pom[i - 1]);
				}
				resultSearchTree.addItem(artifact);
				resultSearchTree.setParent(artifact, pom[pom.length - 1]);
				resultSearchTree.addItem(version);
				resultSearchTree.setParent(version, artifact);
				// end artifact
				resultSearchTree.addItem(website.toString());
				resultSearchTree.setParent(website.toString(), version);
				resultSearchTree.setItemCaption(website.toString(), artifact + "-" + version + "." + packaging);
				resultSearchTree.setChildrenAllowed(website.toString(), false);
				if (packaging.equals("jar") || packaging.equals("war")) {
					resultSearchTree.setItemIcon(website.toString(), FontAwesome.GIFT);
				} else if (packaging.equals("xml") || packaging.equals("pom")) {
					resultSearchTree.setItemIcon(website.toString(), FontAwesome.CODE);
				} else {
					resultSearchTree.setItemIcon(website.toString(), FontAwesome.FILE);
				}
				rbc.close();
			}
		} catch (IOException e) {
			resultSearchTree.clear();
		}
	}

	private void onlyGroupSearch(String group) {
		try {
			list(new URL(centralMavenUrl + group.replace('.', '/') + "/"));
		} catch (IOException e) {
			//resultSearchTree.addItem(centralMavenUrl + group.replace('.', '/'));
			resultSearchTree.clear();
		}
	}

	private void list(URL path) throws IOException {
		Document doc = Jsoup.connect(path.toString()).get();
		for (Element file : doc.select("a").not("[href=../]")) {
			if (file.text().endsWith("/")) {
				resultSearchTree.addItem(path.toString() + file.text());
				resultSearchTree.setItemCaption(path.toString() + file.text(),
						file.text().substring(0, (file.text().length() - 1)));
				resultSearchTree.setParent(path.toString() + file.text(), path.toString());
				list(new URL(path.toString() + file.text()));
			} else {
				resultSearchTree.addItem(path.toString() + file.text());
				resultSearchTree.setItemCaption(path.toString() + file.text(), file.text());
				resultSearchTree.setParent(path.toString() + file.text(), path.toString());
				resultSearchTree.setChildrenAllowed(path.toString() + file.text(), false);
				if (file.text().endsWith(".jar") || file.text().endsWith(".war")) {
					resultSearchTree.setItemIcon(path.toString() + file.text(), FontAwesome.GIFT);
				} else if (file.text().endsWith(".xml") || file.text().endsWith(".pom")) {
					resultSearchTree.setItemIcon(path.toString() + file.text(), FontAwesome.CODE);
				} else {
					resultSearchTree.setItemIcon(path.toString() + file.text(), FontAwesome.FILE);
				}
			}
		}
	}
}
