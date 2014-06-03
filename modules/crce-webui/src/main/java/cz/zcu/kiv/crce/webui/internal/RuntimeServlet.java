package cz.zcu.kiv.crce.webui.internal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.plugins.Executable;

public class RuntimeServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(RuntimeServlet.class);

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String message = null;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (isStoreBufferAction(req)) {
            if (setSessionForForm(req)) {
                req.getRequestDispatcher("jsp/forms/testForm.jsp").forward(req, resp); // FIXME hardcoded
            } else {
                message = "No bundles selected";
                logger.warn(message);
                ResourceServlet.setError(req.getSession(), false, message);
                req.getRequestDispatcher("jsp/" + req.getSession().getAttribute("source") + ".jsp").forward(req, resp);
            }
        }
        ResourceServlet.setError(req.getSession(), false, "Wrong params!");
        req.getRequestDispatcher("resource").forward(req, resp);

    }

    private boolean setSessionForForm(HttpServletRequest req) {
        Resource[] toTest = parseParams(req);
        if (toTest == null) {
            return false;
        } else {
            HttpSession session = req.getSession();
            ResourceServlet.cleanSession(session);
            session.setAttribute("resources", toTest);
            List<Executable> testPlugins = Activator.instance().getPluginManager().getPlugins(Executable.class);
            // Executable is not Serializable so this workaround is needed
            List<String> testPluginIds = new ArrayList<>(testPlugins.size());
            for (Executable executable : testPlugins) {
                testPluginIds.add(executable.getPluginId());
            }
            session.setAttribute("tests", testPluginIds);
            return true;
        }
    }

    private List<Resource> fetchRightResources(String source, HttpServletRequest req) {
        Activator a = Activator.instance();
        if (source.equals("store")) {
            return a.getStore().getResources();
        } else {
            return a.getBuffer(req).getResources();
        }
    }

    private Resource[] parseParams(HttpServletRequest req) {


        String[] uris = req.getParameterValues("check");
        if (uris == null || uris.length == 0) {
            return null;
        }
        List<Resource> resources = fetchRightResources((String) req.getSession().getAttribute("source"), req);
        Resource[] toTest = new Resource[uris.length];
        for (int i = 0; i < uris.length; i++) {
            try {
                toTest[i] = EditServlet.findResource(new URI(uris[i]), resources);
            } catch (FileNotFoundException e) {
                message = "File not found! Please try again!";
                return null;
            } catch (URISyntaxException e) {
                message = "Malformed URI cant make URI from param!";
                return null;
            }

        }
        return toTest;
    }

    private boolean isStoreBufferAction(HttpServletRequest req) {
        String source = (String) req.getSession().getAttribute("source");
        logger.debug("Runtime servlet POST source: {}", source);
        if ("buffer".equals(source) || "store".equals(source)) {
            return true;
        } else {
            return false;
        }
    }
}
