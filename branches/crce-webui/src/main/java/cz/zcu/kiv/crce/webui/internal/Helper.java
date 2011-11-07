package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author kalwi
 */
public class Helper {

    public static void forwardTo(String url, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        System.out.println("Forwarding to: " + url);

        RequestDispatcher rd = req.getRequestDispatcher(url);
        rd.forward(req, res);
    }
}
