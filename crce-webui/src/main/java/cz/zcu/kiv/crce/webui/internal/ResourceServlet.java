package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.framework.InvalidSyntaxException;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;

public class ResourceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
			
			String source = (String )req.getSession().getAttribute("source");
			System.out.println(source);
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
				System.out.println("Default forward");
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
	private static void cleanSession(HttpSession session){
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
		
		else
		{
			link=null;
			return false;
		}
	}
	
}
