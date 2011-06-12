<%@page import="modelo.Carrito"%>
<%@page import="control.GeneradorDeEstadiaticasDeVentas"%>
<%@page import="persistencia.PersistenceInterface"%>
<%@ page contentType="text/html; charset=UTF-8" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Estad&iacute;sticas</title>

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


                    <% PersistenceInterface persistencia = (PersistenceInterface) application.getAttribute("persistence");
                        ArrayList<Carrito> historial = persistencia.requestSalesRecord("1", "1");
                        if (historial != null) {%>
                    <p>
                        <span class="header" >Estad&iacute;sticas de ventas</span>
                    </p>
                    <% GeneradorDeEstadiaticasDeVentas ventasSemana = new GeneradorDeEstadiaticasDeVentas(historial);
                        if (ventasSemana.graficoNumVentasSemana(application.getRealPath("/images/stats/ventasSemana.jpg")) == true) {%>

                    <p><span class="headerTable" style="text-align: center" >N&uacute;mero de compras por dias de la semana</span></p>
                    <center><img src="/images/stats/ventasSemana.jpg" alt="ventas por dia" /></center>
                    <br />

                    <% } else {%>

                    <p>Error generando el gr&aacute;fico</p>

                    <% }%>

                    <% GeneradorDeEstadiaticasDeVentas priceVentasSemana = new GeneradorDeEstadiaticasDeVentas(historial);
                        if (priceVentasSemana.graficoGanadoPorDia(application.getRealPath("/images/stats/porcentajeGastadoDia.jpg")) == true) {%>

                    <p><span class="headerTable" style="text-align: center" >Media que se suelen gastar los clientes por compra por dias de la semana</span></p>
                    <center><img src="/images/stats/porcentajeGastadoDia.jpg" alt="ganado dia semana" /></center>
                    <br />

                    <% } else {%>
                    <p>Error generando el gr&aacute;fico</p>
                    <% }%>

                    <% GeneradorDeEstadiaticasDeVentas procentajeProdDia = new GeneradorDeEstadiaticasDeVentas(historial);
                        if (procentajeProdDia.porcentajeProductosVendidosMes(persistencia, application.getRealPath("/images/stats/porcentajeVendidosDia.jpg")) == true) {%>

                    <p><span class="headerTable" style="text-align: center" >Porcentaje de productos vendidos por mes</span></p>
                    <center><img src="/images/stats/porcentajeVendidosDia.jpg" alt="porcentaje vendidos dia" /></center>
                    <br />

                    <% } else {%>
                    <p>Error generando el gr&aacute;fico</p>
                    <% }%>

                    <% } else {%>
                    <p>
                        <span class="header">Ha ocurrido un error</span>
                        Ha ocurrido un error con la petici&oacute;n y no se mostrar&aacute;n las estad&iacute;sticas, disculpe las molestias.
                        <br />
                        Puede recargar la p&aacute;gina si desea volverlo a intentar de nuevo.
                    </p>
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

