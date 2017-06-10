package cz.zcu.kiv.crce.webui.internal;

import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenLocator;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenResolver;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.VersionFilter;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.CentralMavenRestLocator;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.resolver.MavenAetherResolver;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A servlet for maven search page.
 *
 * Created by Zdenek Vales on 11.4.2017.
 */

// todo: feedback, use threads for big result sets, use groupId filtering
public class MavenServlet extends HttpServlet {

    private static final long serialVersionUID = -7359560802939893940L;

    private static final Logger logger = LoggerFactory.getLogger(MavenServlet.class);

    public static final String COORDINATES_FEEDBACK = "feedback1";
    public static final String PACKAGE_NAME_FEEDBACK = "feedback2";
    public static final String MAIN_FEEDBACK = "feedback3";

    public static final String SEARCH_BY_PARAM_NAME = "by";
    public static final String SEARCH_BY_COORDINATES = "gav";
    public static final String SEARCH_BY_PACKAGE_NAME = "pname";

    public static final String LOWEST_VERSION = "lv";
    public static final String HIGHEST_VERSION = "hv";

    public static final String NO_GROUP_ID_FILTER = "nogId";
    public static final String HIGHEST_GROUP_ID = "hmatch";
    public static final String MANUAL_GROUP_ID = "manualg";
    public static final String MANUAL_GROUP_ID_VAL = "manualgVal";

    // those correspond with name attribute of html input element
    public static final String GROUP_ID_PARAM = "gid";
    public static final String ARTIFACT_ID_PARAM = "aid";
    public static final String VERSION_PARAM = "ver";
    public static final String PACKAGE_NAME_PARAM = "pname";
    public static final String VERSION_FILTER_PARAM = "verFilter";
    public static final String GROUP_FILTER_PARAM_NAME = "gidFilter";

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
            String msg = "Unknown '"+SEARCH_BY_PARAM_NAME+"' parameter value: "+searchBy;
            logger.debug(msg);
            req.setAttribute(MAIN_FEEDBACK, "Unknow search method: "+searchBy+".");
            req.getRequestDispatcher("resource?link=maven").forward(req, resp);
        }
    }

    /**
     * Handles searching by maven coordinates.
     */
    private void searchByCoordinates(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String gid = req.getParameter(GROUP_ID_PARAM);
        String aid = req.getParameter(ARTIFACT_ID_PARAM);
        String ver = req.getParameter(VERSION_PARAM);
        logger.debug("Searching for maven artifacts by coordinates: gid={}; aid={}; version={}",gid, aid, ver);

        // check parameters
        if( gid == null || gid.isEmpty() ||
            aid == null || aid.isEmpty() ||
            ver == null || ver.isEmpty()) {
            logger.warn("Not all parameters were specified.");
            req.setAttribute(COORDINATES_FEEDBACK, "Group Id, artifact Id or version is missing.");
            req.getRequestDispatcher("resource?link=maven").forward(req, resp);
            return;
        }

        // perform search
        MavenLocator locator = new CentralMavenRestLocator();
        MavenResolver resolver = new MavenAetherResolver();
        FoundArtifact foundArtifact = locator.locate(gid, aid, ver);
        if(foundArtifact == null) {
            logger.warn("No artifact found...");
            req.setAttribute(MAIN_FEEDBACK, "0 artifacts found.");
            req.getRequestDispatcher("resource?link=maven").forward(req, resp);
            return;
        }

        File resolvedArtifact = resolver.resolve(foundArtifact);
        if(resolvedArtifact == null) {
            // this really shouldn't happen
            req.setAttribute(MAIN_FEEDBACK, "Error while resolving artifact.");
            req.getRequestDispatcher("resource?link=maven").forward(req, resp);
            return;
        }

        // upload to buffer
        try {
            Activator.instance().getBuffer(req).put(resolvedArtifact.getName(), new FileInputStream(resolvedArtifact));
        } catch (RefusedArtifactException e) {
            logger.warn("Artifact revoked: ", e.getMessage());
            req.setAttribute(MAIN_FEEDBACK, "Error while putting artifact to buffer.");
            req.getRequestDispatcher("resource?link=maven").forward(req, resp);
            return;
        }

        // redirect to buffer page?
        req.setAttribute(MAIN_FEEDBACK, "1 artifact put to buffer.");
        req.getRequestDispatcher("resource?link=maven").forward(req, resp);
    }

    /**
     * Handles searching by package name.
     */
    private void searchByPackageName(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Searching for maven artifacts by package name...");
        String packageName = req.getParameter(PACKAGE_NAME_PARAM);
        String versionFilter = req.getParameter(VERSION_FILTER_PARAM);
        String groupIdFilter = req.getParameter(GROUP_FILTER_PARAM_NAME);
        String manualG = "";

        logger.debug("Package name: "+packageName+"; version filter: "+versionFilter+"; groupId filter: "+groupIdFilter);

        // check parameters
        if( packageName == null || packageName.isEmpty() ||
            versionFilter == null || versionFilter.isEmpty() ||
            groupIdFilter == null || groupIdFilter.isEmpty()) {
            // todo: display some error message?
            logger.warn("Package name, version filter or group id filter not specified.");
            req.setAttribute(PACKAGE_NAME_FEEDBACK, "Package name, version filter or group id filter is missing.");
            req.getRequestDispatcher("resource?link=maven").forward(req, resp);
            return;
        }

        if(groupIdFilter == MANUAL_GROUP_ID) {
            manualG = req.getParameter(MANUAL_GROUP_ID_VAL);
        }

        // perform search
        MavenLocator locator = new CentralMavenRestLocator();
        MavenResolver resolver = new MavenAetherResolver();
        // todo: seearch with manual groupId filter
        Collection<FoundArtifact> foundArtifacts = new ArrayList<>();
        switch (groupIdFilter) {
            case NO_GROUP_ID_FILTER:
                foundArtifacts = locator.locate(packageName, false);
                break;

            case HIGHEST_GROUP_ID:
                foundArtifacts = locator.locate(packageName, true);
                break;

            case MANUAL_GROUP_ID:
                // todo:
                break;

            default:
                foundArtifacts = locator.locate(packageName, true);
        }

        // version filtering
        VersionFilter vf = VersionFilter.HIGHEST_ONLY;
        if(versionFilter.equals(LOWEST_VERSION)) {
            vf = VersionFilter.LOWEST_ONLY;
        }
        foundArtifacts = locator.filter(foundArtifacts, "org.hibernate");
        foundArtifacts = locator.filter(foundArtifacts, vf);
        Collection<File> resolvedArtifacts = resolver.resolveArtifacts(foundArtifacts);
        if(resolvedArtifacts == null) {
            // this really shouldn't happen
            logger.warn("Artifact couldn't been resolved.");
            req.setAttribute(PACKAGE_NAME_FEEDBACK, "Error while locating artifacts.");
            req.getRequestDispatcher("resource?link=maven").forward(req, resp);
            return;
        }

        // upload to buffer
        logger.debug(resolvedArtifacts.size()+" artifacts resolved.");
        try {
            for(File resolvedArtifact : resolvedArtifacts) {
                Activator.instance().getBuffer(req).put(resolvedArtifact.getName(), new FileInputStream(resolvedArtifact));
            }
        } catch (RefusedArtifactException e) {
            logger.warn("Artifact revoked: ", e.getMessage());
            req.setAttribute(PACKAGE_NAME_FEEDBACK, "Error while putting artifact to buffer.");
            req.getRequestDispatcher("resource?link=maven").forward(req, resp);
            return;
        }

        req.setAttribute(MAIN_FEEDBACK, resolvedArtifacts.size()+" artifacts put to buffer.");
        req.getRequestDispatcher("resource?link=maven").forward(req, resp);
    }


}
