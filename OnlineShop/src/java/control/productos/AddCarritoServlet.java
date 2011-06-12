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
public class AddCarritoServlet extends HttpServlet {

    protected boolean validateForm(HttpServletRequest request) {
        if (request.getParameterMap().size() >= 2 && request.getParameter("prod") != null && request.getParameter("cant") != null) {
            return Tools.validateUUID(request.getParameter("prod"));
        } else {
            return false;
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (validateForm(request) == false) {
            response.sendError(404);
            return;
        } else {
            try {
                int cantidadNueva = Tools.validateNumber(request.getParameter("cant"), "Unidades");
                String cod = request.getParameter("prod");
                PersistenceInterface persistencia = (PersistenceInterface) request.getServletContext().getAttribute("persistence");
                if (persistencia.getProduct(cod) != null) {
                    Producto prod = persistencia.getProduct(cod);
                    Carrito carro = (Carrito) request.getSession().getAttribute("carrito");
                    Integer cantidadActual = 0;
                    if (carro != null) {
                        cantidadActual = carro.getArticulos().get(cod);
                        if (cantidadActual == null) {
                            cantidadActual = 0;
                        }
                    }
                    if ((cantidadNueva + cantidadActual) > prod.getStock()) {
                        request.setAttribute("resultados", "No hay suficiente Stock");
                        Tools.anadirMensaje(request, "No hay stock suficiente del producto seleccionado");
                        request.getRequestDispatcher("/shop/products.jsp").forward(request, response);
                        return;
                    }
                    if (carro == null) {
                        carro = new Carrito(Tools.generaUUID(), (String)request.getSession().getAttribute("usuario"));
                        carro.addProduct(request.getParameter("prod"), cantidadNueva, prod.getPrecio());
                        request.getSession().setAttribute("carrito", carro);
                    } else {
                        ((Carrito) request.getSession().getAttribute("carrito")).addProduct(cod, cantidadNueva, prod.getPrecio());
                    }
                } else {
                    request.setAttribute("resultados", "Producto no disponible");
                    Tools.anadirMensaje(request, "El producto elegido no existe");
                }
            } catch (IntrusionException ex) {
                request.setAttribute("resultados", "Intrusión detectada");
                Tools.anadirMensaje(request, ex.getUserMessage());
            } catch (ValidationException ex) {
                request.setAttribute("resultados", "Datos de formulario no válidos");
                Tools.anadirMensaje(request, ex.getUserMessage());
            }
        }
        request.getRequestDispatcher("/shop/products.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(404);
    }
    
    @Override
    public String getServletInfo (){
        return "Servlet encargado de añadir un producto al carrito";
    }
}
