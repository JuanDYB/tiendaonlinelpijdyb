package control;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Juan Díez-Yanguas Barber
 */
public class LogOutServlet extends HttpServlet {
       
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        if (request.getSession().getAttribute("auth") != null && (Boolean)request.getSession().getAttribute("auth") == true){
            request.getSession().invalidate();
            if (request.getAttribute("errorSesion") != null){
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }
            request.getRequestDispatcher("/WEB-INF/admin/logout.jsp").forward(request, response);
        }else{
            response.sendRedirect("/index.jsp");
        }
    } 
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    public String getServletInfo(){
        return "Servlet encargado del cierre de sesión de usuarios";
    }
}
