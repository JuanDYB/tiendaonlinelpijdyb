<%@page import="control.Tools"%>
<%@page import="java.util.LinkedList"%>
<%@page import="modelo.Comentario"%>
<%@page import="modelo.Usuario"%>

<%
Usuario user = null;
if (session.getAttribute("usuario") != null && session.getAttribute("auth") != null && (Boolean) session.getAttribute("auth") == true) {
    String mail = (String) session.getAttribute("usuario");
    user = persistencia.getUser(mail);
    session.setAttribute("backTOURL", request.getQueryString());
       }
%>

<%--COMENTARIOS--%>
                    <p>
                        <span class="header" >Comentarios</span>
                    </p>
                        <% LinkedList<Comentario> comentarios = persistencia.getComentarios("CodigoProducto", prod.getCodigo());
                            if (comentarios == null) { %>
                    <p>No se han encontrado comentarios para este producto</p>
                        <% } else {
                            for (int i = 0; i < comentarios.size(); i++) {%>
                        <p>
                            <span class="headerComment" style="float: left">
                                Nombre: 
                                <span class="textComment" ><%= comentarios.get(i).getNombre()%></span>
                            </span>

                            <span class="headerComment" style="float: right ">
                                Fecha:
                                <span class="textComment"><%= Tools.printDate(comentarios.get(i).getFecha())%> &nbsp; <%=comentarios.get(i).getHora()%></span>
                            </span>
                            <span style="border-bottom: 1px solid #AAA; clear: both; display: block;"></span>
                        </p>

                        <p><%= comentarios.get(i).getComentario()%></p>
                            <% if (user != null && user.getPermisos() == 'a'){ %>
                        <p>
                            <a href="/admin/administration/delcomment?cod=<%= comentarios.get(i).getCodigoComentario() %>" >
                                <img src="/images/icons/deleteProd.png" alt="borrar comentario" title="Borrar comentario"/>
                            </a>
                                &nbsp;
                                &nbsp;
                                <a href="/admin/administration/editcomment.jsp?cod=<%= comentarios.get(i).getCodigoComentario() %>" >
                                    <img src="/images/icons/editProd.png" alt="editar comentario" title="Editar comentario" />
                                </a>
                        </p>
                            <% } %>
                        <% }%>
                        <% }%>
                    <%--FIN COMENTARIOS--%>

                    <%--FORMULARIO COMENTARIO--%>
                    <% if (user != null) {%>
                    <br />
                        <form method="post" name="comentarios" action="/admin/newcomment">
                            <input type="hidden" name="prod" value="<%= prod.getCodigo()%>" />
                            <p><span class="header" >Publicar un comentario</span></p>
                            Puede usar HTML (el html introducido ser&aacute; analizado en busca de etiquetas no permitidas)<br /><br />
                            <textarea id="comentario" name="comentario" cols="60" rows="5" class=":required :only_on_blur"></textarea><br /><br />
                            <input type="submit" name="send" value="Enviar comentario" />
                        </form>
                    <br />
                    <% }else{ %>
                    <p>
                        Si desea a&ntilde;adir un comentario debe <a href="/login.jsp#register" >registrarse</a> o <a href="/login.jsp">iniciar sesi&oacute;n</a>
                    </p>
                    <% } %>


                    <%--FIN FORMULARIO COMENTARIO--%>