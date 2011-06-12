package control.admin;

import modelo.Usuario;
import control.Tools;
import java.io.IOException;
import java.util.Map;
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
public class ChangePassServlet extends HttpServlet {
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (validateForm(request) == true) {
            try {
                PersistenceInterface persistencia = (PersistenceInterface)
                        request.getServletContext().getAttribute("persistence");
                String prevPass = Tools.validatePass(request.getParameter("prevPass"));
                prevPass = Tools.generateMD5Signature(prevPass + prevPass.toLowerCase());
                String newPass = Tools.validatePass(request.getParameter("newPass"));
                String repeatPass = Tools.validatePass(request.getParameter("repeatPass"));
                Usuario user = persistencia.getUser((String)request.getSession().getAttribute("usuario"));
                if (user != null) {
                    if (prevPass.equals(user.getPass()) == false) {
                        Tools.anadirMensaje(request, "La contraseña que ha introducido no es correcta");
                    } else if (newPass.equals(repeatPass) == false) {
                        Tools.anadirMensaje(request, "La contraseña no coincide con la repetición");
                    } else {
                        String huellaPass = Tools.generateMD5Signature(newPass + newPass.toLowerCase());
                        Usuario newUser = new Usuario (user.getNombre(), user.getDir(), user.getMail(), huellaPass, user.getPermisos());
                        boolean ok = persistencia.updateUser(user.getMail(), newUser);
                        if (ok == true){
                            Tools.anadirMensaje(request, "La contraseña ha sido cambiada con éxito");
                        }else{
                            Tools.anadirMensaje(request, "Ha ocurrido un error cambiando la contraseña");
                        }
                    }
                } else {
                    request.setAttribute("errorSesion", "");
                    request.setAttribute("resultados", "No se encontro el usuario de la sesion");
                    Tools.anadirMensaje(request, "No se ha encontrado el usuario activo y se ha cerrado la sesion");
                    request.getRequestDispatcher("/logout").forward(request, response);
                }
            } catch (IntrusionException ex) {
                request.setAttribute("resultados", "Intrusión detectada");
                Tools.anadirMensaje(request, ex.getUserMessage());
                request.getRequestDispatcher("/admin/preferences.jsp").forward(request, response);
            } catch (ValidationException ex) {
                request.setAttribute("resultados", "Datos de formulario inválidos");
                Tools.anadirMensaje(request, ex.getUserMessage());
                request.getRequestDispatcher("/admin/preferences.jsp").forward(request, response);
            }
        }
        request.setAttribute("resultados", "Resultados de la operación");
        request.getRequestDispatcher("/admin/preferences.jsp").forward(request, response);
    }

    protected boolean validateForm(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters.size() == 4 && parameters.containsKey("prevPass")
                && parameters.containsKey("newPass") && parameters.containsKey("repeatPass") && parameters.containsKey("changePass")) {
            return true;
        } else {
            Tools.anadirMensaje(request, "El formulario enviado no tiene la forma correta");
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
        return "Servlet para cambiar contraseña del cliente (usado por los clientes)";
    }
}
