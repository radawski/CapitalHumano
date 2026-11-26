<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="/WEB-INF/jspf/cabecera.jspf" %>
<fmt:setLocale value="es_AR"/>

<c:choose>
<%-- ============================== Pasos 7–10: listado ============================== --%>
<c:when test="${paso == 'listado'}">
    <c:set var="orden" value="${sessionScope['cu028.orden']}"/>
    <c:set var="totalNoCompletos" value="${0}"/>
    <c:forEach var="grupo" items="${orden.noCompletos}">
        <c:set var="totalNoCompletos" value="${totalNoCompletos + grupo.value.size()}"/>
    </c:forEach>

    <section class="panel orden-merito">
        <div class="orden-merito__encabezado">
            <div>
                <p class="panel__subtitulo">CU028 · Pasos 7–10</p>
                <h2 class="panel__titulo">Orden de mérito</h2>
                <p>
                    <strong>Puesto:</strong> <c:out value="${orden.puestoCodigo}"/> – <c:out value="${orden.puestoNombre}"/>
                    | <strong>Evaluación:</strong> <c:out value="${sessionScope['cu028.seleccionEvaluacion']}"/>
                    | <strong>Empresa:</strong> <c:out value="${orden.empresa}"/>
                </p>
                <p class="orden-merito__emision">Emitido el <c:out value="${orden.fechaEmision.format(formatoFechaHora)}"/></p>
            </div>
            <div class="acciones-barra no-imprimir">
                <a class="boton boton--secundario" href="${pageContext.request.contextPath}/orden-merito">Volver</a>
            </div>
        </div>

        <h3 class="orden-merito__bloque">Candidatos que alcanzaron el perfil requerido (ordenados por puntaje)</h3>
        <c:choose>
            <c:when test="${empty orden.aprobados}">
                <p>Ningún candidato alcanzó las ponderaciones mínimas del puesto.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>Posición</th>
                            <th>Documento</th>
                            <th>Apellido y nombre</th>
                            <th>Puntaje obtenido</th>
                            <th>Datos adicionales</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${orden.aprobados}">
                            <tr>
                                <td><c:out value="${item.posicion}"/></td>
                                <td><c:out value="${item.tipoDocumento} ${item.nroDocumento}"/></td>
                                <td><c:out value="${item.apellido}, ${item.nombre}"/></td>
                                <td class="puntaje puntaje--aprobado"><fmt:formatNumber value="${item.puntajeObtenido}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                <td><%@ include file="/WEB-INF/jspf/cu028-datos-adicionales.jspf" %></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <h3 class="orden-merito__bloque">Candidatos que NO alcanzaron las ponderaciones mínimas</h3>
        <c:choose>
            <c:when test="${empty orden.noAprobados}">
                <p>No hay candidatos con cuestionario completo que no alcancen los mínimos.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>Documento</th>
                            <th>Apellido y nombre</th>
                            <th>Puntaje obtenido</th>
                            <th>Datos adicionales</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${orden.noAprobados}">
                            <tr>
                                <td><c:out value="${item.tipoDocumento} ${item.nroDocumento}"/></td>
                                <td><c:out value="${item.apellido}, ${item.nombre}"/></td>
                                <td class="puntaje puntaje--no-aprobado"><fmt:formatNumber value="${item.puntajeObtenido}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                <td><%@ include file="/WEB-INF/jspf/cu028-datos-adicionales.jspf" %></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <h3 class="orden-merito__bloque">Resto de candidatos (cuestionarios no completos, agrupados por estado)</h3>
        <c:choose>
            <c:when test="${empty orden.noCompletos}">
                <p>Todos los cuestionarios están completos.</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="grupo" items="${orden.noCompletos}">
                    <h4 class="orden-merito__estado">
                        Estado: <c:out value="${fn:replace(grupo.key, '_', ' ')}"/> (<c:out value="${grupo.value.size()}"/>)
                    </h4>
                    <table>
                        <thead>
                            <tr>
                                <th>Documento</th>
                                <th>Apellido y nombre</th>
                                <th>Puntaje obtenido</th>
                                <th>Datos adicionales</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${grupo.value}">
                                <tr>
                                    <td><c:out value="${item.tipoDocumento} ${item.nroDocumento}"/></td>
                                    <td><c:out value="${item.apellido}, ${item.nombre}"/></td>
                                    <td class="puntaje">(No aplica)</td>
                                    <td><%@ include file="/WEB-INF/jspf/cu028-datos-adicionales.jspf" %></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:forEach>
            </c:otherwise>
        </c:choose>

        <h3 class="orden-merito__bloque">Totales</h3>
        <table class="orden-merito__totales">
            <tbody>
                <tr><th>Total perfil requerido</th><td><c:out value="${orden.aprobados.size()}"/></td></tr>
                <tr><th>Total no alcanzan ponderación</th><td><c:out value="${orden.noAprobados.size()}"/></td></tr>
                <tr><th>Total cuestionarios no completos</th><td><c:out value="${totalNoCompletos}"/></td></tr>
                <tr><th>Total general de candidatos</th>
                    <td><c:out value="${orden.aprobados.size() + orden.noAprobados.size() + totalNoCompletos}"/></td></tr>
            </tbody>
        </table>
    </section>
</c:when>

<%-- ============================== Pasos 1–6: búsqueda ============================== --%>
<c:otherwise>
<c:if test="${not empty error}">
    <div class="alerta alerta--error" role="alert">
        <c:out value="${error}"/>
    </div>
</c:if>

<section class="panel">
    <p class="panel__subtitulo">CU028 · Pasos 1–3</p>
    <h2 class="panel__titulo">Emitir orden de mérito</h2>
    <p>Ingresá al menos un criterio para buscar el puesto.</p>

    <form method="post" action="${pageContext.request.contextPath}/orden-merito">
        <div class="filtros-grid">
            <div>
                <label for="codigo">Código</label>
                <input id="codigo" name="codigo" type="text" maxlength="20" class="texto-mayusculas"
                       value="<c:out value="${sessionScope['cu028.filtroCodigo']}"/>"
                       placeholder="Ej. P001">
            </div>
            <div>
                <label for="nombre">Nombre del puesto</label>
                <input id="nombre" name="nombre" type="text" maxlength="100" class="texto-mayusculas"
                       value="<c:out value="${sessionScope['cu028.filtroNombre']}"/>"
                       placeholder="Ej. DESARROLLADOR">
            </div>
            <div>
                <label for="empresa">Empresa</label>
                <input id="empresa" name="empresa" type="text" maxlength="100" class="texto-mayusculas"
                       value="<c:out value="${sessionScope['cu028.filtroEmpresa']}"/>"
                       placeholder="Ej. TECH SOLUTIONS">
            </div>
        </div>
        <div class="acciones-barra">
            <%-- Cancelar va en otro formulario: Enter en un filtro tiene que buscar, no cancelar. --%>
            <button class="boton boton--secundario" type="submit" form="cu028-cancelar">Cancelar</button>
            <button class="boton" type="submit" name="accion" value="buscar">Buscar</button>
        </div>
    </form>
    <form id="cu028-cancelar" method="post" action="${pageContext.request.contextPath}/orden-merito">
        <input type="hidden" name="accion" value="cancelar">
    </form>
</section>

<c:if test="${sessionScope['cu028.busquedaRealizada']}">
    <section class="panel">
        <p class="panel__subtitulo">CU028 · Paso 4</p>
        <h2 class="panel__titulo">Puestos encontrados</h2>
        <c:choose>
            <c:when test="${empty sessionScope['cu028.puestos']}">
                <p>No se encontraron puestos para los criterios ingresados.</p>
            </c:when>
            <c:otherwise>
                <div class="tabla-scroll">
                    <table>
                        <thead>
                            <tr>
                                <th>Código</th>
                                <th>Nombre del puesto</th>
                                <th>Empresa</th>
                                <th>Candidatos</th>
                                <th>Evaluaciones completas</th>
                                <th>Acción</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${sessionScope['cu028.puestos']}">
                                <tr>
                                    <td><c:out value="${p.codigo}"/></td>
                                    <td><c:out value="${p.nombre}"/></td>
                                    <td><c:out value="${p.empresa}"/></td>
                                    <td><c:out value="${p.cantidadCandidatos}"/></td>
                                    <td><c:out value="${p.evaluacionesCompletas}"/></td>
                                    <td>
                                        <c:url var="urlEmitir" value="/orden-merito/${p.codigo}"/>
                                        <a class="boton" href="${urlEmitir}">Emitir orden</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</c:if>

<%-- Paso 6: elegir una evaluación o TODAS --%>
<c:if test="${mostrarModalEvaluaciones}">
    <div class="modal-fondo" role="dialog" aria-modal="true" aria-labelledby="modal-evaluaciones-titulo">
        <div class="modal-caja modal-caja--neutra">
            <h3 id="modal-evaluaciones-titulo" class="modal-caja__titulo">
                Emitir orden de mérito – Puesto <c:out value="${sessionScope['cu028.puestoCodigo']}"/>
            </h3>
            <c:if test="${not empty errorModal}">
                <div class="alerta alerta--error" role="alert"><c:out value="${errorModal}"/></div>
            </c:if>
            <form method="post" action="${pageContext.request.contextPath}/orden-merito">
                <label for="evaluacion">Evaluación</label>
                <select id="evaluacion" name="evaluacion" required autofocus>
                    <option value="">Seleccione una evaluación</option>
                    <c:forEach var="e" items="${sessionScope['cu028.evaluaciones']}">
                        <option value="${e.evaluacionId}">
                            <c:out value="${e.fechaCreacion.format(formatoFecha)}"/> · <c:out value="${e.cantidadCandidatos}"/> candidatos
                        </option>
                    </c:forEach>
                    <option value="TODAS">TODAS</option>
                </select>
                <div class="acciones-barra">
                    <a class="boton boton--secundario" href="${pageContext.request.contextPath}/orden-merito">Cancelar</a>
                    <button class="boton" type="submit" name="accion" value="emitir">Emitir</button>
                </div>
            </form>
        </div>
    </div>
</c:if>
</c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/jspf/pie.jspf" %>
