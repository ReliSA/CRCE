package cz.zcu.kiv.crce.webui.internal;

import static org.junit.Assert.*;


import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@Ignore
public class ResourceServletTest {
	

	@Test
	public void testDoGetHttpServletRequestHttpServletResponse() {
		WebClient webClient = new WebClient();
		HtmlPage currentPage;
		try {
			currentPage =(HtmlPage) webClient.getPage("http://localhost:8090/crce/resource");
			
//			Zkontroluj, že se načetl Store 
			String pageAsText = currentPage.asText();
				assertTrue("Page does not contains uri: cz.zcu.kiv.crce.repository.api", pageAsText.contains("cz.zcu.kiv.crce.repository.api"));
				
			((HtmlInput)currentPage.getFirstByXPath("//form/input[@name='filter']")).setValueAttribute("badfilter");
			currentPage = ((HtmlInput)currentPage.getFirstByXPath("//form/input[@value='OK']")).click();
			
//			Kontrola chybové hlášky zadán špatný filter			
			pageAsText = currentPage.asText();
			assertTrue("Page does not contains error.", pageAsText.contains("badfilter is not a valid filter"));
			
//			Klikni na link Upload			
			currentPage = currentPage.getAnchorByText("Upload").click();
			
//			Kontrola, že chybová hláška zmizela			
			pageAsText = currentPage.asText();
			assertTrue("Page does not contains error.", !pageAsText.contains("badfilter is not a valid filter"));
			
			
//			Zkontroluj, že se načetl Buffer			
			pageAsText = currentPage.asText();
			assertTrue("The page is not buffer.", pageAsText.contains("No resources uploaded."));
		
//			Klikni na Plugins
			currentPage = currentPage.getAnchorByText("Plugins").click();
			
//			Zkontroluj, že se načetla stránka s pluginy
			pageAsText = currentPage.asText();
			assertTrue("The page is not plugins.", pageAsText.contains("cz.zcu.kiv.crce.metadata.combined.internal.CombinedResourceDAO"));
			
		} catch (FailingHttpStatusCodeException e) {
			fail("FHSCE: " + e.toString());
		} catch (MalformedURLException e) {
			fail("MUE: " + e.toString());
		} catch (IOException e) {
			fail("IOE: " + e.toString());
		}
	}


}
