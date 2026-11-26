<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jspf/cabecera.jspf" %>

<section class="panel">
    <form method="post" action="${pageContext.request.contextPath}/preguntas/nueva" id="form-alta-pregunta" novalidate>
        <div class="filtros-grid">
            <div class="<c:if test='${camposConError.contains("competencia")}'>campo--error</c:if>">
                <label for="competencia">Competencia <span class="error">*</span></label>
                <select id="competencia" name="competencia" tabindex="1">
                    <option value="">Seleccionar Competencia</option>
                    <c:forEach var="comp" items="${competencias}">
                        <option value="${comp.codigo}"
                                <c:if test="${comp.codigo == competenciaSeleccionada}">selected</c:if>>
                            <c:out value="${comp.nombre}"/>
                        </option>
                    </c:forEach>
                </select>
                <c:if test="${camposConError.contains('competencia')}">
                    <span class="error cu021-error-campo">Obligatorio</span>
                </c:if>
            </div>

            <div class="<c:if test='${camposConError.contains("factor")}'>campo--error</c:if>">
                <label for="factor">Factor <span class="error">*</span></label>
                <select id="factor" name="factor" tabindex="2">
                    <option value="">Seleccionar Factor</option>
                    <c:forEach var="fac" items="${factores}">
                        <option value="${fac.codigo}"
                                <c:if test="${fac.codigo == factorSeleccionado}">selected</c:if>>
                            <c:out value="${fac.nombre}"/>
                        </option>
                    </c:forEach>
                </select>
                <c:if test="${camposConError.contains('factor')}">
                    <span class="error cu021-error-campo">Obligatorio</span>
                </c:if>
            </div>

            <div class="<c:if test='${camposConError.contains("nombre")}'>campo--error</c:if>">
                <label for="nombre">Nombre de Pregunta <span class="error">*</span></label>
                <input type="text"
                       id="nombre"
                       name="nombre"
                       tabindex="3"
                       class="texto-mayusculas"
                       placeholder="INGRESE NOMBRE"
                       value="<c:out value='${nombrePregunta}'/>">
                <div class="cu021-meta-campo">
                    <span><c:if test="${camposConError.contains('nombre')}"><span class="error">Revise este campo</span></c:if></span>
                    <span class="cu021-contador" data-contador="nombre" data-max="100">0 / 100</span>
                </div>
            </div>
        </div>

        <div class="cu021-cuerpo">
            <div class="cu021-columna">
                <div class="cu021-grupo <c:if test='${camposConError.contains("enunciado")}'>campo--error</c:if>">
                    <label for="enunciado">Pregunta <span class="error">*</span></label>
                    <textarea id="enunciado"
                              name="enunciado"
                              rows="4"
                              tabindex="4"
                              class="texto-mayusculas"
                              placeholder="INGRESAR PREGUNTA"><c:out value="${enunciadoPregunta}"/></textarea>
                    <div class="cu021-meta-campo">
                        <span><c:if test="${camposConError.contains('enunciado')}"><span class="error">Revise este campo</span></c:if></span>
                        <span class="cu021-contador" data-contador="enunciado" data-max="500">0 / 500</span>
                    </div>
                </div>

                <div class="cu021-ia-fila">
                    <button type="button" id="btn-generar-ia" class="boton-ia" tabindex="5">
                        Generar con IA &#9734;
                    </button>
                    <span id="ia-estado" class="cu021-ia-estado" aria-live="polite"></span>
                </div>

                <div class="cu021-grupo <c:if test='${camposConError.contains("descripcion")}'>campo--error</c:if>">
                    <label for="descripcion">Descripción</label>
                    <textarea id="descripcion"
                              name="descripcion"
                              rows="3"
                              tabindex="6"
                              class="texto-mayusculas"
                              placeholder="INGRESE LA DESCRIPCIÓN (OPCIONAL)"><c:out value="${descripcionPregunta}"/></textarea>
                    <div class="cu021-meta-campo">
                        <span><c:if test="${camposConError.contains('descripcion')}"><span class="error">Supera 500 caracteres</span></c:if></span>
                        <span class="cu021-contador" data-contador="descripcion" data-max="500">0 / 500</span>
                    </div>
                </div>
            </div>

            <div class="cu021-columna">
                <div class="cu021-grupo <c:if test='${camposConError.contains("opcion")}'>campo--error</c:if>">
                    <label for="opcion">Opción de Respuesta <span class="error">*</span></label>
                    <select id="opcion" name="opcion" tabindex="7">
                        <option value="">Seleccione Opción de Respuesta</option>
                        <c:forEach var="op" items="${opciones}">
                            <option value="${op.codigo}"
                                    <c:if test="${op.codigo == opcionSeleccionada}">selected</c:if>>
                                <c:out value="${op.nombre}"/>
                            </option>
                        </c:forEach>
                    </select>
                    <c:if test="${camposConError.contains('opcion')}">
                        <span class="error cu021-error-campo">Obligatorio</span>
                    </c:if>
                </div>

                <div id="contenedor-valores" class="cu021-valores <c:if test='${empty valoresOpcion}'>cu021-oculto</c:if>">
                    <table>
                        <thead>
                            <tr>
                                <th class="cu021-col-orden">Orden</th>
                                <th>DESCRIPCIÓN</th>
                                <th class="cu021-col-pond">PONDERACIÓN</th>
                            </tr>
                        </thead>
                        <tbody id="tbody-valores">
                            <c:forEach var="val" items="${valoresOpcion}">
                                <c:set var="clavePond" value="${val.id}"/>
                                <c:set var="claveErr" value="ponderacion_${val.id}"/>
                                <tr>
                                    <td class="cu021-col-orden"><c:out value="${val.orden}"/></td>
                                    <td><c:out value="${val.descripcion}"/></td>
                                    <td class="cu021-col-pond <c:if test='${camposConError.contains(claveErr)}'>campo--error</c:if>">
                                        <input type="number"
                                               name="ponderacion_${val.id}"
                                               class="cu021-input-pond"
                                               min="0"
                                               max="10"
                                               step="0.5"
                                               tabindex="8"
                                               value="<c:out value='${ponderacionesIngresadas[clavePond]}'/>">
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <div class="cu021-suma-ponderaciones">
                        Suma actual: <strong id="suma-ponderaciones">0</strong> / 10 puntos
                        <span class="cu021-suma-nota">(todas &ge; 0 y deben sumar exactamente 10)</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="cu021-pie-form">
            <div class="cu021-banner-zona">
                <c:if test="${not empty erroresFormulario}">
                    <div class="alerta alerta--error cu021-banner-error" role="alert">
                        <strong>&#9888; <c:out value="${bannerTitulo}"/></strong>
                        <ul class="cu021-lista-errores">
                            <c:forEach var="err" items="${erroresFormulario}">
                                <li><c:out value="${err}"/></li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>
            </div>
            <div class="cu021-botones-form">
                <a class="boton boton--secundario" href="${pageContext.request.contextPath}/preguntas" tabindex="9">Cancelar</a>
                <button type="submit" class="boton" tabindex="10">Aceptar</button>
            </div>
        </div>
    </form>
</section>

<%-- CU021 Flujo 2.C: Modal de error de negocio (ponderaciones o duplicados) --%>
<c:if test="${not empty erroresNegocio}">
    <div class="modal-fondo" id="modal-error-negocio" role="dialog" aria-modal="true" aria-labelledby="titulo-error-negocio">
        <div class="modal-caja modal-caja--cu023">
            <h2 id="titulo-error-negocio" class="cu023-modal__titulo">Validación de alta de pregunta</h2>
            <div class="alerta alerta--error cu021-modal-lista">
                <c:forEach var="err" items="${erroresNegocio}">
                    <p><c:out value="${err}"/></p>
                </c:forEach>
            </div>
            <div class="cu023-modal__acciones cu023-modal__acciones--centro">
                <button type="button" class="boton" id="btn-cerrar-error-negocio">Aceptar</button>
            </div>
        </div>
    </div>
</c:if>

<%-- CU021 Pasos 3 a 5: Modal de éxito "¿Desea cargar otra pregunta?" --%>
<c:if test="${not empty preguntaCreada}">
    <div class="modal-fondo" id="modal-exito-alta" role="dialog" aria-modal="true" aria-labelledby="titulo-exito-alta">
        <div class="modal-caja modal-caja--cu023">
            <h2 id="titulo-exito-alta" class="cu023-modal__titulo">Alta de preguntas</h2>
            <p class="cu023-modal__mensaje">
                La pregunta <strong><c:out value="${preguntaCreada}"/></strong> se ha cargado correctamente.
                <br>¿Desea cargar otra pregunta?
            </p>
            <div class="cu023-modal__acciones">
                <a class="boton boton--secundario" href="${pageContext.request.contextPath}/preguntas" tabindex="1">NO</a>
                <a class="boton" href="${pageContext.request.contextPath}/preguntas/nueva" tabindex="2">SÍ</a>
            </div>
        </div>
    </div>
</c:if>

<script>
    (function () {
        const urlBase = '${pageContext.request.contextPath}/preguntas';
        const selectCompetencia = document.getElementById('competencia');
        const selectFactor = document.getElementById('factor');
        const selectOpcion = document.getElementById('opcion');
        const contenedorValores = document.getElementById('contenedor-valores');
        const tbodyValores = document.getElementById('tbody-valores');
        const sumaEl = document.getElementById('suma-ponderaciones');
        const btnIA = document.getElementById('btn-generar-ia');
        const iaEstado = document.getElementById('ia-estado');

        const inputNombre = document.getElementById('nombre');
        const inputEnunciado = document.getElementById('enunciado');
        const inputDescripcion = document.getElementById('descripcion');

        function actualizarContador(el) {
            if (!el) return;
            const span = document.querySelector('[data-contador="' + el.id + '"]');
            if (!span) return;
            const max = parseInt(span.getAttribute('data-max'), 10);
            const len = el.value.length;
            span.textContent = len + ' / ' + max;
            span.classList.toggle('error', len > max);
        }

        [inputNombre, inputEnunciado, inputDescripcion].forEach(function (el) {
            if (!el) return;
            actualizarContador(el);
            el.addEventListener('input', function () {
                const pos = this.selectionStart;
                this.value = this.value.toUpperCase();
                if (pos !== null) {
                    this.setSelectionRange(pos, pos);
                }
                actualizarContador(this);
            });
        });

        function actualizarSuma() {
            if (!sumaEl || !tbodyValores) return;
            let total = 0;
            const inputs = tbodyValores.querySelectorAll('.cu021-input-pond');
            inputs.forEach(function (inp) {
                const v = parseFloat(inp.value.replace(',', '.'));
                if (!isNaN(v)) {
                    total += v;
                }
            });
            const redondeado = Math.round(total * 100) / 100;
            sumaEl.textContent = redondeado;
            sumaEl.classList.toggle('error', Math.abs(redondeado - 10) > 1e-6);
        }

        if (tbodyValores) {
            tbodyValores.addEventListener('input', actualizarSuma);
            actualizarSuma();
        }

        if (selectCompetencia && selectFactor) {
            selectCompetencia.addEventListener('change', async function () {
                const cod = this.value;
                selectFactor.innerHTML = '<option value="">Seleccionar Factor</option>';
                if (!cod) return;
                try {
                    const resp = await fetch(urlBase + '?accion=factores&competencia=' + encodeURIComponent(cod));
                    if (!resp.ok) return;
                    const factores = await resp.json();
                    for (const fac of factores) {
                        const opt = document.createElement('option');
                        opt.value = fac.codigo;
                        opt.textContent = fac.nombre;
                        selectFactor.appendChild(opt);
                    }
                } catch (e) {
                    // sin bloqueo
                }
            });
        }

        if (selectOpcion && contenedorValores && tbodyValores) {
            selectOpcion.addEventListener('change', async function () {
                const cod = this.value;
                tbodyValores.innerHTML = '';
                if (!cod) {
                    contenedorValores.classList.add('cu021-oculto');
                    actualizarSuma();
                    return;
                }
                try {
                    const resp = await fetch(urlBase + '?accion=valores&opcion=' + encodeURIComponent(cod));
                    if (!resp.ok) return;
                    const valores = await resp.json();
                    for (const val of valores) {
                        const tr = document.createElement('tr');

                        const tdOrden = document.createElement('td');
                        tdOrden.className = 'cu021-col-orden';
                        tdOrden.textContent = val.orden;

                        const tdDesc = document.createElement('td');
                        tdDesc.textContent = val.descripcion;

                        const tdPond = document.createElement('td');
                        tdPond.className = 'cu021-col-pond';
                        const inp = document.createElement('input');
                        inp.type = 'number';
                        inp.name = 'ponderacion_' + val.id;
                        inp.className = 'cu021-input-pond';
                        inp.min = '0';
                        inp.max = '10';
                        inp.step = '0.5';
                        inp.tabIndex = 8;
                        tdPond.appendChild(inp);

                        tr.appendChild(tdOrden);
                        tr.appendChild(tdDesc);
                        tr.appendChild(tdPond);
                        tbodyValores.appendChild(tr);
                    }
                    contenedorValores.classList.remove('cu021-oculto');
                    actualizarSuma();
                } catch (e) {
                    // sin bloqueo
                }
            });
        }

        if (btnIA) {
            btnIA.addEventListener('click', async function () {
                const codComp = selectCompetencia ? selectCompetencia.value : '';
                const codFac = selectFactor ? selectFactor.value : '';
                iaEstado.textContent = '';
                iaEstado.classList.remove('error');

                if (!codComp || !codFac) {
                    iaEstado.textContent = 'Seleccione primero una Competencia y un Factor.';
                    iaEstado.classList.add('error');
                    return;
                }

                btnIA.disabled = true;
                iaEstado.textContent = 'Generando sugerencia...';
                try {
                    const resp = await fetch(urlBase + '?accion=generar-ia&competencia='
                            + encodeURIComponent(codComp) + '&factor=' + encodeURIComponent(codFac));
                    const data = await resp.json();
                    if (!resp.ok || data.error) {
                        iaEstado.textContent = data.error || 'No se pudo generar la pregunta.';
                        iaEstado.classList.add('error');
                        return;
                    }
                    if (inputNombre) {
                        inputNombre.value = (data.nombre || '').toUpperCase();
                        actualizarContador(inputNombre);
                    }
                    if (inputEnunciado) {
                        inputEnunciado.value = (data.enunciado || '').toUpperCase();
                        actualizarContador(inputEnunciado);
                    }
                    if (inputDescripcion) {
                        inputDescripcion.value = (data.descripcion || '').toUpperCase();
                        actualizarContador(inputDescripcion);
                    }
                    iaEstado.textContent = 'Sugerencia precargada. Revise los datos antes de Aceptar.';
                } catch (e) {
                    iaEstado.textContent = 'Error de conexión al generar con IA.';
                    iaEstado.classList.add('error');
                } finally {
                    btnIA.disabled = false;
                }
            });
        }

        const modalError = document.getElementById('modal-error-negocio');
        const btnCerrarError = document.getElementById('btn-cerrar-error-negocio');
        if (modalError && btnCerrarError) {
            btnCerrarError.focus();
            btnCerrarError.addEventListener('click', function () {
                modalError.remove();
            });
        }
    })();
</script>

<%@ include file="/WEB-INF/jspf/pie.jspf" %>
