package control.admin;

import control.Tools;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import modelo.Producto;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import persistencia.PersistenceInterface;

/**
 * @author Juan Díez-Yanguas Barber
 */
@MultipartConfig
public class EditProductServlet extends HttpServlet {

    private boolean validateForm(HttpServletRequest request) throws IOException, ServletException {
        if (request.getParts().size() >= 7 && request.getPart("codigo") != null && request.getPart("name") != null
                && request.getPart("price") != null && request.getPart("stock") != null
                && request.getPart("desc") != null && request.getPart("detail") != null
                && request.getPart("sendProd") != null) {
            return Tools.validateUUID(Tools.getcontentPartText(request.getPart("codigo")));
        }
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(404);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PersistenceInterface persistencia = (PersistenceInterface) request.getServletContext().getAttribute("persistence");
        if (validateForm(request) == true) {
            try {
                String codigo = Tools.getcontentPartText(request.getPart("codigo"));
                String nombre = Tools.validateProdText(Tools.getcontentPartText(request.getPart("name")), 70, "Nombre");
                double precio = Tools.validatePrice(Tools.getcontentPartText(request.getPart("price")));
                int nStock = Tools.validateNumber(Tools.getcontentPartText(request.getPart("stock")), "Unidades disponibles");
                String descripcion = Tools.validateProdText(Tools.getcontentPartText(request.getPart("desc")), 100, "Descripción");
                String detalles = Tools.getContentTextArea(request.getPart("detail"));
                Tools.validateHTML(detalles);
                String rutaImagen = request.getServletContext().getRealPath("/images/products/" + codigo);
                ///-----Tratar imagenes
                if (request.getPart("conserv") == null && Tools.existeElFichero(rutaImagen)) {
                    Tools.borrarImagenDeProdructoDelSistemaDeFicheros(rutaImagen);
                    if (request.getPart("foto").getSize() <= 0) {
                        Tools.anadirMensaje(request, "ADVERTENCIA: Se ha decidido no conservar la imagen anterior y no se ha seleccionado otra");

                    }
                }
                if (request.getPart("foto").getSize() > 0 && !Tools.recuperarYGuardarImagenFormulario(request, response, codigo)) {
                    return;
                }
                ///-----Fin tratado de imagenes

                Producto prod = new Producto(codigo, nombre, precio, nStock, descripcion, detalles);
                boolean ok = persistencia.updateProduct(prod.getCodigo(), prod);
                request.setAttribute("resultados", "Resultados de la operación");
                if (ok == true) {
                    Tools.anadirMensaje(request, "El producto se ha editado correctamente");
                } else {
                    Tools.anadirMensaje(request, "Ha ocurrido un error al modificar el producto. El producto no ha sido encontrado");
                }
                request.getRequestDispatcher("/admin/administration/products_administration.jsp").forward(request, response);
            } catch (IntrusionException ex) {
                request.setAttribute("resultados", "Intrusión detectada");
                Tools.anadirMensaje(request, ex.getUserMessage());
                request.getRequestDispatcher("/admin/administration/products_administration.jsp").forward(request, response);
            } catch (ValidationException ex) {
                request.setAttribute("resultados", "Validación de formulario fallida");
                Tools.anadirMensaje(request, ex.getUserMessage());
                request.getRequestDispatcher("/admin/administration/products_administration.jsp").forward(request, response);
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet encargado de la edición de productos";
    }
}
