<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="titulo" value="Cuestionario de Evaluación" scope="request"/>
<%@ include file="/WEB-INF/jspf/cabecera.jspf" %>

<%-- Estado que se muestra en la insignia, deducido de lo que la pantalla tiene para mostrar --%>
<c:choose>
    <c:when test="${not empty bloque}">
        <c:set var="estado" value="EN PROCESO"/><c:set var="estadoClase" value="en-proceso"/>
    </c:when>
    <c:when test="${not empty exito}">
        <c:set var="estado" value="COMPLETO"/><c:set var="estadoClase" value="completo"/>
    </c:when>
    <c:when test="${not empty instrucciones}">
        <c:set var="estado" value="ACTIVO"/><c:set var="estadoClase" value="activo"/>
    </c:when>
    <c:when test="${fn:contains(mensaje, 'vencido')}">
        <c:set var="estado" value="SIN CONTESTAR"/><c:set var="estadoClase" value="cerrado"/>
    </c:when>
    <c:when test="${fn:contains(mensaje, 'tiempo máximo') or fn:contains(mensaje, 'máxima de accesos')}">
        <c:set var="estado" value="INCOMPLETO"/><c:set var="estadoClase" value="cerrado"/>
    </c:when>
</c:choose>

<div class="cu026">
    <c:if test="${not empty estado}">
        <div class="cu026-barra-estado">
            <span>Estado del cuestionario</span>
            <span class="cu026-insignia cu026-insignia--${estadoClase}"><c:out value="${estado}"/></span>
        </div>
    </c:if>

    <c:choose>
        <%-- Pasos 4 a 7: wizard de bloques --%>
        <c:when test="${not empty bloque}">
            <section class="panel cu026-panel">
                <div class="cu026-bloque-encabezado">
                    <h2 class="cu026-titulo">Bloque <c:out value="${bloque.nroBloque}"/></h2>
                    <span class="cu026-bloque-nota">
                        <c:choose>
                            <c:when test="${bloque.esUltimo()}">Último bloque del cuestionario</c:when>
                            <c:otherwise>Responda todas las preguntas para pasar al siguiente bloque</c:otherwise>
                        </c:choose>
                    </span>
                </div>

                <c:if test="${not empty error}">
                    <div class="alerta alerta--error" role="alert"><c:out value="${error}"/></div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/cuestionario/responder">
                    <c:forEach var="pregunta" items="${bloque.preguntas}">
                        <c:set var="idPregunta" value="${pregunta.preguntaId.toString()}"/>
                        <c:set var="faltaResponder" value="${not empty sinResponder and sinResponder.contains(idPregunta)}"/>
                        <fieldset class="cu026-pregunta ${faltaResponder ? 'cu026-pregunta--error' : ''}">
                            <c:if test="${faltaResponder}">
                                <p class="cu026-pregunta__error">Es obligatorio responder</p>
                            </c:if>
                            <legend class="cu026-pregunta__enunciado">
                                <span class="cu026-pregunta__numero"><c:out value="${pregunta.orden}"/>.</span>
                                <c:out value="${pregunta.enunciado}"/>
                            </legend>
                            <div class="cu026-opciones">
                                <c:forEach var="valor" items="${pregunta.valores}">
                                    <label class="cu026-opcion">
                                        <input type="radio" name="respuesta_${idPregunta}"
                                               value="${valor.valorOpcionId}"
                                               <c:if test="${respuestas[idPregunta] eq valor.valorOpcionId.toString()}">checked</c:if>>
                                        <span><c:out value="${valor.descripcion}"/></span>
                                    </label>
                                </c:forEach>
                            </div>
                        </fieldset>
                    </c:forEach>

                    <div class="cu026-acciones">
                        <%-- Flujo 2.A: abandona el cuestionario; queda guardado lo respondido en bloques anteriores --%>
                        <button class="boton cu026-boton-oscuro" type="submit" formnovalidate
                                formaction="${pageContext.request.contextPath}/cuestionario/cancelar">Cancelar</button>
                        <button class="boton" type="submit">
                            <c:choose>
                                <c:when test="${bloque.esUltimo()}">Finalizar</c:when>
                                <c:otherwise>Siguiente</c:otherwise>
                            </c:choose>
                        </button>
                    </div>
                </form>
            </section>
        </c:when>

        <%-- Paso 8: cuestionario completo --%>
        <c:when test="${not empty exito}">
            <div class="modal-fondo" role="dialog" aria-modal="true" aria-labelledby="cu026-titulo-modal">
                <div class="modal-caja cu026-modal cu026-modal--exito">
                    <h2 id="cu026-titulo-modal" class="cu026-modal__titulo">Cuestionario enviado</h2>
                    <div class="cu026-modal__mensaje">
                        <p><c:out value="${exito}"/></p>
                        <p>El sistema guardó su resultado y el cuestionario quedó en estado "Completo".</p>
                    </div>
                    <div class="cu026-modal__acciones">
                        <a class="boton" href="${pageContext.request.contextPath}/" autofocus>Aceptar</a>
                    </div>
                </div>
            </div>
        </c:when>

        <%-- Paso 1: instrucciones --%>
        <c:when test="${not empty instrucciones}">
            <section class="panel cu026-panel">
                <h2 class="cu026-titulo">Instrucciones del Cuestionario</h2>
                <p class="cu026-instrucciones"><c:out value="${instrucciones}"/></p>
                <p class="cu026-instrucciones-cierre">Si está listo para comenzar, presione "Aceptar".</p>
                <div class="cu026-acciones cu026-acciones--derecha">
                    <form method="post" action="${pageContext.request.contextPath}/cuestionario/cancelar">
                        <button class="boton cu026-boton-oscuro" type="submit">Cancelar</button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/cuestionario/aceptar">
                        <button class="boton" type="submit" autofocus>Aceptar</button>
                    </form>
                </div>
            </section>
        </c:when>

        <%-- Mensajes del ingreso: flujos 1.A, 1.B, cancelación y cuestionario no disponible --%>
        <c:otherwise>
            <c:choose>
                <c:when test="${fn:contains(mensaje, 'vencido') or fn:contains(mensaje, 'tiempo máximo')}">
                    <c:set var="tituloModal" value="Tiempo excedido"/><c:set var="tipoModal" value="error"/>
                </c:when>
                <c:when test="${fn:contains(mensaje, 'cancelada')}">
                    <c:set var="tituloModal" value="Operación cancelada"/><c:set var="tipoModal" value="info"/>
                </c:when>
                <c:when test="${fn:contains(mensaje, 'Ingrese con su clave')}">
                    <c:set var="tituloModal" value="Ingreso requerido"/><c:set var="tipoModal" value="info"/>
                </c:when>
                <c:otherwise>
                    <c:set var="tituloModal" value="No es posible continuar"/><c:set var="tipoModal" value="error"/>
                </c:otherwise>
            </c:choose>

            <div class="modal-fondo" role="dialog" aria-modal="true" aria-labelledby="cu026-titulo-modal">
                <div class="modal-caja cu026-modal cu026-modal--${tipoModal}">
                    <h2 id="cu026-titulo-modal" class="cu026-modal__titulo"><c:out value="${tituloModal}"/></h2>
                    <div class="cu026-modal__mensaje">
                        <p><c:out value="${mensaje}"/></p>
                        <c:if test="${estado == 'INCOMPLETO'}">
                            <p>El sistema guardó su progreso actual y el cuestionario quedó en estado "Incompleto".</p>
                        </c:if>
                        <c:if test="${estado == 'SIN CONTESTAR'}">
                            <p>El cuestionario quedó en estado "Sin contestar".</p>
                        </c:if>
                    </div>
                    <div class="cu026-modal__acciones">
                        <c:if test="${fn:contains(mensaje, 'Ingrese con su clave') or fn:contains(mensaje, 'cancelada')}">
                            <a class="boton cu026-boton-oscuro" href="${pageContext.request.contextPath}/login?tipo=CANDIDATO">Ingresar con mi clave</a>
                        </c:if>
                        <a class="boton" href="${pageContext.request.contextPath}/" autofocus>Aceptar</a>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="/WEB-INF/jspf/pie.jspf" %>
