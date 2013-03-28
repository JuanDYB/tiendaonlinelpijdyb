package control.productos;

import control.Tools;
import java.io.IOException;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import modelo.Producto;
import persistencia.PersistenceInterface;

/**
 * @author Juan Díez-Yanguas Barber
 */
public class SearchProductServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        PersistenceInterface persistence = (PersistenceInterface) request.getServletContext().getAttribute("persistence");
        if (validateForm (request)){            
            String redirect = request.getParameter("redirect");
            if (redirect.equals("/shop/products.jsp") == false && redirect.equals("/admin/administration/products_administration.jsp") == false){
                response.sendError(404);
                return;
            }
            String term = request.getParameter("term");
            String destination = devolverCampo(request.getParameter("campo"));
            Map <String, Producto> resultados = persistence.searchProd(destination, term);
            request.setAttribute("resultados", "Resultados de la búsqueda");
            if (resultados == null || resultados.size() <= 0){
                Tools.anadirMensaje(request, "No se han encontrado coincidencias. Se mostrarán todos los productos");
            }else{
                Tools.anadirMensaje(request, "Se han encontrado " + resultados.size() + " coincidencias");
            }
            request.setAttribute("resultadosBusqueda", resultados);
            RequestDispatcher mostrar = request.getRequestDispatcher(redirect);
            mostrar.forward(request, response);
        }
    }

    private boolean validateForm (HttpServletRequest request){
        if (request.getParameterMap().size() >= 4 && request.getParameter("term") != null 
                && request.getParameter("campo") != null && request.getParameter("search") != null 
                && request.getParameter("redirect") != null){
            if (request.getParameter("campo").equals("name") || request.getParameter("campo").equals("desc")
                    || request.getParameter("campo").equals("detail")){
                return true;
            }
        }
        return false;
    }
    
    private String devolverCampo (String form){
        if (form.equals("name")){
            return "Nombre";
        }else if (form.equals("desc")){
            return "Descripcion";
        }else if (form.equals("detail")){
            return "Detalles";
        }else{
            return "Nombre";
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
        return "Servlet encargado de la búsqueda de productos dentro de la lista";
    }
}