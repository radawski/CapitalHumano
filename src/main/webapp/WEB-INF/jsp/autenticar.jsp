<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jspf/cabecera.jspf" %>

<div class="cu001">
<c:choose>
    <c:when test="${tipoAcceso == 'CONSULTOR'}">
        <%-- Pasos 2 a 5: ingreso del consultor --%>
        <section class="panel cu001-tarjeta">
            <div class="cu001-encabezado">
                <span class="cu001-icono" aria-hidden="true">&#128188;</span>
                <div>
                    <p class="panel__subtitulo">Ingresar como usuario consultor</p>
                    <h2 class="cu001-titulo">Ingreso del consultor</h2>
                </div>
            </div>

            <c:if test="${not empty error}">
                <div class="alerta alerta--error" role="alert">
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/login" autocomplete="off">
                <input type="hidden" name="tipo" value="CONSULTOR">
                <div class="cu001-campos">
                    <div>
                        <label for="usuario">Usuario <span class="cu001-obligatorio">*</span></label>
                        <input id="usuario" name="usuario" type="text" maxlength="15" required autofocus
                               placeholder="Ingrese su usuario" tabindex="1"
                               style="text-transform: uppercase"
                               oninput="this.value = this.value.toUpperCase()">
                    </div>
                    <div>
                        <label for="password">Contraseña <span class="cu001-obligatorio">*</span></label>
                        <input id="password" name="password" type="password" maxlength="20" required
                               placeholder="Ingrese su contraseña" tabindex="2">
                    </div>
                </div>
                <p class="cu001-nota">* Campos obligatorios</p>
                <div class="cu001-acciones">
                    <a class="boton cu001-boton-oscuro" href="${pageContext.request.contextPath}/login" tabindex="4">Volver</a>
                    <button class="boton" type="submit" tabindex="3">Ingresar</button>
                </div>
            </form>
        </section>
    </c:when>

    <c:when test="${tipoAcceso == 'CANDIDATO'}">
        <%-- Pasos 3.A.1 a 3.A.3: ingreso del candidato --%>
        <section class="panel cu001-tarjeta">
            <div class="cu001-encabezado">
                <span class="cu001-icono" aria-hidden="true">&#128221;</span>
                <div>
                    <p class="panel__subtitulo">Realizar cuestionario</p>
                    <h2 class="cu001-titulo">Ingreso del candidato</h2>
                </div>
            </div>

            <c:if test="${not empty error}">
                <div class="alerta alerta--error" role="alert">
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/login" autocomplete="off">
                <input type="hidden" name="tipo" value="CANDIDATO">
                <div class="cu001-campos cu001-campos--documento">
                    <div>
                        <label for="tipoDocumento">Tipo de documento <span class="cu001-obligatorio">*</span></label>
                        <select id="tipoDocumento" name="tipoDocumento" required tabindex="1">
                            <c:forEach var="t" items="${tiposDocumento}">
                                <option value="${t}" <c:if test="${t == tipoDocumento}">selected</c:if>>${t}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label for="nroDocumento">Número de documento <span class="cu001-obligatorio">*</span></label>
                        <input id="nroDocumento" name="nroDocumento" type="text" maxlength="20" required autofocus
                               placeholder="Ingrese su número de documento" tabindex="2"
                               value="<c:out value='${nroDocumento}'/>">
                    </div>
                </div>
                <div class="cu001-campos">
                    <div>
                        <label for="clave">Clave de ingreso <span class="cu001-obligatorio">*</span></label>
                        <input id="clave" name="clave" type="password" maxlength="20" required
                               placeholder="Ingrese su clave" tabindex="3"
                               style="text-transform: uppercase"
                               oninput="this.value = this.value.toUpperCase()">
                    </div>
                </div>
                <p class="cu001-nota">* Campos obligatorios</p>
                <div class="cu001-acciones">
                    <a class="boton cu001-boton-oscuro" href="${pageContext.request.contextPath}/login" tabindex="5">Volver</a>
                    <button class="boton" type="submit" tabindex="4">Ingresar</button>
                </div>
            </form>
        </section>
    </c:when>

    <c:otherwise>
        <%-- Paso 1: selección del tipo de acceso --%>
        <section class="panel cu001-tarjeta cu001-tarjeta--ancha">
            <p class="panel__subtitulo">Bienvenido a Capit@l Humano</p>
            <h2 class="cu001-titulo">Seleccione el tipo de acceso</h2>

            <div class="cu001-opciones">
                <a class="cu001-opcion" href="${pageContext.request.contextPath}/login?tipo=CONSULTOR">
                    <span class="cu001-icono" aria-hidden="true">&#128188;</span>
                    <span class="cu001-opcion__titulo">Ingresar como usuario consultor</span>
                    <span class="cu001-opcion__texto">Gestione preguntas, evalúe candidatos y emita reportes.</span>
                    <span class="boton">Ingresar</span>
                </a>
                <a class="cu001-opcion" href="${pageContext.request.contextPath}/login?tipo=CANDIDATO">
                    <span class="cu001-icono" aria-hidden="true">&#128221;</span>
                    <span class="cu001-opcion__titulo">Realizar cuestionario</span>
                    <span class="cu001-opcion__texto">Ingrese con su documento y la clave que le asignó la consultora.</span>
                    <span class="boton">Comenzar</span>
                </a>
            </div>

            <div class="cu001-acciones">
                <a class="boton cu001-boton-oscuro" href="${pageContext.request.contextPath}/">Salir</a>
            </div>
        </section>
    </c:otherwise>
</c:choose>
</div>

<%@ include file="/WEB-INF/jspf/pie.jspf" %>
