<%@page import="control.Tools"%>
<%@page import="modelo.Producto"%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Previsualizaci&oacute;n de producto</title>
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
        <% String operation = (String)request.getAttribute("operation");
            String nombre = "";
            String precio = "";
            String stock = "";
            String desc = "";
            String detalles = "";
            String codigo = "";


        if (operation.equals("add")){
           Producto prod = (Producto) session.getAttribute("productoEnCursoAdd");
           nombre = prod.getNombre();
           precio = Double.toString(prod.getPrecio());
           stock = Integer.toString(prod.getStock());
           desc = prod.getDesc();
           detalles = prod.getDetalles();
           codigo = prod.getCodigo();
        } else if (operation.equals("edit")){
           Producto prod = (Producto) session.getAttribute("productoEnCursoEdit");
           nombre = prod.getNombre();
           precio = Double.toString(prod.getPrecio());
           stock = Integer.toString(prod.getStock());
           desc = prod.getDesc();
           detalles = prod.getDetalles();
           codigo = prod.getCodigo();
        }
           
        %>
        <p>
            <span class="header" >Previsualizaci&oacute;n del producto: <%= nombre %></span> <br />
            <% boolean imagen = false;
            if (Tools.fileExists(application.getRealPath("/images/products/" + codigo)) == true){
            imagen = true;%>
            <a href="/images/products/<%= codigo %>" target="_blank">
                <img src ="/images/products/<%= codigo %>" alt="imagen producto" height="400" width="450" align="right"/>
            </a>
            <% } %>
            <ul>
                <li><b>Precio: </b><%= precio %> &euro;</li><br />
                <li><b>Stock disponible: </b><%= stock %> unidades</li><br />
                <li><b>Descripci&oacute;n: </b><%= desc %></li><br />
                <li><b>Detalles: </b><br /><%= detalles %></li>
            </ul>
            <br />
            <% if (imagen == true){ %>
            <br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br />
            <% } %>
            
            
            <% if (operation.equals("add")){ %>
            <a style="margin-left: 2em" href="/admin/administration/addproduct.jsp">Editar datos</a>
            <a style="margin-left: 15em" href="/admin/administration/addprod">Confirmar nuevo producto</a>
            <% } else{ %>
            <a style="margin-left: 2em" href="/admin/administration/modifyprod.jsp">Editar datos</a>
            <a style="margin-left: 15em" href="/admin/administration/editprod">Confirmar edici&oacute;n de producto</a>
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
<%! String menuProductos = ""; %>
<%! String menuLogin = ""; %>
<%! String menuPreferencias = "class=\"active\""; %>
<%! String menuAbout = ""; %>
