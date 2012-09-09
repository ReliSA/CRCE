package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.log.LogService;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.webui.internal.bean.Category;

public class ResourceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
			
			String source = (String )req.getSession().getAttribute("source");
			Activator.instance().getLog().log(LogService.LOG_DEBUG, source);
			
		//if form was submit, set session parameters{
			if (req.getParameter("showStoreTag") != null
					&& req.getParameter("showStoreTag").equalsIgnoreCase("yes")) {
				req.getSession().setAttribute("showStoreTag", "yes");
				Activator.instance().getLog().log(LogService.LOG_INFO,
						"showStoreTag session attribute set to yes");
			} else {
				req.getSession().setAttribute("showStoreTag", "no");
				Activator.instance().getLog().log(LogService.LOG_INFO,
						"showStoreTag session attribute set to no");
			}
			
			if(req.getParameter("showBufferTag")!=null 
					&& req.getParameter("showBufferTag").equalsIgnoreCase("yes")) {
				req.getSession().setAttribute("showBufferTag", "yes");
				Activator.instance().getLog().log(LogService.LOG_INFO, 
						"showBufferTag session attribute set to yes");		
			} else {
				req.getSession().setAttribute("showBufferTag", "no");
				Activator.instance().getLog().log(LogService.LOG_INFO, 
						"showBufferTag session attribute set to no");
			}


			
			if(source!= null && (source.equals("upload") || source.equals("commit")))
			{
				doGet(req,resp);
				return;
			}
			else if(req.getParameter("filter")!=null){
				
				String filter = req.getParameter("filter");
				fillSession(source,req,filter);
				req.getRequestDispatcher("jsp/"+source+".jsp").forward(req, resp);
			}
			else
			{				
				doGet(req,resp);
			}
			
			

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		
		String link = null;
		
		if (req.getParameter("link") != null && req.getParameter("link") instanceof String) 
		{
			link = req.getParameter("link");
			
		}
		try {
			if(fillSession(link,req,null))
			{
				//resp.sendRedirect("jsp/"+link+".jsp");
				req.getRequestDispatcher("jsp/"+link+".jsp").forward(req, resp);
			}
			else
			{
				Activator.instance().getLog().log(LogService.LOG_DEBUG, "Default forward");
				req.getRequestDispatcher("resource?link=store").forward(req, resp);
			}

		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void cleanSession(HttpSession session){
		session.removeAttribute("resources");
		session.removeAttribute("plugins");
		session.removeAttribute("store");		
	}
	
	public static void setError(HttpSession session, boolean success, String message){
		session.setAttribute("success", success);
		session.setAttribute("message", message);
	}
	
	private boolean fillSession(String link, HttpServletRequest req, String filter){
		String errorMessage = filter+" is not a valid filter";
		HttpSession session = req.getSession();
		cleanSession(session);
		if(link==null) return false;
		
		if(link.equals("buffer"))
		{
			Resource[] buffer;
			if(filter==null) buffer = Activator.instance().getBuffer(req).getRepository().getResources();
			else
				try {
					buffer = Activator.instance().getBuffer(req).getRepository().getResources(filter);
				} catch (InvalidSyntaxException e) {
					setError(session,false,errorMessage);
					buffer = Activator.instance().getBuffer(req).getRepository().getResources();
				}
			session.setAttribute("buffer", buffer);
			return true;
		}
		
		else if(link.equals("plugins"))
		{
			    Plugin[] plugins;
				if(filter==null) plugins = Activator.instance().getPluginManager().getPlugins();
				else plugins = Activator.instance().getPluginManager().getPlugins(Plugin.class, filter);
				session.setAttribute("plugins", plugins);
				return true;
		}
		
		else if(link.equals("store"))
		{
			Resource[] store;
			if(filter==null) store = Activator.instance().getStore().getRepository().getResources();
			else
				try {
					store = Activator.instance().getStore().getRepository().getResources(filter);
				} catch (InvalidSyntaxException e) {
					setError(session,false,errorMessage);
					store = Activator.instance().getStore().getRepository().getResources();
				}
			session.setAttribute("store", store);
			return true;
		}
		
		else if(link.equals("tags"))
		{
			/*if(session.getAttribute("showStoreTag") == null) {
				session.setAttribute("showStoreTag","yes");
			}
			if(session.getAttribute("showBufferTag") == null) {
				session.setAttribute("showBufferTag","no");
			}*/
			
			Resource[] resources = prepareResourceArray(req, filter);
			ArrayList<Category> categoryList = prepareCategoryList(resources);
			ArrayList<Resource> filteredResourceList;
		
			String selectedCategory;
			if (req.getParameter("tag") != null && req.getParameter("tag") instanceof String) 
			{
				selectedCategory = req.getParameter("tag");

				filteredResourceList = filterResurces(selectedCategory, resources);
				
			} else {
				filteredResourceList = null;
			}
			
			session.setAttribute("resources", filteredResourceList);
			session.setAttribute("categoryList", categoryList);
			
			return true;
		}
		
		else
		{
			link=null;
			return false;
		}
	}
	
	/**
	 * Prepare resource array.
	 * Resource array is created from store and buffer.
	 * Store resources are added, if request parameter <code>store</code> is "yes".
	 * Buffer resources are added, if request parameter <code>buffer</code> is "yes".
	 * @param req request with parameters
	 * @param filter possible filter of resources
	 * @return array of resources
	 */
	private Resource[] prepareResourceArray(HttpServletRequest req, String filter) {
		String errorMessage = filter+" is not a valid filter";
		HttpSession session = req.getSession();
		Resource[] storeResources, bufferResources, resources;
		storeResources = new Resource[0];
		bufferResources = new Resource[0];
		
		String sessStoreAtr = (String) session.getAttribute("showStoreTag");
		String sessBufferAtr = (String) session.getAttribute("showBufferTag");
		
		if(sessStoreAtr != null && sessStoreAtr.equalsIgnoreCase("yes")) {
			if(filter==null) {
				storeResources = Activator.instance().getStore().getRepository().getResources();
			} else {
				try {
					storeResources = Activator.instance().getStore().getRepository().getResources(filter);
				} catch (InvalidSyntaxException e) {
					setError(session,false,errorMessage);
					storeResources = Activator.instance().getStore().getRepository().getResources();
				}
			}
		}
		
		if(sessBufferAtr!= null && sessBufferAtr.equalsIgnoreCase("yes")) {
			if(filter==null) {
				bufferResources = Activator.instance().getBuffer(req).getRepository().getResources();
			} else {
				try {
					bufferResources = Activator.instance().getBuffer(req).getRepository().getResources(filter);
				} catch (InvalidSyntaxException e) {
					setError(session,false,errorMessage);
					bufferResources = Activator.instance().getBuffer(req).getRepository().getResources();
				}
			}
		}

		//merge two resources arrays
		resources = Arrays.copyOf(storeResources, storeResources.length + bufferResources.length);
		System.arraycopy(bufferResources, 0, resources, storeResources.length, bufferResources.length);
		
		return resources;
	}

	/**
	 * Prepare list of categories (tags) that are on all resources in the store.
	 * Category is represented by name and the number of occurrences.
	 * @param resources - all resources from the store
	 * @return array list of categories
	 */
	private ArrayList<Category> prepareCategoryList(Resource[] resources) {
		
		HashMap<String, Integer> categoryMap = new HashMap<String, Integer>();
		
		for(Resource resource: resources) {
			String[] categories = resource.getCategories();
			for(String category : categories) {
				if(categoryMap.containsKey(category)){
					//category is already contained, increase count
					categoryMap.put(category, new Integer(categoryMap.get(category).intValue() + 1));
				} else {
					//add new category
					categoryMap.put(category, new Integer(1));
				}
			}
		}
		
		ArrayList<Category> categoryList = new ArrayList<Category>();
		Set<String> categorySet = categoryMap.keySet();
		
		/*Get categories from map to list*/
		for(String category: categorySet) {
			Category newCategory = new Category(category);
			newCategory.setCount(categoryMap.get(category));
			categoryList.add(newCategory);
		}
		
		//sort category list
		Collections.sort(categoryList);
		
		return categoryList;
		
		
	}

	/**
	 * Filter resources according some category.
	 * Output list contains only resources that have required category.
	 * @param filterCategory required category
	 * @param resources resources
	 * @return filtered resources list
	 */
	private ArrayList<Resource> filterResurces(String filterCategory, Resource[] resources) {
		ArrayList<Resource> filteredResourceList = new ArrayList<Resource>();
		for(Resource resource: resources) {
			String[] categories = resource.getCategories();
			for(String category : categories) {
				if(category.equals(filterCategory)) {
					filteredResourceList.add(resource);
					break;
				}
			}
		}
		
		return filteredResourceList;
	}



}
