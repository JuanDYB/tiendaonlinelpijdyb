package control.productos;

import modelo.Producto;
import control.Tools;
import java.io.IOException;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import persistencia.PersistenceInterface;

/**
 *
 * @author Juan Díez-Yanguas Barber
 */
public class SearchProductServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        if (validateForm (request) == true){
            
            String redirect = request.getParameter("redirect");
            if (redirect.equals("/shop/products.jsp") == false && redirect.equals("/admin/administration/products_administration.jsp") == false){
                response.sendError(404);
                return;
            }

            String term = request.getParameter("term");
            String destination = devolverCampo(request.getParameter("campo"));
            Map <String, Producto> resultados = ((PersistenceInterface)request.getServletContext().getAttribute("persistence")).searchProd(destination, term);
            request.setAttribute("resultados", "Resultados de la búsqueda");
            if (resultados == null || resultados.size() <= 0){
                Tools.anadirMensaje(request, "No se han encontrado coincidencias. Se mostrarán todos los productos");
            }else{
                Tools.anadirMensaje(request, "Se han encontrado " + resultados.size() + " coincidencias");
            }
            request.setAttribute("resultadosBusqueda", resultados);
            RequestDispatcher mostrar = request.getRequestDispatcher(redirect);
            mostrar.forward(request, response);
        }
    }

    private boolean validateForm (HttpServletRequest request){
        if (request.getParameterMap().size() >= 4 && request.getParameter("term") != null 
                && request.getParameter("campo") != null && request.getParameter("search") != null 
                && request.getParameter("redirect") != null){
            if (request.getParameter("campo").equals("name") == true
                    || request.getParameter("campo").equals("desc") == true
                    || request.getParameter("campo").equals("detail") == true){
                return true;
            }else return false;
        }else return false;
    }
    
    private String devolverCampo (String form){
        if (form.equals("name") == true){
            return "Nombre";
        }else if (form.equals("desc") == true){
            return "Descripcion";
        }else if (form.equals("detail") == true){
            return "Detalles";
        }else{
            return "Nombre";
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
        response.sendError(404);
        return;
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
        processRequest(request, response);
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
