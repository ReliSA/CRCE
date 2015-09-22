package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;
import java.util.Arrays;
import java.util.Dictionary;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.repository.PausableStore;
import cz.zcu.kiv.crce.repository.Store;



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
				String id = req.getParameter("repoId");				
				Dictionary<String, Object> props = Activator.instance().getConfig(id);				
				String cfgS = props.toString();
				cfgS = cfgS.substring(1, cfgS.length()-1);
				String [] cfg = cfgS.split(",");
				Arrays.sort(cfg);

				
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
		String link = "";
		String repoId = req.getParameter("repoId");

		if (req.getParameter("link") != null) {
			link = req.getParameter("link");
		}
		
		if(link.equals("resolveArtifact")){
			
			Store s = Activator.instance().getStore(repoId);
			if(s instanceof PausableStore){
				PausableStore ps = (PausableStore)s;
				
				if (req.getParameter("start") != null) {
					ps.startResolve();					
					
					
				} else if (req.getParameter("pause") != null) {
				   ps.pauseResolve();
				}
				
				
				else if (req.getParameter("resume") != null) {
				   ps.resumeResolve();
				}			
				
			}
			
		}
		
		HttpSession session = req.getSession();
		session.setAttribute("repositoryId", repoId);
		req.getRequestDispatcher("resource?link=store&repositoryId="+repoId).forward(req, resp);
	}	
}
