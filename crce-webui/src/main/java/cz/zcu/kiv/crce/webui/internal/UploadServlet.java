package cz.zcu.kiv.crce.webui.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.osgi.service.log.LogService;

/**
 *
 * @author kalwi
 */
public class UploadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);

        PrintWriter out = resp.getWriter();
        out.println("m_stack: " + (Activator.getBuffer() != null ? "found" : "not found"));

        Enumeration en = session.getAttributeNames();

        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            out.println(name + ": " + session.getAttribute(name));
        }

        session.setAttribute("id" + new Random().nextInt(100), new Random().nextInt(10));


        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean success = true;
        if (ServletFileUpload.isMultipartContent(req)) {
            ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
            List fileItemsList;
            try {
                fileItemsList = servletFileUpload.parseRequest(req);
            } catch (FileUploadException e) {
                Activator.getLog().log(LogService.LOG_ERROR, "Exception handling request: " + req.getRequestURL(), e);
                sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            for (Object o : fileItemsList) {
                FileItem fi = (FileItem) o;

                if (fi.isFormField()) {
                    // do nothing
                } else {
                    String fileName = fi.getName();
                    InputStream is = fi.getInputStream();
                    if (Activator.getBuffer().put(fileName, is) == null) {
                        success = false;
                    }
                    is.close();
                }
            }
        } else {
            success = false;
        }

        resp.sendRedirect("index.jsp?success=" + success);
    }

    // send a response with the specified status code
    private void sendResponse(HttpServletResponse response, int statusCode) {
        sendResponse(response, statusCode, "");
    }

    // send a response with the specified status code and description
    private void sendResponse(HttpServletResponse response, int statusCode, String description) {
        try {
            response.sendError(statusCode, description);
        } catch (Exception e) {
//            m_log.log(LogService.LOG_WARNING, "Unable to send response with status code '" + statusCode + "'", e);
        }
    }
}
