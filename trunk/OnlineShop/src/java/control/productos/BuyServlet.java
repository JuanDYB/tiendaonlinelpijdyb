package control.productos;

import modelo.Carrito;
import modelo.Producto;
import control.SendMail;
import control.Tools;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
public class BuyServlet extends HttpServlet {

    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (validateForm(request) == true) {
            try {
                String name = Tools.validateName(request.getParameter("name"));
                String dir = Tools.validateAdress(request.getParameter("dir"));
                String email = Tools.validateEmail(request.getParameter("email"));

                Carrito carro = (Carrito) request.getSession().getAttribute("carrito");
                if (carro != null) {
                    PersistenceInterface persistencia = (PersistenceInterface)
                            request.getServletContext().getAttribute("persistence");
                     StringBuilder tablaEmail = new StringBuilder();
                    Map<Producto, Integer> listado = new HashMap<Producto, Integer>();
                    boolean terminarCompra = persistencia.updateProductIfAvailable(carro.getArticulos(),
                            request, listado);
                    if (terminarCompra) {
                        Iterator<Producto> iterador = listado.keySet().iterator();
                        while (iterador.hasNext()) {
                            Producto prod = iterador.next();
                            tablaEmail.append(generarFilaTabla(prod, listado.get(prod)));
                        }
                        String formPago = formaPago(request);
                        request.setAttribute("formPago", formPago);
                        request.setAttribute("resultados", "Información adicional");
                        if (persistenciaCompra(persistencia, email, carro, formPago) == false) {
                            Tools.anadirMensaje(request, "La compra se ha realizado correctamente pero no se ha guardado el registro de la misma");
                        } else {
                            Tools.anadirMensaje(request, "Se ha guardado un registro de la compra, puede verlo en el panel de usuario");
                        }                        
                        boolean ok = enviarMail(request, carro, tablaEmail, name, dir, email, formPago);                        
                        if (ok){
                            Tools.anadirMensaje(request, "Se le ha enviado un email con los detalles de su compra");
                        }else{
                            Tools.anadirMensaje(request, "Ocurrio un error al enviar un email con los detalles de la compra");
                        }
                        request.getRequestDispatcher("/shop/buyinformation.jsp").forward(request, response);
                    } else {
                        request.setAttribute("resultados", "Compra no realizada");
                        request.getRequestDispatcher("/shop/buycart.jsp").forward(request, response);
                    }
                } else {
                    request.setAttribute("resultados", "Carrito no encontrado");
                    Tools.anadirMensaje(request, "No se ha encontrado un carrito en la sesión, es posible que se haya cerrado la sesión");
                    request.getRequestDispatcher("/shop/products.jsp").forward(request, response);
                }

            } catch (IntrusionException ex) {
                request.setAttribute("resultados", "Intrusión detectada");
                Tools.anadirMensaje(request, ex.getUserMessage());
                request.getRequestDispatcher("/shop/buycart.jsp").forward(request, response);
            } catch (ValidationException ex) {
                request.setAttribute("resultados", "Datos de formulario no válidos");
                Tools.anadirMensaje(request, ex.getUserMessage());
                request.getRequestDispatcher("/shop/buycart.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("resultados", "Formulario incorrecto");
            Tools.anadirMensaje(request, "El formulario no tiene los datos esperados");
            request.getRequestDispatcher("/shop/buycart.jsp").forward(request, response);
        }
    }

    protected boolean enviarMail(HttpServletRequest request, Carrito carro, StringBuilder filas, String name,
            String dir, String email, String formPago) {
        SendMail mailConfig = (SendMail) request.getServletContext().getAttribute("EmailSend");
        Session mailSession = mailConfig.startSession((Authenticator)
                request.getServletContext().getAttribute("autorizacionMail"));

        String contenido = Tools.leerArchivoClassPath("/plantillaCompra.html");
        contenido = contenido.replace("&LISTA", generateTableProductos(filas));
        contenido = contenido.replace("&TOTAL", Tools.roundDouble(carro.getPrecio()) + " &euro;");
        contenido = contenido.replace("&NAME", name);
        contenido = contenido.replace("&EMAIL", email);
        contenido = contenido.replace("&DIR", dir);
        contenido = contenido.replace("&FORMPAGO", formPago);

        MimeMessage mensaje = mailConfig.newMail("Compra realizada con éxito", email, contenido, mailSession);
        if (mensaje == null) {
            Tools.anadirMensaje(request, "No se pudo enviar su email, disculpe las molestias");
            return false;
        } else {
            return mailConfig.sendEmail(mensaje, mailSession);
        }
    }

    protected String generateTableProductos(StringBuilder filas) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table border=\"0\" align=\"center\" width=\"90%\">");
        sb.append("\n");
        sb.append("<tr class=\"headerTable\"><td>Producto</td><td>Unidades</td><td>Precio Unidad</td><td>Precio</td></tr>");
        sb.append("\n");
        sb.append("<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>");
        sb.append("\n");
        sb.append(filas);
        sb.append("\n");
        sb.append("</table>");
        return sb.toString();
    }

    protected StringBuilder generarFilaTabla(Producto prod, int unidades) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr class=\"contentTable\">");
        sb.append("\n");

        sb.append("<td>");
        sb.append(prod.getNombre());
        sb.append("</td>");

        sb.append("<td>");
        sb.append(unidades);
        sb.append("</td>");

        sb.append("<td>");
        sb.append(Tools.roundDouble(prod.getPrecio()));
        sb.append(" &euro;</td>");

        sb.append("<td>");
        sb.append(Tools.roundDouble(prod.getPrecio() * unidades));
        sb.append(" &euro;</td>");

        sb.append("</tr>");
        sb.append("\n");

        return sb;

    }

    protected String formaPago(HttpServletRequest request) {
        Map<String, String[]> parametros = request.getParameterMap();
        if (parametros.containsKey("creditCard.x") && parametros.containsKey("creditCard.y")) {
            return "Tarjeta de crédito";
        } else if (parametros.containsKey("reembolso.x") && parametros.containsKey("reembolso.y")) {
            return "Pago contrareembolso";
        } else if (parametros.containsKey("transfer.x") && parametros.containsKey("transfer.y")) {
            return "Transferencia bancaria";
        } else {
            return null;
        }
    }

    protected boolean persistenciaCompra(PersistenceInterface persistencia, String client,
            Carrito cart, String formPago) {
        boolean ok = persistencia.deleteImcompleteCartsClient(client);
        if (ok == false) {
            return false;
        } else {
            cart.setUser(client);
            return persistencia.saveCart(cart, true, Tools.getDate(), formPago);
        }
    }

    protected boolean validateForm(HttpServletRequest request) {
        if (request.getParameterMap().size() >= 6 && request.getParameter("name") != null &&
                request.getParameter("dir") != null && request.getParameter("email") != null &&
                request.getParameter("buy") != null && ((request.getParameter("creditCard.x") != null &&
                request.getParameter("creditCard.y") != null) || (request.getParameter("reembolso.x") != null &&
                request.getParameter("reembolso.y") != null)  || (request.getParameter("transfer.x") != null &&
                request.getParameter("transfer.y") != null))) {
            return true;
        } else {
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
        return "Servlet que realiza la compra definitiva de un producto";
    }
}
