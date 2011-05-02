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

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.Type;

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
				if (!success){
					ResourceServlet.setError(req.getSession(), false, "Cannot add category.");
					success = true;
				}
			} else {
				ResourceServlet.setError(req.getSession(), false, "Cannot add category.");
				success = true;
			}
		} else if ("requirements".equals(form)) {
			if (saveRequirements(req,resp, parameters)) {
				success = editRequirements(req, resp, parameters);
				if (!success){
					ResourceServlet.setError(req.getSession(), false, "Cannot save requirements.");
					success = true;
				}
			}
		} else if ("addRequirement".equals(form)) {
			if (addRequirementForm(req,resp, parameters)) {
				success = editRequirements(req, resp, parameters);
				if (!success){
					ResourceServlet.setError(req.getSession(), false, "Cannot add requirement.");
					success = true;
				} 
				} else {
					ResourceServlet.setError(req.getSession(), false, "Cannot add requirement.");
					success = true;
			}
		} else if ("capabilities".equals(form)) {
			if (saveCapabilities(req,resp, parameters)) {
				success = editCapabilities(req, resp, parameters);
				if (!success){
					ResourceServlet.setError(req.getSession(), false, "Cannot save capabilities.");
					success = true;
				}
			}
		}
		if (!success) {
			resp.sendError(HttpServletResponse.SC_ACCEPTED,"NOT FOUND OR FAILED TO PROCEED");
		}
	}
	
	private boolean addRequirementForm(HttpServletRequest req,
			HttpServletResponse resp, Map<?, ?> parameters) {
		String uri = null;
		if (parameters.containsKey("uri")) {
			uri = ((String[]) parameters.get("uri"))[0];
		} else {
			return false;
		}
		
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
			String name = null;
			Requirement requir = null;
			String filter = null;
			boolean multiple = false;
			boolean extend = false;
			boolean optional = false;
			Requirement requirBefore = null;
				if(parameters.containsKey("name")
						&& parameters.containsKey("filter")) {
					name = ((String[]) parameters.get("name"))[0];
					filter = ((String[]) parameters.get("filter"))[0];
					if(parameters.containsKey("multiple")) {
						multiple = (((String[]) parameters.get("multiple"))[0]).equals("on");
					}
					if(parameters.containsKey("optional")) {
						optional = (((String[]) parameters.get("optional"))[0]).equals("on");
					}
					if(parameters.containsKey("extend")) {
						extend = (((String[]) parameters.get("extend"))[0]).equals("on");
					}
					requir = resource.createRequirement(name);
					try {
						requir.setFilter(filter);
					} catch (IllegalArgumentException e) {
						resource.unsetRequirement(requir);
						resource.addRequirement(requirBefore);
					}
					requir.setMultiple(multiple);
					requir.setOptional(optional);
					requir.setExtend(extend);
				}
			
		} catch (URISyntaxException e) {
			return false;
		} catch (FileNotFoundException e) {
			return false;
		}
		req.getSession().setAttribute("success", true);
		return true;

	}

	private boolean saveCapabilities(HttpServletRequest req,
			HttpServletResponse resp, Map<?, ?> parameters) {
		int capabilityId = -1;
		String uri = null;
		if (parameters.containsKey("uri")
				&& parameters.containsKey("capabilityId")) {
			uri = ((String[]) parameters.get("uri"))[0];
			capabilityId = Integer.valueOf(((String[]) parameters.get("capabilityId"))[0]);
		} else {
			return false;
		}
		
		try {
			URI resURI = new URI(uri);
			String link = (String) req.getSession().getAttribute("source");
			Resource[] array;
			if ("store".equals(link)) {
				array = Activator.instance().getStore().getRepository().getResources();
			} else  if ("buffer".equals(link)) {
				array = Activator.instance().getBuffer(req).getRepository().getResources();
			} else {
				{
					return false;
				}
			}
			Resource resource = findResource(resURI, array);
			
			Capability capability= resource.getCapabilities()[capabilityId - 1];
			Property[] properties = capability.getProperties();
			for (int i = 0; i < properties.length; i++) {
				String name = ((String[]) parameters.get("name_" + (i + 1)))[0];
				String type = ((String[]) parameters.get("type_" + (i + 1)))[0];
				String value = ((String[]) parameters.get("value_" + (i + 1)))[0];
				Property propBefore = properties[i];
				int propertiesLengthBefore = properties.length;
				capability.unsetProperty(properties[i].getName());
				
				if(propertiesLengthBefore == capability.getProperties().length){
					req.getSession().setAttribute("success", false);
					req.getSession().setAttribute("message", "Cannot change property.");
					System.err.println("Cannot change property.");
					continue;
				}
				
				try{
					capability.setProperty(name, value, Type.getValue(type));
				} catch (IllegalArgumentException e){
					capability.setProperty(propBefore);
					req.getSession().setAttribute("success", false);
					req.getSession().setAttribute("message", "Cannot change property.");
					System.err.println("Cannot change property.");
				}
				}
//				resource.addRequirement(requir);
		} catch (URISyntaxException e) {
			return false;
		} catch (FileNotFoundException e) {
			return false;
		}
		return true;
	}

	private boolean saveRequirements(HttpServletRequest req,
			HttpServletResponse resp, Map<?, ?> parameters) {
		String uri = null;
		if (parameters.containsKey("uri")) {
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
			
			Requirement[] requirements = resource.getRequirements();
			int requirLengthBefore = 0;
			String name = null;
			Requirement requir = null;
			String filter = null;
			boolean multiple = false;
			boolean extend = false;
			boolean optional = false;
			Requirement requirBefore = null;
			for (int i = 0; i < requirements.length; i++) {
				System.out.println(i);
				if(parameters.containsKey("name_" + (i + 1))
						&& parameters.containsKey("filter_" + (i + 1))) {
					
					name = ((String[]) parameters.get("name_" + (i + 1)))[0];
					filter = ((String[]) parameters.get("filter_" + (i + 1)))[0];
					
					if(parameters.containsKey("multiple_" + (i + 1))) {
						multiple = (((String[]) parameters.get("multiple_" + (i + 1)))[0]).equals("on");
					}
					if(parameters.containsKey("optional_" + (i + 1))) {
						optional = (((String[]) parameters.get("optional_" + (i + 1)))[0]).equals("on");
					}
					if(parameters.containsKey("extend_" + (i + 1))) {
						extend = (((String[]) parameters.get("extend_" + (i + 1)))[0]).equals("on");
					}
					System.out.println(i + " : " + multiple);
					System.out.println(i + " : " + optional);
					System.out.println(i + " : " + extend);
//					requirBefore = requirements[i];
					requirLengthBefore = requirements.length;
					resource.unsetRequirement(requirements[i]);
					if(requirLengthBefore == resource.getRequirements().length){
						req.getSession().setAttribute("success", false);
						req.getSession().setAttribute("message", "Cannot change requirement.");
						System.err.println("Cannot change requirement.");
						continue;
					}
					
					requir = resource.createRequirement(name);
					try {
						requir.setFilter(filter);
					} catch (IllegalArgumentException e) {
						req.getSession().setAttribute("success", false);
						req.getSession().setAttribute("message", "Cannot change requirement.");
						System.err.println("Cannot change requirement.");
						resource.unsetRequirement(requir);
						resource.addRequirement(requirBefore);
						continue;
					}
					requir.setMultiple(multiple);
					requir.setOptional(optional);
					requir.setExtend(extend);
//					resource.addRequirement(requir);
				}
			}
			System.out.println("2");
			
		} catch (URISyntaxException e) {
			System.out.println("3");
			return false;
		} catch (FileNotFoundException e) {
			System.out.println("3b");
			return false;
		}
		return true;
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
		
		req.getSession().removeAttribute("success");
		boolean success	= false;
		Map<?,?> parameters = (Map<?,?>) req.getParameterMap();
		
		String type = null;;
		if (parameters.containsKey("type")) {
			type = ((String[]) parameters.get("type"))[0];
			System.out.println("deb 1" + type);
			
		} 
		if ("deleteCompoment".equals(type)) {
			success = deleteComponent(req, resp, parameters);
			if (!success){
				ResourceServlet.setError(req.getSession(), false, "Cannot delete component.");
				success = true;
			}
		
		} else if("category".equals(type)){
			success = editCategories(req, resp);
			if (!success){
				ResourceServlet.setError(req.getSession(), false, "Cannot edit category.");
				success = true;
			}
			
		} else if("deleteCategory".equals(type)) {
			if(!deleteCategory(req, resp, parameters)){
				ResourceServlet.setError(req.getSession(), false, "Cannot delete category.");
				success = true;
			}
			success = editCategories(req, resp);
		} else if("addCategory".equals(type)) {
			success = addCategories(req, resp, parameters);
			if (!success){
				ResourceServlet.setError(req.getSession(), false, "Cannot add category.");
				success = true;
			}
				
		} else if("addCapability".equals(type)) {
			success = addCapabilities(req, resp, parameters);
			if (!success){
				ResourceServlet.setError(req.getSession(), false, "Cannot add capability.");
				success = true;
			}
			
		} else if("capability".equals(type)) {
			success = editCapabilities(req, resp, parameters);
			if (!success){
				ResourceServlet.setError(req.getSession(), false, "Cannot edit capabilities.");
				success = true;
			}
				
		} else if("requirement".equals(type)) {
			success = editRequirements(req, resp, parameters);
			if (!success){
				ResourceServlet.setError(req.getSession(), false, "Cannot edit requirements.");
				success = true;
			}
		
		} else if("addRequirement".equals(type)) {
					success = addRequirement(req, resp, parameters);
					if (!success){
						ResourceServlet.setError(req.getSession(), false, "Cannot add requirement.");
						success = true;
					}
					
		} else {
			success = false;
		}
		
		if (!success) {
			resp.sendError(HttpServletResponse.SC_ACCEPTED,"NOT FOUND OR FAILED TO PROCEED");
		}
	}
	
	private boolean addRequirement(HttpServletRequest req,
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
			req.getRequestDispatcher("jsp/forms/requirementForm.jsp").forward(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
			
		return true;
	}

	private boolean editRequirements(HttpServletRequest req,
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
			req.getRequestDispatcher("jsp/forms/requirementsForm.jsp").forward(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean addCapabilities(HttpServletRequest req,
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
			req.getRequestDispatcher("jsp/forms/capabilityForm.jsp").forward(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
			
		return true;
	}

	private boolean editCapabilities(HttpServletRequest req,
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
			String id;
			String resURI = null;;
			if (parameters.containsKey("capabilityId")
					&& parameters.containsKey("uri")) {
				id = ((String[]) parameters.get("capabilityId"))[0];
				resURI  = ((String[]) parameters.get("uri"))[0];
			} else {
				System.out.println("deb3ALE");
				return false;
			}
			System.out.println("deb3aALE");
			Resource resource = findResource(new URI(resURI), array);
			
			req.getSession().setAttribute("resource", resource);
			req.getSession().setAttribute("capability", resource.getCapabilities()[Integer.valueOf(id) - 1]);
			req.getSession().setAttribute("capabilityId", id);
			req.getRequestDispatcher("jsp/forms/capabilitiesForm.jsp").forward(req, resp);
		} catch (Exception e) {
			System.out.println("deb3bALE");
			e.printStackTrace();
			return false;
		}
		
		return true;
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
			if (categoriesLengthBefore == resource.getCategories().length) {
				return false;
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
				if(!Activator.instance().getBuffer(req).remove(found)){
				}
				
				
			} else {
				return false;
			}
			
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
