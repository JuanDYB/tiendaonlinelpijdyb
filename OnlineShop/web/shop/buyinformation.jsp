<%@page import="persistencia.PersistenceInterface"%>
<%@page import="java.util.Iterator"%>
<%@page import="modelo.Producto"%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Compra finalizada</title>

<link rel="stylesheet" type="text/css" href="/css/screen_yellow.css" media="screen, tv, projection" />
</head>

<body>
<!-- Contenedor principal-->
<div id="siteBox">

    <!--Cabecera-->
    <%@include file="/WEB-INF/include/header.jsp" %>


    <!-- Contenido de la pagina -->
    <div id="content">

  <!-- Menu Izquiero -->
  <%@include file="/WEB-INF/include/menu.jsp" %>

    <!-- Contenido de la columna derecha -->
    <div id="contentRight">
        <%@include file="/WEB-INF/include/resultados.jsp" %>

    <p>
        <span class="header">Compra realizada con &eacute;xito</span><br /><br />
    </p>
            <p>
                <span class="subHeader">Detalles de la compra</span><br />
                <ul>
                    <li><b>Nombre: </b><%= request.getParameter("name") %></li>
                    <li><b>Direcci&oacute;n: </b><%= request.getParameter("dir") %></li>
                    <li><b>Email: </b><%= request.getParameter("email") %></li>
                    <li><b>Forma de pago: </b><%= request.getAttribute("formPago") %></li>
                </ul>
            </p>
            <p>
        <span class="subHeader">Relaci&oacute;n de productos</span>
        <% Carrito carro = (Carrito) session.getAttribute("carrito");
        if (carro == null){%>
            No se han a&ntilde;adido productos a la cesta de la compra
        <% } else if (carro.getArticulos().size() == 0){ %>
            No se han a&ntilde;adido productos a la cesta de la compra
        <% } else { %>
        <p>
            <table border="0" align="center" width="90%">
            <tr class="headerTable" > <td>Producto</td> <td>Unidades</td> <td>Precio Unidad</td> <td>Precio Total</td> </tr>
            <tr> <td>&nbsp;</td> <td>&nbsp;</td> <td>&nbsp;</td> <td>&nbsp;</td> </tr>
           <% Iterator <String> iteradorCarro = carro.getArticulos().keySet().iterator();
            while (iteradorCarro.hasNext()){
                String cod = iteradorCarro.next();
                int cant = carro.getArticulos().get(cod);
                Producto prod = ((PersistenceInterface)request.getServletContext().getAttribute("persistence")).getProduct(cod); %>
                <tr class="contentTable" >
                    <td><%= prod.getNombre() %></td>
                    <td><%= cant %></td>
                    <td><%= Tools.roundDouble(prod.getPrecio()) %> &euro;</td>
                    <td><%= Tools.roundDouble(prod.getPrecio() * cant) %> &euro;</td>
                </tr>
            <% } %>
        </table>
        </p>
        <br />
        <p><b>Precio Total:</b> <%= Tools.roundDouble(carro.getPrecio()) %> &euro;</p>
        <br />

        <% } %>

    </p>

      <!-- Crea las esquinas redondeadas abajo -->
      <img src="/images/template/corner_sub_bl.gif" alt="bottom corner" class="vBottom"/>

    </div>
</div>

<!-- Pie de pagina -->
<%@include file="/WEB-INF/include/footer.jsp" %>

</div>

</body>
</html>
<% session.removeAttribute("carrito"); %>

<%! String menuInicio = ""; %>
<%! String menuProductos = "class=\"active\""; %>
<%! String menuLogin = ""; %>
<%! String menuPreferencias = ""; %>
<%! String menuAbout = ""; %>