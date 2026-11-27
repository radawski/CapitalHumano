<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jspf/cabecera.jspf" %>

<c:choose>
    <c:when test="${paso == 'puesto'}">
        <c:if test="${not empty error}">
            <div class="alerta alerta--error" role="alert">
                <c:out value="${error}"/>
            </div>
        </c:if>

        <section class="panel">
            <h2 class="panel__titulo">Selección del puesto vacante</h2>
            <p>Candidatos habilitados a evaluar:
                <strong><c:out value="${sessionScope['cu025.candidatos'].size()}"/></strong>.</p>

            <form method="post" action="${pageContext.request.contextPath}/evaluar-candidatos">
                <c:choose>
                    <c:when test="${empty puestos}">
                        <p>No hay puestos activos disponibles.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="tabla-scroll">
                            <table>
                                <thead>
                                    <tr>
                                        <th class="col-check">Sel.</th>
                                        <th>Función</th>
                                        <th>Empresa</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="p" items="${puestos}">
                                        <tr>
                                            <td class="col-check">
                                                <input type="radio" name="codigoPuesto"
                                                       value="<c:out value='${p.codigo}'/>"
                                                       aria-label="Seleccionar función ${p.nombre}">
                                            </td>
                                            <td><c:out value="${p.nombre}"/></td>
                                            <td><c:out value="${p.empresa}"/></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>

                <div class="acciones-barra">
                    <div class="botones-par">
                        <a class="boton boton--secundario" href="${pageContext.request.contextPath}/evaluar-candidatos">Volver a candidatos</a>
                        <button class="boton boton--secundario" type="submit" name="accion" value="cancelar">Cancelar</button>
                    </div>
                    <c:if test="${not empty puestos}">
                        <button class="boton" type="submit" name="accion" value="seleccionarPuesto">Siguiente</button>
                    </c:if>
                </div>
            </form>
        </section>
    </c:when>

    <c:when test="${paso == 'competencias'}">
        <c:set var="datosPuesto" value="${sessionScope['cu025.datosPuesto']}"/>
        <c:choose>
            <c:when test="${not empty datosPuesto.competenciasNoEvaluables}">
                <section class="panel">
                    <h2 class="panel__titulo">No es posible evaluar el puesto seleccionado</h2>
                    <div class="alerta alerta--error" role="alert">
                        La función <strong><c:out value="${datosPuesto.puesto.nombre}"/></strong>
                        (<c:out value="${datosPuesto.puesto.empresa}"/>) posee competencias que no tienen
                        al menos un factor con 2 o más preguntas activas:
                    </div>
                    <div class="tabla-scroll">
                        <table>
                            <thead>
                                <tr>
                                    <th>Código</th>
                                    <th>Competencia no evaluable</th>
                                    <th>Tipo</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="comp" items="${datosPuesto.competenciasNoEvaluables}">
                                    <tr>
                                        <td><c:out value="${comp.codigo}"/></td>
                                        <td><c:out value="${comp.nombre}"/></td>
                                        <td><c:out value="${comp.tipo}"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <form method="post" action="${pageContext.request.contextPath}/evaluar-candidatos"
                          class="acciones-barra acciones-barra--derecha">
                        <button class="boton" type="submit" name="accion" value="cancelar">Aceptar</button>
                    </form>
                </section>
            </c:when>

            <c:otherwise>
                <section class="panel">
                    <h2 class="panel__titulo">
                        Competencias evaluables — <c:out value="${datosPuesto.puesto.nombre}"/>
                        (<c:out value="${datosPuesto.puesto.empresa}"/>)
                    </h2>
                    <p>Candidatos seleccionados:
                        <strong><c:out value="${sessionScope['cu025.candidatos'].size()}"/></strong>.
                        Verificá las competencias requeridas y su ponderación mínima antes de generar las claves.</p>

                    <div class="tabla-scroll">
                        <table>
                            <thead>
                                <tr>
                                    <th>Código</th>
                                    <th>Competencia</th>
                                    <th>Tipo</th>
                                    <th>Ponderación mínima</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="entry" items="${datosPuesto.ponderacionesRequeridas}">
                                    <tr>
                                        <td><c:out value="${entry.key.codigo}"/></td>
                                        <td><c:out value="${entry.key.nombre}"/></td>
                                        <td><c:out value="${entry.key.tipo}"/></td>
                                        <td><c:out value="${entry.value}"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <form method="post" action="${pageContext.request.contextPath}/evaluar-candidatos" class="acciones-barra">
                        <div class="botones-par">
                            <a class="boton boton--secundario" href="${pageContext.request.contextPath}/evaluar-candidatos/puesto">Cambiar puesto</a>
                            <button class="boton boton--secundario" type="submit" name="accion" value="cancelar">Cancelar</button>
                        </div>
                        <button class="boton" type="submit" name="accion" value="generarEvaluacion">Siguiente</button>
                    </form>
                </section>
            </c:otherwise>
        </c:choose>
    </c:when>

    <c:when test="${paso == 'claves'}">
        <c:set var="evalActual" value="${sessionScope['cu025.evaluacion']}"/>
        <section class="panel">
            <h2 class="panel__titulo">
                Claves de ingreso generadas — <c:out value="${evalActual.puesto.nombre}"/>
            </h2>
            <p>Las claves fueron generadas en estado <strong>ACTIVO</strong>. Presioná <strong>Finalizar</strong>
                para confirmar y guardar la evaluación, o <strong>Cancelar</strong> para descartarla.</p>

            <div class="tabla-scroll">
                <table>
                    <thead>
                        <tr>
                            <th>Apellido</th>
                            <th>Nombre</th>
                            <th>Tipo Doc.</th>
                            <th>Nro. Documento</th>
                            <th>Clave de ingreso</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${clavesCandidatos}">
                            <tr>
                                <td><c:out value="${item.apellido}"/></td>
                                <td><c:out value="${item.nombre}"/></td>
                                <td><c:out value="${item.tipoDocumento}"/></td>
                                <td><c:out value="${item.nroDocumento}"/></td>
                                <td class="clave-codigo"><c:out value="${item.clave}"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <form method="post" action="${pageContext.request.contextPath}/evaluar-candidatos" class="acciones-barra">
                <button class="boton boton--secundario" type="submit" name="accion" value="cancelar">Cancelar</button>
                <button class="boton" type="submit" name="accion" value="finalizar">Finalizar</button>
            </form>
        </section>
    </c:when>

    <c:when test="${paso == 'exportar'}">
        <c:set var="evalGuardada" value="${sessionScope['cu025.evaluacionGuardada']}"/>
        <section class="panel">
            <h2 class="panel__titulo">Evaluación guardada correctamente</h2>
            <div class="alerta alerta--exito">
                La evaluación para <strong><c:out value="${evalGuardada.puesto.nombre}"/></strong>
                quedó registrada con <strong><c:out value="${evalGuardada.cuestionarios.size()}"/></strong>
                cuestionario(s) en estado <strong>ACTIVO</strong>.
            </div>

            <c:choose>
                <c:when test="${confirmarExportar}">
                    <p>Confirmá la descarga del archivo Excel (<code>aaaa-mm-dd-hh-mm-NombreFunción.xls</code>);
                        el navegador te permitirá elegir dónde guardarlo.</p>
                    <form method="post" action="${pageContext.request.contextPath}/evaluar-candidatos" class="acciones-barra">
                        <div class="botones-par">
                            <a class="boton boton--secundario" href="${pageContext.request.contextPath}/evaluar-candidatos/exportar">Cancelar</a>
                            <button class="boton boton--secundario" type="submit" name="accion" value="cancelar">Salir al menú</button>
                        </div>
                        <button class="boton" type="submit" name="accion" value="descargarExcel">Guardar .xls</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <p>¿Deseás exportar las claves generadas a un archivo Excel (<code>.xls</code>)?</p>
                    <form method="post" action="${pageContext.request.contextPath}/evaluar-candidatos" class="acciones-barra">
                        <button class="boton boton--secundario" type="submit" name="accion" value="cancelar">No exportar (Finalizar)</button>
                        <button class="boton" type="submit" name="accion" value="solicitarExportar">Exportar a Excel (.xls)</button>
                    </form>
                </c:otherwise>
            </c:choose>
        </section>
    </c:when>

    <c:otherwise>
        <c:if test="${not empty error}">
            <div class="alerta alerta--error" role="alert">
                <c:out value="${error}"/>
            </div>
        </c:if>

        <section class="panel">
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
