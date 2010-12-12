package cz.zcu.kiv.ccer.webui.internal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Dictionary;
import java.util.Enumeration;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

/**
 *
 * @author kalwi
 */
public class TestServlet extends HttpServlet implements ManagedService {
    
    private static final long serialVersionUID = -5870408422415688046L; // TODO regenerate

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        PrintWriter out = resp.getWriter();
        
        ServletContext cx = this.getServletContext();
        
        out.println("Hello, world!");

        Enumeration e = cx.getInitParameterNames();

        while (e.hasMoreElements()) {
            String element = (String) e.nextElement();
            out.println(element + ": " + cx.getInitParameter(element));
        }
        
        
        out.close();
        
//        try {
//            forwardTo("/index.html", req, resp);
//        } catch (Exception e) {
//            System.out.println("***********");
//            e.printStackTrace();
//        }
        
    }

    private void forwardTo(String url, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        System.out.println("url: " + url);
        
            RequestDispatcher rd = req.getRequestDispatcher(url);
            
            System.out.println("rd: " + (rd != null));
            
            rd.forward(req, res);
    }
    
    
    @Override
    public void updated(Dictionary dctnr) throws ConfigurationException {
        System.out.println("servlet updated");
    }

}
