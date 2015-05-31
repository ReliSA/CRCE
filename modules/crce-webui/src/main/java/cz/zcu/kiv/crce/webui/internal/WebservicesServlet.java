package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;
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
        req.getRequestDispatcher("resource?link=webservices");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // process HTTP POST parameter with uri of Webservice IDL
        String uri;
        if (req.getParameter("uri") != null) {
            uri = req.getParameter("uri");
            logger.debug("Got \"uri\" parameter with value \"" + uri + "\".");
            
            // TODO invoke processing of remote IDL document
            
        } else {
            logger.debug("Empty \"uri\" parameter during HTTP POST.");
        }
        
        // redirect to Webservices section
        req.getSession().setAttribute("source", "webservices");
        req.getRequestDispatcher("resource?link=webservices").forward(req, resp);
    }
}
