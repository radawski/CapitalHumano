<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Menú principal" scope="request"/>
<%@ include file="/WEB-INF/jspf/cabecera.jspf" %>

<section class="panel">
    <p class="panel__subtitulo">Casos de uso de la entrega final</p>
    <h2 class="panel__titulo">Consultor</h2>
    <ul class="menu-principal">
        <li><a href="${pageContext.request.contextPath}/preguntas">CU020 Gestionar preguntas</a> (incluye CU023 baja)</li>
        <li><a href="${pageContext.request.contextPath}/preguntas/nueva">CU021 Dar de alta pregunta</a></li>
        <li><a href="${pageContext.request.contextPath}/evaluar-candidatos">CU025 Evaluar a candidatos</a></li>
        <li><a href="${pageContext.request.contextPath}/orden-merito">CU028 Emitir orden de mérito</a></li>
    </ul>
</section>

<section class="panel">
    <h2 class="panel__titulo">Candidato</h2>
    <ul class="menu-principal">
        <li><a href="${pageContext.request.contextPath}/login">CU001 Ingresar</a> &rarr; CU026 Completar cuestionario</li>
    </ul>
</section>

<section class="panel">
    <p class="panel__subtitulo">Tienen clase en el diseño pero no se implementan en la entrega final</p>
    <h2 class="panel__titulo">Fuera del alcance</h2>
    <ul class="menu-principal">
        <li><a class="fuera-de-alcance" href="${pageContext.request.contextPath}/importar-candidatos">CU006 Importar candidatos</a></li>
        <li><a class="fuera-de-alcance" href="${pageContext.request.contextPath}/exportar-resultados">CU007 Exportar resultados</a></li>
        <li><a class="fuera-de-alcance" href="${pageContext.request.contextPath}/puestos">Gestionar puestos</a></li>
        <li><a class="fuera-de-alcance" href="${pageContext.request.contextPath}/competencias">Gestionar competencias</a></li>
        <li><a class="fuera-de-alcance" href="${pageContext.request.contextPath}/factores">Gestionar factores</a></li>
        <li><a class="fuera-de-alcance" href="${pageContext.request.contextPath}/opciones-respuesta">Gestionar opciones de respuesta</a></li>
        <li><a class="fuera-de-alcance" href="${pageContext.request.contextPath}/reporte-comparativo">CU027 Reporte comparativo</a></li>
    </ul>
</section>

<%@ include file="/WEB-INF/jspf/pie.jspf" %>
