package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Dictionary;
import java.util.Enumeration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kalwi
 */
public class TestServlet extends HttpServlet implements ManagedService {

    private static final long serialVersionUID = -5870408422415688046L;
    
    private static final Logger logger = LoggerFactory.getLogger(TestServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (PrintWriter out = resp.getWriter()) {
            ServletContext cx = this.getServletContext();

            out.println("Hello, world!");

            Enumeration e = cx.getInitParameterNames();

            while (e.hasMoreElements()) {
                String element = (String) e.nextElement();
                out.println(element + ": " + cx.getInitParameter(element));
            }
        }

    }

    @Override
    public void updated(Dictionary dctnr) throws ConfigurationException {
        logger.info("Test servlet updated.");
    }
}
