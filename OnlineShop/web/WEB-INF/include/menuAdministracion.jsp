<%@page import="persistencia.PersistenceInterface"%>
<%@page import="beans.Usuario"%>
<div id="contentLeft">
  <p>
        <span class="header">Panel de usuario</span> <br />
  </p>
  <% Usuario actualUser = ((PersistenceInterface)application.getAttribute("persistence")).getUser((String)session.getAttribute("usuario")); %>
  <p>
      <span class="subHeader">Opciones</span>
        <a href="/admin/preferences.jsp" title="Preferencias" class="menuItem">Preferencias</a>
        <% if (actualUser.getPermisos() != 'a'){ %>
        <a href="/admin/salesrecord.jsp" title="Historial de compras" class="menuItem">Histórico de compras</a>
        <% } %>
        <a href="/logout" title="Cerrar sesión" class="menuItem">Cerrar sesión</a>
  </p>
  <% if ( actualUser.getPermisos() == 'a' ){ %>
  <p>
      <span class="subHeader">Administración</span>
      <a href="/admin/administration/user_administration.jsp" title="Administración de usuarios" class="menuItem">Administración de usuarios</a>
      <a href="/admin/administration/products_administration.jsp" title="Administración de productos" class="menuItem">Administración de productos</a>
      <a href="/admin/salesrecord.jsp" title="Registro de ventas" class="menuItem">Histórico de ventas</a>
      <a href="/admin/administration/stats.jsp" title="Estadísticas de ventas" class="menuItem">Estadísticas de ventas</a>
  </p>
  <% } %>

      <!-- Esquina redondeada en la parte de abajo del menú -->
      <div class="bottomCorner">
        <img src="/images/template/corner_sub_br.gif" alt="bottom corner" class="vBottom"/>
      </div>

    </div>
  