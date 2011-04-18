package cz.zcu.kiv.crce.webui.internal;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.zcu.kiv.crce.metadata.Resource;

public class DownloadServlet extends HttpServlet {

	private static final int BUFSIZE = 128;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		Activator.instance().getBuffer(req).commit(true);
		req.getSession().setAttribute("source","commit");
		req.getRequestDispatcher("resource?link=store").forward(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		boolean failed = false;
		if (req.getParameter("uri") != null)
		{
			String link = (String) req.getSession().getAttribute("source");
			String uri = (String) req.getParameter("uri");

			try {
				URI fileUri = new URI(uri);
				if(link.equals("store"))
				{
					Resource[] array = Activator.instance().getStore().getRepository().getResources();
					Resource found = EditServlet.findResource(fileUri,array);
					doDownload(req ,resp ,found);

				}
				else if(link.equals("buffer"))
				{
					Resource[] array = Activator.instance().getBuffer(req).getRepository().getResources();
					Resource found = EditServlet.findResource(fileUri, array);
					System.out.println("Found!"+found.getPresentationName());
					doDownload(req ,resp ,found);
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



	private void doDownload( HttpServletRequest req, HttpServletResponse resp,
			Resource found ) throws IOException {

		File                f        = new File(found.getRelativeUri());
		int                 length   = 0;
		ServletOutputStream op       = resp.getOutputStream();
		ServletContext      context  = getServletConfig().getServletContext();
		String              mimetype = context.getMimeType(found.getRelativeUri().toString());

		//
		//  Set the response and go!
		//
		//
		resp.setContentType( (mimetype != null) ? mimetype : "application/octet-stream" );
		resp.setContentLength( (int)f.length() );
		resp.setHeader( "Content-Disposition", "attachment; filename=\"" + found.getSymbolicName() + chooseCategory(found.getCategories()) + "\"" );

		//
		//  Stream to the requester.
		//
		byte[] bbuf = new byte[BUFSIZE];
		DataInputStream in = new DataInputStream(new FileInputStream(f));

		while ((in != null) && ((length = in.read(bbuf)) != -1))
		{
			op.write(bbuf,0,length);
		}

		in.close();
		op.flush();
		op.close();
	}
	
	private String chooseCategory(String[] strings) {
		String suffix = ""; 
		for (String string : strings) {
			if(string.equals("jpeg")) return ".jpg";
			if(string.equals("zip")) suffix = ".zip";
			if(	string.equals("osgi")
				|| string.equals("cosi")) return ".jar";
			
		}
		return suffix;
	}

}
