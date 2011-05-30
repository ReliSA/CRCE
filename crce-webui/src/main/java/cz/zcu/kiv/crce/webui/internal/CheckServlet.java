package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cz.zcu.kiv.crce.metadata.Reason;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resolver;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.webui.custom.ResourceExt;
/**
 * A class for the Check Action
 * @author Sandcrew
 *
 */
public class CheckServlet extends HttpServlet {
	/**
	 * Handles the CheckAction
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {		
		Resource[] res = chooseFrom(req);
		if(res==null)
		{			
			req.getRequestDispatcher("resource").forward(req, resp);
		}
		else
		{
			String source = (String) req.getSession().getAttribute("source");
			req.getSession().setAttribute(source, res);
			req.getSession().removeAttribute("source");
			req.getRequestDispatcher("jsp/"+source+".jsp").forward(req, resp);
		}

	}
	/**
	 * Parses request attribute source and returns an array of Resources from the right repository
	 * calling doCheck
	 * @param req 
	 * @return
	 */
	private Resource[] chooseFrom(HttpServletRequest req){
		String source = (String )req.getSession().getAttribute("source");		
		if(source == null) return null;
		else if(source.equals("buffer")) return doCheck(Activator.instance().getBuffer(req).getRepository());
		else if(source.equals("store")) return doCheck(Activator.instance().getStore().getRepository());
		else return null;
	}
	/**
	 * Makes the check on the repository selected by source param.
	 * Creates ResourceExt wrapper for all the Resources
	 * @param repository - Repository to be checked
	 * @return ResourceExt with unsatisfied Requirements of selected repository
	 */
	private Resource[] doCheck(Repository repository) {		
		Resource[] resources = repository.getResources();
		Resource[] cloned = new Resource[resources.length];
		System.arraycopy(resources, 0, cloned, 0, resources.length);
		ArrayList<Resource> ext = new ArrayList<Resource>();
		HashMap<URI,Resource> extMap = new HashMap<URI,Resource>();
		ResourceCreator rc = Activator.instance().getCreator();
		Resolver resolver = rc.createResolver(repository);
		for (Resource r : cloned) 
		{
			resolver.add(r);
			r.getUri();
			extMap.put(r.getUri(), new ResourceExt(r));
			ext.add(new ResourceExt(r));
		}		
		if (!resolver.resolve()) 
		{			
			for (Reason r : resolver.getUnsatisfiedRequirements()) 
			{				
				if(extMap.containsKey(r.getResource().getUri()))
				{
					extMap.get(r.getResource().getUri()).addRequirement(r.getRequirement());					
				}
				
			}			
			return extMap.values().toArray(new Resource[extMap.values().size()]);
		}
		else
		{			
			return resources;
		}
	}
}
