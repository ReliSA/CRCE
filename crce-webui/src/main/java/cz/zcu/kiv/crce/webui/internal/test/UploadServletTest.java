package cz.zcu.kiv.crce.webui.internal.test;
import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Ignore;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import junit.framework.TestCase;

@Ignore
public class UploadServletTest extends TestCase {
	WebClient webClient = new WebClient();
	HtmlPage currentPage;
	
	public void testUpload(){
		try {
			currentPage = (HtmlPage) webClient.getPage("http://localhost:8090/crce/resource");
			
			
			
			
		} catch (FailingHttpStatusCodeException e) {
			fail(e.getLocalizedMessage());
		} catch (MalformedURLException e) {			
			fail(e.getLocalizedMessage());
		} catch (IOException e) {			
			fail(e.getLocalizedMessage());
		}
	}
}
