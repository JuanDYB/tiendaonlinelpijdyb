<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Preferencias de usuario</title>

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
  
  <!-- Menu Izquiero : side bar links/news/search/etc. -->
  <%@include file="/WEB-INF/include/menuAdministracion.jsp" %>
    
    <!-- Contenido de la columna derecha -->
    <div id="contentRight">
        <%@include file="/WEB-INF/include/resultados.jsp" %>

    
    <p>
        <span class="header">Cambiar contrase&ntilde;a</span><br />
        Puede usar este formulario para cambiar su contrase&ntilde;a
    </p>
      <form name="changePass" action="/admin/ChangePass" method="post">
      	Contrase&ntilde;a anterior<br />
        <input name="prevPass" type="password" size="25" maxlength="20" class=":password :required :only_on_blur"/><br /><br />
        Nueva contrase&ntilde;a<br />
        <input id="pass" name="newPass" type="password" size="25" maxlength="20" class=":password :required :only_on_blur"/><br /><br />
        Repita la nueva contrase&ntilde;a por seguridad<br />
        <input name="repeatPass" type="password" size="25" maxlength="20" class=":same_as;pass :required :only_on_blur"/><br /><br /><br />
        
        <input name="changePass" type="submit" value="Cambiar contrase&ntilde;a"/>
        </form>
      
      <p>
        <span class="header">Cambiar los datos de registro</span><br />
        Puede usar este formulario para cambiar sus datos personales
      </p>
      <form name="changeData" action="/admin/edituser" method="post">
      	Nombre<br />
        <input name="name" type="text" size="50" maxlength="100" class=":alpha :required :only_on_blur" value="<%= actualUser.getNombre() %>"/><br /><br />
        
        Direcci&oacute;n<br />
        Ejemplo: Calle, 1 28002-Madrid<br />
        <input name="dir" type="text" size="70" maxlength="200" class=":dir :required :only_on_blur" value="<%= actualUser.getDir() %>" /><br /><br />
        
        <input name="changeData" type="submit" value="Modificar perfil" /> 
      </form>
      
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