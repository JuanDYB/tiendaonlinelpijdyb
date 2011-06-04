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
import org.apache.commons.lang.RandomStringUtils;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import persistencia.PersistenceInterface;

/**
 *
 * @author Juan Díez-Yanguas Barber
 */
public class PassRecoverServlet extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (validateParam(request) == true) {
            try {
                String email = Tools.validateEmail(request.getParameter("email"));

                PersistenceInterface persistencia = (PersistenceInterface) request.getServletContext().getAttribute("persistence");
                Usuario user = persistencia.getUser(email);

                if (user == null) {
                    request.setAttribute("resultados", "Usuario no encontrado");
                    Tools.anadirMensaje(request, "Ha intentado recuperar la contraseña de un usuario que no existe");
                } else {
                    SendMail mailConfig = (SendMail) request.getServletContext().getAttribute("EmailSend");
                    //Generamos contraseña y su hash
                    String newPass = RandomStringUtils.randomAlphanumeric(19);
                    String newPassHash = Tools.MD5Signature(newPass + newPass.toLowerCase());
                    Usuario newUser = new Usuario(user.getNombre(), user.getDir(), user.getMail(), newPassHash, user.getPermisos());
                    
                    boolean cambioPass = persistencia.updateUser(user.getMail(), newUser);
                    if (cambioPass == true){
                        boolean mail = EnviarMail(request, user, newPass);
                        if (mail == false){
                            request.setAttribute("resultados", "Ocurrió un error");
                            Tools.anadirMensaje(request, "Se intentó enviar su contraseña por email pero no fue posible, inténtelo de nuevo, disculpe las molestias");
                        }
                    }else{
                        request.setAttribute("resultados", "Ha ocurrido un error");
                        Tools.anadirMensaje(request, "Error en el proceso de recuperación de la contraseña, inténtelo de nuevo");
                    }
                }

            } catch (IntrusionException ex) {
                request.setAttribute("resultados", "Intrusión detectada");
                Tools.anadirMensaje(request, ex.getUserMessage());
            } catch (ValidationException ex) {
                request.setAttribute("resultados", "Error en la validación");
                Tools.anadirMensaje(request, ex.getUserMessage());
            } finally {
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }

        } else {
            request.setAttribute("resultados", "Formulario no valido");
            Tools.anadirMensaje(request, "El formulario recibido no es correcto");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    protected boolean EnviarMail(HttpServletRequest request, Usuario user, String newPass) {
        SendMail mailConfig = (SendMail) request.getServletContext().getAttribute("EmailSend");
        Session mailSession = mailConfig.startSession((Authenticator) request.getServletContext().getAttribute("autorizacionMail"));

        String contenido = Tools.leerArchivoClassPath("/plantillaRecuperarPass.html");
        contenido = contenido.replace("&NAME", user.getNombre());
        contenido = contenido.replace("&EMAIL", user.getMail());
        contenido = contenido.replace("&PASS", newPass);

        MimeMessage mensaje = mailConfig.newMail("Recuperación de contraseña tienda online", user.getMail(), contenido, mailSession);
        if (mensaje == null) {
            request.setAttribute("resultados", "Error enviando mensaje");
            Tools.anadirMensaje(request, "No se pudo enviar su email, disculpe las molestias");
            return false;
        } else {
            boolean ok = mailConfig.sendEmail(mensaje, mailSession);

            if (ok == true) {
                request.setAttribute("resultados", "Mensaje enviado correctamente");
                Tools.anadirMensaje(request, "Se ha enviado a su dirección de correo una nueva contraseña");
                return true;
            } else {
                request.setAttribute("resultados", "Error enviando mensaje");
                Tools.anadirMensaje(request, "No se pudo enviar su email, disculpe las molestias");
                return false;
            }
        }

    }

    protected boolean validateParam(HttpServletRequest request) {
        if (request.getParameterMap().size() >= 1 && request.getParameter("email") != null) {
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
        processRequest(request, response);
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
