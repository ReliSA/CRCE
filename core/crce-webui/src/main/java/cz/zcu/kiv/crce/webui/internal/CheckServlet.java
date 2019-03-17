package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import cz.zcu.kiv.crce.metadata.Resolver;
import cz.zcu.kiv.crce.metadata.Resource;
//import cz.zcu.kiv.crce.metadata.ResourceCreator;

public class CheckServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CheckServlet.class);

    private static final long serialVersionUID = -6116518932972052481L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Resource> res = chooseFrom(req);
        if (res == null) {
            req.getRequestDispatcher("resource").forward(req, resp);
        } else {
            String source = (String) req.getSession().getAttribute("source");
            req.getSession().setAttribute(source, res);
            req.getSession().removeAttribute("source");
            req.getRequestDispatcher("jsp/" + source + ".jsp").forward(req, resp);
        }

    }

    private List<Resource> chooseFrom(HttpServletRequest req) {
        String source = (String) req.getSession().getAttribute("source");
        if (source == null) {
            return null;
        } else if (source.equals("buffer")) {
            return doCheck(Activator.instance().getBuffer(req).getResources());
        } else if (source.equals("store")) {
            return doCheck(Activator.instance().getStore(null).getResources());
        } else {
            return null;
        }
    }

    private List<Resource> doCheck(List<Resource> resources) {
        logger.warn("Resolver is not designed yet in new Metadata API, returning empty list of resources. Checked resource: {}", resources);
//        Resource[] resources = repository.getResources();
//        Resource[] cloned = new Resource[resources.length];
//        System.arraycopy(resources, 0, cloned, 0, resources.length);
//        ArrayList<Resource> ext = new ArrayList<>();
//        HashMap<URI, Resource> extMap = new HashMap<>();
//        ResourceCreator rc = Activator.instance().getCreator();
//        Resolver resolver = rc.createResolver(repository);
//        for (Resource r : cloned) {
//            resolver.add(r);
//            r.getUri();
//            extMap.put(r.getUri(), new ResourceExt(r));
//            ext.add(new ResourceExt(r));
//        }
//        if (!resolver.resolve()) {
//            for (Reason r : resolver.getUnsatisfiedRequirements()) {
//                if (extMap.containsKey(r.getResource().getUri())) {
//                    extMap.get(r.getResource().getUri()).addRequirement(r.getRequirement());
//                }
//
//            }
//            return extMap.values().toArray(new Resource[extMap.values().size()]);
//        } else {
//            return resources;
//        }
        return Collections.emptyList();
    }
}
