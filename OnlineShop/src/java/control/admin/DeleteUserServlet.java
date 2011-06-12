package control.admin;

import modelo.Usuario;
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
public class DeleteUserServlet extends HttpServlet {
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (validar(request) == true) {
            try {
                String email = Tools.validateEmail(request.getParameter("user"));
                request.setAttribute("resultados", "Resultados de la operación");
                PersistenceInterface persistencia = (PersistenceInterface)
                        request.getServletContext().getAttribute("persistence");
                Usuario user = persistencia.getUser(email);
                if (user != null) {
                    if (persistencia.anyAdmin() == 1 && user.getPermisos() == 'a') {
                        Tools.anadirMensaje(request, "El usuario elegido es el único administrador que queda, no se puede borrar");                        
                    } else {
                        boolean ok = persistencia.delUser(email);
                        persistencia.deleteImcompleteCartsClient(email);
                        if (ok == true) {
                            Tools.anadirMensaje(request, "El usuario se ha borrado correctamente");
                            //Lo que he añadido para que administrador que se borra a si mismo salga
                            if (email.equals((String)request.getSession().getAttribute("usuario")) == true){
                                response.sendRedirect("/logout");
                                return;
                            }
                        }else{
                            Tools.anadirMensaje(request, "Ha ocurrido un error borrando el usuario");
                        }
                    }
                } else {
                    Tools.anadirMensaje(request, "El usuario seleccionado no se ha encontrado, imposible borrar");
                }
            } catch (IntrusionException ex) {
                Tools.anadirMensaje(request, ex.getUserMessage());
            } catch (ValidationException ex) {
                Tools.anadirMensaje(request, ex.getUserMessage());
            }finally{
                request.getRequestDispatcher("/admin/administration/user_administration.jsp").forward(request, response);
            }
        } else {
            response.sendError(404);
        }
    }

    protected boolean validar(HttpServletRequest request) {
        if (request.getParameterMap().size() >= 1 && request.getParameter("user") != null) {
            return true;
        } else {
            return false;
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
    
    @Override
    public String getServletInfo (){
        return "Servlet para el borrado de un cliente";
    }
}
