<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Tienda Online</title>

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
                <%@include file="/WEB-INF/include/menu.jsp" %>

                <!-- Contenido de la columna derecha -->
                <div id="contentRight">

                    <p>
                        <span class="header">Tienda Online</span>
                        Hemos desarrollado esta aplicaci&oacute;n online para que sus compras sean lo m&aacute;s c&oacute;modas posibles. En caso de que tenga alg&uacute;n problema con el 
                        servicio no dude en ponerse en contacto con nostros y haremos todo lo que este en nuestra mano para solucionar el problema lo antes 
                        posible
                    </p>
                    
                    <p>
                        <span class="header" >Sobre las compras</span>
                        Puede <a href="/login.jsp">registrarse</a> en la tienda o puede comprar sin un registro previo.
                        Usando la opci&oacute;n del registro previo solo tendr&aacute; que introducir una vez los datos personales. El resto
                        de las veces lo &uacute;nico que tendr&aacute; que hacer es elegir la forma de pago con que dese abonar el precio de la compra.
                    </p>

                    <p>
                        Puede llenar su carrito de la compra y nosotros lo guardaremos por usted hasta que decida comprarlo, su carrito de la compra 
                        ser&aacute; conservado de aunque cierre la sesi&oacute;n. Cuando usted cierre la sesi&oacute;n con un carrito lleno sin haberlo confirmado guardaremos 
                        su carrito y podr&aacute; verlo lleno la pr&oacute;xima vez que inicie sesi&oacute;n.
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

<%! String menuInicio = "class=\"active\"";%>
<%! String menuProductos = "";%>
<%! String menuLogin = "";%>
<%! String menuPreferencias = "";%>
<%! String menuAbout = "";%>