package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;
import java.util.Dictionary;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Servlet implementation class ResolveMavenArtifactServlet
 */
public class MvnRepoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(MvnRepoServlet.class);
       

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		String link = "";

		if (req.getParameter("link") != null) {
			link = req.getParameter("link");
		}

		try {
			if (link.equals("config")) {
				String id = req.getParameter("id");				
				Dictionary<String, Object> props = Activator.instance().getConfig(id);
				String cfgS = props.toString();
				cfgS = cfgS.substring(1, cfgS.length()-1);
				String [] cfg = cfgS.split(",");

				
				session.setAttribute("cfg", cfg);
				req.getRequestDispatcher("jsp/forms/mvnRepoCfg.jsp").forward(req, resp);

			} else {
				logger.debug("Default forward");
				resp.sendRedirect("resource?link=store");
			}

		} catch (ServletException e) {
			logger.warn("Can't forward: {}", e);
		} catch (IOException e) {
			logger.error("Can't forward", e);
		}
	}
			
		
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("POST");
		//doGet(req, resp);		
	}	

}
