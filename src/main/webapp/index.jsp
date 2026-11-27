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
            <p class="inicio-texto">Sesión iniciada como <strong><c:out value="${sessionScope.usuario}"/></strong>. Elija una opción para comenzar.</p>
        </div>
    </section>

    <h2 class="inicio-seccion">Consultor</h2>
    <div class="inicio-grilla">
        <a class="inicio-tarjeta" href="${ctx}/preguntas">
            <span class="inicio-tarjeta__icono" aria-hidden="true">&#10067;</span>
            <span class="inicio-tarjeta__titulo">Gestionar preguntas</span>
            <span class="inicio-tarjeta__texto">Busque preguntas por competencia y factor, cargue preguntas nuevas o délas de baja.</span>
        </a>
        <a class="inicio-tarjeta" href="${ctx}/evaluar-candidatos">
            <span class="inicio-tarjeta__icono" aria-hidden="true">&#128101;</span>
            <span class="inicio-tarjeta__titulo">Evaluar a candidatos</span>
            <span class="inicio-tarjeta__texto">Asigne un puesto a los candidatos y genere sus claves de cuestionario.</span>
        </a>
        <a class="inicio-tarjeta" href="${ctx}/orden-merito">
            <span class="inicio-tarjeta__icono" aria-hidden="true">&#127942;</span>
            <span class="inicio-tarjeta__titulo">Emitir orden de mérito</span>
            <span class="inicio-tarjeta__texto">Obtenga el ranking de candidatos evaluados para un puesto e imprímalo.</span>
        </a>
    </div>
</div>

<%@ include file="/WEB-INF/jspf/pie.jspf" %>
