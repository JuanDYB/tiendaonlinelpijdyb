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
public class EditUserServlet extends HttpServlet {
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (validateForm(request)) {
            try {
                PersistenceInterface persistencia = (PersistenceInterface)
                        request.getServletContext().getAttribute("persistence");
                Usuario user = persistencia.getUser((String) request.getSession().getAttribute("usuario"));
                String nombre = Tools.validateName(request.getParameter("name"));
                String dir = Tools.validateAdress(request.getParameter("dir"));                
                Usuario newUser = new Usuario (nombre, dir, user.getMail(), user.getPass(), user.getPermisos());
                boolean ok = persistencia.updateUser(user.getMail(), newUser);
                if (ok){
                    request.setAttribute("resultados", "Resultados de la operación");
                    Tools.anadirMensaje(request, "Los datos han sido modificados correctamente");
                }else{
                    request.setAttribute("resultados", "Resultados de la operación");
                    Tools.anadirMensaje(request, "Ha ocurrido un error modificando el usuario");
                }

            } catch (IntrusionException ex) {
                request.setAttribute("resultados", "Intrusión detectada");
                Tools.anadirMensaje(request, ex.getUserMessage());
            } catch (ValidationException ex) {
                request.setAttribute("resultados", "Datos de formulario inválidos");
                Tools.anadirMensaje(request, ex.getUserMessage());
            }finally{
                request.getRequestDispatcher("/admin/preferences.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("resultados", "Formulario incorrecto");
            Tools.anadirMensaje(request, "El formulario enviado no es correcto");
            request.getRequestDispatcher("/admin/preferences.jsp").forward(request, response);
        }
    }

    protected boolean validateForm(HttpServletRequest request) {
        if (request.getParameterMap().size() >= 3 && request.getParameter("name") != null && 
                request.getParameter("dir") != null  && request.getParameter("changeData") != null) {
            return true;
        } else {
            return false;
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
    
    @Override
    public String getServletInfo (){
        return "Servlet para edición de los datos de un cliente (usado por los clientes)";
    }
}
