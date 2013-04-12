package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.plugin.MetadataIndexingResultService;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;

/**
 *
 * @author kalwi
 */
public class UploadServlet extends HttpServlet {

    private static final long serialVersionUID = -7359560802937893940L;

    private static final Logger logger = LoggerFactory.getLogger(UploadServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean failed = false;

        if (req.getParameter("uri") != null) {
            Buffer buffer = Activator.instance().getBuffer(req);
//            List<Resource> resources = buffer.getResources();
//            String uriParam = req.getParameter("uri");
            try {
//                URI uri = new URI(uriParam);
//                Resource found = EditServlet.findResource(uri, resources);
                buffer.commit(true); //TODO! Bad API -> Resources should be committed one by one
//            } catch (URISyntaxException e) {
//                logger.error("Invalid URI syntax", e);
//                failed = true;
            } catch (IOException e) {
                logger.error("Could not commit", e);
                failed = true;
            }
        } else {
            failed = true;
        }
        if (failed) {
            logger.error("Commit failed");
            ResourceServlet.setError(req.getSession(), !failed, "Commit failed");
        } else {
            req.getRequestDispatcher("resource?link=buffer");
        }

        /*PrintWriter out = resp.getWriter();
         out.println("<h1>Resources to commit:</h1>");
         for (Resource res : buffer.getRepository().getResources()) {
         out.println(res.getId() + " " + res.getUri());
         }

         List<Resource> commited = buffer.commit(true);
         out.println("<h1>Commited resources " + commited.size() + ":</h1>");
         for (Resource res : commited) {
         out.println(res.getId() + " " + res.getUri() + "<br>");
         }*/

        //        HttpSession session = req.getSession(true);
        //
        //        PrintWriter out = resp.getWriter();
        //        out.println("m_stack: " + (Activator.instance().getBuffer(req) != null ? "found" : "not found")); // XXX
        //
        //        Enumeration en = session.getAttributeNames();
        //
        //        while (en.hasMoreElements()) {
        //            String name = (String) en.nextElement();
        //            out.println(name + ": " + session.getAttribute(name));
        //        }
        //
        //        session.setAttribute("id" + new Random().nextInt(100), new Random().nextInt(10));
        //
        //
        //        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean success = true;
        String message;
        if (ServletFileUpload.isMultipartContent(req)) {
            ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
            List<?> fileItemsList;
            try {
                fileItemsList = servletFileUpload.parseRequest(req);
            } catch (FileUploadException e) {
                logger.error("Exception handling request: " + req.getRequestURL(), e);
                sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            for (Object o : fileItemsList) {
                FileItem fi = (FileItem) o;

                if (!fi.isFormField()) {
                    String fileName = fi.getName();
                    try (InputStream is = fi.getInputStream()) {
                        try {
                            if (Activator.instance().getBuffer(req).put(fileName, is) == null) {
                                success = false;
                            }
                        } catch (RevokedArtifactException ex) {
                            logger.warn("Artifact revoked: ", ex.getMessage());
                            success = false;
                        }
                    }
                }
            }
        } else {
            success = false;
        }
        StringBuilder metadataIndexerResult = new StringBuilder();
        if (success) {
            message = "Upload was succesful.";

            MetadataIndexingResultService indexerResult = Activator.instance().getMetadataIndexerResult();
            if (!indexerResult.isEmpty()) {
                String[] metadataIndexerMessages = indexerResult.getMessages();
                for (String indexerMessage : metadataIndexerMessages) {
                    metadataIndexerResult.append("<BR>").append(indexerMessage);
                }
                indexerResult.removeAllMessages();
            }
        } else {
            message = "Upload failed.";
        }

        ResourceServlet.setError(req.getSession(), success, message + metadataIndexerResult);
        req.getSession().setAttribute("source", "upload");
        req.getRequestDispatcher("resource?link=buffer").forward(req, resp);
    }

    // send a response with the specified status code
    private void sendResponse(HttpServletResponse response, int statusCode) {
        sendResponse(response, statusCode, "");
    }

    // send a response with the specified status code and description
    private void sendResponse(HttpServletResponse response, int statusCode, String description) {
        try {
            response.sendError(statusCode, description);
        } catch (Exception e) {
            logger.warn("Unable to send response with status code '{}'", statusCode, e);
        }
    }
}
