package cz.zcu.kiv.crce.webui.internal;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cz.zcu.kiv.crce.metadata.Resource;

public class EditServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		boolean success = false;
		Map<?,?> parameters = req.getParameterMap();
		String form = null;
		
		if (req.getParameter("htmlFormName") != null) {
			System.out.println((String)req.getParameter("htmlFormName"));
		}
		
		if (parameters.containsKey("form")) {
			form =((String[]) parameters.get("form"))[0];
		}
		if ("addCategory".equals(form)) {
			if (addCategory(req,resp, parameters)) {
				success = editCategories(req, resp);
			}
		}
		if (!success) {
			resp.sendError(HttpServletResponse.SC_ACCEPTED,"NOT FOUND OR FAILED TO PROCEED");
		}
	}
	
	private boolean addCategory(HttpServletRequest req,
			HttpServletResponse resp, Map<?, ?> parameters) {
		String category = null;
		String uri = null;
		if (parameters.containsKey("category")
				&& parameters.containsKey("uri")) {
			category = ((String[]) parameters.get("category"))[0];
			uri = ((String[]) parameters.get("uri"))[0];
			
		} else return false;
		
		try {
			URI resURI = new URI(uri);
			String link = (String) req.getSession().getAttribute("source");
			Resource[] array;
			if ("store".equals(link)) {
				array = Activator.instance().getStore().getRepository().getResources();
			} else  if ("buffer".equals(link)) {
				array = Activator.instance().getBuffer(req).getRepository().getResources();
			} else {
				return false;
			}
			Resource resource = findResource(resURI, array);
			
			int categoriesLengthBefore = resource.getCategories().length; 
			resource.addCategory(category);
			
//			Zjištění zda kategorie byla odstraněna.
			if (categoriesLengthBefore < resource.getCategories().length) {
				req.getSession().setAttribute("success", false);
			} else {
				req.getSession().setAttribute("success", true);
			}
			
		} catch (URISyntaxException e) {
			return false;
		} catch (FileNotFoundException e) {
			return false;
		}
		return true;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		cleanSession(req.getSession());
		
		boolean success	= false;
		Map<?,?> parameters = (Map<?,?>) req.getParameterMap();
		
		String type = null;;
		if (parameters.containsKey("type")) {
			type = ((String[]) parameters.get("type"))[0];
			System.out.println("deb 1" + type);
			
		} 
		if ("deleteCompoment".equals(type)) {
			success = deleteComponent(req, resp, parameters);
		
		} else if("category".equals(type)){
			success = editCategories(req, resp);
			
		} else if("deleteCategory".equals(type)) {
			if(deleteCategory(req, resp, parameters)){
				
				success = editCategories(req, resp);
			} else {
				success = false;
			}
		} else if("addCategory".equals(type)) {
				success = addCategories(req, resp, parameters);
				
		} else {
			success = false;
		}
		
		if (!success) {
			resp.sendError(HttpServletResponse.SC_ACCEPTED,"NOT FOUND OR FAILED TO PROCEED");
		}
	}
	
	private boolean addCategories(HttpServletRequest req,
			HttpServletResponse resp, Map<?, ?> parameters) {
		
		String link = (String) req.getSession().getAttribute("source");
		Resource[] array;
		if ("store".equals(link)) {
			array = Activator.instance().getStore().getRepository().getResources();
		} else  if ("buffer".equals(link)) {
			array = Activator.instance().getBuffer(req).getRepository().getResources();
		} else {
			return false;
		}
		
		try {
			URI resURI = new URI((String) req.getParameter("uri"));
			Resource resource = findResource(resURI, array);
			req.getSession().setAttribute("resource", resource);			
			req.getRequestDispatcher("jsp/forms/categoryForm.jsp").forward(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
			
		return true;
	}

	private boolean editCategories(HttpServletRequest req, HttpServletResponse resp) {
		String link = (String) req.getSession().getAttribute("source");
		Resource[] array;
		if ("store".equals(link)) {
			array = Activator.instance().getStore().getRepository().getResources();
		} else  if ("buffer".equals(link)) {
			array = Activator.instance().getBuffer(req).getRepository().getResources();
		} else {
			return false;
		}
		
		try {
			URI resURI = new URI((String) req.getParameter("uri"));
			Resource resource = findResource(resURI, array);
			req.getSession().setAttribute("resource", resource);			
			req.getRequestDispatcher("jsp/forms/categoriesForm.jsp").forward(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private boolean deleteCategory(HttpServletRequest req,
			HttpServletResponse resp, Map<?, ?> parameters) {
		String category = null;
		String uri = null;
		if (parameters.containsKey("category")
			&& parameters.containsKey("uri")) {
			category = ((String[]) parameters.get("category"))[0];
			uri = ((String[]) parameters.get("uri"))[0];
			
		} else return false;
		try {
			URI resURI = new URI(uri);
			String link = (String) req.getSession().getAttribute("source");
			Resource[] array;
			if ("store".equals(link)) {
				array = Activator.instance().getStore().getRepository().getResources();
			} else  if ("buffer".equals(link)) {
				array = Activator.instance().getBuffer(req).getRepository().getResources();
			} else {
				return false;
			}
			
			Resource resource = findResource(resURI, array);
			
			int categoriesLengthBefore = resource.getCategories().length; 
			resource.unsetCategory(category);
			
//			Zjištění zda kategorie byla odstraněna.
			if (categoriesLengthBefore < resource.getCategories().length) {
				req.getSession().setAttribute("success", false);
			} else {
				req.getSession().setAttribute("success", true);
			}
			
		} catch (URISyntaxException e) {
			return false;
		} catch (FileNotFoundException e) {
			return false;
		}
		return true;
	}

	private boolean deleteComponent(HttpServletRequest req, HttpServletResponse resp,final Map<?, ?> parameters) {
		String link = null;
		String uri = null;
		if (parameters.containsKey("link")
			&& parameters.containsKey("uri")) {
			link = ((String[]) parameters.get("link"))[0];
			uri = ((String[]) parameters.get("uri"))[0];
			
		} else return false;
		try {
			URI fileUri = new URI(uri);
			if("store".equals(link)) {
				Resource[] array = Activator.instance().getStore().getRepository().getResources();
				Resource found = findResource(fileUri,array);
				Activator.instance().getStore().remove(found);
			} else if("buffer".equals(link)) {
				Resource[] array = Activator.instance().getBuffer(req).getRepository().getResources();
				Resource found = findResource(fileUri, array);
				System.out.println("Found!"+found.getPresentationName());
				if(!Activator.instance().getBuffer(req).remove(found)) System.out.println("wtf?");;
				
				
			} else return false;
			
			req.getRequestDispatcher("resource").forward(req, resp);
							
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static Resource findResource(URI uri, Resource[] array) throws FileNotFoundException{
		Resource found = null;
		for(Resource r : array)
		{
			if(r.getUri().equals(uri))
				{
					found = r;
					break;
				}
		}
		if(found==null) throw new FileNotFoundException();
		return found;
	}
	
	private static void cleanSession(HttpSession session){
		session.removeAttribute("resource");
	}
}
