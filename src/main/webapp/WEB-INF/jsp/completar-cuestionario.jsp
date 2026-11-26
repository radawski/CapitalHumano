<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jspf/cabecera.jspf" %>

<section class="panel">
    <p class="panel__subtitulo">PantallaCompletarCuestionario</p>

    <c:choose>
        <%-- Pasos 4 a 7: wizard de bloques --%>
        <c:when test="${not empty bloque}">
            <h2 class="panel__titulo">Cuestionario - Bloque <c:out value="${bloque.nroBloque}"/></h2>

            <c:if test="${not empty error}">
                <div class="alerta alerta--error"><c:out value="${error}"/></div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/cuestionario/responder">
                <c:forEach var="pregunta" items="${bloque.preguntas}">
                    <c:set var="idPregunta" value="${pregunta.preguntaId.toString()}"/>
                    <c:set var="faltaResponder" value="${not empty sinResponder and sinResponder.contains(idPregunta)}"/>
                    <fieldset class="${faltaResponder ? 'campo--error' : ''}" style="margin-bottom: 16px;">
                        <legend>
                            <strong><c:out value="${pregunta.orden}"/>.</strong>
                            <c:out value="${pregunta.enunciado}"/>
                        </legend>
                        <c:forEach var="valor" items="${pregunta.valores}">
                            <label style="display:block">
                                <input type="radio" name="respuesta_${idPregunta}"
                                       value="${valor.valorOpcionId}"
                                       <c:if test="${respuestas[idPregunta] eq valor.valorOpcionId.toString()}">checked</c:if>>
                                <c:out value="${valor.descripcion}"/>
                            </label>
                        </c:forEach>
                        <c:if test="${faltaResponder}">
                            <p class="error">Es obligatorio responder</p>
                        </c:if>
                    </fieldset>
                </c:forEach>

                <div class="acciones-barra acciones-barra--derecha">
                    <button class="boton" type="submit">
                        <c:choose>
                            <c:when test="${bloque.esUltimo()}">Finalizar</c:when>
                            <c:otherwise>Siguiente</c:otherwise>
                        </c:choose>
                    </button>
                </div>
            </form>
        </c:when>

        <%-- Paso 8: cuestionario completo --%>
        <c:when test="${not empty exito}">
            <h2 class="panel__titulo">Cuestionario enviado</h2>
            <div class="alerta alerta--exito"><c:out value="${exito}"/></div>
            <a class="boton" href="${pageContext.request.contextPath}/">Salir</a>
        </c:when>

        <%-- Paso 1: instrucciones y mensajes del ingreso --%>
        <c:otherwise>
            <h2 class="panel__titulo">Cuestionario - Instrucciones</h2>

            <c:if test="${not empty mensaje}">
                <p><c:out value="${mensaje}"/></p>
            </c:if>

            <c:if test="${not empty instrucciones}">
                <p style="white-space: pre-line;"><c:out value="${instrucciones}"/></p>

                <form method="post" action="${pageContext.request.contextPath}/cuestionario/aceptar" style="display:inline">
                    <button class="boton" type="submit">Aceptar</button>
                </form>
                <form method="post" action="${pageContext.request.contextPath}/cuestionario/cancelar" style="display:inline">
                    <button class="boton boton--secundario" type="submit">Cancelar</button>
                </form>
            </c:if>

            <c:if test="${empty instrucciones}">
                <a class="boton" href="${pageContext.request.contextPath}/">Salir</a>
            </c:if>
        </c:otherwise>
    </c:choose>
</section>

<%@ include file="/WEB-INF/jspf/pie.jspf" %>
