<%@page import="modelo.Producto"%>
<%@page import="persistencia.PersistenceInterface"%>
<%@page import="control.Tools"%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<% if (validateEntry(request) == false) {
        response.sendError(404);
        return;
    }
%>
<% PersistenceInterface persistencia = (PersistenceInterface) application.getAttribute("persistence");
    Producto prod = persistencia.getProduct(request.getParameter("prod"));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <% if (prod == null) {%>
        <title>Producto no encontrado</title>
        <% } else {%>
        <title><%= prod.getNombre()%></title>
        <% }%>
        
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
                <%@include file="/WEB-INF/include/menu.jsp" %>

                <!-- Contenido de la columna derecha -->
                <div id="contentRight">

                    <%-- Resultados de la operaci&oacute;n --%>
                    <%@include file="/WEB-INF/include/resultados.jsp" %>

                    <% if (prod == null) {%>
                    <p>
                        <span class="header">Producto no encontrado</span>
                    </p>
                    <p>
                        El producto seleccionado no se ha encontrado. Posiblemente esta petici&oacute;n fue alterada <br />
                        Puede volver al <a href="/shop/products.jsp">listado de productos</a>
                    </p>
                    <% } else {%>
                    <p>
                        <span class="header" >Hoja de producto: <span class="headerComplement" ><%= prod.getNombre()%></span></span>
                        <br />
                        <% if (Tools.existeElFichero(application.getRealPath("/images/products/" + prod.getCodigo())) == true){ %>
                            <img src ="/images/products/<%= prod.getCodigo() %>" alt="imagen producto" height="400" width="450" align="right"/>
                        <% } %>
                            
                        <ul>
                            <li> <b>Precio: </b><%= Tools.roundDouble(prod.getPrecio())%> &euro; </li>
                            <li> <b>Disponibilidad: </b> <%= prod.getDisponibilidad()%> </li>
                            <li> <b>Descripci&oacute;n: </b> <%= prod.getDesc()%> </li>

                        </ul>
                    </p>
                            <p>
                                <a href="/shop/addcart?prod=<%= prod.getCodigo() %>&cant=1"><img src="/images/icons/addCarro.png" alt="a&ntilde;adir a la cesta" title="A&ntilde;adir producto a la cesta"/></a>
                            </p>
                    <p>
                        <span class="subHeader">Detalles del producto</span><br />
                        <%= prod.getDetalles()%>
                    </p>
                    
                        <%@include file="/WEB-INF/include/comentarios.jsp" %>
                    
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

<%! private boolean validateEntry(HttpServletRequest request) {
        if (request.getParameterMap().size() >= 1 && request.getParameter("prod") != null) {
            return Tools.validateUUID(request.getParameter("prod"));
        } else {
            return false;
        }
    }%>

<%! String menuInicio = "";%>
<%! String menuProductos = "class=\"active\"";%>
<%! String menuLogin = "";%>
<%! String menuPreferencias = "";%>
<%! String menuAbout = "";%>
