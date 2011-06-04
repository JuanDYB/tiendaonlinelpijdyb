package control.admin;

import modelo.Usuario;
import control.Tools;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.errors.IntrusionException;
import persistencia.PersistenceInterface;

/**
 *
 * @author JuanDYB
 */
public class AddCommentServlet extends HttpServlet {

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
        if (validateForm(request) == true){
            try{
                String codProd = request.getParameter("prod");
                String comentario = request.getParameter("comentario");
                Tools.validateHTML(comentario);
                
                PersistenceInterface persistencia = (PersistenceInterface) request.getServletContext().getAttribute("persistence");
                Usuario user = persistencia.getUser((String) request.getSession().getAttribute("usuario"));
                
                boolean ok = persistencia.newComment(user, codProd, Tools.genUUID(), Tools.getDate(), comentario);
                
                if (ok == false){
                    request.setAttribute("resultados", "Error en la operación");
                    Tools.anadirMensaje(request, "Ha ocurrido un error en el transcurso de la operación");
                    request.getRequestDispatcher("/shop/viewprod.jsp?" + back).forward(request, response);
                }else{
                    response.sendRedirect("/shop/viewprod.jsp?" + back);
                }
                
                
            }catch (IntrusionException ex){                
                request.setAttribute("resultados", "HTML no válido");
                Tools.anadirMensaje(request, ex.getMessage());
                request.getRequestDispatcher("/shop/viewprod.jsp?" + back).forward(request, response);
            }
        }else{
            request.setAttribute("resultados", "Formulario enviado incorrecto");
            Tools.anadirMensaje(request, "El formulario que ha enviado no es correcto");
            request.getRequestDispatcher("/shop/viewprod.jsp?" + back).forward(request, response);
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
//        processRequest(request, response);
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
    
    protected boolean validateForm (HttpServletRequest request){
        if (request.getParameterMap().size() >= 3 && request.getParameter("comentario") != null 
                && request.getParameter("send") != null && request.getParameter("prod") != null){
            
            return Tools.validateUUID(request.getParameter("prod"));
        }else{
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
