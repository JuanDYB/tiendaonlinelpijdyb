<div id="header">
    
    
    <!--Esquina redondeada de arriba-->
    <img src="/images/template/corner_tl.gif" alt="corner" style="float:left;" />
    
    <!-- Site title and subTitle -->
    <span class="title">
      <span class="white">Tienda On</span>line
      <span class="subTitle">
          &copy; Juan D&iacute;ez-Yanguas Barber
      </span>
    </span>

    <% Boolean auth = (Boolean) session.getAttribute("auth"); %>
    <!-- El menu se define en orden inverso a como se muestra (caused by float: right) -->
    <a href="/about.jsp" <%= menuAbout %> class="lastMenuItem">Acerca de<span class="desc">Contacto</span></a>

    <% if (auth != null && auth == true){ %>
    <a href="/admin/index.jsp" <%= menuPreferencias %> >Preferencias<span class="desc">gestionar</span></a>
    <% } %>

    <% if (auth == null || auth == false){ %>
        <a href="/login.jsp" <%= menuLogin %> >Login / Registro<span class="desc">iniciar</span></a>
    <% }else { %>
    <a href="/logout" <%= menuLogin %> >Cerrar sesi&oacute;n<span class="desc">salir</span></a>
    <% } %>
    
    <a href="/shop/products.jsp" <%= menuProductos %> >Productos<span class="desc">Nuestros productos</span></a>
    
    <a href="/index.jsp" <%= menuInicio %> >Inicio<span class="desc">Bienvenido</span></a>

</div>