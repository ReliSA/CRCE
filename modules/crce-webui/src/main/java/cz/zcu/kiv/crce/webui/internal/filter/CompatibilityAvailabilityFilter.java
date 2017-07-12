package cz.zcu.kiv.crce.webui.internal.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import cz.zcu.kiv.crce.webui.internal.Activator;

/**
 * This filter checks for CompatibilityService presence
 * and sets hasCompatInfo attribute for the request accordingly.
 *
 * Date: 25.3.15
 *
 * @author Jakub Danek
 */
public class CompatibilityAvailabilityFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
       request.setAttribute("hasCompatInfo", Activator.instance().isCompatibilityServicePresent());
       chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
