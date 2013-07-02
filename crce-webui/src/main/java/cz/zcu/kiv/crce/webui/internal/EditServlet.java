package cz.zcu.kiv.crce.webui.internal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.impl.GenericAttributeType;
import cz.zcu.kiv.crce.webui.internal.legacy.Type;

public class EditServlet extends HttpServlet {

    private static final long serialVersionUID = -4949620462179710290L;

    private static final Logger logger = LoggerFactory.getLogger(EditServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = false;
        Map<?, ?> parameters = req.getParameterMap();
        String form = null;

        if (parameters.containsKey("form")) {
            form = ((String[]) parameters.get("form"))[0];
        }
        if (form != null) {
            switch (form) {
                case "addCategory":
                    if (addCategory(req, resp, parameters)) {
                        success = editCategories(req, resp);
                        if (!success) {
                            ResourceServlet.setError(req.getSession(), false, "Cannot add category.");
                            success = true;
                        }
                    } else {
                        ResourceServlet.setError(req.getSession(), false, "Cannot add category.");
                        success = true;
                    }
                    break;

                case "requirements":
                    if (saveRequirements(req, resp, parameters)) {
                        success = editRequirements(req, resp, parameters);
                        if (!success) {
                            ResourceServlet.setError(req.getSession(), false, "Cannot save requirements.");
                            success = true;
                        }
                    }
                    break;

                case "addRequirement":
                    if (!addRequirementForm(req, resp, parameters)) {
                        ResourceServlet.setError(req.getSession(), false, "Cannot add requirement.");
                    }
                    success = editRequirements(req, resp, parameters);
                    if (!success) {
                        ResourceServlet.setError(req.getSession(), false, "Cannot add requirement.");
                        success = true;
                    }
                    break;

                case "capabilities":
                    if (saveCapabilities(req, resp, parameters)) {
                        success = editCapabilities(req, resp, parameters);
                        if (!success) {
                            ResourceServlet.setError(req.getSession(), false, "Cannot save capabilities.");
                            success = true;
                        }

                    } else {
                        ResourceServlet.setError(req.getSession(), false, "Cannot save capabilities.");
                        success = true;
                    }
                    break;

                case "capability":
                    if (saveCapability(req, resp, parameters)) {
                        success = editCapabilities(req, resp, parameters, true);
                        if (!success) {
                            ResourceServlet.setError(req.getSession(), false, "Cannot add capability.");
                            success = true;
                        }
                    } else {
                        ResourceServlet.setError(req.getSession(), false, "Cannot add capability.");
                        success = true;
                    }
                    break;

                case "property":
                    if (!saveProperty(req, resp, parameters)) {
                        ResourceServlet.setError(req.getSession(), false, "Cannot add property.");
                    }
                    success = editCapabilities(req, resp, parameters, true);
                    if (!success) {
                        ResourceServlet.setError(req.getSession(), false, "Error while loading capabilities.");
                        success = true;
                    }
                    break;

                case "editProperties":
                    if (!saveResourceProperty(req, resp, parameters)) {
                        ResourceServlet.setError(req.getSession(), false, "Cannot change properties.");
                    }
                    success = editProperties(req, resp, parameters);
                    if (!success) {
                        ResourceServlet.setError(req.getSession(), false, "Error while loading capabilities.");
                        success = true;
                    }
                    break;

                default:
            }
        }
        if (!success) {
            resp.sendError(HttpServletResponse.SC_ACCEPTED, "NOT FOUND OR FAILED TO PROCEED");
        }
    }

    private boolean saveResourceProperty(HttpServletRequest req, HttpServletResponse resp, Map<?, ?> parameters) { // NOPMD resp would be used in the future
        String uri;
        if (parameters.containsKey("uri")) {
            uri = ((String[]) parameters.get("uri"))[0];
        } else {
            return false;
        }
        try {
            URI resURI = new URI(uri);
            String link = (String) req.getSession().getAttribute("source");
            List<Resource> resources;
            if ("store".equals(link)) {
                resources = Activator.instance().getStore().getResources();
            } else if ("buffer".equals(link)) {
                resources = Activator.instance().getBuffer(req).getResources();
            } else {
                return false;
            }
            Resource resource = findResource(resURI, resources);
            // TODO why was the following here:
//            String symbolicName = ((String[]) parameters.get("symbolicName"))[0];
//            String version = ((String[]) parameters.get("version"))[0];

//            LegacyMetadataHelper.setVersion(Activator.instance().getResourceFactory(), resource, new Version(version));
//            LegacyMetadataHelper.setSymbolicName(Activator.instance().getResourceFactory(), resource, symbolicName);

            Activator.instance().getResourceDAO().saveResource(resource);
        } catch (URISyntaxException e) {
            logger.warn("Can't save resource property: {}", e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            logger.error("Can't save resource property, file not found: {}", uri);
            return false;
        } catch (IOException e) {
            logger.error("Can't save resource property", e);
            return false;
        }

        return true;
    }

    private boolean saveProperty(HttpServletRequest req, HttpServletResponse resp, Map<?, ?> parameters) { // NOPMD resp would be used in the future
        String uri;
        String id;
        if (parameters.containsKey("uri") && parameters.containsKey("capabilityId")) {
            uri = ((String[]) parameters.get("uri"))[0];
            id = ((String[]) parameters.get("capabilityId"))[0];
        } else {
            return false;
        }
        try {
            URI resURI = new URI(uri);
            String link = (String) req.getSession().getAttribute("source");
            List<Resource> array;
            if ("store".equals(link)) {
                array = Activator.instance().getStore().getResources();
            } else if ("buffer".equals(link)) {
                array = Activator.instance().getBuffer(req).getResources();
            } else {
                return false;
            }
            Resource resource = findResource(resURI, array);
            Capability capability = resource.getCapabilities().get(Integer.valueOf(id) - 1);
            String name = ((String[]) parameters.get("name"))[0];
            String type = ((String[]) parameters.get("propertyType"))[0];
            Object value = ((String[]) parameters.get("value"))[0];

            try {
                capability.setAttribute(new GenericAttributeType(name, type), value);
            } catch (IllegalArgumentException e) {
                return false;
            }
            Activator.instance().getResourceDAO().saveResource(resource);

        } catch (URISyntaxException e) {
            logger.warn("Can't save property: {}", e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            logger.error("Can't save property, file not found: {}", uri);
            return false;
        } catch (IOException e) {
            logger.error("Can't save property", e);
            return false;
        }

        return true;
    }

    private boolean editCapabilities(HttpServletRequest req,
            HttpServletResponse resp, Map<?, ?> parameters) {
        return editCapabilities(req, resp, parameters, false);
    }

    private boolean saveCapability(HttpServletRequest req,
            HttpServletResponse resp, Map<?, ?> parameters) {
        String uri;
        String capabilityName;
        if (parameters.containsKey("uri")
                && parameters.containsKey("capability")) {
            uri = ((String[]) parameters.get("uri"))[0];
            capabilityName = ((String[]) parameters.get("capability"))[0];
        } else {
            return false;
        }
        try {
            URI resURI = new URI(uri);
            String link = (String) req.getSession().getAttribute("source");
            List<Resource> array;
            if ("store".equals(link)) {
                array = Activator.instance().getStore().getResources();
            } else if ("buffer".equals(link)) {
                array = Activator.instance().getBuffer(req).getResources();
            } else {
                {
                    return false;
                }
            }
            Resource resource = findResource(resURI, array);
            int lengthBefore = resource.getCapabilities().size();
            resource.addCapability(Activator.instance().getResourceFactory().createCapability(capabilityName));
            if (lengthBefore == resource.getCapabilities().size()) {
                resp.sendRedirect("resource");
                return false;
            }

            Activator.instance().getResourceDAO().saveResource(resource);

            req.setAttribute("capabilityId", String.valueOf(resource.getCapabilities().size()));
        } catch (URISyntaxException e) {
            logger.warn("Can't save capability: {}", e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            logger.error("Can't save capability, file not found: {}", uri);
            return false;
        } catch (IOException e) {
            logger.error("Can't save capability", e);
            return false;
        }

        return true;
    }

    private boolean addRequirementForm(HttpServletRequest req, HttpServletResponse resp, Map<?, ?> parameters) { // NOPMD resp would be used in the future
        String uri;
        if (parameters.containsKey("uri")) {
            uri = ((String[]) parameters.get("uri"))[0];
        } else {
            return false;
        }

        try {
            URI resURI = new URI(uri);
            String link = (String) req.getSession().getAttribute("source");
            List<Resource> array;
            if ("store".equals(link)) {
                array = Activator.instance().getStore().getResources();
            } else if ("buffer".equals(link)) {
                array = Activator.instance().getBuffer(req).getResources();
            } else {
                return false;
            }
            Resource resource = findResource(resURI, array);
            String name = null;
            Requirement requir = null;
            String filter = null;
            boolean multiple = false;
            boolean extend = false;
            boolean optional = false;
            Requirement requirBefore = null;
            if (parameters.containsKey("name")
                    && parameters.containsKey("filter")) {
                name = ((String[]) parameters.get("name"))[0];
                filter = ((String[]) parameters.get("filter"))[0];
                if (parameters.containsKey("multiple")) {
                    multiple = ((String[]) parameters.get("multiple"))[0].equals("on");
                }
                if (parameters.containsKey("optional")) {
                    optional = ((String[]) parameters.get("optional"))[0].equals("on");
                }
                if (parameters.containsKey("extend")) {
                    extend = ((String[]) parameters.get("extend"))[0].equals("on");
                }
                String comment = null;
                if (parameters.containsKey("comment")) {
                    comment = ((String[]) parameters.get("comment"))[0];
                }
                int lengthBefore = resource.getRequirements().size();
                requir = Activator.instance().getResourceFactory().createRequirement(name);
                resource.addRequirement(requir);
                if (lengthBefore == resource.getRequirements().size()) {
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Cannot add requirement.");
                    return false;
                }
                try {
                    logger.warn("OSGi filter is not supported yet with new Metadata API, setting as a directive to track it's usage: {}", filter);
                    requir.setDirective("filter", filter);
                } catch (IllegalArgumentException e) {
                    resource.removeRequirement(requir);
                    resource.addRequirement(requirBefore);
                }
                requir.setDirective("multiple", String.valueOf(multiple));
                requir.setDirective("optional", String.valueOf(optional));
                requir.setDirective("extend", String.valueOf(extend));
                requir.setDirective("comment", comment);
            }
            Activator.instance().getResourceDAO().saveResource(resource);
        } catch (URISyntaxException e) {
            logger.warn("Can't add requirement: {}", e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            logger.error("Can't add requirement, file not found: {}", uri);
            return false;
        } catch (IOException e) {
            logger.error("Can't requirement", e);
            return false;
        }

        req.getSession().setAttribute("success", true);
        return true;

    }

    private boolean saveCapabilities(HttpServletRequest req, HttpServletResponse resp, Map<?, ?> parameters) { // NOPMD resp would be used in the future
        int capabilityId = -1;
        String uri;
        if (parameters.containsKey("uri") && parameters.containsKey("capabilityId")) {
            uri = ((String[]) parameters.get("uri"))[0];
            capabilityId = Integer.valueOf(((String[]) parameters.get("capabilityId"))[0]);
        } else {
            return false;
        }

        try {
            URI resURI = new URI(uri);
            String link = (String) req.getSession().getAttribute("source");
            List<Resource> array;
            if ("store".equals(link)) {
                array = Activator.instance().getStore().getResources();
            } else if ("buffer".equals(link)) {
                array = Activator.instance().getBuffer(req).getResources();
            } else {
                {
                    return false;
                }
            }
            Resource resource = findResource(resURI, array);

            Capability capability = resource.getCapabilities().get(capabilityId - 1);
            List<? extends Attribute<?>> attributes = capability.getAttributes();
//            Property[] properties = capability.getProperties();
            for (int i = 0; i < attributes.size(); i++) {
                String name = ((String[]) parameters.get("name_" + (i + 1)))[0];
                String type = ((String[]) parameters.get("type_" + (i + 1)))[0];
                String value = ((String[]) parameters.get("value_" + (i + 1)))[0];
                Attribute<?> propBefore = attributes.get(i);
                int propertiesLengthBefore = attributes.size();
                capability.removeAttribute(attributes.get(i).getAttributeType());

                if (propertiesLengthBefore == capability.getAttributes().size()) {
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Cannot change property.");
                    logger.debug("Cannot change property, resource: {}", resource);
                    continue;
                }

                try {
                    capability.setAttribute(new GenericAttributeType(name, type), value);
                } catch (IllegalArgumentException e) {
                    capability.setAttribute(propBefore);
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Cannot change property.");
                    logger.warn("Cannot change property, resource: {}, capability: {}", resource, capability);
                }
            }
            Activator.instance().getResourceDAO().saveResource(resource);
        } catch (URISyntaxException e) {
            logger.warn("Can't save capabilities: {}", e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            logger.error("Can't save capabilities, file not found: {}", uri);
            return false;
        } catch (IOException e) {
            logger.error("Can't save capabilities", e);
            return false;
        }
        return true;
    }

    private boolean saveRequirements(HttpServletRequest req, HttpServletResponse resp, Map<?, ?> parameters) { // NOPMD resp would be used in the future
        String uri;
        if (parameters.containsKey("uri")) {
            uri = ((String[]) parameters.get("uri"))[0];
        } else {
            return false;
        }

        try {
            URI resURI = new URI(uri);
            String link = (String) req.getSession().getAttribute("source");
            List<Resource> array;
            if ("store".equals(link)) {
                array = Activator.instance().getStore().getResources();
            } else if ("buffer".equals(link)) {
                array = Activator.instance().getBuffer(req).getResources();
            } else {
                return false;
            }
            Resource resource = findResource(resURI, array);

            List<Requirement> requirements = resource.getRequirements();
            int requirLengthBefore = 0;
            String name = null;
            Requirement requir = null;
            String filter = null;
            String comment = null;
            boolean multiple = false;
            boolean extend = false;
            boolean optional = false;
            Requirement requirBefore = null;
            for (int i = 0; i < requirements.size(); i++) {

                if (parameters.containsKey("name_" + (i + 1))
                        && parameters.containsKey("filter_" + (i + 1))) {

                    name = ((String[]) parameters.get("name_" + (i + 1)))[0];
                    filter = ((String[]) parameters.get("filter_" + (i + 1)))[0];

                    if (parameters.containsKey("multiple_" + (i + 1))) {
                        multiple = ((String[]) parameters.get("multiple_" + (i + 1)))[0].equals("on");
                    }
                    if (parameters.containsKey("optional_" + (i + 1))) {
                        optional = ((String[]) parameters.get("optional_" + (i + 1)))[0].equals("on");
                    }
                    if (parameters.containsKey("extend_" + (i + 1))) {
                        extend = ((String[]) parameters.get("extend_" + (i + 1)))[0].equals("on");
                    }
                    if (parameters.containsKey("comment_" + (i + 1))) {
                        comment = ((String[]) parameters.get("comment_" + (i + 1)))[0];
                    }
//					requirBefore = requirements[i];
                    requirLengthBefore = requirements.size();
                    resource.removeRequirement(requirements.get(i));
                    if (requirLengthBefore == resource.getRequirements().size()) {
//TODO						req.getSession().setAttribute("success", false);
//TODO						req.getSession().setAttribute("message", "Cannot change requirement.");
                        continue;
                    }
                    requir = Activator.instance().getResourceFactory().createRequirement(name);
                    resource.addRequirement(requir);
                    try {
                        logger.warn("OSGi filter is not supported yet with new Metadata API, setting as a directive to track it's usage: {}", filter);
                        requir.setDirective("filter", filter);
                    } catch (IllegalArgumentException e) {
//TODO						req.getSession().setAttribute("success", false);
//TODO						req.getSession().setAttribute("message", "Cannot change requirement.");
                        resource.removeRequirement(requir);
                        resource.addRequirement(requirBefore);
                        continue;
                    }
                    requir.setDirective("multiple", String.valueOf(multiple));
                    requir.setDirective("optional", String.valueOf(optional));
                    requir.setDirective("extend", String.valueOf(extend));
                    requir.setDirective("comment", comment);
//					resource.addRequirement(requir);
                }
            }
            Activator.instance().getResourceDAO().saveResource(resource);
        } catch (URISyntaxException e) {
            logger.warn("Can't save requirements: {}", e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            logger.error("Can't save requirements, file not found: {}", uri);
            return false;
        } catch (IOException e) {
            logger.error("Can't save requirements", e);
            return false;
        }
        return true;
    }

    private boolean addCategory(HttpServletRequest req, HttpServletResponse resp, Map<?, ?> parameters) { // NOPMD resp would be used in the future
        String category = null;
        String uri = null;
        if (parameters.containsKey("category")
                && parameters.containsKey("uri")) {
            category = ((String[]) parameters.get("category"))[0];
            uri = ((String[]) parameters.get("uri"))[0];

        } else {
            return false;
        }

        try {
            URI resURI = new URI(uri);
            String link = (String) req.getSession().getAttribute("source");
            List<Resource> array;
            if ("store".equals(link)) {
                array = Activator.instance().getStore().getResources();
            } else if ("buffer".equals(link)) {
                array = Activator.instance().getBuffer(req).getResources();
            } else {
                return false;
            }
            Resource resource = findResource(resURI, array);


            int categoriesLengthBefore = Activator.instance().getMetadataService().getCategories(resource).size();
            Activator.instance().getMetadataService().addCategory(resource, category);

            // check that category was really added
            if (categoriesLengthBefore >= Activator.instance().getMetadataService().getCategories(resource).size()) {
                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Cannot add category.");
            }

            Activator.instance().getResourceDAO().saveResource(resource);

        } catch (URISyntaxException e) {
            logger.warn("Can't add category: {}", e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            logger.error("Can't add category, file not found: {}", uri);
            return false;
        } catch (IOException e) {
            logger.error("Can't add category", e);
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        cleanSession(req.getSession());

        req.getSession().removeAttribute("success");
        boolean success = false;
        Map<?, ?> parameters = (Map<?, ?>) req.getParameterMap();

        String type = null;
        if (parameters.containsKey("type")) {
            type = ((String[]) parameters.get("type"))[0];
        }
        if (type != null) {
            switch (type) {
                case "deleteCompoment":
                    success = deleteComponent(req, resp, parameters);
                    if (!success) {
                        ResourceServlet.setError(req.getSession(), false, "Cannot delete component.");
                        success = true;
                    }
                    break;

                case "category":
                    success = editCategories(req, resp);
                    if (!success) {
                        ResourceServlet.setError(req.getSession(), false, "Cannot edit category.");
                        success = true;
                    }
                    break;

                case "deleteCategory":
                    if (!deleteCategory(req, resp, parameters)) {
                        ResourceServlet.setError(req.getSession(), false, "Cannot delete category.");
                        success = true;
                    }
                    success = editCategories(req, resp);
                    break;

                case "addCategory":
                    success = addCategories(req, resp, parameters);
                    if (!success) {
                        ResourceServlet.setError(req.getSession(), false, "Cannot add category.");
                        success = true;
                    }
                    break;

                case "addCapability":
                    success = addCapabilities(req, resp, parameters);
                    if (!success) {
                        ResourceServlet.setError(req.getSession(), false, "Cannot add capability.");
                        success = true;
                    }
                    break;

                case "addCapabilityProperty":
                    success = addCapabilityProperty(req, resp, parameters);
                    if (!success) {
                        ResourceServlet.setError(req.getSession(), false, "Cannot add capability.");
                        success = true;
                    }
                    break;

                case "capability":
                    success = editCapabilities(req, resp, parameters);
                    if (!success) {
                        ResourceServlet.setError(req.getSession(), false, "Cannot edit capabilities.");
                        success = true;
                    }
                    break;

                case "requirement":
                    success = editRequirements(req, resp, parameters);
                    if (!success) {
                        ResourceServlet.setError(req.getSession(), false, "Cannot edit requirements.");
                        success = true;
                    }
                    break;

                case "addRequirement":
                    success = addRequirement(req, resp, parameters);
                    if (!success) {
                        ResourceServlet.setError(req.getSession(), false, "Cannot add requirement.");
                        success = true;
                    }
                    break;

                case "properties":
                    success = editProperties(req, resp, parameters);
                    if (!success) {
                        ResourceServlet.setError(req.getSession(), false, "Cannot add requirement.");
                        success = true;
                    }
                    break;
                default:
                    success = false;
                    break;
            }
        }
        if (!success) {
            resp.sendError(HttpServletResponse.SC_ACCEPTED, "NOT FOUND OR FAILED TO PROCEED");
        }
    }

    private boolean editProperties(HttpServletRequest req, HttpServletResponse resp, Map<?, ?> parameters) { // NOPMD parameters would be used in the future
        String link = (String) req.getSession().getAttribute("source");
        List<Resource> array;
        if ("store".equals(link)) {
            array = Activator.instance().getStore().getResources();
        } else if ("buffer".equals(link)) {
            array = Activator.instance().getBuffer(req).getResources();
        } else {
            return false;
        }
        String uri = req.getParameter("uri");
        try {
            URI resURI = new URI(uri);
            Resource resource = findResource(resURI, array);
            req.getSession().setAttribute("resource", resource);
            req.getRequestDispatcher("jsp/forms/propertiesForm.jsp").forward(req, resp); // FIXME hardcoded
        } catch (URISyntaxException e) {
            logger.warn("Can't edit properties: {}", e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            logger.error("Can't edit properties, file not found: {}", uri);
            return false;
        } catch (IOException e) {
            logger.error("Can't edit properties", e);
            return false;
        } catch (ServletException e) {
            logger.error("Can't forward", e);
            return false;
        }
        return true;
    }

    private boolean addCapabilityProperty(HttpServletRequest req, HttpServletResponse resp, Map<?, ?> parameters) { // NOPMD parameters would be used in the future
        String link = (String) req.getSession().getAttribute("source");
        List<Resource> array;
        if ("store".equals(link)) {
            array = Activator.instance().getStore().getResources();
        } else if ("buffer".equals(link)) {
            array = Activator.instance().getBuffer(req).getResources();
        } else {
            return false;
        }
        String uri = req.getParameter("uri");
        try {
            URI resURI = new URI(uri);
            Resource resource = findResource(resURI, array);
            req.getSession().setAttribute("resource", resource);
            req.getSession().setAttribute("capabilityId", req.getParameter("capabilityId"));
            req.getRequestDispatcher("jsp/forms/propertyForm.jsp").forward(req, resp); // FIXME hardcoded
        } catch (URISyntaxException e) {
            logger.warn("Can't add capability property: {}", e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            logger.error("Can't add capability property, file not found: {}", uri);
            return false;
        } catch (IOException e) {
            logger.error("Can't add capability property", e);
            return false;
        } catch (ServletException e) {
            logger.error("Can't forward", e);
            return false;
        }

        return true;
    }

    private boolean addRequirement(HttpServletRequest req, HttpServletResponse resp, Map<?, ?> parameters) { // NOPMD parameters would be used in the future

        String link = (String) req.getSession().getAttribute("source");
        List<Resource> array;
        if ("store".equals(link)) {
            array = Activator.instance().getStore().getResources();
        } else if ("buffer".equals(link)) {
            array = Activator.instance().getBuffer(req).getResources();
        } else {
            return false;
        }
        String uri = req.getParameter("uri");
        try {
            URI resURI = new URI(uri);
            Resource resource = findResource(resURI, array);
            req.getSession().setAttribute("resource", resource);
            req.getRequestDispatcher("jsp/forms/requirementForm.jsp").forward(req, resp); // FIXME hardcoded
        } catch (URISyntaxException e) {
            logger.warn("Can't add requirement: {}", e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            logger.error("Can't add requirement, file not found: {}", uri);
            return false;
        } catch (IOException e) {
            logger.error("Can't add requirement", e);
            return false;
        } catch (ServletException e) {
            logger.error("Can't forward", e);
            return false;
        }

        return true;
    }

    private boolean editRequirements(HttpServletRequest req, HttpServletResponse resp, Map<?, ?> parameters) { // NOPMD parameters would be used in the future

        String link = (String) req.getSession().getAttribute("source");
        List<Resource> array;
        if ("store".equals(link)) {
            array = Activator.instance().getStore().getResources();
        } else if ("buffer".equals(link)) {
            array = Activator.instance().getBuffer(req).getResources();
        } else {
            return false;
        }

        String uri = req.getParameter("uri");

        try {
            URI resURI = new URI(uri);
            Resource resource = findResource(resURI, array);
            req.getSession().setAttribute("resource", resource);
            req.getRequestDispatcher("jsp/forms/requirementsForm.jsp").forward(req, resp); // FIXME hardcoded
        } catch (URISyntaxException e) {
            logger.warn("Can't edit requirement: {}", e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            logger.error("Can't edit requirement, file not found: {}", uri);
            return false;
        } catch (IOException e) {
            logger.error("Can't edit requirement", e);
            return false;
        } catch (ServletException e) {
            logger.error("Can't forward", e);
            return false;
        }
        return true;
    }

    private boolean addCapabilities(HttpServletRequest req, HttpServletResponse resp, Map<?, ?> parameters) { // NOPMD parameters would be used in the future
        String link = (String) req.getSession().getAttribute("source");
        List<Resource> array;
        if ("store".equals(link)) {
            array = Activator.instance().getStore().getResources();
        } else if ("buffer".equals(link)) {
            array = Activator.instance().getBuffer(req).getResources();
        } else {
            return false;
        }

        String uri = req.getParameter("uri");

        try {
            URI resURI = new URI(uri);
            Resource resource = findResource(resURI, array);
            req.getSession().setAttribute("resource", resource);
            req.getSession().setAttribute("types", Type.values());
            req.getRequestDispatcher("jsp/forms/capabilityForm.jsp").forward(req, resp); // FIXME hardcoded
        } catch (URISyntaxException e) {
            logger.warn("Can't add capabilities: {}", e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            logger.error("Can't add capabilities, file not found: {}", uri);
            return false;
        } catch (IOException e) {
            logger.error("Can't add capabilities", e);
            return false;
        } catch (ServletException e) {
            logger.error("Can't forward", e);
            return false;
        }

        return true;
    }

    private boolean editCapabilities(HttpServletRequest req, HttpServletResponse resp, Map<?, ?> parameters, boolean b) {
        String link = (String) req.getSession().getAttribute("source");
        List<Resource> array;

        if ("store".equals(link)) {
            array = Activator.instance().getStore().getResources();
        } else if ("buffer".equals(link)) {
            array = Activator.instance().getBuffer(req).getResources();
        } else {
            return false;
        }

        String uri = null;
        try {
            String id = null;
            if (b && parameters.containsKey("uri") && req.getAttribute("capabilityId") != null) {
                uri = ((String[]) parameters.get("uri"))[0];
                id = (String) req.getAttribute("capabilityId");
            } else if (parameters.containsKey("capabilityId") && parameters.containsKey("uri")) {
                uri = ((String[]) parameters.get("uri"))[0];
                id = ((String[]) parameters.get("capabilityId"))[0];
            } else {
                return false;
            }
            Resource resource = findResource(new URI(uri), array);

            req.getSession().setAttribute("resource", resource);
            req.getSession().setAttribute("types", Type.values());
            req.getSession().setAttribute("capability", resource.getCapabilities().get(Integer.valueOf(id) - 1));
            req.getSession().setAttribute("capabilityId", id);
            req.getRequestDispatcher("jsp/forms/capabilitiesForm.jsp").forward(req, resp); // FIXME hardcoded
        } catch (URISyntaxException e) {
            logger.warn("Can't edit capabilities: {}", e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            logger.error("Can't edit capabilities, file not found: {}", uri);
            return false;
        } catch (IOException e) {
            logger.error("Can't edit capabilities", e);
            return false;
        } catch (ServletException e) {
            logger.error("Can't forward", e);
            return false;
        }

        return true;
    }

    private boolean addCategories(HttpServletRequest req, HttpServletResponse resp, Map<?, ?> parameters) { // NOPMD parameters would be used in the future

        String link = (String) req.getSession().getAttribute("source");
        List<Resource> array;
        if ("store".equals(link)) {
            array = Activator.instance().getStore().getResources();
        } else if ("buffer".equals(link)) {
            array = Activator.instance().getBuffer(req).getResources();
        } else {
            return false;
        }

        String uri = req.getParameter("uri");
        try {
            URI resURI = new URI(uri);
            Resource resource = findResource(resURI, array);
            req.getSession().setAttribute("resource", resource);
            req.getRequestDispatcher("jsp/forms/categoryForm.jsp").forward(req, resp); // FIXME hardcoded
        } catch (URISyntaxException e) {
            logger.warn("Can't add capabilities: {}", e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            logger.error("Can't add capabilities, file not found: {}", uri);
            return false;
        } catch (IOException e) {
            logger.error("Can't add capabilities", e);
            return false;
        } catch (ServletException e) {
            logger.error("Can't forward", e);
            return false;
        }

        return true;
    }

    private boolean editCategories(HttpServletRequest req, HttpServletResponse resp) {
        String link = (String) req.getSession().getAttribute("source");
        List<Resource> array;
        if ("store".equals(link)) {
            array = Activator.instance().getStore().getResources();
        } else if ("buffer".equals(link)) {
            array = Activator.instance().getBuffer(req).getResources();
        } else {
            return false;
        }

        String uri = req.getParameter("uri");
        try {
            URI resURI = new URI(uri);
            Resource resource = findResource(resURI, array);
            req.getSession().setAttribute("resource", resource);
            req.getRequestDispatcher("jsp/forms/categoriesForm.jsp").forward(req, resp); // FIXME hardcoded
        } catch (URISyntaxException e) {
            logger.warn("Can't edit categories: {}", e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            logger.error("Can't edit categories, file not found: {}", uri);
            return false;
        } catch (IOException e) {
            logger.error("Can't edit categories", e);
            return false;
        } catch (ServletException e) {
            logger.error("Can't forward", e);
            return false;
        }

        return true;
    }

    private boolean deleteCategory(HttpServletRequest req, HttpServletResponse resp, Map<?, ?> parameters) { // NOPMD resp would be used in the future
        String category = null;
        String uri = null;
        if (parameters.containsKey("category")
                && parameters.containsKey("uri")) {
            category = ((String[]) parameters.get("category"))[0];
            uri = ((String[]) parameters.get("uri"))[0];

        } else {
            return false;
        }
        try {
            URI resURI = new URI(uri);
            String link = (String) req.getSession().getAttribute("source");
            List<Resource> array;
            if ("store".equals(link)) {
                array = Activator.instance().getStore().getResources();
            } else if ("buffer".equals(link)) {
                array = Activator.instance().getBuffer(req).getResources();
            } else {
                return false;
            }

            Resource resource = findResource(resURI, array);

            int categoriesLengthBefore = Activator.instance().getMetadataService().getCategories(resource).size();
            Activator.instance().getMetadataService().removeCategory(resource, category);

//			Zjištění zda kategorie byla odstraněna.
            if (categoriesLengthBefore == Activator.instance().getMetadataService().getCategories(resource).size()) {
                return false;
            }

            Activator.instance().getResourceDAO().saveResource(resource);

        } catch (URISyntaxException e) {
            logger.warn("Can't delete category: {}", e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            logger.error("Can't delete category, file not found: {}", uri);
            return false;
        } catch (IOException e) {
            logger.error("Can't delete category", e);
            return false;
        }

        return true;
    }

    private boolean deleteComponent(HttpServletRequest req, HttpServletResponse resp, final Map<?, ?> parameters) {
        String link = null;
        String uri = null;
        if (parameters.containsKey("link")
                && parameters.containsKey("uri")) {
            link = ((String[]) parameters.get("link"))[0];
            uri = ((String[]) parameters.get("uri"))[0];

        } else {
            return false;
        }
        try {
            URI fileUri = new URI(uri);
            if ("store".equals(link)) {
                List<Resource> array = Activator.instance().getStore().getResources();
                Resource found = findResource(fileUri, array);
                Activator.instance().getStore().remove(found);
            } else if ("buffer".equals(link)) {
                List<Resource> array = Activator.instance().getBuffer(req).getResources();
                Resource found = findResource(fileUri, array);
                if (!Activator.instance().getBuffer(req).remove(found)) {
                    logger.warn("Buffer didn't contain removed resource: {}", found);
                }
            } else {
                return false;
            }

            req.getRequestDispatcher("resource").forward(req, resp);

        } catch (URISyntaxException e) {
            logger.warn("Can't delete component: {}", e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            logger.error("Can't delete component, file not found: {}", uri);
            return false;
        } catch (IOException e) {
            logger.error("Can't delete component", e);
            return false;
        } catch (ServletException e) {
            logger.error("Can't forward", e);
            return false;
        }
        return true;
    }

    public static Resource findResource(URI uri, List<Resource> array) throws FileNotFoundException {
        Resource found = null;
        for (Resource r : array) {
            if (Activator.instance().getMetadataService().getUri(r).equals(uri)) {
                found = r;
                break;
            }
        }
        if (found == null) {
            throw new FileNotFoundException();
        }
        return found;
    }

    private static void cleanSession(HttpSession session) {
        session.removeAttribute("resource");
    }
}
