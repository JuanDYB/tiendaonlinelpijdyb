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
public class EditUserCompleteServlet extends HttpServlet {
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (validateForm(request) == true) {
            try {
                PersistenceInterface persistencia = (PersistenceInterface)
                        request.getServletContext().getAttribute("persistence");
                String mail = Tools.validateEmail(request.getParameter("mail"));
                String nombre = Tools.validateName(request.getParameter("nombre"));
                String dir = Tools.validateAdress(request.getParameter("dir"));
                char perm = request.getParameter("perm").charAt(0);
                Usuario user = persistencia.getUser(mail);
                if (user == null) {
                    request.setAttribute("resultados", "Usuario no encontrado");
                    Tools.anadirMensaje(request, "El usuario que quiere editar no ha sido encontrado");
                } else {
                    Usuario updateUser = new Usuario(nombre, dir, user.getMail(), user.getPass(), perm);
                    if (persistencia.anyAdmin() == 1 && user.getPermisos() == 'a' &&
                            updateUser.getPermisos() == 'c') {
                        request.setAttribute("resultados", "Error editando permisos");
                        Tools.anadirMensaje(request, "Este usuario es el último administrador, no puede cambiar sus permisos");
                    } else {
                        boolean ok = persistencia.updateUser(user.getMail(), updateUser);
                        if (ok == true) {
                            request.setAttribute("resultados", "Usuario editado correctamente");
                            Tools.anadirMensaje(request, "El usuario ha sido editado correctamente");
                        } else {
                            request.setAttribute("resultados", "Operación fallida");
                            Tools.anadirMensaje(request, "Ocurrió un error editando el usuario, es posible que el usuario que desea editar no exista");
                        }
                    }
                }
            } catch (IntrusionException ex) {
                request.setAttribute("resultados", "Intrusión detectada");
                Tools.anadirMensaje(request, ex.getUserMessage());
            } catch (ValidationException ex) {
                request.setAttribute("resultados", "Validación de formulario fallida");
                Tools.anadirMensaje(request, ex.getUserMessage());
            } finally {
                request.getRequestDispatcher("/admin/administration/user_administration.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("resultados", "Formulario incorrecto");
            Tools.anadirMensaje(request, "El formulario recibido no es correcto");
            request.getRequestDispatcher("/admin/administration/user_administration.jsp").forward(request, response);
        }
    }

    protected boolean validateForm(HttpServletRequest request) {
        if (request.getParameterMap().size() >= 5 && request.getParameter("nombre") != null && 
                request.getParameter("dir") != null && request.getParameter("perm") != null &&
                request.getParameter("edit") != null && request.getParameter("mail") != null &&
                Tools.validatePermisos(request.getParameter("perm").charAt(0))) {
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
        return "Servlet para la edición de clientes (usado por el administrador)";
    }
}
