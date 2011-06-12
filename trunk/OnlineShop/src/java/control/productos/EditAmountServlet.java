package control.productos;

import modelo.Carrito;
import modelo.Producto;
import control.Tools;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import persistencia.PersistenceInterface;

/**
 * @author Juan Díez-Yanguas Barber
 */
public class EditAmountServlet extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (validateForm(request) == true) {
            if (request.getSession().getAttribute("carrito") == null) {
                request.setAttribute("resultados", "Carrito no encontrado");
                Tools.anadirMensaje(request, "La sesión no contiene carrito de compra, es posible que haya caducado la sesión");
                request.getRequestDispatcher("/shop/products.jsp").forward(request, response);
                return;
            }
            try {
                int cantidad = Tools.validateNumber(request.getParameter("cant"), "Unidades");
                String cod = request.getParameter("prod");
                PersistenceInterface persistencia = (PersistenceInterface) request.getServletContext().getAttribute("persistence");
                Producto prod = persistencia.getProduct(cod);
                if (prod != null) {
                    //Se comprueba si se han pedido mas unidades de las que hay
                    if (cantidad > prod.getStock()){
                        request.setAttribute("resultados", "Stock insuficiente");
                        Tools.anadirMensaje(request, "No tenemos disponibles las unidades que nos solicita");
                        request.getRequestDispatcher("/shop/cart.jsp").forward(request, response);
                        return;
                    }
                    
                    boolean ok = ((Carrito) request.getSession().getAttribute("carrito")).editCant(cod, cantidad, prod.getPrecio());
                    if (ok == false) {
                        request.setAttribute("resultados", "Produco no encontrado en la cesta");
                        Tools.anadirMensaje(request, "El producto que ha seleccionado para modificar su cantidad no se encuentra en el carrito");
                    }
                    request.getRequestDispatcher("/shop/cart.jsp").forward(request, response);
                } else {
                    request.setAttribute("resultados", "Producto no disponible");
                    Tools.anadirMensaje(request, "El producto seleccionado no se ha encontrado en la tienda");
                    request.getRequestDispatcher("/shop/cart.jsp").forward(request, response);
                }
            } catch (IntrusionException ex) {
                request.setAttribute("resultados", "Intrusión detectada");
                Tools.anadirMensaje(request, ex.getUserMessage());
                request.getRequestDispatcher("/shop/cart.jsp").forward(request, response);
            } catch (ValidationException ex) {
                request.setAttribute("resultados", "Datos de formulario inválidos");
                Tools.anadirMensaje(request, ex.getUserMessage());
                request.getRequestDispatcher("/shop/cart.jsp").forward(request, response);
            }
        } else {
            response.sendError(404);
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

    protected boolean validateForm(HttpServletRequest request) {
        if (request.getParameterMap().size() >= 2 && request.getParameter("prod") != null && request.getParameter("cant") != null) {
            return Tools.validateUUID(request.getParameter("prod"));
        } else {
            return false;
        }
    }
    
    @Override
    public String getServletInfo (){
        return "Servlet para la edición de cantidad de un producto en la cesta";
    }
}
