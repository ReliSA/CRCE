package cz.zcu.kiv.crce.webui.internal;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Buffer;

public class EditServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		boolean failed = false;
		if (req.getParameter("uri") != null && req.getParameter("link") != null)
		{
			String link = (String) req.getParameter("link");
			String uri = (String) req.getParameter("uri");
			
			try {
				URI fileUri = new URI(uri);
				if(link.equals("store"))
				{
					Resource[] array = Activator.instance().getStore().getRepository().getResources();
					Resource found = findResource(fileUri,array);
					Activator.instance().getStore().remove(found);
				}
				else if(link.equals("buffer"))
				{
					Resource[] array = Activator.instance().getBuffer(req).getRepository().getResources();
					Resource found = findResource(fileUri, array);
					System.out.println("Found!"+found.getPresentationName());
					if(!Activator.instance().getBuffer(req).remove(found)) System.out.println("wtf?");;
				}
				else failed=true;
								
			} 
			catch (Exception e) {
				failed = true;
				e.printStackTrace();
			}
		} 
		else {
			failed = true;
		}
		if(failed) resp.sendError(resp.SC_ACCEPTED,"NOT FOUND OR FAILED TO PROCEED");
		else req.getRequestDispatcher("resource").forward(req, resp);
		
	}
	public static Resource findResource(URI uri, Resource[] array) throws FileNotFoundException{
		Resource found = null;
		for(Resource r : array)
		{
			if(r.getUri().equals(uri))
				{
					found = r;
					break;
				}
		}
		if(found==null) throw new FileNotFoundException();
		return found;
	}
}
