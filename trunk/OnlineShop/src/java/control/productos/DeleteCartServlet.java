package control.productos;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import persistencia.PersistenceInterface;

/**
 * @author Juan Díez-Yanguas Barber
 */
public class DeleteCartServlet extends HttpServlet {
    

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.getSession().removeAttribute("carrito");
        PersistenceInterface persistencia = (PersistenceInterface) request.getServletContext().getAttribute("persistence");
        String email = (String)request.getSession().getAttribute("usuario");
        Boolean auth = (Boolean) request.getSession().getAttribute("auth");
        if (auth != null && auth == true && email != null){
            persistencia.deleteImcompleteCartsClient(email);
        }
        request.getRequestDispatcher("/shop/cart.jsp").forward(request, response);
    } 

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.sendError(404);
    }
    
    @Override
    public String getServletInfo (){
        return "Servlet para el borrado de un carrito de la compra de la sesión del usuario";
    }

}
