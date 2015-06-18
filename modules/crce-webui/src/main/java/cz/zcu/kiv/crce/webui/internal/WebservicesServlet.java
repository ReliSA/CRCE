package cz.zcu.kiv.crce.webui.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;
import cz.zcu.kiv.crce.webservices.indexer.WebservicesDescription;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebservicesServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(WebservicesServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean failed = false;

        if (req.getParameter("uri") != null) {
            Buffer buffer = Activator.instance().getWsBuffer(req);
            try {
                buffer.commit(true);
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
            req.getRequestDispatcher("resource?link=webservices");
        }
        
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // process HTTP POST parameter with uri of Webservice IDL
        String uri;
        boolean upload_success = true;
        if (req.getParameter("uri") != null && req.getParameter("uri").length() > 0) {
            uri = req.getParameter("uri");
            logger.debug("Got \"uri\" parameter with value \"{}\".", uri);
            
            // invoke processing of remote IDL document
            WebservicesDescription wd = Activator.instance().getWebservicesDescription();
            List<Resource> resources = wd.createWebserviceRepresentations(uri);

            // save all returned resources into buffer
            if (resources == null) {
                logger.warn("Could not parse web services IDL at \"{}\" into CRCE artifact.", uri);
            } else {
                for (Resource resource : resources) {
                    if (resource == null) {
                        logger.warn("Could not parse web service IDL at \"{}\" into CRCE artifact.", uri);
                    } else {
                        logger.info("Web service IDL at \"{}\" was successfully parsed into CRCE artifact.", uri);

                        // save CRCE resource into repository
                        try {
                            Activator.instance().getWsBuffer(req).put(resource);
                        } catch (RefusedArtifactException ex) {
                            logger.warn("Artifact revoked: ", ex.getMessage());
                            upload_success = false;
                        }
                    }
                }
            }
        } else {
            logger.debug("Empty \"uri\" parameter during HTTP POST.");
        }
        
        // redirect to Webservices section
        ResourceServlet.setError(req.getSession(), upload_success, upload_success ? "Upload was succesful." : "Upload failed.");
        req.getSession().setAttribute("source", "webservices");
        req.getRequestDispatcher("resource?link=webservices").forward(req, resp);
    }
}
