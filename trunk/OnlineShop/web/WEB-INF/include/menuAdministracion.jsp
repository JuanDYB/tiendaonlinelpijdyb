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
        <a href="/admin/salesrecord.jsp" title="Historial de compras" class="menuItem">HistÛrico de compras</a>
        <% } %>
        <a href="/logout" title="Cerrar sesiÛn" class="menuItem">Cerrar sesiÛn</a>
  </p>
  <% if ( actualUser.getPermisos() == 'a' ){ %>
  <p>
      <span class="subHeader">AdministraciÛn</span>
      <a href="/admin/administration/user_administration.jsp" title="AdministraciÛn de usuarios" class="menuItem">AdministraciÛn de usuarios</a>
      <a href="/admin/administration/products_administration.jsp" title="AdministraciÛn de productos" class="menuItem">AdministraciÛn de productos</a>
      <a href="/admin/salesrecord.jsp" title="Registro de ventas" class="menuItem">HistÛrico de ventas</a>
      <a href="/admin/administration/stats.jsp" title="EstadÌsticas de ventas" class="menuItem">EstadÌsticas de ventas</a>
  </p>
  <% } %>

      <!-- Esquina redondeada en la parte de abajo del men? -->
      <div class="bottomCorner">
        <img src="/images/template/corner_sub_br.gif" alt="bottom corner" class="vBottom"/>
      </div>

    </div>
  