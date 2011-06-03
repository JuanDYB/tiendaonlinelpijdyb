package admin;

import beans.Usuario;
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
 *
 * @author Juan Díez-Yanguas Barber
 */
public class EditUserServlet extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (validateForm(request) == true) {
            try {
                PersistenceInterface persistencia = (PersistenceInterface) request.getServletContext().getAttribute("persistence");
                Usuario user = persistencia.getUser((String) request.getSession().getAttribute("usuario"));
                String nombre = Tools.validateName(request.getParameter("name"));
                String dir = Tools.validateAdress(request.getParameter("dir"));
                
                Usuario newUser = new Usuario (nombre, dir, user.getMail(), user.getPass(), user.getPermisos());
                boolean ok = persistencia.updateUser(user.getMail(), newUser);
                if (ok == true){
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
        if (request.getParameterMap().size() >= 3 && request.getParameter("name") != null && request.getParameter("dir") != null
                && request.getParameter("changeData") != null) {
            return true;
        } else {
            return false;
        }
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
        response.sendError(404);
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
