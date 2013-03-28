package control;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelo.Carrito;
import modelo.Usuario;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import persistencia.PersistenceInterface;

/**
 * @author Juan Díez-Yanguas Barber
 */
public class AuthServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (validateForm(request) == false) {
            request.setAttribute("resultados", "Error iniciando sesion");
            Tools.anadirMensaje(request, "El formulario enviado no es correcto");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else if (request.getSession().getAttribute("intentosLogin") != null && (Integer) request.getSession().getAttribute("intentosLogin") >= 5) {
            request.setAttribute("resultados", "Innicio de sesión bloqueado");
            Tools.anadirMensaje(request, "Se han superado el número de intentos de inicio de sesión, se ha bloqueado el incio de sesión");
            Tools.anadirMensaje(request, "Deberá esperar unos minutos para volver a intentarlo");
            this.starTimer(request.getSession());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else {
            try {
                String email = Tools.validateEmail(request.getParameter("email"));
                String password = Tools.validatePass(request.getParameter("pass"));

                PersistenceInterface persistence = (PersistenceInterface) request.getServletContext().getAttribute("persistence");

                Usuario user = persistence.getUser(email);
                if (user != null) {
                    //Inicio correcto
                    if (Tools.generateMD5Signature(password
                            + password.toLowerCase()).equals(user.getPass()) == true) {
                        request.getSession().setAttribute("auth", true);
                        request.getSession().setAttribute("usuario", user.getMail());

                        Carrito carro = persistence.requestLastIncompleteCart(user.getMail());
                        if (carro != null) {
                            request.getSession().setAttribute("carrito", carro);
                        }
                        if (request.getSession().getAttribute("requestedPage") != null) {
                            String redirect = (String) request.getSession().getAttribute("requestedPage");
                            request.getSession().removeAttribute("requestedPage");
                            response.sendRedirect(redirect);
                        } else {
                            response.sendRedirect("/index.jsp");
                        }
                        request.getSession().removeAttribute("intentosLogin");
                        return;
                    //Contraseña incorrecta
                    } else {
                        Tools.anadirMensaje(request, "La contraseña introducida es incorrecta");
                        Tools.anadirMensaje(request, "Haga click <a href=\"/recoverpass?email="
                                + user.getMail() + "\" >aquí</a> si olvidó la contraseña y desea recuperarla");
                    }
                } else {
                    Tools.anadirMensaje(request, "No se ha encontrado ningún usuario con los datos especificados");
                }
                this.incrementarIntentos(request.getSession());
                request.setAttribute("resultados", "Error inciando sesion");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            } catch (IntrusionException ex) {
                request.setAttribute("resultados", "Intrusión detectada");
                Tools.anadirMensaje(request, ex.getUserMessage());
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            } catch (ValidationException ex) {
                request.setAttribute("resultados", "Datos de formulario no válidos");
                Tools.anadirMensaje(request, ex.getUserMessage());
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        }
    }

    protected boolean validateForm(HttpServletRequest request) {
        Map<String, String[]> param = request.getParameterMap();
        if (param.size() == 3 && param.containsKey("email") && param.containsKey("pass")
                && param.containsKey("login")) {
            return true;
        } else {
            Tools.anadirMensaje(request, "El formulario enviado no tiene el formato correcto");
            return false;
        }
    }

    protected void starTimer(final HttpSession sesion) {
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                sesion.invalidate();
            }
        };

        Timer timer = new Timer();
        //10 minutos ---> 600.000 milisegundos
        timer.schedule(timerTask, 600000);
    }

    protected void incrementarIntentos(HttpSession sesion) {
        if (sesion.getAttribute("intentosLogin") == null) {
            sesion.setAttribute("intentosLogin", 1);
        } else {
            sesion.setAttribute("intentosLogin", (Integer) sesion.getAttribute("intentosLogin") + 1);
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
    public String getServletInfo() {
        return "Servlet para la autentificación de usuarios";
    }
}
