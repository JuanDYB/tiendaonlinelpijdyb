package control.admin;

import control.Tools;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import persistencia.PersistenceInterface;

/**
 * @author Juan Díez-Yanguas Barber
 */
public class DeleteProductServlet extends HttpServlet {
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (validar(request) == true) {
            request.setAttribute("resultados", "Resultados de la operación");
            PersistenceInterface persistencia = (PersistenceInterface) request.getServletContext().getAttribute("persistence");
            boolean ok = persistencia.delProduct(request.getParameter("prod"));

            if (ok == true) {
                Tools.anadirMensaje(request, "El producto ha sido borrado correctamente");
            } else {
                Tools.anadirMensaje(request, "Ha ocurrido un error borrando el producto");
            }
           String rutaDeLaImagen = request.getServletContext().getRealPath("/images/products/" +
                   request.getParameter("cod"));
            if (Tools.existeElFichero(rutaDeLaImagen)) {
                boolean image = Tools.borrarImagenDeProdructoDelSistemaDeFicheros(rutaDeLaImagen);
                if (image) {
                    Tools.anadirMensaje(request, "Se ha borrado correctamente la imagen del producto");
                } else {
                    Tools.anadirMensaje(request, "Hubo un error borrando la imagen del producto");
                }
            }            
            RequestDispatcher borrado = request.getRequestDispatcher("/admin/administration/products_administration.jsp");
            borrado.forward(request, response);
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
        response.sendError(404);
    }

    protected boolean validar(HttpServletRequest request) {
        if (request.getParameterMap().size() >= 1 && request.getParameter("prod") != null) {
            return Tools.validateUUID(request.getParameter("prod"));
        } else {
            return false;
        }
    }
    
    @Override
    public String getServletInfo (){
        return "Servlet para borrado de un producto";
    }
}
