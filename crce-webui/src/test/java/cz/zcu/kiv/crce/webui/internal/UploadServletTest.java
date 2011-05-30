package cz.zcu.kiv.crce.webui.internal;


import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Ignore;



import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;

import com.gargoylesoftware.htmlunit.html.HtmlFileInput;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

import junit.framework.TestCase;

@Ignore
public class UploadServletTest extends TestCase{
	WebClient webClient = new WebClient();
	HtmlPage currentPage;
	@Override
	protected void setUp() throws Exception {		
		super.setUp();
		try {
			currentPage = (HtmlPage) webClient.getPage("http://localhost:8080/crce/resource?link=buffer");
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void testUpload() throws IOException{		
		String beforeText = currentPage.asText();
		HtmlFileInput hfi = (HtmlFileInput)currentPage.getFirstByXPath("//input[@name='bundle']");
		String path = System.getProperties().getProperty("user.dir").toString()+"\\target\\"+"crce-webui-1.0.0-SNAPSHOT.war";
		hfi.setValueAttribute(path);		
		currentPage = ((HtmlSubmitInput)currentPage.getFirstByXPath("//input[@type='submit' and @value='Upload']")).click();
		String afterText = currentPage.asText();
		assertFalse(beforeText.equals(afterText));
		assertTrue("Not uploaded!!!",currentPage.asText().contains("Upload was succesful"));
		//Testing buffer
		currentPage = ((HtmlSubmitInput)currentPage.getFirstByXPath("//input[@type='submit' and @value='commit all to repository']")).click();
		assertTrue("Not transferred to buffer!!!", currentPage.asText().contains("All resources commited succesfully"));
	}
	
}
