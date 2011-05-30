package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.log.LogService;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;

public class ResourceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	/**
	 * Main core of the application. Responsible for loading
	 * Resources from repository and though update the page
	 * DoPost is just redirected to doGet
	 */
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
	/**
	 * Reads required data for the page. Saves them to the session
	 * and redirects via link param.
	 * 
	 */
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
	/**
	 * A static session cleaner method
	 * Removes atts such as resources, plugins and store
	 * @param session - session to be cleaned
	 * 
	 */
	public static void cleanSession(HttpSession session){
		session.removeAttribute("resources");
		session.removeAttribute("plugins");
		session.removeAttribute("store");		
	}
	/**
	 * A core method for getting the page now how the action ended	 * 
	 * @param session - session of the page
	 * @param success - true if there was an success false - error
	 * @param message - A message which will be displayd on the page
	 */
	public static void setError(HttpSession session, boolean success, String message){
		session.setAttribute("success", success);
		session.setAttribute("message", message);
	}
	/**
	 * Private method for filling the session
	 * @param link - Which page will be displayed
	 * @param req - Request to parse
	 * @param filter - Filter to narrow selected resources
	 * @return
	 */
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
		
		else
		{
			link=null;
			return false;
		}
	}
	
}
