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
 *
 * @author Juan Díez-Yanguas Barber
 */
@MultipartConfig
public class AddProductServlet extends HttpServlet {

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getSession().getAttribute("productoEnCursoAdd") == null) {
            //Si he intentado entrar en la pagina directamente, la pagina no existe
            response.sendError(404);
        } else {
            Producto prod = (Producto) request.getSession().getAttribute("productoEnCursoAdd");
            PersistenceInterface persistencia = (PersistenceInterface) request.getServletContext().getAttribute("persistence");
            boolean ok = persistencia.addProduct(prod);
            request.getSession().removeAttribute("productoEnCursoAdd");

            request.setAttribute("resultados", "Resultados de la operación");
            if (ok == true) {
                Tools.anadirMensaje(request, "El producto se ha añadido correctamente");
            } else {
                Tools.anadirMensaje(request, "Ha ocurrido un error al añadir el producto. El producto está duplicado. Inténtelo de nuevo");
            }
            RequestDispatcher correcto = request.getRequestDispatcher("/admin/administration/products_administration.jsp");
            correcto.forward(request, response);
        }
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (validateForm(request) == true) {
            try {
                String nombre = Tools.validateProdText(Tools.getcontentPartText(request.getPart("name")), 70, "Nombre");
                double precio = Tools.validatePrice(Tools.getcontentPartText(request.getPart("price")));
                int nStock = Tools.validateNumber(Tools.getcontentPartText(request.getPart("stock")), "Unidades disponibles");
                String descripcion = Tools.validateProdText(Tools.getcontentPartText(request.getPart("desc")), 100, "Descripción");
                String detalles = Tools.getContentTextArea(request.getPart("detail"));
//                String nombre = Tools.validateProdText(request.getParameter("name"), 70, "Nombre");
//                double precio = Tools.validatePrice(request.getParameter("price"));
//                int nStock = Tools.validateNumber(request.getParameter("stock"), "Unidades disponibles");
//                String descripcion = Tools.validateProdText(request.getParameter("desc"), 100, "Descripción");
//                String detalles = Tools.saltosTextArea(request.getParameter("detail"));
                
                Tools.validateHTML(detalles);
                
                String codigo = null;
                if (request.getSession().getAttribute("productoEnCursoAdd") == null){
                    codigo = Tools.genUUID();
                }else{
                    codigo = ((Producto) request.getSession().getAttribute("productoEnCursoAdd")).getCodigo();
                }
                
                if (request.getPart("foto").getSize() < 0) {
                    if (request.getPart("foto").getContentType().contains("image") == false || request.getPart("foto").getSize() > 8388608) {
                        request.setAttribute("resultados", "Archivo no válido");
                        Tools.anadirMensaje(request, "Solo se admiten archivos de tipo imagen");
                        Tools.anadirMensaje(request, "El tamaño máximo de archivo son 8 Mb");
                        request.getRequestDispatcher("/admin/administration/addproduct.jsp").forward(request, response);
                        return;
                    }else{
                        String fileName = this.getServletContext().getRealPath("/images/products/" + codigo);
                        boolean ok = Tools.guardarFoto(request.getPart("foto").getInputStream(), fileName);
                        if (ok == false){
                            request.setAttribute("resultados", "Fallo al guardar archivo");
                            Tools.anadirMensaje(request, "Ocurrio un error guardando la imagen");
                            request.getRequestDispatcher("/admin/administration/addproduct.jsp").forward(request, response);
                            return;
                        }
                    }
                }


                Producto prod = new Producto(codigo, nombre, precio, nStock, descripcion, detalles);

                request.getSession().setAttribute("productoEnCursoAdd", prod);

                request.setAttribute("operation", "add");
                RequestDispatcher previsualizacion = request.getRequestDispatcher("/WEB-INF/admin/preview_prod.jsp");
                previsualizacion.forward(request, response);
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

//    private boolean validateForm(HttpServletRequest request) {
//        Map<String, String[]> param = request.getParameterMap();
//        if (param.size() == 6 && param.containsKey("name")
//                && param.containsKey("price") && param.containsKey("stock") && param.containsKey("desc")
//                && param.containsKey("detail") && param.containsKey("sendProd")) {
//
//            return true;
//        } else {
//            return false;
//        }
//    }
    private boolean validateForm(HttpServletRequest request) throws IOException, ServletException {
        if (request.getParts().size() >= 6 && request.getPart("name") != null && request.getPart("price") != null && request.getPart("stock") != null
                && request.getPart("desc") != null && request.getPart("detail") != null && request.getPart("sendProd") != null) {
            return true;
        }
        return false;
    }

    
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
