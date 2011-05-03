package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.zcu.kiv.crce.metadata.Resource;



public class RuntimeServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		//if(storeBufferAction(req)) parseParams(req);
		//req.getSession().setAttribute("resources", parseParams(req));
		
		// TEST!!!!!! - NENI SPRAVNE!!!!!!
		req.getSession().setAttribute("resources", Activator.instance().getStore().getRepository().getResources());
		req.getSession().setAttribute("tests", Activator.instance().getPluginManager().getPlugins());
		
		req.getRequestDispatcher("jsp/forms/testForm.jsp").forward(req, resp);
	}
	
	private Resource[] parseParams(HttpServletRequest req){
		String[] uris = (String []) req.getParameterValues("check");
		System.out.println(uris.length);
		
		//return uris;
		return null;
	}
	
	private boolean storeBufferAction(HttpServletRequest req){
		String source = (String) req.getSession().getAttribute("source");
		if(source!=null && source.equals("buffer") && source.equals("store")) return true;
		else return false;
	}

}
