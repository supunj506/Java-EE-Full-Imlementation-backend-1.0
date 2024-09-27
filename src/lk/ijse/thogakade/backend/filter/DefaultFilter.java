/*
 * @author : xCODE
 * Project : ThogakadeJavaEEFull Implimentation_1.0
 * Date    : 9/25/2024 (Wednesday)
 * Time    : 2:10 PM
 * For GDSE course of IJSE institute.
 */

package lk.ijse.thogakade.backend.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebFilter(urlPatterns = "/*")
public class DefaultFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        resp.addHeader("Access-Control-Allow-Origin","*");
        resp.addHeader("Access-Control-Allow-Methods","DELETE,PUT,OPTIONS");
        resp.addHeader("Access-Control-Allow-Headers","Content-Type");
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
