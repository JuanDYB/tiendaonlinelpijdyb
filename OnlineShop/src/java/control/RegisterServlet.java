package control;

import modelo.Usuario;
import java.io.IOException;
import javax.mail.Authenticator;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
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
public class RegisterServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(404);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (validateForm(request) == true) {
            try {
                String name = Tools.validateName(request.getParameter("name"));
                String dir = Tools.validateAdress(request.getParameter("dir"));
                String email = Tools.validateEmail(request.getParameter("email"));
                String pass = Tools.validatePass(request.getParameter("pass"));
                String repeatPass = Tools.validatePass(request.getParameter("repeatPass"));

                if (pass.equals(repeatPass)) {
                    pass = Tools.generateMD5Signature(pass + pass.toLowerCase());
                    Usuario user = new Usuario(name, dir, email, pass, 'c');
                    PersistenceInterface persistencia = (PersistenceInterface)
                            request.getServletContext().getAttribute("persistence");
                    boolean ok = persistencia.addUser(user);
                    if (ok) {
                        request.setAttribute("resultados", "Usuario registrado");
                        Tools.anadirMensaje(request, "Su usuario ha sido registrado correctamente");
                        Tools.anadirMensaje(request, "Ya puede realizar las compras que desee con su usuario, dispone de un formulario de login en esta misma página");
                        Tools.anadirMensaje(request, "Puede acceder a su panel de usuario desde el menú después de iniciar sesión");
                        mandarEmail(request, user);
                    } else {
                        request.setAttribute("resultados", "Error en el registro");
                        Tools.anadirMensaje(request, "Ya hay un usuario registrado con este email");
                    }
                } else {
                    request.setAttribute("resultados", "Datos incorrectos");
                    Tools.anadirMensaje(request, "La contraseña no coincide con su repeticion");
                }

            } catch (IntrusionException ex) {
                request.setAttribute("resultados", "Detectada una intrusión");
                Tools.anadirMensaje(request, ex.getUserMessage());
            } catch (ValidationException ex) {
                request.setAttribute("resultados", "Error en el formulario");
                Tools.anadirMensaje(request, ex.getUserMessage());
            }
        } else {
            request.setAttribute("resultados", "Ocurrio un error en el registro");
            Tools.anadirMensaje(request, "El formulario enviado no es correcto");
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    protected boolean validateForm(HttpServletRequest request) {
        if (request.getParameterMap().size() >= 6 && request.getParameter("name") != null && request.getParameter("dir") != null
                && request.getParameter("email") != null && request.getParameter("pass") != null
                && request.getParameter("repeatPass") != null && request.getParameter("register") != null) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean mandarEmail(HttpServletRequest request, Usuario user) {
        SendMail mailConfig = (SendMail) request.getServletContext().getAttribute("EmailSend");
        Session mailSession = mailConfig.startSession((Authenticator) request.getServletContext().getAttribute("autorizacionMail"));
        String contenido = Tools.leerArchivoClassPath("/plantillaRegistro.html");
        contenido = contenido.replace("&NAME", user.getNombre());
        contenido = contenido.replace("&EMAIL", user.getMail());
        contenido = contenido.replace("&DIR", user.getDir());
        MimeMessage mensaje = mailConfig.newMail("Registro completado", user.getMail(), contenido, mailSession);

        if (mensaje == null) {
            request.setAttribute("resultados", "Error enviando mensaje");
            Tools.anadirMensaje(request, "No se pudo enviar su email, disculpe las molestias");
            return false;
        } else {
            boolean ok = mailConfig.sendEmail(mensaje, mailSession);

            if (ok == true) {
                Tools.anadirMensaje(request, "Se le ha enviado un email con los datos del registro");
                return true;
            } else {
                Tools.anadirMensaje(request, "No se puedo enviar el email con los datos del registro");
                return false;
            }
        }
    }
    
    @Override
    public String getServletInfo (){
        return "Servlet para el registro de usuarios (clientes)";
    }
}
