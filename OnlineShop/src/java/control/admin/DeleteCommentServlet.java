package control.admin;

import control.Tools;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import persistencia.PersistenceInterface;

/**
 *
 * @author Juan Díez-Yanguas Barber
 */
public class DeleteCommentServlet extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String back = (String) request.getSession().getAttribute("backTOURL");
        request.getSession().removeAttribute("backTOURL");
        if (validateForm(request) == false){
            response.sendError(404);
            return;
        }
        String codigoComentario = request.getParameter("cod");
        PersistenceInterface persistencia = (PersistenceInterface) request.getServletContext().getAttribute("persistence");
        boolean ok = persistencia.deleteComment(codigoComentario);
        if (ok == true){
            request.setAttribute("resultados", "Operación completada");
            Tools.anadirMensaje(request, "El comentario se ha borrado correctamente");
        }else{
            request.setAttribute("resultados", "Operación fallida");
            Tools.anadirMensaje(request, "Ha ocurrido un error borrando el comentario, disculpe las molestias");
        }
        request.getRequestDispatcher("/shop/viewprod.jsp?" + back).forward(request, response); 
    }
    
    protected boolean validateForm (HttpServletRequest request){
        if (request.getParameterMap().size() >= 1 && request.getParameter("cod") != null){
            return Tools.validateUUID(request.getParameter("cod"));
        }
        return false;
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
//        processRequest(request, response);
        response.sendError(404);
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
