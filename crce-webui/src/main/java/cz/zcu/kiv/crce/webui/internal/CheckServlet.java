package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;
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

public class CheckServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println(req.getSession().getAttribute("source"));
		Resource[] res = chooseFrom(req);
		if(res==null) req.getRequestDispatcher("resource").forward(req, resp);
		else
		{
			String source = (String) req.getSession().getAttribute("source");
			req.getSession().setAttribute(source, res);
			req.getSession().removeAttribute("source");
			req.getRequestDispatcher("jsp/"+source+".jsp").forward(req, resp);
		}

	}
	private Resource[] chooseFrom(HttpServletRequest req){
		String source = (String )req.getSession().getAttribute("source");
		if(source == null) return null;
		else if(source.equals("buffer")) return doCheck(Activator.instance().getBuffer(req).getRepository());
		else if(source.equals("store")) return doCheck(Activator.instance().getStore().getRepository());
		else return null;
	}
	private Resource[] doCheck(Repository repository) {		
		Resource[] resources = repository.getResources();
		ArrayList<Resource> ext = new ArrayList<Resource>();
		ResourceCreator rc = Activator.instance().getCreator();
		Resolver resolver = rc.createResolver(repository);
		for (Resource r : resources) 
		{
			resolver.add(r);
			ext.add(new ResourceExt(r));
		}
		if (!resolver.resolve()) 
		{
			for (Reason r : resolver.getUnsatisfiedRequirements()) 
			{
				if(ext.indexOf(r.getResource())>0)
				{
					ext.get(ext.indexOf(r.getResource())).addRequirement(r.getRequirement());
				}
				
			}
			return ext.toArray(new Resource[ext.size()]);

		}
		else
		{
			return resources;
		}
	}
}
