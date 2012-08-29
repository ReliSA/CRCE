package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;
import java.util.ArrayList;
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
import org.osgi.framework.InvalidSyntaxException;

public class ResourceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
			
			String source = (String )req.getSession().getAttribute("source");
			Activator.instance().getLog().log(LogService.LOG_DEBUG, source);
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
			Resource[] resources;
			
			if(filter==null) {
				resources = Activator.instance().getStore().getRepository().getResources();
			} else {
				try {
					resources = Activator.instance().getStore().getRepository().getResources(filter);
				} catch (InvalidSyntaxException e) {
					setError(session,false,errorMessage);
					resources = Activator.instance().getStore().getRepository().getResources();
				}
			}
		
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
	 * Prepare list of ccategories (tags) that are on all resurces in the store.
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
					//category is allready contained, increase count
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
