<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jspf/cabecera.jspf" %>

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

<%@ include file="/WEB-INF/jspf/pie.jspf" %>
