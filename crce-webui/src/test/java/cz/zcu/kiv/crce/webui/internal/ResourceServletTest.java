package cz.zcu.kiv.crce.webui.internal;

import static org.junit.Assert.*;


import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


@Ignore
public class ResourceServletTest {
	

	@Test
	public void testDoGetHttpServletRequestHttpServletResponse() {
		WebClient webClient = new WebClient();
		HtmlPage currentPage;
		try {
			currentPage =(HtmlPage) webClient.getPage("http://localhost:8090/crce/resource");
			
			String pageAsText = currentPage.asText();
				assertTrue("Page does not contains uri: cz.zcu.kiv.crce.repository.api", pageAsText.contains("cz.zcu.kiv.crce.repository.api"));
		} catch (FailingHttpStatusCodeException e) {
			fail("FHSCE: " + e.toString());
		} catch (MalformedURLException e) {
			fail("MUE: " + e.toString());
		} catch (IOException e) {
			fail("IOE: " + e.toString());
		}
	}


}
