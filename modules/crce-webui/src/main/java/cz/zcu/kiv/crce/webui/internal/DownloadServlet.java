package cz.zcu.kiv.crce.webui.internal;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.webui.internal.custom.ResourceExt;

public class DownloadServlet extends HttpServlet {

    private static final long serialVersionUID = 6399102910617353070L;

    private static final Logger logger = LoggerFactory.getLogger(DownloadServlet.class);

    private static final int BUFSIZE = 128;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        boolean success;
        String message;
        @SuppressWarnings("unchecked")
        List<ResourceExt> buffer = (List<ResourceExt>) req.getSession().getAttribute("buffer");
        List<Resource> list = Activator.instance().getBuffer(req).commit(true);
        if (list.size() == buffer.size()) {
            success = true;
            message = "All resources commited successfully";
        } else {
            success = false;
            message = "Not all resources commited successfully";
        }
        req.getSession().setAttribute("source", "commit");
        ResourceServlet.setError(req.getSession(), success, message);
        req.getRequestDispatcher("resource?link=store").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean failed = false;
        String message;
        if (req.getParameter("uri") != null) {
            String link = (String) req.getSession().getAttribute("source");
            String uri = req.getParameter("uri");
            try {
                URI fileUri = new URI(uri);
                if (link != null) {
                    switch (link) {
                        case "store": {
                            List<Resource> resources = Activator.instance().getStore(null).getResources();
                            Resource found = EditServlet.findResource(fileUri, resources);
                            doDownload(req, resp, found);
                            break;
                        }
                        case "buffer": {
                            List<Resource> resources = Activator.instance().getBuffer(req).getResources();
                            Resource found = EditServlet.findResource(fileUri, resources);
                            logger.debug("Found!" + Activator.instance().getMetadataService().getPresentationName(found));
                            doDownload(req, resp, found);
                            break;
                        }
                        default:
                            failed = true;
                            break;
                    }
                } else {
                    failed = true;
                }

            } catch (IOException e) {
                failed = true;
                logger.error("Failed to download resource: {}", uri, e);
            } catch (URISyntaxException e) {
                failed = true;
                logger.error("Failed to download resource, invalid URI: {}", uri, e);
            }
        } else {
            failed = true;
        }

        if (failed) {
            message = "Download failed";
        } else {
            message = "Download successful";
        }
        ResourceServlet.setError(req.getSession(), !failed, message);

    }

    private void doDownload(HttpServletRequest req, HttpServletResponse resp, Resource found) throws IOException { // NOPMD req would be used in the future

        File f = new File(Activator.instance().getMetadataService().getUri(found));
        int length = 0;
        try (ServletOutputStream op = resp.getOutputStream()) {
            ServletContext context = getServletConfig().getServletContext();
            String mimetype = context.getMimeType(Activator.instance().getMetadataService().getUri(found).toString());

            //
            //  Set the response and go!
            //
            //
            resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
            resp.setContentLength((int) f.length());
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + Activator.instance().getMetadataService().getFileName(found) + "\"");

            //
            //  Stream to the requester.
            //
            byte[] bbuf = new byte[BUFSIZE];
            try (DataInputStream in = new DataInputStream(new FileInputStream(f))) {
                while (in != null && (length = in.read(bbuf)) != -1) {
                    op.write(bbuf, 0, length);
                }
                op.flush();
            }
        }
    }
}
