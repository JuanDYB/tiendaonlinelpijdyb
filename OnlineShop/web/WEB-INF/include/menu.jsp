<%@page import="control.Tools"%>
<%@page import="modelo.Carrito"%>
<%-- Contenido menu izquierdo --%>
<div id="contentLeft">
  <p>
      <span class="header">Men&uacute;</span>
  </p>

  <p>
      <a href="/index.jsp" class="menuItem">Inicio</a>
      <a href="/shop/products.jsp" class="menuItem">Tienda</a>
      <% if (session.getAttribute("auth") != null && (Boolean)session.getAttribute("auth") == true){ %>
      <a href="/admin/index.jsp" class="menuItem">Preferencias</a>
      <a href="/logout" class="menuItem">Cerrar sesi&oacute;n</a>
      <% } else{ %>
      <a href="/login.jsp" class="menuItem">Iniciar sesi&oacute;n</a>
      <% } %>
  </p>
  
  
  <% if (session.getAttribute("carrito") != null){
  Carrito carro = (Carrito)session.getAttribute("carrito"); %>
  <p>
      <span class="header" >Carrito</span>
  </p>
<% if (carro.getArticulos().size() == 0){ %>
<center><a href="/shop/cart.jsp" ><img src="/images/icons/cartEmpty.png" alt="Carrito" title="Ir al carrito (carrito vac&iacute;o)" /></a></center>
  <% }else{ %>
<center><a href="/shop/cart.jsp" ><img src="/images/icons/cartFull.png" alt="Carrito" title="Ir al carrito" /></a></center>
  <% } %>
  <p>
      <b>N&uacute;mero de productos: </b><%= carro.getLenght() %><br />
      <b>Precio: </b> <%= Tools.roundDouble(carro.getPrecio()) %> &euro;<br />
  </p>
  <center><a href="/shop/updatecart" ><img src="/images/icons/validatecartlittle.png" alt="Validar carro" title="Validar carrito" /></a></center>
  
  <% } %>


      <!-- Esquina redondeada en la parte de abajo del menu -->
      <div class="bottomCorner">
        <img src="/images/template/corner_sub_br.gif" alt="bottom corner" class="vBottom"/>
      </div>

    </div>
  <%-- Fin menu izquierdo --%>