<%@page import="java.util.ArrayList"%>
<%-- Solo se muestra si hay resultados de una operaci&oacute;n que mostrar --%>
<% if (request.getAttribute("resultados") != null) {%>
<p>
    <span class="subHeader"><%= request.getAttribute("resultados")%></span></p>
<img src="/images/icons/information.png" alt="information" align="left" />
<ul style="margin-left: 4em">
    <%ArrayList<String> listado = (ArrayList<String>) request.getAttribute("listaResultados");
                for (String actual : listado) {%>
    <li><%= actual%></li>
    <% }%>
</ul>
<br />
<br /><br /><br />
<% }%>
