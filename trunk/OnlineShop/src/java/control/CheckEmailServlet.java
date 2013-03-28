package control;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class CheckEmailServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/x-json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println ("{");

        if (request.getParameter("value") != null) {
            try {
                String email = Tools.validateEmail(request.getParameter("value"));
                PersistenceInterface persistencia = (PersistenceInterface) request.getServletContext().getAttribute("persistence");
                if (persistencia.getUser(email) == null) {
                    out.println("\"success\": true,");
                    out.println("\"message\": \"Email disponible\"");
                } else {
                    out.println("\"success\": false,");
                    out.println("\"message\": \"Direcci&oacute;n de email no disponible\"");
                }

            } catch (IntrusionException ex) {
                //Intrusión detectada
                out.println("\"success\": false,");
                out.println("\"message\": \"\"");
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Intento de intrusión validación ajax de email");
            } catch (ValidationException ex) {
                //Validación ajax fallida
                out.println("\"success\": false,");
                out.println("\"message\": \"\"");
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Validación de email via ajax fallida");
            } finally {
                out.println("}");
                out.close();
            }
        } else {
            try {
                out.println("\"success\": false,");
                out.println("\"message\": \"Validaci&oacute;n ajax fallida\"");
            } finally {
                out.println("}");
                out.close();
            }
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
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet para la comprobación de email ajax";
    }
}
