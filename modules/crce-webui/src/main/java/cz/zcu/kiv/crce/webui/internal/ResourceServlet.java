package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.compatibility.CompatibilityVersionComparator;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiIdentity;
import cz.zcu.kiv.crce.metadata.type.Version;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.webui.internal.bean.Category;
import cz.zcu.kiv.crce.webui.internal.custom.ResourceExt;

public class ResourceServlet extends HttpServlet {

    private static final long serialVersionUID = -4218424299866417104L;

    private static final Logger logger = LoggerFactory.getLogger(ResourceServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String source = (String) req.getSession().getAttribute("source");
        logger.debug("Resource servlet POST source: {}", source);

        //if form was submit, set session parameters{
        if ("yes".equalsIgnoreCase(req.getParameter("showStoreTag"))) {
            req.getSession().setAttribute("showStoreTag", "yes");
            logger.debug("showStoreTag session attribute set to yes");
        } else {
            req.getSession().setAttribute("showStoreTag", "no");
            logger.debug("showStoreTag session attribute set to no");
        }

        if ("yes".equalsIgnoreCase(req.getParameter("showBufferTag"))) {
            req.getSession().setAttribute("showBufferTag", "yes");
            logger.debug("showBufferTag session attribute set to yes");
        } else {
            req.getSession().setAttribute("showBufferTag", "no");
            logger.debug("showBufferTag session attribute set to no");
        }

        if ("upload".equals(source) || "commit".equals(source)) {
            doGet(req, resp);
        } else {
            String filter = req.getParameter("filter");
            if (filter != null && !filter.isEmpty()) {

                logger.warn("LDAP filter is not supported yet with new Metadata API, returning all resources for filter: {}", filter);

                fillSession(source, req, null); // TODO there was filter instead of null in implementation with old metadata
                req.getRequestDispatcher("jsp/" + source + ".jsp").forward(req, resp);
            } else {
                doGet(req, resp);
            }
        }






//        else if (req.getParameter("repositorySelection") != null) {
//            req.getSession().setAttribute("repositoryId", req.getParameter("repositorySelection"));
//        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

        String link = null;

        if (req.getParameter("link") != null) {
            link = req.getParameter("link");
        }

        try {
            if (fillSession(link, req, null)) {
                //resp.sendRedirect("jsp/"+link+".jsp");
                req.getRequestDispatcher("jsp/" + link + ".jsp").forward(req, resp);
            } else {
                logger.debug("Default forward");
                req.getRequestDispatcher("resource?link=store").forward(req, resp);
            }

        } catch (ServletException e) {
            logger.warn("Can't forward: {}", e);
        } catch (IOException e) {
            logger.error("Can't forward", e);
        }
    }

    public static void cleanSession(HttpSession session) {
        session.removeAttribute("resources");
        session.removeAttribute("plugins");
        session.removeAttribute("store");
    }

    public static void setError(HttpSession session, boolean success, String message) {
        session.setAttribute("success", success);
        session.setAttribute("message", message);
    }

    private boolean fillSession(String link, HttpServletRequest req, Requirement filter) {

        String errorMessage = filter + " is not a valid filter";
        HttpSession session = req.getSession();
        cleanSession(session);
        if (link == null) {
            return false;
        }

        Set<Map.Entry<String, String>> repositories = Activator.instance().getRepositories().entrySet();
        session.setAttribute("showRepositorySelection", repositories.size() > 1); // TODO this logic should be in header.jsp
        session.setAttribute("repositories", repositories);
        session.setAttribute("repositoryId", getRepositoryId(req));

        switch (link) {
            case "buffer": {
                List<Resource> resources;
                if (filter == null) {
                    resources = Activator.instance().getBuffer(req).getResources();
                } else {
                    try {
                        resources = Activator.instance().getBuffer(req).getResources(filter);
                    } catch (Exception e) { // TODO there was catch of InvalidSyntaxException, why?
                        setError(session, false, errorMessage);
                        resources = Activator.instance().getBuffer(req).getResources();
                    }
                }
                List<ResourceExt> bufferResourcesExt = new ArrayList<>(resources.size());
                for (Resource resource : resources) {
                    bufferResourcesExt.add(new ResourceExt(resource, Activator.instance().getMetadataService()));
                }
                session.setAttribute("buffer", bufferResourcesExt);
                return true;
            }

            case "plugins": {
                List<Plugin> plugins;
                if (filter == null) {
                    plugins = Activator.instance().getPluginManager().getPlugins();
                } else {
                    plugins = Activator.instance().getPluginManager().getPlugins();
                    logger.warn("Filtering plugins with new Metadata API is not supported yet, returning all plugins.");
//                    plugins = Activator.instance().getPluginManager().getPlugins(Plugin.class, filter);
                }
                List<cz.zcu.kiv.crce.webui.internal.custom.Plugin> pluginWrappers = new ArrayList<>(plugins.size());
                for (Plugin plugin : plugins) {
                    pluginWrappers.add(new cz.zcu.kiv.crce.webui.internal.custom.Plugin(plugin));
                }
                session.setAttribute("plugins", pluginWrappers);
                return true;
            }

            case "store": {
                String id = getRepositoryId(req);
                if (id == null) {
                    return true;
                }
                List<Resource> resources;
                if (filter == null) {
                    resources = Activator.instance().getStore(id).getResources();
                } else {
                    try {
                        resources = Activator.instance().getStore(id).getResources(filter);
                    } catch (Exception e) { // TODO there was catch of InvalidSyntaxException, why?
                        setError(session, false, errorMessage);
                        resources = Activator.instance().getStore(id).getResources();
                    }
                }
                List<ResourceExt> storeResourcesExt = new ArrayList<>(resources.size());
                for (Resource resource : resources) {
                    storeResourcesExt.add(new ResourceExt(resource, Activator.instance().getMetadataService()));
                }
                session.setAttribute("store", storeResourcesExt);
                return true;
            }

            case "tags": {
                List<Resource> resources = prepareResources(req, filter);
                ArrayList<Category> categoryList = prepareCategoryList(resources);
                ArrayList<ResourceExt> filteredResourceList;

                String selectedCategory;
                if (req.getParameter("tag") != null) {
                    selectedCategory = req.getParameter("tag");

                    filteredResourceList = filterResources(selectedCategory, resources);

                } else {
                    filteredResourceList = null;
                }

                session.setAttribute("resources", filteredResourceList);
                session.setAttribute("categoryList", categoryList);

                return true;
            }

            case "webservices": {
                List<Resource> resources;
                if (filter == null) {
                    resources = Activator.instance().getWsBuffer(req).getResources();
                } else {
                    try {
                        resources = Activator.instance().getWsBuffer(req).getResources(filter);
                    } catch (Exception e) { // TODO there was catch of InvalidSyntaxException, why?
                        setError(session, false, errorMessage);
                        resources = Activator.instance().getWsBuffer(req).getResources();
                    }
                }
                List<ResourceExt> bufferResourcesExt = new ArrayList<>(resources.size());
                for (Resource resource : resources) {
                    bufferResourcesExt.add(new ResourceExt(resource, Activator.instance().getMetadataService()));
                }
                session.setAttribute("wsBuffer", bufferResourcesExt);
                return true;
            }

            case "compatibility": {
                String name = req.getParameter("name");
                String version = req.getParameter("version");
                String id = getRepositoryId(req);

                List<Compatibility> lower = null;
                List<Compatibility> upper = null;

                if (name == null || name.isEmpty() || version == null || version.isEmpty() || id == null) {
                    session.setAttribute("nodata", true);
                    return true;
                }
                session.setAttribute("nodata", false);

                Requirement resFilter = Activator.instance().getMetadataFactory().createRequirement(NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
                resFilter.addAttribute(NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME, name);
                Version v = new Version(version);
                resFilter.addAttribute(NsOsgiIdentity.ATTRIBUTE__VERSION, v);

                List<Resource> res = Activator.instance().getStore(id).getResources(resFilter);
                if (!res.isEmpty()) {
                    lower = Activator.instance().getCompatibilityService().listLowerCompatibilities(res.get(0));
                    Collections.sort(lower, CompatibilityVersionComparator.getBaseComparator());

                    upper = Activator.instance().getCompatibilityService().listUpperCompatibilities(res.get(0));
                    Collections.sort(upper, CompatibilityVersionComparator.getUpperComparator());

                }

                session.setAttribute("pivotName", name);
                session.setAttribute("pivotVersion", version);
                session.setAttribute("lower", lower);
                session.setAttribute("upper", upper);

                return true;
            }

            default:
                return false;
        }
    }

    /**
     * Prepares resource array. Resource array is created from store, buffer and webservice buffer.
     * 
     * Store resources are added, if request parameter <code>showStoreTag</code> is set to "yes".
     * Buffer resources are added, if request parameter <code>showBufferTag</code> is set to "yes".
     * Webservice buffer resources are added, if request parameter <code>showWsBufferTag</code> is set to "yes".
     *
     * @param req    request with parameters
     * @param filter possible filter of resources
     * @return array of resources
     */
    private List<Resource> prepareResources(HttpServletRequest req, Requirement filter) {
        HttpSession session = req.getSession();

        List<Resource> storeResources = Collections.emptyList();
        List<Resource> bufferResources = Collections.emptyList();
        List<Resource> wsBufferResources = Collections.emptyList();

        String id = getRepositoryId(req);
        if (id != null && "yes".equalsIgnoreCase((String) session.getAttribute("showStoreTag"))) {
            if (filter == null) {
                storeResources = Activator.instance().getStore(id).getResources();
            } else {
                try {
                    storeResources = Activator.instance().getStore(id).getResources(filter);
                } catch (Exception e) { // TODO there was catch of InvalidSyntaxException, why?
                    logger.warn("Invalid syntax", e);
                    setError(session, false, filter + " is not a valid filter");
                    storeResources = Activator.instance().getStore(id).getResources();
                }
            }
        }

        if ("yes".equals((String) session.getAttribute("showBufferTag"))) {
            if (filter == null) {
                bufferResources = Activator.instance().getBuffer(req).getResources();
            } else {
                try {
                    bufferResources = Activator.instance().getBuffer(req).getResources(filter);
                } catch (Exception e) { // TODO there was catch of InvalidSyntaxException, why?
                    logger.warn("Invalid syntax", e);
                    setError(session, false, filter + " is not a valid filter");
                    bufferResources = Activator.instance().getBuffer(req).getResources();
                }
            }
        }

        if ("yes".equals((String) session.getAttribute("showWsBufferTag"))) {
            if (filter == null) {
                wsBufferResources = Activator.instance().getWsBuffer(req).getResources();
            } else {
                try {
                    wsBufferResources = Activator.instance().getWsBuffer(req).getResources(filter);
                } catch (Exception e) { // TODO there was catch of InvalidSyntaxException, why?
                    logger.warn("Invalid syntax", e);
                    setError(session, false, filter + " is not a valid filter");
                    wsBufferResources = Activator.instance().getWsBuffer(req).getResources();
                }
            }
        }

        //merge all resource arrays together
        List<Resource> resources = new ArrayList<>(storeResources.size() + bufferResources.size() + wsBufferResources.size());
        resources.addAll(storeResources);
        resources.addAll(bufferResources);
        resources.addAll(wsBufferResources);

        return resources;
    }

    /**
     * Prepare list of categories (tags) that are on all resources in the store. Category is represented by name and the number of
     * occurrences.
     *
     * @param resources - all resources from the store
     * @return array list of categories
     */
    private ArrayList<Category> prepareCategoryList(List<Resource> resources) {

        HashMap<String, Integer> categoryMap = new HashMap<>();

        for (Resource resource : resources) {
            List<String> categories = Activator.instance().getMetadataService().getCategories(resource);
            for (String category : categories) {
                if (categoryMap.containsKey(category)) {
                    //category is already contained, increase count
                    categoryMap.put(category, categoryMap.get(category) + 1);
                } else {
                    //add new category
                    categoryMap.put(category, 1);
                }
            }
        }

        ArrayList<Category> categoryList = new ArrayList<>();

        //Get categories from map to list
        for (Map.Entry<String, Integer> entry : categoryMap.entrySet()) {
            Category newCategory = new Category(entry.getKey());
            newCategory.setCount(entry.getValue());
            categoryList.add(newCategory);
        }

        //sort category list
        Collections.sort(categoryList);

        return categoryList;


    }

    /**
     * Filter resources according some category. Output list contains only resources that have required category.
     *
     * @param filterCategory required category
     * @param resources      resources
     * @return filtered resources list
     */
    private ArrayList<ResourceExt> filterResources(String filterCategory, List<Resource> resources) {
        ArrayList<ResourceExt> filteredResourceList = new ArrayList<>();
        for (Resource resource : resources) {
            List<String> categories = Activator.instance().getMetadataService().getCategories(resource);
            for (String category : categories) {
                if (category.equals(filterCategory)) {
                    filteredResourceList.add(new ResourceExt(resource, Activator.instance().getMetadataService()));
                    break;
                }
            }
        }

        return filteredResourceList;
    }

    @CheckForNull
    private String getRepositoryId(HttpServletRequest req) {
        String id = req.getParameter("repositoryId");
        if (id == null) {
            Map<String, String> stores = Activator.instance().getRepositories();
            if (stores.isEmpty()) {
                return null;
            }
            id = stores.keySet().iterator().next();
            logger.trace("Store ID not specified, using the first store found: " + id);
        }
        return id;
    }
}
