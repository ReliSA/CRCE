package cz.zcu.kiv.crce.webui.internal;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import cz.zcu.kiv.crce.metadata.Resource;
@Ignore
public class ResourceServletTest {
	

	@Test
	public void testDoGetHttpServletRequestHttpServletResponse() {
		WebClient webClient = new WebClient();
		HtmlPage currentPage;
		try {
			System.out.println("dsdsasa");
			currentPage =(HtmlPage) webClient.getPage("http://localhost:8090/crce/resource");
			Resource[] resources = Activator.instance().getStore().getRepository().getResources();
			String pageAsText = currentPage.asText();
			for (Resource resource : resources) {
				assertTrue("Page does not contains uri: " + resource.getUri().toString(), pageAsText.contains(resource.getUri().toString()));
				System.out.println("URI Found: " + resource.getUri().toString());
			}	
		} catch (FailingHttpStatusCodeException e) {
			fail("FHSCE: " + e.toString());
		} catch (MalformedURLException e) {
			fail("MUE: " + e.toString());
		} catch (IOException e) {
			fail("IOE: " + e.toString());
		}
	}


}
