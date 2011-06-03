<%@page import="control.Tools"%>
<%@page import="beans.Carrito"%>
<%-- Contenido menu izquierdo --%>
<div id="contentLeft">
  <p>
        <span class="header">Menú</span>
  </p>

  <p>
      <a href="/index.jsp" class="menuItem">Inicio</a>
      <a href="/shop/products.jsp" class="menuItem">Tienda</a>
      <% if (session.getAttribute("auth") != null && (Boolean)session.getAttribute("auth") == true){ %>
      <a href="/admin/index.jsp" class="menuItem">Preferencias</a>
      <a href="/logout" class="menuItem">Cerrar sesión</a>
      <% } else{ %>
      <a href="/login.jsp" class="menuItem">Iniciar sesión</a>
      <% } %>
  </p>
  
  
  <% if (session.getAttribute("carrito") != null){
  Carrito carro = (Carrito)session.getAttribute("carrito"); %>
  <p>
      <span class="header" >Carrito</span>
  </p>
<% if (carro.getArticulos().size() == 0){ %>
<center><a href="/shop/cart.jsp" ><img src="/images/icons/cartEmpty.png" alt="Carrito" title="Ir al carrito (carrito vacío)" /></a></center>
  <% }else{ %>
<center><a href="/shop/cart.jsp" ><img src="/images/icons/cartFull.png" alt="Carrito" title="Ir al carrito" /></a></center>
  <% } %>
  <p>
      <b>Número de productos: </b><%= carro.getLenght() %><br />
      <b>Precio: </b> <%= Tools.roundDouble(carro.getPrecio()) %> &euro;<br />
  </p>
  <center><a href="/shop/updatecart" ><img src="/images/icons/validatecartlittle.png" alt="Validar carro" title="Validar carrito" /></a></center>
  
  <% } %>


      <!-- Esquina redondeada en la parte de abajo del menú -->
      <div class="bottomCorner">
        <img src="/images/template/corner_sub_br.gif" alt="bottom corner" class="vBottom"/>
      </div>

    </div>
  <%-- Fin menu izquierdo --%>