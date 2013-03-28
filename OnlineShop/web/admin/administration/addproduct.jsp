<%@page import="modelo.Producto"%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<title>A&ntilde;adir producto</title>

<script type="text/javascript" src="/scripts/jquery-1.6.1.js"></script>
<script type="text/javascript" src="/scripts/vanadium.js"></script>
<link rel="stylesheet" type="text/css" href="/css/validacion.css" media="screen, tv, projection" />

<script type="text/javascript" src="/scripts/tiny_mce/tiny_mce.js"></script>
<script type="text/javascript" src="/scripts/scripts.js"></script>

<link rel="stylesheet" type="text/css" href="/css/screen_yellow.css" media="screen, tv, projection" />
</head>

    <body onload="loadEditor();">
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
        <%@include file="/WEB-INF/include/resultados.jsp" %>
        

        <p>
            <span class="header">A&ntilde;adir producto</span>
        </p>

        <form name="addprod" method="post" action="/admin/administration/addprod" enctype="multipart/form-data" >

            <b>Nombre</b> <br />
            <input type="text" name="name" maxlength="70" size="50" class=":alpha :required :only_on_blur" /><br /><br />
            <b>Precio</b> <br />
            <input type="text" name="price" maxlength="10" size="13" class=":number :required :only_on_blur" /><br /><br />
            <b>Unidades en stock</b> <br />
            <input type="text" name="stock" maxlength="6" size="8" class=":digits :required :only_on_blur" /><br /><br />
            <b>Descripci&oacute;n corta</b> <br />
            <input type="text" name="desc" maxlength="100" size ="50" class=":required :only_on_blur" /><br /><br />
            <b>Escoja una foto de producto</b>&nbsp;(Opcional)<br />
            <input type="file" name="foto" /><br /><br />
            <b>Detalles</b> <br />
            Puede usar etiquetas html (si usa etiquetas invalidas sera bloqueado)<br />
            <textarea name="detail" cols="60" rows="15" class=":required :only_on_blur"></textarea><br /><br />
            <input type="submit" name="sendProd" value="Enviar datos" />
        </form>
      <!-- Crea las esquinas redondeadas abajo -->
      <img src="/images/template/corner_sub_bl.gif" alt="bottom corner" class="vBottom" />

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
