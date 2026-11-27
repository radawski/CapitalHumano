<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jspf/cabecera.jspf" %>

<c:choose>
    <c:when test="${not empty vistaPlaceholder}">
        <section class="panel">
            <p class="panel__subtitulo">PantallaGestionarPreguntas</p>
            <h2 class="panel__titulo"><c:out value="${titulo}"/></h2>
            <p><c:out value="${vistaPlaceholder}"/></p>
            <div class="acciones-barra">
                <a class="boton" href="${pageContext.request.contextPath}/preguntas">Volver a Gestión de preguntas</a>
            </div>
        </section>
    </c:when>
    <c:otherwise>
        <section class="panel">
            <form method="get" action="${pageContext.request.contextPath}/preguntas" id="form-busqueda-preguntas">
                <input type="hidden" name="buscar" value="1">
                <div class="filtros-grid">
                    <div>
                        <label for="competencia">Competencia</label>
                        <select id="competencia" name="competencia" tabindex="1">
                            <option value="">Todas</option>
                            <c:forEach var="comp" items="${competencias}">
                                <option value="${comp.codigo}"
                                        <c:if test="${comp.codigo == competenciaSeleccionada}">selected</c:if>>
                                    <c:out value="${comp.nombre}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div>
                        <label for="factor">Factor</label>
                        <select id="factor" name="factor" tabindex="2">
                            <option value="">Todos</option>
                            <c:forEach var="fac" items="${factores}">
                                <option value="${fac.codigo}"
                                        <c:if test="${fac.codigo == factorSeleccionado}">selected</c:if>>
                                    <c:out value="${fac.nombre}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div>
                        <label for="nombre">Nombre de pregunta</label>
                        <input type="text"
                               id="nombre"
                               name="nombre"
                               maxlength="100"
                               tabindex="3"
                               class="texto-mayusculas"
                               placeholder="INGRESE EL NOMBRE DE LA PREGUNTA"
                               value="<c:out value='${nombrePregunta}'/>">
                    </div>
                </div>

                <div class="cu020-barra-acciones">
                    <div class="cu020-botones-principales">
                        <a class="boton" href="${pageContext.request.contextPath}/preguntas/nueva" tabindex="5">Nuevo</a>
                        <button type="submit" class="boton" tabindex="4">Buscar</button>
                    </div>
                    <p class="cu020-sugerencia">
                        <strong>* Sugerencia de uso:</strong> Utilice los filtros de Competencia y Factor para acotar la búsqueda.
                        Presione el botón "Nuevo" para agregar una pregunta.
                    </p>
                </div>
            </form>

            <c:if test="${not empty error}">
                <div class="alerta alerta--error cu020-alerta" role="alert">
                    <c:out value="${error}"/>
                </div>
            </c:if>
        </section>

        <c:if test="${busquedaRealizada}">
            <section class="panel">
                <p class="panel__subtitulo">Resultados de la búsqueda</p>
                <h2 class="panel__titulo">
                    Preguntas (<c:out value="${totalResultados}"/> encontradas)
                </h2>

                <c:choose>
                    <c:when test="${empty preguntas}">
                        <div class="alerta">
                            No se encontraron preguntas activas para los criterios ingresados.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <table class="cu020-grilla">
                            <thead>
                                <tr>
                                    <th>Competencia</th>
                                    <th>Factor</th>
                                    <th>Pregunta</th>
                                    <th class="cu020-col-acciones">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="preg" items="${preguntas}">
                                    <tr>
                                        <td><c:out value="${preg.factor.competencia.nombre}"/></td>
                                        <td><c:out value="${preg.factor.nombre}"/></td>
                                        <td>
                                            <strong><c:out value="${preg.nombre}"/></strong>
                                            <div class="cu020-enunciado"><c:out value="${preg.enunciado}"/></div>
                                        </td>
                                        <td class="cu020-col-acciones">
                                            <button type="button"
                                                    class="boton-icono boton-icono--deshabilitado"
                                                    disabled
                                                    title="Modificar pregunta (no disponible)"
                                                    aria-label="Modificar pregunta (deshabilitado)">
                                                &#9998; Modificar
                                            </button>
                                            <c:url var="urlEliminar" value="/preguntas/eliminar">
                                                <c:param name="id" value="${preg.id}"/>
                                                <c:param name="buscar" value="1"/>
                                                <c:param name="competencia" value="${competenciaSeleccionada}"/>
                                                <c:param name="factor" value="${factorSeleccionado}"/>
                                                <c:param name="nombre" value="${nombrePregunta}"/>
                                                <c:param name="pagina" value="${paginaActual}"/>
                                            </c:url>
                                            <a class="boton-icono boton-icono--eliminar"
                                               href="${urlEliminar}"
                                               title="Eliminar pregunta"
                                               aria-label="Eliminar pregunta">
                                                &#128465; Eliminar
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>

                        <nav class="paginador" aria-label="Paginación de preguntas">
                            <c:choose>
                                <c:when test="${paginaActual > 1}">
                                    <c:url var="urlAnterior" value="/preguntas">
                                        <c:param name="buscar" value="1"/>
                                        <c:param name="competencia" value="${competenciaSeleccionada}"/>
                                        <c:param name="factor" value="${factorSeleccionado}"/>
                                        <c:param name="nombre" value="${nombrePregunta}"/>
                                        <c:param name="pagina" value="${paginaActual - 1}"/>
                                    </c:url>
                                    <a class="paginador__item" href="${urlAnterior}" aria-label="Página anterior">&larr;</a>
                                </c:when>
                                <c:otherwise>
                                    <span class="paginador__item paginador__item--deshabilitado" aria-disabled="true">&larr;</span>
                                </c:otherwise>
                            </c:choose>

                            <c:forEach var="nro" begin="1" end="${totalPaginas}">
                                <c:choose>
                                    <c:when test="${nro == paginaActual}">
                                        <span class="paginador__item paginador__item--activa" aria-current="page">${nro}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <c:url var="urlPag" value="/preguntas">
                                            <c:param name="buscar" value="1"/>
                                            <c:param name="competencia" value="${competenciaSeleccionada}"/>
                                            <c:param name="factor" value="${factorSeleccionado}"/>
                                            <c:param name="nombre" value="${nombrePregunta}"/>
                                            <c:param name="pagina" value="${nro}"/>
                                        </c:url>
                                        <a class="paginador__item" href="${urlPag}">${nro}</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>

                            <c:choose>
                                <c:when test="${paginaActual < totalPaginas}">
                                    <c:url var="urlSiguiente" value="/preguntas">
                                        <c:param name="buscar" value="1"/>
                                        <c:param name="competencia" value="${competenciaSeleccionada}"/>
                                        <c:param name="factor" value="${factorSeleccionado}"/>
                                        <c:param name="nombre" value="${nombrePregunta}"/>
                                        <c:param name="pagina" value="${paginaActual + 1}"/>
                                    </c:url>
                                    <a class="paginador__item" href="${urlSiguiente}" aria-label="Página siguiente">&rarr;</a>
                                </c:when>
                                <c:otherwise>
                                    <span class="paginador__item paginador__item--deshabilitado" aria-disabled="true">&rarr;</span>
                                </c:otherwise>
                            </c:choose>
                        </nav>
                    </c:otherwise>
                </c:choose>
            </section>
        </c:if>

        <div class="acciones-barra acciones-barra--derecha">
            <a class="boton" href="${pageContext.request.contextPath}/">Salir</a>
        </div>

        <%-- CU023 - Paso 1 a 2: Modal de confirmación de baja --%>
        <c:if test="${not empty preguntaAEliminar}">
            <div class="modal-fondo" role="dialog" aria-modal="true" aria-labelledby="titulo-confirmar-baja">
                <div class="modal-caja modal-caja--cu023">
                    <h2 id="titulo-confirmar-baja" class="cu023-modal__titulo">Eliminar pregunta</h2>
                    <p class="cu023-modal__mensaje">
                        Los datos de la Pregunta
                        <strong><c:out value="${preguntaAEliminar.nombre}"/></strong>
                        serán eliminados del sistema
                    </p>
                    <form method="post" action="${pageContext.request.contextPath}/preguntas/eliminar" class="cu023-modal__acciones">
                        <input type="hidden" name="id" value="<c:out value='${preguntaAEliminar.id}'/>">
                        <input type="hidden" name="nombrePregunta" value="<c:out value='${preguntaAEliminar.nombre}'/>">
                        <c:if test="${busquedaRealizada}">
                            <input type="hidden" name="buscar" value="1">
                            <input type="hidden" name="competencia" value="<c:out value='${competenciaSeleccionada}'/>">
                            <input type="hidden" name="factor" value="<c:out value='${factorSeleccionado}'/>">
                            <input type="hidden" name="nombre" value="<c:out value='${nombrePregunta}'/>">
                            <input type="hidden" name="pagina" value="<c:out value='${paginaActual}'/>">
                        </c:if>

                        <c:url var="urlCancelarBaja" value="/preguntas">
                            <c:if test="${busquedaRealizada}">
                                <c:param name="buscar" value="1"/>
                                <c:param name="competencia" value="${competenciaSeleccionada}"/>
                                <c:param name="factor" value="${factorSeleccionado}"/>
                                <c:param name="nombre" value="${nombrePregunta}"/>
                                <c:param name="pagina" value="${paginaActual}"/>
                            </c:if>
                        </c:url>
                        <a class="boton boton--secundario" href="${urlCancelarBaja}" tabindex="1">CANCELAR</a>
                        <button type="submit" class="boton" tabindex="2">ELIMINAR</button>
                    </form>
                </div>
            </div>
        </c:if>

        <%-- CU023 - Paso 3 a 5: Modal de éxito --%>
        <c:if test="${not empty preguntaEliminada}">
            <div class="modal-fondo" id="modal-exito-baja" role="dialog" aria-modal="true" aria-labelledby="titulo-exito-baja">
                <div class="modal-caja modal-caja--cu023">
                    <h2 id="titulo-exito-baja" class="cu023-modal__titulo">Eliminar pregunta</h2>
                    <p class="cu023-modal__mensaje">
                        Los datos de la Pregunta
                        <strong><c:out value="${preguntaEliminada}"/></strong>
                        han sido eliminados del sistema
                    </p>
                    <p class="cu023-modal__nota">Presione cualquier tecla o Aceptar para continuar.</p>
                    <div class="cu023-modal__acciones cu023-modal__acciones--centro">
                        <button type="button" class="boton" id="btn-cerrar-exito-baja">Aceptar</button>
                    </div>
                </div>
            </div>
        </c:if>

        <script>
            (function () {
                const inputNombre = document.getElementById('nombre');
                const selectCompetencia = document.getElementById('competencia');
                const selectFactor = document.getElementById('factor');
                const urlBase = '${pageContext.request.contextPath}/preguntas';

                if (inputNombre) {
                    inputNombre.addEventListener('input', function () {
                        const pos = this.selectionStart;
                        this.value = this.value.toUpperCase();
                        if (pos !== null) {
                            this.setSelectionRange(pos, pos);
                        }
                    });
                }

                if (selectCompetencia && selectFactor) {
                    selectCompetencia.addEventListener('change', async function () {
                        const codCompetencia = this.value;
                        selectFactor.innerHTML = '<option value="">Todos</option>';
                        if (!codCompetencia) {
                            return;
                        }
                        try {
                            const resp = await fetch(urlBase + '?accion=factores&competencia=' + encodeURIComponent(codCompetencia));
                            if (!resp.ok) {
                                return;
                            }
                            const factores = await resp.json();
                            for (const fac of factores) {
                                const opt = document.createElement('option');
                                opt.value = fac.codigo;
                                opt.textContent = fac.nombre;
                                selectFactor.appendChild(opt);
                            }
                        } catch (e) {
                            // Si falla el fetch, el combo queda en "Todos" sin bloquear la búsqueda
                        }
                    });
                }

                const modalExito = document.getElementById('modal-exito-baja');
                if (modalExito) {
                    const cerrarModalExito = function () {
                        modalExito.remove();
                        document.removeEventListener('keydown', cerrarModalExito);
                    };
                    const btnAceptar = document.getElementById('btn-cerrar-exito-baja');
                    if (btnAceptar) {
                        btnAceptar.addEventListener('click', cerrarModalExito);
                        btnAceptar.focus();
                    }
                    modalExito.addEventListener('click', function (ev) {
                        if (ev.target === modalExito) {
                            cerrarModalExito();
                        }
                    });
                    document.addEventListener('keydown', cerrarModalExito);
                }
            })();
        </script>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/jspf/pie.jspf" %>
