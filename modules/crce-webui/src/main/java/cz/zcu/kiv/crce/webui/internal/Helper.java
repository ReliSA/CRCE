package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kalwi
 */
public class Helper {

    private static final Logger logger = LoggerFactory.getLogger(Helper.class);
    
    public static void forwardTo(String url, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        logger.debug("Forwarding to: " + url);

        RequestDispatcher rd = req.getRequestDispatcher(url);
        rd.forward(req, res);
    }
}
