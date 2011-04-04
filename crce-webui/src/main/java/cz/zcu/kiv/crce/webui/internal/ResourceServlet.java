package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;

public class ResourceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp){
		HttpSession session = req.getSession();
		session.setAttribute("hello", "Hello world !!!!");
		Resource[] resources = Activator.instance().getBuffer(req).getRepository().getResources();
		session.setAttribute("resources", resources);
		Plugin[] plugins = Activator.instance().getPluginManager().getPlugins();
		session.setAttribute("plugins", plugins);
		
		Resource[] store = Activator.instance().getStore().getRepository().getResources();
		session.setAttribute("store", store);	
		try {
			req.getRequestDispatcher("jsp/plugins.jsp").forward(req, resp);
			
			
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
