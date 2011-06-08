<%@page import="persistencia.PersistenceInterface"%>
<%@page import="java.util.Iterator"%>
<%@page import="modelo.Producto"%>
<%@page import="modelo.Carrito"%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Carrito</title>

<script type="text/javascript" src="/scripts/jquery-1.6.1.js"></script>
<script type="text/javascript" src="/scripts/vanadium.js"></script>
<link rel="stylesheet" type="text/css" href="/css/validacion.css" media="screen, tv, projection" />

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
        <span class="header">Carrito de la compra</span>
        <% Carrito carro = (Carrito) session.getAttribute("carrito");
        if (carro == null){%>
            No se han a&ntilde;adido productos a la cesta de la compra
        <% } else if (carro.getArticulos().size() == 0){ %>
            No se han a&ntilde;adido productos a la cesta de la compra
        <% } else { %>
        <table border ="0" align="center" width="90%">
            <tr class="headerTable">
                <td>Nombre</td><td>Precio Unidad</td><td>Precio Total</td><td>Unidades</td>
            </tr>
            <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
            <% Iterator <String> iteradorProductos = carro.getArticulos().keySet().iterator();
            PersistenceInterface persistencia = (PersistenceInterface) application.getAttribute("persistence");
            while (iteradorProductos.hasNext()){
                String cod = iteradorProductos.next();
                Producto prod = persistencia.getProduct(cod); %>
                <tr class="contentTable">
                    <td><%= prod.getNombre() %></td>
                    <td><%= Tools.roundDouble(prod.getPrecio()) %> &euro;</td>
                    <td><%= Tools.roundDouble(carro.getArticulos().get(cod) * prod.getPrecio()) %> &euro;</td>
                    <td>
                        <form name="cantidad" action="/shop/editcart" method="post">
                            <input type="text" name="cant" maxlength="6" size="3" class=":digits :required" value="<%= carro.getArticulos().get(cod) %>" />
                            <input type="hidden" name="prod" value="<%= cod %>" />
                            <input type="submit" name="setCant" value="Actualizar" />
                        </form>
                    </td>
                            <td><a href="/shop/editcart?prod=<%= cod %>&cant=0" ><img src="/images/icons/removeprodcart.png" alt="Eliminar de la cesta" title="Eliminar del carrito" /></a></td>
                </tr>
            <% } %>
        </table>
        <p>
            Precio total:<%= Tools.roundDouble(carro.getPrecio()) %> &euro;
        </p>
        <p>
            <span class="subHeader"></span>
            <table border="0" align="center" width="90%" ><tr class="contentTable" >
                    <td><a href="/shop/delcart" ><img src="/images/icons/delcart.png" alt="Vaciar carrito" title="Vaciar carrito" /></a></td>
                    <td><a href="/shop/updatecart" ><img src="/images/icons/validatecart.png" alt="Validar carrito" title="Validar carrito" /></a></td>
                    <td><a href="/shop/products.jsp"><img src="/images/icons/continueshopping.png" alt="Continuar comprando" title="Continuar comprando" /></a></td>
            </tr></table>
        </p>
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

<%! String menuInicio = ""; %>
<%! String menuProductos = "class=\"active\""; %>
<%! String menuLogin = ""; %>
<%! String menuPreferencias = ""; %>
<%! String menuAbout = ""; %>
