package control.admin;

import modelo.Producto;
import control.Tools;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import persistencia.PersistenceInterface;

/**
 * @author Juan Díez-Yanguas Barber
 */
@MultipartConfig
public class AddProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(404);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PersistenceInterface persistencia = (PersistenceInterface) request.getServletContext().getAttribute("persistence");
        if (validateForm(request)) {
            try {
                String nombre = Tools.validateProdText(Tools.getcontentPartText(request.getPart("name")), 70, "Nombre");
                double precio = Tools.validatePrice(Tools.getcontentPartText(request.getPart("price")));
                int nStock = Tools.validateNumber(Tools.getcontentPartText(request.getPart("stock")), "Unidades disponibles");
                String descripcion = Tools.validateProdText(Tools.getcontentPartText(request.getPart("desc")), 100, "Descripción");
                String detalles = Tools.getContentTextArea(request.getPart("detail"));
                Tools.validateHTML(detalles);
                String codigo = Tools.generaUUID();
                
                ////----Guardar Imagen si hay, si hay error guardando se aborta y notifica
                if (request.getPart("foto").getSize() > 0 && !Tools.recuperarYGuardarImagenFormulario(request, response, codigo)) {
                    return;
                }
                ///-----Fin tratado de imagen

                Producto prod = new Producto(codigo, nombre, precio, nStock, descripcion, detalles);
                boolean ok = persistencia.addProduct(prod);
                request.setAttribute("resultados", "Resultados de la operación");
                if (ok) {
                    Tools.anadirMensaje(request, "El producto se ha añadido correctamente");
                } else {
                    Tools.anadirMensaje(request, "Ha ocurrido un error al añadir el producto. El producto está duplicado. Inténtelo de nuevo");
                }
                request.getRequestDispatcher("/admin/administration/products_administration.jsp").forward(request, response);
            } catch (IntrusionException ex) {
                request.setAttribute("resultados", "Intrusión detectada");
                Tools.anadirMensaje(request, ex.getUserMessage());
                request.getRequestDispatcher("/admin/administration/addproduct.jsp").forward(request, response);
            } catch (ValidationException ex) {
                request.setAttribute("resultados", "Datos de formulario no válidos");
                Tools.anadirMensaje(request, ex.getUserMessage());
                request.getRequestDispatcher("/admin/administration/addproduct.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("resultados", "Formulario no válido");
            Tools.anadirMensaje(request, "El formulario recibido no tiene los campos esperados");
            request.getRequestDispatcher("/admin/addproduct.jsp").forward(request, response);
        }
    }

    protected boolean validateForm(HttpServletRequest request) throws IOException, ServletException {
        if (request.getParts().size() >= 6 && request.getPart("name") != null && request.getPart("price") != null
                && request.getPart("stock") != null && request.getPart("desc") != null
                && request.getPart("detail") != null && request.getPart("sendProd") != null) {
            return true;
        }
        return false;
    }

    @Override
    public String getServletInfo() {
        return "Servlet para añadir productos al catálogo";
    }
}

