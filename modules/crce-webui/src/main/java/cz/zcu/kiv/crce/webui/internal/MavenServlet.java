package cz.zcu.kiv.crce.webui.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet for maven search page.
 *
 * Created by Zdenek Vales on 11.4.2017.
 */
public class MavenServlet extends HttpServlet {

    private static final long serialVersionUID = -7359560802939893940L;

    private static final Logger logger = LoggerFactory.getLogger(MavenServlet.class);

    public static final String SEARCH_BY_PARAM_NAME = "by";
    public static final String SEARCH_BY_COORDINATES = "gav";
    public static final String SEARCH_BY_PACKAGE_NAME = "pname";

    // those correspond with name attribute of html input element
    public static final String GROUP_ID_PARAM = "gid";
    public static final String ARTIFACT_ID_PARAM = "aid";
    public static final String VERSION_PARAM = "ver";
    public static final String PACKAGE_NAME_PARAM = "pname";
    public static final String VERSION_FILTER_PARAM = "verFilter";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // do nothing and display the maven search page
        req.getRequestDispatcher("resource?link=maven").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // search by coordinates or package name?
        String searchBy = null;
        if (req.getParameter(SEARCH_BY_PARAM_NAME) != null) {
            searchBy = req.getParameter(SEARCH_BY_PARAM_NAME);
        }

        if(searchBy.equalsIgnoreCase(SEARCH_BY_COORDINATES)) {
            searchByCoordinates(req, resp);
        } else if (searchBy.equalsIgnoreCase(SEARCH_BY_PACKAGE_NAME)) {
            searchByPackageName(req, resp);
        } else {
            logger.debug("Unknown '"+SEARCH_BY_PARAM_NAME+"' parameter value: "+searchBy);
            req.getRequestDispatcher("resource?link=maven").forward(req, resp);
        }
    }

    /**
     * Handles searching by maven coordinates.
     */
    private void searchByCoordinates(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Searching for maven artifacts by coordinates...");
        String gid = req.getParameter(GROUP_ID_PARAM);
        String aid = req.getParameter(ARTIFACT_ID_PARAM);
        String ver = req.getParameter(VERSION_PARAM);

        // check parameters

        // perform search

        // upload to buffer

        // redirect to buffer page?
        req.getRequestDispatcher("resource?link=maven").forward(req, resp);
    }

    /**
     * Handles searching by package name.
     */
    private void searchByPackageName(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Searching for maven artifacts by package name...");
        String packageName = req.getParameter(PACKAGE_NAME_PARAM);
        String versionFilter = req.getParameter(VERSION_FILTER_PARAM);

        // check parameters

        // perform search

        // upload to buffer

        // redirect to buffer page?
        req.getRequestDispatcher("resource?link=maven").forward(req, resp);
    }


}
