<%@page import="org.owasp.esapi.errors.ValidationException"%>
<%@page import="persistencia.PersistenceInterface"%>
<%@page import="control.Tools"%>
<%@page import="modelo.Producto"%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%if (validar (request) == false){
    response.sendError(404);
    return;
} %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Modificar producto</title>

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
    <%@include file="/WEB-INF/include/resultados.jsp" %>
    
    <div id="contentRight">
        <% PersistenceInterface persistencia = (PersistenceInterface)application.getAttribute("persistence");
        Usuario user = persistencia.getUser(request.getParameter("user"));
        if (user == null){
            request.setAttribute("resultados", "Usuario no encontrado");
            Tools.anadirMensaje(request, "El usuario que desea editar no ha sido encontrado"); %>
            <jsp:forward page="/admin/administration/user_administration.jsp" />
        <% }else{ %>
        <p>
            <span class="header">Editar Usuario</span>
            <p>
                Email: <%= user.getMail() %>
                <form action="/admin/administration/edituser" method="post" name="editUser">
                    <input type="hidden" name="mail" value="<%= user.getMail() %>" />
                    Nombre <br />
                    <input type="text" name="nombre" maxlength="100" size="50" class=":alpha :required :only_on_blur" value="<%= user.getNombre() %>" /><br /><br />
                    
                    Direcci&oacute;n <br />
                    <input type="text" name="dir" maxlength="200" size="70" class=":dir :required :only_on_blur" value="<%= user.getDir() %>" /><br /><br />
                    
                    Permisos<br />
                    <select name="perm">
                        <% if (user.getPermisos() == 'a' && persistencia.anyAdmin() == 1){%>
                        <option value="a" selected >Administrador</option>
                        <% }else if (user.getPermisos() == 'a'){ %>
                        <option value="a" selected >Administrador</option>
                        <option value="c">Usuario registrado</option>
                        <% }else{ %>
                        <option value="a">Administrador</option>
                        <option value="c" selected >Usuario registrado</option>
                        <% } %>
                    </select>
                    <br /><br />
                    <input type="submit" name="edit" value="Editar usuario"/>       
                </form>
            </p>
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

<%--Funcion para validar la entrada en esta jsp con los parametros necesarios--%>
<%! private boolean validar (HttpServletRequest request){
    if (request.getParameterMap().size()>=1 && request.getParameter("user")!= null ){
        try{
            Tools.validateEmail(request.getParameter("user"));
            return true;
        }catch (ValidationException ex){
            return false;
        }
    }else{
        return false;
    }
} %>

<%! String menuInicio = ""; %>
<%! String menuProductos = ""; %>
<%! String menuLogin = ""; %>
<%! String menuPreferencias = "class=\"active\""; %>
<%! String menuAbout = ""; %>