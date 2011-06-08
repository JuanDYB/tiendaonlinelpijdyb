<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Refresh" content="3; url=/login.jsp" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>No permitido</title>

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
    
    <p>
        <span class="header">Acceso Restringido</span>
        
        <img src="/images/icons/restrictedArea.png" alt="lock" align="left" />
            <br />
            <br />
        No puede acceder al panel de preferencias. No est&aacute; registrado.
        <br />
        <br />
        Ser&aacute; redirigido en 3 segundos a la p&aacute;gina de registro<br /><br />
        Si no quiere esperar haga click <a href="/login.jsp">aqu&iacute;</a>
        <br /><br /><br /><br /><br />
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
<%! String menuPreferencias = ""; %>
<%! String menuAbout = ""; %>