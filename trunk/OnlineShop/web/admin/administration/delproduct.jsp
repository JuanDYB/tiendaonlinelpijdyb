<%@page import="persistencia.PersistenceInterface"%>
<%@page import="control.Tools"%>
<%@page import="modelo.Producto"%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>

<%if (validar (request, session) == false){
    request.setAttribute("error", "");
    response.sendError(404);
    return;
} %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<title>Borrar producto</title>

<link rel="stylesheet" type="text/css" href="/css/screen_yellow.css" media="screen, tv, projection" />
</head>

<body>
<!-- Contenedor principal-->
<div id="siteBox">

	<!--Cabecera-->
    <%@include file="/WEB-INF/include/header.jsp" %>


  <!-- Contenido de la pagina -->
  <div id="content">

  <!-- Menu Izquiero : side bar links/news/search/etc. -->
  <%@include file="/WEB-INF/include/menuAdministracion.jsp" %>

    <!-- Contenido de la columna derecha -->
    <div id="contentRight">
        <% PersistenceInterface persistence = (PersistenceInterface)application.getAttribute("persistence");
        Producto prod = persistence.getProduct(request.getParameter("prod"));
        if (prod != null){ %>
        <p>
            <span class="header">Borrar producto: <%= prod.getNombre() %></span>
        </p>
        <ul>
                <li><b>Precio: </b><%= Tools.roundDouble(prod.getPrecio()) %> &euro;</li><br />
                <li><b>Stock disponible: </b><%= prod.getStock() %> unidades</li><br />
                <li><b>Descripci&oacute;n: </b><%= prod.getDesc() %></li><br />
                <li><b>Detalles: </b><br /><%= prod.getDetalles() %></li>
        </ul><br /><br />
        <p>¿Esta seguro de que desea borrar el producto?</p>
        <a style="margin-left: 2em" href="/admin/administration/products_administration.jsp">No eliminar</a>
        <a style="margin-left: 15em" href="/admin/administration/delprod?prod=<%= prod.getCodigo() %>">Eliminar producto</a>
        <% } else{ %>
        <p>
        <span class="header">Producto no encontrado</span><br />
        Ha ocurrido un error en la operaci&oacute;n. El producto no ha sido encontrado<br /><br />
        <a href="/admin/administration/products_administration.jsp">Administraci&oacute;n de productos</a>
        </p>
        <% } %>

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
<%! String menuProductos = ""; %>
<%! String menuLogin = ""; %>
<%! String menuPreferencias = "class=\"active\""; %>
<%! String menuAbout = ""; %>

<%--Funcion para validar la entrada en esta jsp con los parametros necesarios--%>
<%! private boolean validar (HttpServletRequest request, HttpSession sesion){
    if (request.getParameterMap().size()>=1 && request.getParameter("prod")!= null ){
        return Tools.validateUUID(request.getParameter("prod"));
    }else return false;
} %>