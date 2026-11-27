<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Menú principal" scope="request"/>
<%@ include file="/WEB-INF/jspf/cabecera.jspf" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="inicio">
    <section class="panel inicio-bienvenida">
        <div>
            <p class="panel__subtitulo">Gestión de Evaluaciones de Perfiles</p>
            <h2 class="inicio-titulo">Bienvenido a Capit@l Humano</h2>
            <c:choose>
                <c:when test="${not empty sessionScope.usuario}">
                    <p class="inicio-texto">Sesión iniciada como <strong><c:out value="${sessionScope.usuario}"/></strong>. Elija una opción para comenzar.</p>
                </c:when>
                <c:otherwise>
                    <p class="inicio-texto">Ingrese con su usuario de consultor para trabajar con el sistema, o con su clave si va a realizar un cuestionario.</p>
                </c:otherwise>
            </c:choose>
        </div>
        <c:if test="${empty sessionScope.usuario}">
            <a class="boton" href="${ctx}/login">Ingresar</a>
        </c:if>
    </section>

    <h2 class="inicio-seccion">Consultor</h2>
    <div class="inicio-grilla">
        <a class="inicio-tarjeta" href="${ctx}/preguntas">
            <span class="inicio-tarjeta__icono" aria-hidden="true">&#10067;</span>
            <span class="inicio-tarjeta__titulo">Gestionar preguntas</span>
            <span class="inicio-tarjeta__texto">Busque preguntas por competencia y factor, y modifíquelas o délas de baja.</span>
            <span class="inicio-tarjeta__cu">CU020 · CU023</span>
        </a>
        <a class="inicio-tarjeta" href="${ctx}/preguntas/nueva">
            <span class="inicio-tarjeta__icono" aria-hidden="true">&#10133;</span>
            <span class="inicio-tarjeta__titulo">Dar de alta pregunta</span>
            <span class="inicio-tarjeta__texto">Cargue una pregunta nueva con su opción de respuesta y ponderaciones.</span>
            <span class="inicio-tarjeta__cu">CU021</span>
        </a>
        <a class="inicio-tarjeta" href="${ctx}/evaluar-candidatos">
            <span class="inicio-tarjeta__icono" aria-hidden="true">&#128101;</span>
            <span class="inicio-tarjeta__titulo">Evaluar a candidatos</span>
            <span class="inicio-tarjeta__texto">Asigne un puesto a los candidatos y genere sus claves de cuestionario.</span>
            <span class="inicio-tarjeta__cu">CU025</span>
        </a>
        <a class="inicio-tarjeta" href="${ctx}/orden-merito">
            <span class="inicio-tarjeta__icono" aria-hidden="true">&#127942;</span>
            <span class="inicio-tarjeta__titulo">Emitir orden de mérito</span>
            <span class="inicio-tarjeta__texto">Obtenga el ranking de candidatos evaluados para un puesto e imprímalo.</span>
            <span class="inicio-tarjeta__cu">CU028</span>
        </a>
    </div>

    <h2 class="inicio-seccion">Candidato</h2>
    <div class="inicio-grilla">
        <a class="inicio-tarjeta" href="${ctx}/login?tipo=CANDIDATO">
            <span class="inicio-tarjeta__icono" aria-hidden="true">&#128221;</span>
            <span class="inicio-tarjeta__titulo">Realizar cuestionario</span>
            <span class="inicio-tarjeta__texto">Ingrese con su documento y la clave que le asignó la consultora.</span>
            <span class="inicio-tarjeta__cu">CU001 · CU026</span>
        </a>
    </div>

    <details class="inicio-fuera">
        <summary>Funciones fuera del alcance de la entrega final</summary>
        <p class="inicio-fuera__nota">Tienen clase en el diseño, pero no se implementan en esta entrega.</p>
        <ul class="inicio-fuera__lista">
            <li><a href="${ctx}/importar-candidatos">Importar candidatos <span>CU006</span></a></li>
            <li><a href="${ctx}/exportar-resultados">Exportar resultados <span>CU007</span></a></li>
            <li><a href="${ctx}/puestos">Gestionar puestos <span>CU008</span></a></li>
            <li><a href="${ctx}/competencias">Gestionar competencias</a></li>
            <li><a href="${ctx}/factores">Gestionar factores</a></li>
            <li><a href="${ctx}/opciones-respuesta">Gestionar opciones de respuesta</a></li>
            <li><a href="${ctx}/reporte-comparativo">Reporte comparativo <span>CU027</span></a></li>
        </ul>
    </details>
</div>

<%@ include file="/WEB-INF/jspf/pie.jspf" %>
