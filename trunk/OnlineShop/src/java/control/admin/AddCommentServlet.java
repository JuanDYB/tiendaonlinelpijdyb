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
 * @author Juan Díez-Yanguas Barber
 */
public class AddCommentServlet extends HttpServlet {
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String back = (String) request.getSession().getAttribute("backTOURL");
        request.getSession().removeAttribute("backTOURL");
        if (validateForm(request) == true){
            try{
                String codProd = request.getParameter("prod");
                String comentario = request.getParameter("comentario");
                Tools.validateHTML(comentario);                
                PersistenceInterface persistencia = (PersistenceInterface)
                        request.getServletContext().getAttribute("persistence");
                Usuario user = persistencia.getUser((String) request.getSession().getAttribute("usuario"));
                
                boolean ok = persistencia.newComment(user, codProd, Tools.generaUUID(), 
                        Tools.getDate(), comentario);
                if (!ok){
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
    
    protected boolean validateForm (HttpServletRequest request){
        if (request.getParameterMap().size() >= 3 && request.getParameter("comentario") != null 
                && request.getParameter("send") != null && request.getParameter("prod") != null){            
            return Tools.validateUUID(request.getParameter("prod"));
        }else{
            return false;
        }
    }
    
    @Override
    public String getServletInfo (){
        return "Servlet para añadir comentarios a un producto";
    }
}
