package control.admin;

import modelo.Comentario;
import control.Tools;
import java.io.IOException;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.errors.IntrusionException;
import persistencia.PersistenceInterface;

/**
 * @author Juan Díez-Yanguas Barber
 */
public class EditCommentServlet extends HttpServlet {

    
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
            Calendar cal = Calendar.getInstance(Tools.getLocale());
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
    
    private boolean validateForm (HttpServletRequest request){
        if (request.getParameterMap().size() >= 3 && request.getParameter("codComentario") != null && request.getParameter("comentario") != null
                && request.getParameter("editComment") != null){
            return true;
        }
        return false;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {        
        response.sendError(404);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    public String getServletInfo (){
        return "Servlet que permite a los administradores editar comentarios";
    }
}
