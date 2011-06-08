package control.admin;

import modelo.Usuario;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import persistencia.PersistenceInterface;

/**
 * @author JuanDYB
 */
public class AdministrationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpSession sesion = ((HttpServletRequest)request).getSession();
        Usuario user = ((PersistenceInterface)request.getServletContext().getAttribute("persistence")).getUser((String)sesion.getAttribute("usuario"));
        if (user.getPermisos() == 'a'){
            chain.doFilter(request, response);
        }else{
            ((HttpServletResponse)response).sendRedirect("/admin/index.jsp");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {

    }

}
