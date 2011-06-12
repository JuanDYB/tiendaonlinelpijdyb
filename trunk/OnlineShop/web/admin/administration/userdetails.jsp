<%@page import="org.owasp.esapi.errors.ValidationException"%>
<%@page import="control.Tools"%>
<%@page import="java.util.Map"%>
<%@page import="persistencia.PersistenceInterface"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<%
    if (validar(request) == false) {
        response.sendError(404);
    }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Detalles de usuario</title>

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
                        <span class="header" >Datos de usuario</span>
                    </p>
                    <%
                        PersistenceInterface persistencia = (PersistenceInterface) application.getAttribute("persistence");
                        Usuario user = persistencia.getUser(request.getParameter("email"));

            if (user == null) {%>
                    <p>
                        No se ha encontrado el usuario solicitado
                    </p>
                    <% } else {%>
                        <ul>
                            <li><b>Nombre: </b><%= user.getNombre()%></li>
                            <li><b>Direcci&oacute;n: </b><%= user.getDir()%></li>
                            <li><b>Email: </b><%= user.getMail()%></li>
                            <li><b>Tipo de usuario: </b><%= user.getPrintablePermissions()%></li>
                        </ul>
                    <% }%>


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
    private boolean validar(HttpServletRequest request) {
        if (request.getParameterMap().size() == 1 && request.getParameter("email") != null) {
            try {
                String email = Tools.validateEmail(request.getParameter("email"));
                return true;
            } catch (ValidationException ex) {
                return false;
            }
        }
        return false;
    }

%>

<%! String menuInicio = "";%>
<%! String menuProductos = "";%>
<%! String menuLogin = "";%>
<%! String menuPreferencias = "class=\"active\"";%>
<%! String menuAbout = "";%>
