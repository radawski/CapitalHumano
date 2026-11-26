package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorOrdenMerito;
import capitalhumano.dtos.EvaluacionResumenDTO;
import capitalhumano.dtos.OrdenMeritoDTO;
import capitalhumano.dtos.PuestoResumenDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** {@code <<boundary>>} Orden de mérito (CU028: #5, #6 y #7). */
@WebServlet(urlPatterns = {"/orden-merito", "/orden-merito/*"})
public class PantallaOrdenMerito extends HttpServlet {

    private static final String JSP = "/WEB-INF/jsp/orden-merito.jsp";
    private static final String PREFIJO_SESION = "cu028.";
    private static final String ATTR_SESION_FILTRO_CODIGO = "cu028.filtroCodigo";
    private static final String ATTR_SESION_FILTRO_NOMBRE = "cu028.filtroNombre";
    private static final String ATTR_SESION_FILTRO_EMPRESA = "cu028.filtroEmpresa";
    private static final String ATTR_SESION_PUESTOS = "cu028.puestos";
    private static final String ATTR_SESION_BUSQUEDA_REALIZADA = "cu028.busquedaRealizada";
    private static final String ATTR_SESION_PUESTO_CODIGO = "cu028.puestoCodigo";
    private static final String ATTR_SESION_EVALUACIONES = "cu028.evaluaciones";
    private static final String ATTR_SESION_ORDEN = "cu028.orden";
    private static final String ATTR_SESION_SELECCION = "cu028.seleccionEvaluacion";

    private static final String RUTA_LISTADO = "/listado";
    private static final String OPCION_TODAS = "TODAS";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private GestorOrdenMerito gestorOrdenMerito;

    @Override
    public void init() {
        gestorOrdenMerito = InicializadorAplicacion.obtener(getServletContext(), GestorOrdenMerito.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession();

        if (RUTA_LISTADO.equals(pathInfo)) {
            mostrarOrdenDeMerito(request, response, session);
            return;
        }

        if (pathInfo != null && pathInfo.length() > 1) {
            seleccionarPuestoYEmitir(request, response, session, pathInfo.substring(1));
            return;
        }

        // Pasos 1–2: búsqueda (con los filtros y la grilla que hayan quedado en la sesión).
        mostrarPantallaBusqueda(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        HttpSession session = request.getSession();

        if ("cancelar".equals(accion)) {
            // Flujo 2.A: el CU termina.
            limpiarSesion(session);
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        if ("buscar".equals(accion)) {
            ingresarCriteriosYBuscar(request, response, session);
            return;
        }

        if ("emitir".equals(accion)) {
            seleccionarEvaluaciones(request, response, session);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/orden-merito");
    }

    /** Pasos 3–4: valida que haya al menos un criterio y muestra la grilla de puestos. */
    private void ingresarCriteriosYBuscar(HttpServletRequest request, HttpServletResponse response,
            HttpSession session) throws ServletException, IOException {
        String codigo = normalizarTexto(request.getParameter("codigo"));
        String nombre = normalizarTexto(request.getParameter("nombre"));
        String empresa = normalizarTexto(request.getParameter("empresa"));
        session.setAttribute(ATTR_SESION_FILTRO_CODIGO, codigo);
        session.setAttribute(ATTR_SESION_FILTRO_NOMBRE, nombre);
        session.setAttribute(ATTR_SESION_FILTRO_EMPRESA, empresa);

        if (!validarAlMenosUnCriterio(codigo, nombre, empresa)) {
            session.removeAttribute(ATTR_SESION_PUESTOS);
            session.removeAttribute(ATTR_SESION_BUSQUEDA_REALIZADA);
            request.setAttribute("error", "Ingrese al menos un criterio de búsqueda");
            mostrarPantallaBusqueda(request, response);
            return;
        }

        List<PuestoResumenDTO> puestos = gestorOrdenMerito.buscarPuestos(codigo, nombre, empresa);
        session.setAttribute(ATTR_SESION_PUESTOS, puestos);
        session.setAttribute(ATTR_SESION_BUSQUEDA_REALIZADA, Boolean.TRUE);
        response.sendRedirect(request.getContextPath() + "/orden-merito");
    }

    /**
     * Pasos 5–6: evaluaciones del puesto elegido. Si no tiene, flujo 6.A (mensaje y vuelve al
     * paso 4); si tiene, el modal para elegir una evaluación o TODAS.
     */
    private void seleccionarPuestoYEmitir(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, String puestoCodigo) throws ServletException, IOException {
        String codigo = normalizarTexto(puestoCodigo);
        List<EvaluacionResumenDTO> evaluaciones = gestorOrdenMerito.obtenerEvaluaciones(codigo);
        if (evaluaciones.isEmpty()) {
            session.removeAttribute(ATTR_SESION_PUESTO_CODIGO);
            session.removeAttribute(ATTR_SESION_EVALUACIONES);
            request.setAttribute("error", "El puesto seleccionado no posee evaluaciones");
            mostrarPantallaBusqueda(request, response);
            return;
        }
        session.setAttribute(ATTR_SESION_PUESTO_CODIGO, codigo);
        session.setAttribute(ATTR_SESION_EVALUACIONES, evaluaciones);
        request.setAttribute("mostrarModalEvaluaciones", Boolean.TRUE);
        mostrarPantallaBusqueda(request, response);
    }

    /** Paso 7: genera el orden de mérito con la evaluación elegida (o todas) y lo muestra. */
    private void seleccionarEvaluaciones(HttpServletRequest request, HttpServletResponse response,
            HttpSession session) throws ServletException, IOException {
        String puestoCodigo = (String) session.getAttribute(ATTR_SESION_PUESTO_CODIGO);
        List<EvaluacionResumenDTO> evaluaciones = obtenerEvaluacionesSesion(session);
        if (puestoCodigo == null || evaluaciones.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/orden-merito");
            return;
        }

        String seleccion = request.getParameter("evaluacion");
        List<UUID> evaluacionIds = new ArrayList<>();
        String descripcionSeleccion = null;
        if (OPCION_TODAS.equals(seleccion)) {
            for (EvaluacionResumenDTO evaluacion : evaluaciones) {
                evaluacionIds.add(evaluacion.getEvaluacionId());
            }
            descripcionSeleccion = OPCION_TODAS;
        } else {
            for (EvaluacionResumenDTO evaluacion : evaluaciones) {
                if (evaluacion.getEvaluacionId().toString().equals(seleccion)) {
                    evaluacionIds.add(evaluacion.getEvaluacionId());
                    descripcionSeleccion = describir(evaluacion);
                }
            }
        }

        if (evaluacionIds.isEmpty()) {
            request.setAttribute("errorModal", "Seleccione una evaluación");
            request.setAttribute("mostrarModalEvaluaciones", Boolean.TRUE);
            mostrarPantallaBusqueda(request, response);
            return;
        }

        OrdenMeritoDTO ordenMerito = gestorOrdenMerito.generarOrdenMerito(puestoCodigo, evaluacionIds);
        session.setAttribute(ATTR_SESION_ORDEN, ordenMerito);
        session.setAttribute(ATTR_SESION_SELECCION, descripcionSeleccion);
        response.sendRedirect(request.getContextPath() + "/orden-merito" + RUTA_LISTADO);
    }

    private void mostrarOrdenDeMerito(HttpServletRequest request, HttpServletResponse response,
            HttpSession session) throws ServletException, IOException {
        if (!(session.getAttribute(ATTR_SESION_ORDEN) instanceof OrdenMeritoDTO)) {
            response.sendRedirect(request.getContextPath() + "/orden-merito");
            return;
        }
        request.setAttribute("titulo", "Orden de mérito");
        request.setAttribute("paso", "listado");
        agregarFormatos(request);
        request.getRequestDispatcher(JSP).forward(request, response);
    }

    private void mostrarPantallaBusqueda(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Orden de mérito");
        request.setAttribute("paso", "busqueda");
        agregarFormatos(request);
        request.getRequestDispatcher(JSP).forward(request, response);
    }

    private static void agregarFormatos(HttpServletRequest request) {
        request.setAttribute("formatoFecha", FORMATO_FECHA);
        request.setAttribute("formatoFechaHora", FORMATO_FECHA_HORA);
    }

    private static String describir(EvaluacionResumenDTO evaluacion) {
        return evaluacion.getFechaCreacion().format(FORMATO_FECHA);
    }

    @SuppressWarnings("unchecked")
    private static List<EvaluacionResumenDTO> obtenerEvaluacionesSesion(HttpSession session) {
        Object valor = session.getAttribute(ATTR_SESION_EVALUACIONES);
        return valor instanceof List<?> lista ? (List<EvaluacionResumenDTO>) lista : List.of();
    }

    private static boolean validarAlMenosUnCriterio(String codigo, String nombre, String empresa) {
        return !codigo.isEmpty() || !nombre.isEmpty() || !empresa.isEmpty();
    }

    private static void limpiarSesion(HttpSession session) {
        for (String nombre : Collections.list(session.getAttributeNames())) {
            if (nombre.startsWith(PREFIJO_SESION)) {
                session.removeAttribute(nombre);
            }
        }
    }

    /** Los literales se ingresan siempre en mayúscula (requerimiento especial del CU). */
    private static String normalizarTexto(String valor) {
        return valor == null ? "" : valor.trim().toUpperCase(Locale.ROOT);
    }
}
