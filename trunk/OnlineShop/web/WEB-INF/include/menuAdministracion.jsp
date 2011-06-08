<%@page import="persistencia.PersistenceInterface"%>
<%@page import="modelo.Usuario"%>
<div id="contentLeft">
  <p>
        <span class="header">Panel de usuario</span> <br />
  </p>
  <% Usuario actualUser = ((PersistenceInterface)application.getAttribute("persistence")).getUser((String)session.getAttribute("usuario")); %>
  <p>
      <span class="subHeader">Opciones</span>
        <a href="/admin/preferences.jsp" title="Preferencias" class="menuItem">Preferencias</a>
        <% if (actualUser.getPermisos() != 'a'){ %>
        <a href="/admin/salesrecord.jsp" title="Historial de compras" class="menuItem">Hist&oacute;rico de compras</a>
        <% } %>
        <a href="/logout" title="Cerrar sesi&uacute;n" class="menuItem">Cerrar sesi&oacute;n</a>
  </p>
  <% if ( actualUser.getPermisos() == 'a' ){ %>
  <p>
      <span class="subHeader">Administraci&oacute;n</span>
      <a href="/admin/administration/user_administration.jsp" title="Administraci&oacute;n de usuarios" class="menuItem">Administraci&oacute;n de usuarios</a>
      <a href="/admin/administration/products_administration.jsp" title="Administraci&oacute;n de productos" class="menuItem">Administraci&oacute;n de productos</a>
      <a href="/admin/salesrecord.jsp" title="Registro de ventas" class="menuItem">Hist&oacute;rico de ventas</a>
      <a href="/admin/administration/stats.jsp" title="Estad&iacute;sticas de ventas" class="menuItem">Estad&iacute;sticas de ventas</a>
  </p>
  <% } %>

      <!-- Esquina redondeada en la parte de abajo del menu -->
      <div class="bottomCorner">
        <img src="/images/template/corner_sub_br.gif" alt="bottom corner" class="vBottom"/>
      </div>

    </div>
  