package admin;

import control.Tools;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import persistencia.PersistenceInterface;

/**
 *
 * @author JuanDYB
 */
public class DeleteProductServlet extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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
            
            //Borra la imagen si existe
            if (Tools.fileExists(request.getServletContext().getRealPath("/images/products/" + request.getParameter("cod"))) == true) {
                boolean image = Tools.borrarImagenProd(request.getServletContext().getRealPath("/images/products/" + request.getParameter("cod")));
                if (image == true) {
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

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
