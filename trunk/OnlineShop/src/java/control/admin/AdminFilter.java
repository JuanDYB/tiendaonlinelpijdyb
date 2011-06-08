package control.admin;

import modelo.Usuario;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import persistencia.PersistenceInterface;

/**
 * @author Juan Díez-Yanguas Barber
 */
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest requestMod = ((HttpServletRequest) request);
        if (isPermited(requestMod) == false){
            requestMod.getSession().setAttribute("requestedPage", requestMod.getRequestURL().toString());
            RequestDispatcher noPermited = request.getRequestDispatcher("/WEB-INF/paginasError/restricted.jsp");
            noPermited.forward(request, response);
        }else{
            chain.doFilter(request, response);
        }
    }

    private boolean isPermited(HttpServletRequest request) {      
        if (request.getSession().getAttribute("auth") == null && request.getSession().getAttribute("usuario") == null) {
            return false;
        } else if ((Boolean)request.getSession().getAttribute("auth")){
            Usuario user = ((PersistenceInterface)request.getServletContext().getAttribute("persistence")).getUser((String)request.getSession().getAttribute("usuario"));
            if (user == null){
                return false;
            }else{
                return true;
            }
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
