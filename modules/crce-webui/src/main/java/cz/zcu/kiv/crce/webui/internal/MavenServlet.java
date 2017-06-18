package cz.zcu.kiv.crce.webui.internal;

import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenLocator;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenResolver;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.VersionFilter;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.CentralMavenRestLocator;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.resolver.MavenAetherResolver;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * A servlet for maven search page.
 *
 * Created by Zdenek Vales on 11.4.2017.
 */
public class MavenServlet extends HttpServlet {

    private static final long serialVersionUID = -7359560802939893940L;

    private static final Logger logger = LoggerFactory.getLogger(MavenServlet.class);

    public static final String COORDINATES_FEEDBACK = "coordFeedback";
    public static final String PACKAGE_NAME_FEEDBACK = "packageFeedback";
    public static final String MAIN_FEEDBACK = "mainFeedback";
    public static final String CONF_FEEDBACK = "confFeedback";

    public static final String LOWEST_VERSION = "lv";
    public static final String HIGHEST_VERSION = "hv";
    public static final String NO_VERSION_FILTER = "no-v";

    public static final String NO_GROUP_ID_FILTER = "no-gid";
    public static final String HIGHEST_GROUP_ID = "h-match";
    public static final String MANUAL_GROUP_ID = "manual-gid";
    public static final String MANUAL_GROUP_ID_VAL = "manual-gid-val";

    // those correspond with name attribute of html input element
    public static final String GROUP_ID_PARAM = "gid";
    public static final String ARTIFACT_ID_PARAM = "aid";
    public static final String VERSION_PARAM = "ver";
    public static final String PACKAGE_NAME_PARAM = "pname";
    public static final String PACKAGE_VERSION_FILTER = "package-ver-filter";
    public static final String COORD_VERSION_FILTER = "coord-ver-filter";
    public static final String GROUP_FILTER_PARAM_NAME = "gid-filter";
    public static final String RESOLVER_CONFIGRATION = "resolver-conf-file";

    // 'search' button names
    public static final String COORD_SEARCH = "coord-search";
    public static final String PACKAGE_SEARCH = "package-search";

    private MavenLocator locator = new CentralMavenRestLocator();
    private MavenAetherResolver resolver = new MavenAetherResolver();

    private Map<String, String> parameters;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // do nothing and display the maven search page
        req.getRequestDispatcher("resource?link=maven").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // first of all load configuration if needed
        loadConfiguration(req, resp);

        // search by coordinates or package name?
        if(parameters.containsKey(COORD_SEARCH)) {
            searchByCoordinates(req, resp);
        } else if (parameters.containsKey(PACKAGE_SEARCH)) {
            searchByPackageName(req, resp);
        } else  {
            String msg = "Unknown action.";
            logger.debug(msg);
            displayFeedback(req, resp, MAIN_FEEDBACK, msg);
            return;
        }
    }

    /**
     * Check if any configuration files were uploaded and reconfigure components.
     * Uses apache commons library to get the uploadded file and must be called before any
     * 'getParameter()' method is called on the request object.
     *
     * @param req Request.
     * @param resp Response.
     */
    private void loadConfiguration(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        parameters = new HashMap<>();
        try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);
            for(FileItem fi : items) {
                if(!fi.isFormField()) {
                    String fieldName = fi.getFieldName();
                    if(fieldName.equals(RESOLVER_CONFIGRATION)) {
                        // load configuration file for resolver
                        InputStream is = fi.getInputStream();
                        resolver.reconfigure(is);
                        is.close();
                    }
                } else {
                    // normal parameter
                    String fieldName = fi.getFieldName();
                    String fieldValue = fi.getString();
                    parameters.put(fieldName, fieldValue);
                }
            }
        } catch (FileUploadException | IOException e) {
            logger.error("Exception while getting the uploaded file: "+e.getMessage());
            displayFeedback(req, resp, CONF_FEEDBACK, "Error while getting uploaded configuration file.");
            return;
        }
    }

    /**
     * Handles searching by maven coordinates.
     */
    private void searchByCoordinates(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String gid = parameters.get(GROUP_ID_PARAM);
        String aid = parameters.get(ARTIFACT_ID_PARAM);
        String ver = parameters.get(VERSION_PARAM);
        String verFilter = parameters.get(COORD_VERSION_FILTER);
        VersionFilter vf = VersionFilter.HIGHEST_ONLY;
        logger.debug("Searching for maven artifacts by coordinates: gid={}; aid={}; version={}",gid, aid, ver);

        // check parameters
        if( gid == null || gid.isEmpty() ||
            aid == null || aid.isEmpty() ||
               (ver == null || ver.isEmpty()) && verFilter.equals(NO_VERSION_FILTER)
            ) {
            logger.warn("Not all parameters were specified.");
            displayFeedback(req, resp, COORDINATES_FEEDBACK, "Group Id, artifact Id or version is missing.");
            return;
        }

        // perform search
        Collection<FoundArtifact> foundArtifacts = new ArrayList<>();
        if(ver == null || ver.isEmpty() || !verFilter.equals(NO_VERSION_FILTER)) {
            foundArtifacts = locator.locate(gid, aid);
            if(verFilter.equals(LOWEST_VERSION)) {
                vf = VersionFilter.LOWEST_ONLY;
            }
            foundArtifacts = locator.filter(foundArtifacts, vf);
        } else {
            foundArtifacts.add(locator.locate(gid, aid, ver));
        }

        if(foundArtifacts.isEmpty()) {
            logger.warn("No artifact found...");
            displayFeedback(req, resp, MAIN_FEEDBACK, "0 artifacts found.");
            return;
        }

        Collection<File> resolvedArtifacts = resolver.resolveArtifacts(foundArtifacts);
        if(resolvedArtifacts == null) {
            // this really shouldn't happen
            displayFeedback(req, resp, MAIN_FEEDBACK, "Error while resolving artifact.");
            return;
        }

        // upload to buffer
        addResolvedArtifactsToBuffer(req, resp, resolvedArtifacts);
    }

    /**
     * Handles searching by package name.
     */
    private void searchByPackageName(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Searching for maven artifacts by package name...");
        String packageName = parameters.get(PACKAGE_NAME_PARAM);
        String versionFilter = parameters.get(PACKAGE_VERSION_FILTER);
        String groupIdFilter = parameters.get(GROUP_FILTER_PARAM_NAME);
        String manualG = "";

        logger.debug("Package name: "+packageName+"; version filter: "+versionFilter+"; groupId filter: "+groupIdFilter);

        // check parameters
        if( packageName == null || packageName.isEmpty() ||
            versionFilter == null || versionFilter.isEmpty() ||
            groupIdFilter == null || groupIdFilter.isEmpty()) {
            // todo: display some error message?
            logger.warn("Package name, version filter or group id filter not specified.");
            displayFeedback(req, resp, PACKAGE_NAME_FEEDBACK, "Package name, version filter or group id filter is missing.");
            return;
        }

        if(groupIdFilter.equals(MANUAL_GROUP_ID)) {
            manualG = parameters.get(MANUAL_GROUP_ID_VAL);
            if(manualG == null || manualG.isEmpty()) {
                logger.warn("Manual groupId filter si missing.");
                displayFeedback(req, resp, PACKAGE_NAME_FEEDBACK, "Value for manual groupId filter is missing");
                return;
            } else {
                logger.debug("Value of manual groupId filter: "+manualG);
            }
        }

        // perform search
        Collection<FoundArtifact> foundArtifacts = new ArrayList<>();
        switch (groupIdFilter) {
            case NO_GROUP_ID_FILTER:
                foundArtifacts = locator.locate(packageName);
                break;

            case HIGHEST_GROUP_ID:
                foundArtifacts = locator.locate(packageName, true);
                break;

            case MANUAL_GROUP_ID:
                foundArtifacts = locator.locate(packageName, manualG, false);
                break;

            default:
                foundArtifacts = locator.locate(packageName, true);
        }

        // version filtering
        VersionFilter vf = VersionFilter.HIGHEST_ONLY;
        if(versionFilter.equals(LOWEST_VERSION)) {
            vf = VersionFilter.LOWEST_ONLY;
        }
        foundArtifacts = locator.filter(foundArtifacts, vf);
        Collection<File> resolvedArtifacts = resolver.resolveArtifacts(foundArtifacts);
        if(resolvedArtifacts == null) {
            // this really shouldn't happen
            logger.warn("Artifact couldn't been resolved.");
            displayFeedback(req, resp, PACKAGE_NAME_FEEDBACK, "Error while locating artifacts.");
            return;
        }

        // upload to buffer
        addResolvedArtifactsToBuffer(req, resp, resolvedArtifacts);
    }

    private void addResolvedArtifactsToBuffer(HttpServletRequest req, HttpServletResponse resp, Collection<File> resolvedArtifacts) throws IOException, ServletException {
        logger.debug(resolvedArtifacts.size()+" artifacts resolved.");
        int bufferCounter = 0;
        for(File resolvedArtifact : resolvedArtifacts) {
            FileInputStream fi = new FileInputStream(resolvedArtifact);
            try {
                Activator.instance().getBuffer(req).put(resolvedArtifact.getName(), fi);
                bufferCounter++;
            } catch (RefusedArtifactException e) {
                logger.warn("Artifact revoked: ", e.getMessage());
                displayFeedback(req, resp, PACKAGE_NAME_FEEDBACK, "Error while putting artifact to buffer.");
                return;
            } finally {
                fi.close();
            }
        }

        displayFeedback(req, resp, MAIN_FEEDBACK, bufferCounter+" artifact(s) resolved");
    }

    /**
     * Sets the feedback attribute and redirects to maven search page immediately.
     * @param feedbackName Name of the feedback.
     * @param feedback Feedback to be displayed
     */
    private void displayFeedback(HttpServletRequest req, HttpServletResponse resp, String feedbackName, String feedback) throws ServletException, IOException {
        req.setAttribute(feedbackName, feedback);
        req.getRequestDispatcher("resource?link=maven").forward(req, resp);
        return;
    }
}
