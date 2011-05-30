package cz.zcu.kiv.crce.webui.internal;

import static org.junit.Assert.*;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.attachment.Attachment;
import com.gargoylesoftware.htmlunit.attachment.AttachmentHandler;
import com.gargoylesoftware.htmlunit.attachment.CollectingAttachmentHandler;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@Ignore
public class DownloadServletTest {
	

	@Test
	public void testDoGetHttpServletRequestHttpServletResponse() {
		WebClient webClient = new WebClient();
		HtmlPage currentPage;
		try {
			currentPage =(HtmlPage) webClient.getPage("http://localhost:8080/crce/resource");
			
//			Zkontroluj, že se načetl Store 
			String pageAsText = currentPage.asText();
				assertTrue("Page does not contains uri: cz.zcu.kiv.crce.repository.api", pageAsText.contains("cz.zcu.kiv.crce.repository.api"));
			
				
			List<Attachment> attList = new ArrayList<Attachment>();
			AttachmentHandler currentAttachmentHandler = webClient.getAttachmentHandler();
			webClient.setAttachmentHandler(new CollectingAttachmentHandler(attList));
			
			currentPage.getAnchorByHref("download?uri=file:/home/koty/REPO/cz.zcu.kiv.crce.repository.api_1.0.0.SNAPSHOT.jar").click();

//          Kontrola že se soubor 'stáhnul'
            assertEquals("Expected number of files are different than actual number.", 1, attList.size());
            assertEquals("Expected filename is different than actual name.", "cz.zcu.kiv.crce.repository.api.jar", attList.get(0).getSuggestedFilename());
		           
//		    Vrácení původního handleru.
		    webClient.setAttachmentHandler(currentAttachmentHandler);
		    
		} catch (FailingHttpStatusCodeException e) {
			fail("FHSCE: " + e.toString());
		} catch (MalformedURLException e) {
			fail("MUE: " + e.toString());
		} catch (IOException e) {
			fail("IOE: " + e.toString());
		}
	}


}
