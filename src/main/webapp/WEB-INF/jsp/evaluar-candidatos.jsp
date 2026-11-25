<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jspf/cabecera.jspf" %>

<c:choose>
    <c:when test="${paso == 'puesto'}">
        <section class="panel">
            <p class="panel__subtitulo">CU025 · Pasos 7–10</p>
            <h2 class="panel__titulo">Selección de puesto y competencias evaluables</h2>
            <p>Candidatos validados y guardados en sesión (<code>cu025.candidatos</code>):
                <strong><c:out value="${sessionScope['cu025.candidatos'].size()}"/></strong>.</p>
            <p>Esta etapa continúa en el issue #11.</p>
            <div class="acciones-barra">
                <a class="boton boton--secundario" href="${pageContext.request.contextPath}/evaluar-candidatos">Volver a candidatos</a>
                <form method="post" action="${pageContext.request.contextPath}/evaluar-candidatos">
                    <button class="boton" type="submit" name="accion" value="cancelar">Salir</button>
                </form>
            </div>
        </section>
    </c:when>

    <c:otherwise>
        <c:if test="${not empty error}">
            <div class="alerta alerta--error" role="alert">
                <c:out value="${error}"/>
            </div>
        </c:if>

        <section class="panel">
            <p class="panel__subtitulo">CU025 · Pasos 1–3</p>
            <h2 class="panel__titulo">Búsqueda de candidatos</h2>
            <form method="post" action="${pageContext.request.contextPath}/evaluar-candidatos">
                <div class="filtros-grid">
                    <div>
                        <label for="apellido">Apellido</label>
                        <input id="apellido" name="apellido" type="text"
                               value="<c:out value="${sessionScope['cu025.filtroApellido']}"/>"
                               placeholder="Ej. GOMEZ">
                    </div>
                    <div>
                        <label for="nombre">Nombre</label>
                        <input id="nombre" name="nombre" type="text"
                               value="<c:out value="${sessionScope['cu025.filtroNombre']}"/>"
                               placeholder="Ej. LUCAS">
                    </div>
                    <div>
                        <label for="nroCandidato">Nº de candidato</label>
                        <input id="nroCandidato" name="nroCandidato" type="text"
                               value="<c:out value="${sessionScope['cu025.filtroNroCandidato']}"/>"
                               placeholder="Ej. 1001">
                    </div>
                    <div>
                        <button class="boton" type="submit" name="accion" value="buscar">Buscar</button>
                    </div>
                </div>
            </form>
        </section>

        <div class="paneles-dobles">
            <section class="panel">
                <p class="panel__subtitulo">Paso 4 · Selección</p>
                <h2 class="panel__titulo">Resultados de búsqueda</h2>
                <form method="post" action="${pageContext.request.contextPath}/evaluar-candidatos">
                    <c:choose>
                        <c:when test="${not sessionScope['cu025.busquedaRealizada']}">
                            <p>Ingresá filtros (o dejá vacío para listar todos) y presioná <strong>Buscar</strong>.</p>
                        </c:when>
                        <c:when test="${empty sessionScope['cu025.resultados']}">
                            <p>No se encontraron candidatos para los filtros ingresados.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="tabla-scroll">
                                <table>
                                    <thead>
                                        <tr>
                                            <th class="col-check">Sel.</th>
                                            <th>Nº</th>
                                            <th>Apellido</th>
                                            <th>Nombre</th>
                                            <th>Documento</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="c" items="${sessionScope['cu025.resultados']}">
                                            <tr>
                                                <td class="col-check">
                                                    <input type="checkbox" name="nroCandidatoAgregar"
                                                           value="${c.nroCandidato}"
                                                           aria-label="Seleccionar candidato ${c.nroCandidato}">
                                                </td>
                                                <td><c:out value="${c.nroCandidato}"/></td>
                                                <td><c:out value="${c.apellido}"/></td>
                                                <td><c:out value="${c.nombre}"/></td>
                                                <td><c:out value="${c.documentoCompleto}"/></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <div class="acciones-barra acciones-barra--derecha">
                        <button class="boton" type="submit" name="accion" value="agregar">Agregar &rarr;</button>
                    </div>
                </form>
            </section>

            <section class="panel">
                <p class="panel__subtitulo">Pasos 5–6 · Lista para evaluar</p>
                <h2 class="panel__titulo">Candidatos a evaluar</h2>
                <form method="post" action="${pageContext.request.contextPath}/evaluar-candidatos">
                    <c:choose>
                        <c:when test="${empty sessionScope['cu025.candidatos']}">
                            <p>Todavía no agregaste candidatos a evaluar.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="tabla-scroll">
                                <table>
                                    <thead>
                                        <tr>
                                            <th class="col-check">Sel.</th>
                                            <th>Nº</th>
                                            <th>Apellido</th>
                                            <th>Nombre</th>
                                            <th>Documento</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="c" items="${sessionScope['cu025.candidatos']}">
                                            <tr>
                                                <td class="col-check">
                                                    <input type="checkbox" name="nroCandidatoQuitar"
                                                           value="${c.nroCandidato}"
                                                           aria-label="Quitar candidato ${c.nroCandidato}">
                                                </td>
                                                <td><c:out value="${c.nroCandidato}"/></td>
                                                <td><c:out value="${c.apellido}"/></td>
                                                <td><c:out value="${c.nombre}"/></td>
                                                <td><c:out value="${c.documentoCompleto}"/></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <div class="acciones-barra acciones-barra--derecha">
                        <button class="boton boton--secundario" type="submit" name="accion" value="quitar">&larr; Quitar</button>
                    </div>
                </form>
            </section>
        </div>

        <form method="post" action="${pageContext.request.contextPath}/evaluar-candidatos" class="acciones-barra">
            <button class="boton boton--secundario" type="submit" name="accion" value="cancelar">Cancelar</button>
            <button class="boton" type="submit" name="accion" value="siguiente">Siguiente</button>
        </form>

        <c:if test="${not empty candidatosNoHabilitados}">
            <div class="modal-fondo" role="dialog" aria-modal="true" aria-labelledby="modal-6a-titulo">
                <div class="modal-caja">
                    <h3 id="modal-6a-titulo" class="modal-caja__titulo">
                        Candidatos con cuestionario activo o en proceso
                    </h3>
                    <p>Los siguientes candidatos ya poseen un cuestionario <strong>ACTIVO</strong> o
                        <strong>EN PROCESO</strong> y no pueden ser incluidos en una nueva evaluación.
                        Quitalos de la lista para continuar.</p>
                    <div class="tabla-scroll">
                        <table>
                            <thead>
                                <tr>
                                    <th>Nº</th>
                                    <th>Apellido</th>
                                    <th>Nombre</th>
                                    <th>Documento</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="c" items="${candidatosNoHabilitados}">
                                    <tr>
                                        <td><c:out value="${c.nroCandidato}"/></td>
                                        <td><c:out value="${c.apellido}"/></td>
                                        <td><c:out value="${c.nombre}"/></td>
                                        <td><c:out value="${c.documentoCompleto}"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="acciones-barra acciones-barra--derecha">
                        <a class="boton" href="${pageContext.request.contextPath}/evaluar-candidatos">Aceptar</a>
                    </div>
                </div>
            </div>
        </c:if>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/jspf/pie.jspf" %>
