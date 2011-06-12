<%@page import="control.Tools"%>
<%@page import="modelo.Producto"%>
<%@page import="persistencia.PersistenceInterface"%>
<%@page import="java.util.Map"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Gesti&oacute;n de productos</title>

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
                <%@include file="/WEB-INF/include/menuAdministracion.jsp" %>

                <!-- Contenido de la columna derecha -->
                <div id="contentRight">
                    <%-- Quito los areibutos de la sesi&oacute;n de a&ntilde;adir y editar productos que se usan por si alguna de las dos operaciones fue abandonada sin acabar --%>
                    <% session.removeAttribute("productoEnCursoAdd");
            session.removeAttribute("productoEnCursoEdit");%>

                    <%@include file="/WEB-INF/include/resultados.jsp" %>

                    <p>
                        <span class="header">Administraci&oacute;n de productos</span><br /><br />
                        <span class="subHeader">Funciones disponibles</span>
                    </p>
                    <ul>
                        <li><a href="/admin/administration/addproduct.jsp">A&ntilde;adir Producto</a></li> <br />
                        <li>
                            <form name="busquedaProductos" method="post" action="/shop/search_prod">
                                <input type="hidden" name="redirect" value="/admin/administration/products_administration.jsp" />
                                <input name="term" type="text" class=":alpha :required :only_on_blur" />
                                <select name="campo">
                                    <option value="name">Nombre</option>
                                    <option value="desc">Descripci&oacute;n</option>
                                    <option value="detail">Detalles</option>
                                </select>
                                <input name="search" value="Buscar productos" type="submit" />
                            </form>
                        </li>
                    </ul>
                    <br />
                    <% Map<String, Producto> productos = null;
                        if (request.getAttribute("resultadosBusqueda") == null) {
                            productos = ((PersistenceInterface) application.getAttribute("persistence")).getProducts();
                        } else {
                            productos = (Map<String, Producto>) request.getAttribute("resultadosBusqueda");
        }
        if (productos != null && productos.size() != 0) {%>
                    <p>
                        <span class="subHeader">Listado de productos</span>
                    </p>
                        
                        <br />

                        <table border="0" align="center" width="90%">
                            <tr class="headerTable"><td>Nombre</td><td>Precio</td><td>Stock</td><td>Descripci&oacute;n</td><td>&nbsp;</td></tr>
                            <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
                            <% for (Producto prod : productos.values()) {%>
                            <tr class="contentTable">
                                <td><a href="/shop/viewprod.jsp?prod=<%= prod.getCodigo()%>"><%= prod.getNombre()%></a></td>
                                <td><%= Tools.roundDouble(prod.getPrecio())%> &euro;</td>
                                <td><%= prod.getStock()%></td>
                                <td><%= prod.getDesc()%></td>
                                <td>
                                    <a href="/admin/administration/modifyprod.jsp?prod=<%= prod.getCodigo()%>">
                                        <img title="Editar producto" alt="Editar producto" src="/images/icons/editProd.png"/>
                                    </a>&nbsp;&nbsp;
                                    <a href="/admin/administration/delproduct.jsp?prod=<%= prod.getCodigo()%>">
                                        <img title="Borrar producto" alt="Borrar producto" src="/images/icons/deleteProd.png"/>
                                    </a>
                                </td>
                            </tr>
                            <% }%>
                        </table>
                    <% } else {%>
                    <p>No se han encontrado productos</p>
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

<%! String menuInicio = "";%>
<%! String menuProductos = "";%>
<%! String menuLogin = "";%>
<%! String menuPreferencias = "class=\"active\"";%>
<%! String menuAbout = "";%>

