<%@page import="modelo.Comentario"%>
<%@page import="control.Tools"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<% if (validate (request) == false){
    response.sendError(404);
    return;
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Editar comentario</title>

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
  
  <!-- Menu Izquiero -->
  <%@include file="/WEB-INF/include/menuAdministracion.jsp" %>
    
    <!-- Contenido de la columna derecha -->
    <div id="contentRight">
        
        <%@include file="/WEB-INF/include/resultados.jsp" %>
        
        
        <% String volver = "cod=" + request.getParameter("cod"); 
        session.setAttribute("backTOEditComment", volver);
        PersistenceInterface persistencia = (PersistenceInterface) application.getAttribute("persistence");
        Comentario comment = persistencia.getComment(request.getParameter("cod"));
        if (comment != null){ %>
        <p>
        <span class="header">Editar comentario: <span class="headerComplement"><%= request.getParameter("cod") %></span></span>
        <form method="post" name="editComment" action="/admin/administration/editcomment">
            <input type="hidden" name="codComentario" value="<%= request.getParameter("cod") %>" />
            <b>Comentario</b><br />
            Puede introducir etiquetas HTML, el texto introducido ser&aacute; verificado<br /><br />
            <textarea name="comentario" cols="60" rows="5" class=":required :only_on_blur"><%= comment.getComentario().replace("<br />", "") %></textarea><br /><br/>
            <input type="submit" name="editComment" value="Editar comentario" />
        </form>
        </p>
            <% } else { %>
            <span class="header">Editar comentario: <span class="headerComplement"><%= request.getParameter("cod") %></span></span>
            <p>El comentario que desea editar no ha sido encontrado</p>
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
<%!
protected boolean validate (HttpServletRequest request){
    if (request.getParameterMap().size()>=1 && request.getParameter("cod") != null){
        return Tools.validateUUID(request.getParameter("cod"));
    }
    return false;
}
%>

<%! String menuInicio = ""; %>
<%! String menuProductos = ""; %>
<%! String menuLogin = ""; %>
<%! String menuPreferencias = "class=\"active\""; %>
<%! String menuAbout = ""; %>


