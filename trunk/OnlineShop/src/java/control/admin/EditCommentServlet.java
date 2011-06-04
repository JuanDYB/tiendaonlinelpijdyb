package control.admin;

import modelo.Comentario;
import control.Tools;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.errors.IntrusionException;
import persistencia.PersistenceInterface;

/**
 *
 * @author Juan Díez-Yanguas Barber
 */
public class EditCommentServlet extends HttpServlet {

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
        
        String backEdit = (String)request.getSession().getAttribute("backTOEditComment");
        request.getSession().removeAttribute("backTOEditComment");
        
        if (validateForm(request) == false){
            request.setAttribute("resultados", "Formulario incorrecto");
            Tools.anadirMensaje(request, "El formulario que ha enviado no es correcto");
            request.getRequestDispatcher("/admin/administration/editcomment.jsp?" + backEdit).forward(request, response);
            return;
        }
        try{
            String codComentario = request.getParameter("codComentario");
            String comentario = request.getParameter("comentario");
            Tools.validateHTML(comentario);
            
            PersistenceInterface persistencia = (PersistenceInterface) request.getServletContext().getAttribute("persistence");
            Comentario comentarioActual = persistencia.getComment(codComentario);
            Calendar cal = Calendar.getInstance(new Locale ("es", "ES"));
            String fecha = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
            String hora = cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
            Comentario newComment = new Comentario (codComentario, fecha, hora, comentarioActual.getCodigoProducto(), comentarioActual.getEmail(), comentarioActual.getNombre(), comentario);
            
            boolean ok = persistencia.updateComment(codComentario, newComment);
            if (ok == true){
                request.setAttribute("resultados", "Operación completada");
                Tools.anadirMensaje(request, "El comentario se ha modificado con éxito");
                request.getRequestDispatcher("/shop/viewprod.jsp?" + back).forward(request, response);
            }else{
                request.setAttribute("resultados", "Operación fallida");
                Tools.anadirMensaje(request, "Ha ocurrido un error editando el comentario");
                request.getRequestDispatcher("/shop/viewprod.jsp?" + back).forward(request, response);
            }
        }catch (IntrusionException ex){
            request.setAttribute("resultados", "Validación de HTML fallida");
            Tools.anadirMensaje(request, ex.getUserMessage());
            request.getRequestDispatcher("/admin/administration/editcomment.jsp?" + backEdit).forward(request, response);
        }
        
    }
    
    private boolean validateForm (HttpServletRequest request)
    {
        if (request.getParameterMap().size() >= 3 && request.getParameter("codComentario") != null && request.getParameter("comentario") != null
                && request.getParameter("editComment") != null){
            return true;
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
//        processRequest(request, response);
        response.sendError(404);
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
