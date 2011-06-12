package control.admin;

import control.Tools;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import persistencia.PersistenceInterface;

/**
 * @author Juan Díez-Yanguas Barber
 */
public class DeleteCommentServlet extends HttpServlet {

    
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
    
    @Override
    public String getServletInfo (){
        return "Servlet para el borrado de comentarios";
    }
}
