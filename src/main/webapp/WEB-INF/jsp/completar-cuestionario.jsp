<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jspf/cabecera.jspf" %>

<section class="panel">
    <p class="panel__subtitulo">PantallaCompletarCuestionario</p>
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
            <button class="boton" type="submit">Cancelar</button>
        </form>
    </c:if>

    <c:if test="${empty instrucciones}">
        <a class="boton" href="${pageContext.request.contextPath}/">Salir</a>
    </c:if>
</section>

<%@ include file="/WEB-INF/jspf/pie.jspf" %>