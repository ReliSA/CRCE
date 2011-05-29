package cz.zcu.kiv.crce.webui.internal;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@Ignore
public class EditServletTest {
	WebClient webClient = new WebClient();
	HtmlPage currentPage;
	String pageAsText;
	
	@Before
	public void prepareWebClient() {
		try {
			currentPage =(HtmlPage) webClient.getPage("http://localhost:8090/crce/resource");
		} catch (FailingHttpStatusCodeException e) {
			fail("FHSCE: " + e.toString());
		} catch (MalformedURLException e) {
			fail("MUE: " + e.toString());
		} catch (IOException e) {
			fail("IOE: " + e.toString());
		}
	}
	
	@Test
	public void testEditCategories() {
		try {
			currentPage = currentPage.getAnchorByHref("edit?type=category&uri=file:/home/koty/REPO/cz.zcu.kiv.crce.repository.api_1.0.0.SNAPSHOT.jar").click();
			
//			Kontrola zda se načetla správná stránka
			pageAsText = currentPage.asText();
			assertTrue("Wrong page.", pageAsText.contains("Existing categories"));
			
			currentPage = currentPage.getAnchorByText("[remove]").click();
			
//			Kontrola zda vyskočila chybová hláška protože původní kategorie nejdou odstranit.
			pageAsText = currentPage.asText();
			assertTrue("Page does not contains error.", pageAsText.contains("Cannot delete category."));
			
			currentPage = currentPage.getAnchorByText("[add new category]").click();
			
//			Kontrola zda se načetla správná stránka
			pageAsText = currentPage.asText();
			assertTrue("Wrong page.", pageAsText.contains("Add new category"));
			
			((HtmlInput)currentPage.getFirstByXPath("//input[@name='category']")).setValueAttribute("newCategory");
			currentPage = ((HtmlInput)currentPage.getFirstByXPath("//input[@value='Save category']")).click();
			
			
			HtmlForm form = currentPage.getFormByName("addCategory");
			HtmlButton submitButton = (HtmlButton)currentPage.createElement("button");
			submitButton.setAttribute("type", "submit");
			form.appendChild(submitButton);
			currentPage = submitButton.click();
			
//			Kontrola zda se načetla správná stránka
			pageAsText = currentPage.asText();
			assertTrue("Wrong page.", pageAsText.contains("Existing categories"));
			
//			Kontrola zda se přidala kategorie
			pageAsText = currentPage.asText();
			assertTrue("Category does not exist.", pageAsText.contains("newCategory"));
			
//			Odstraň novou kategorii
			currentPage = currentPage.getAnchorByHref("edit?type=deleteCategory&uri=file:/home/koty/REPO/cz.zcu.kiv.crce.repository.api_1.0.0.SNAPSHOT.jar&category=newCategory").click();
			
//			Kontrola zda se odstranila kategorie
			pageAsText = currentPage.asText();
			assertTrue("Category should not exist.", !pageAsText.contains("newCategory"));	
			
//			Kontrola zda nevyskočila chybová hláška protože původní kategorie nejdou odstranit.
			pageAsText = currentPage.asText();
			assertTrue("Page contains error.", !pageAsText.contains("Cannot delete category."));
			
			
		} catch (FailingHttpStatusCodeException e) {
			fail("FHSCE: " + e.toString());
		} catch (MalformedURLException e) {
			fail("MUE: " + e.toString());
		} catch (IOException e) {
			fail("IOE: " + e.toString());
		}
	}
}
