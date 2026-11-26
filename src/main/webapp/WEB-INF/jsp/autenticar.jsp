<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jspf/cabecera.jspf" %>

<c:if test="${not empty error}">
    <div class="alerta alerta--error" role="alert">
        <c:out value="${error}"/>
    </div>
</c:if>

<c:choose>
    <c:when test="${tipoAcceso == 'CONSULTOR'}">
        <section class="panel">
            <p class="panel__subtitulo">CU001 · Ingresar como usuario consultor</p>
            <h2 class="panel__titulo">Ingreso del consultor</h2>
            <form method="post" action="${pageContext.request.contextPath}/login" autocomplete="off">
                <input type="hidden" name="tipo" value="CONSULTOR">
                <div class="filtros-grid">
                    <div>
                        <label for="usuario">Usuario</label>
                        <input id="usuario" name="usuario" type="text" maxlength="15" required autofocus
                               style="text-transform: uppercase"
                               oninput="this.value = this.value.toUpperCase()">
                    </div>
                    <div>
                        <label for="password">Contraseña</label>
                        <input id="password" name="password" type="password" maxlength="20" required>
                    </div>
                </div>
                <div class="acciones-barra">
                    <div>
                        <a class="boton boton--secundario" href="${pageContext.request.contextPath}/login">Cambiar tipo de acceso</a>
                        <a class="boton boton--secundario" href="${pageContext.request.contextPath}/">Salir</a>
                    </div>
                    <button class="boton" type="submit">Ingresar</button>
                </div>
            </form>
        </section>
    </c:when>

    <c:when test="${tipoAcceso == 'CANDIDATO'}">
        <section class="panel">
            <p class="panel__subtitulo">CU001 · Realizar cuestionario</p>
            <h2 class="panel__titulo">Ingreso del candidato</h2>
            <form method="post" action="${pageContext.request.contextPath}/login" autocomplete="off">
                <input type="hidden" name="tipo" value="CANDIDATO">
                <div class="filtros-grid">
                    <div>
                        <label for="tipoDocumento">Tipo de documento</label>
                        <select id="tipoDocumento" name="tipoDocumento" required>
                            <c:forEach var="t" items="${tiposDocumento}">
                                <option value="${t}" <c:if test="${t == tipoDocumento}">selected</c:if>>${t}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label for="nroDocumento">Número de documento</label>
                        <input id="nroDocumento" name="nroDocumento" type="text" maxlength="20" required autofocus
                               value="<c:out value='${nroDocumento}'/>">
                    </div>
                    <div>
                        <label for="clave">Clave</label>
                        <input id="clave" name="clave" type="password" maxlength="20" required
                               style="text-transform: uppercase"
                               oninput="this.value = this.value.toUpperCase()">
                    </div>
                </div>
                <div class="acciones-barra">
                    <div>
                        <a class="boton boton--secundario" href="${pageContext.request.contextPath}/login">Cambiar tipo de acceso</a>
                        <a class="boton boton--secundario" href="${pageContext.request.contextPath}/">Salir</a>
                    </div>
                    <button class="boton" type="submit">Ingresar</button>
                </div>
            </form>
        </section>
    </c:when>

    <c:otherwise>
        <section class="panel">
            <p class="panel__subtitulo">CU001 · Paso 1</p>
            <h2 class="panel__titulo">Seleccione el tipo de acceso</h2>
            <div class="acciones-barra">
                <div>
                    <a class="boton" href="${pageContext.request.contextPath}/login?tipo=CONSULTOR">Ingresar como usuario consultor</a>
                    <a class="boton" href="${pageContext.request.contextPath}/login?tipo=CANDIDATO">Realizar cuestionario</a>
                </div>
                <a class="boton boton--secundario" href="${pageContext.request.contextPath}/">Salir</a>
            </div>
        </section>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/jspf/pie.jspf" %>
