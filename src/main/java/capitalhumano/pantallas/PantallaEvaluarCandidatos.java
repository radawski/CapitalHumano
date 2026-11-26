package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorEvaluacionCandidatos;
import capitalhumano.dominio.Candidato;
import capitalhumano.dominio.Evaluacion;
import capitalhumano.dominio.Puesto;
import capitalhumano.dtos.ClaveCandidatoDTO;
import capitalhumano.dtos.DatosPuestoEvaluacionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/** {@code <<boundary>>} Evaluar candidatos (CU025: #10, #11, #12 y #13). */
@WebServlet(urlPatterns = {"/evaluar-candidatos", "/evaluar-candidatos/*"})
public class PantallaEvaluarCandidatos extends HttpServlet {

    public static final String ATTR_SESION_CANDIDATOS = "cu025.candidatos";
    private static final String ATTR_SESION_RESULTADOS = "cu025.resultados";
    private static final String ATTR_SESION_BUSQUEDA_REALIZADA = "cu025.busquedaRealizada";
    private static final String ATTR_SESION_FILTRO_APELLIDO = "cu025.filtroApellido";
    private static final String ATTR_SESION_FILTRO_NOMBRE = "cu025.filtroNombre";
    private static final String ATTR_SESION_FILTRO_NRO = "cu025.filtroNroCandidato";
    private static final String ATTR_SESION_DATOS_PUESTO = "cu025.datosPuesto";
    private static final String ATTR_SESION_EVALUACION = "cu025.evaluacion";
    private static final String ATTR_SESION_EVALUACION_GUARDADA = "cu025.evaluacionGuardada";

    private GestorEvaluacionCandidatos gestorEvaluacionCandidatos;

    @Override
    public void init() {
        gestorEvaluacionCandidatos = InicializadorAplicacion.obtener(
                getServletContext(), GestorEvaluacionCandidatos.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession();

        if ("/puesto".equals(pathInfo)) {
            if (obtenerListaSesion(session, ATTR_SESION_CANDIDATOS).isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/evaluar-candidatos");
                return;
            }
            mostrarPantallaPuestos(request, response);
            return;
        }

        if ("/competencias".equals(pathInfo)) {
            if (obtenerListaSesion(session, ATTR_SESION_CANDIDATOS).isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/evaluar-candidatos");
                return;
            }
            if (!(session.getAttribute(ATTR_SESION_DATOS_PUESTO) instanceof DatosPuestoEvaluacionDTO)) {
                response.sendRedirect(request.getContextPath() + "/evaluar-candidatos/puesto");
                return;
            }
            request.setAttribute("titulo", "Evaluar candidatos - Competencias del puesto");
            request.setAttribute("paso", "competencias");
            request.getRequestDispatcher("/WEB-INF/jsp/evaluar-candidatos.jsp").forward(request, response);
            return;
        }

        if ("/claves".equals(pathInfo)) {
            if (!(session.getAttribute(ATTR_SESION_EVALUACION) instanceof Evaluacion evaluacion)) {
                response.sendRedirect(request.getContextPath() + "/evaluar-candidatos/puesto");
                return;
            }
            List<ClaveCandidatoDTO> claves = gestorEvaluacionCandidatos.obtenerClavesCandidatos(evaluacion);
            request.setAttribute("clavesCandidatos", claves);
            request.setAttribute("titulo", "Evaluar candidatos - Claves generadas");
            request.setAttribute("paso", "claves");
            request.getRequestDispatcher("/WEB-INF/jsp/evaluar-candidatos.jsp").forward(request, response);
            return;
        }

        if ("/exportar".equals(pathInfo)) {
            if (!(session.getAttribute(ATTR_SESION_EVALUACION_GUARDADA) instanceof Evaluacion)) {
                response.sendRedirect(request.getContextPath() + "/evaluar-candidatos");
                return;
            }
            request.setAttribute("confirmarExportar", "true".equals(request.getParameter("confirmar")));
            request.setAttribute("titulo", "Evaluar candidatos - Exportación de claves");
            request.setAttribute("paso", "exportar");
            request.getRequestDispatcher("/WEB-INF/jsp/evaluar-candidatos.jsp").forward(request, response);
            return;
        }

        if ("true".equals(request.getParameter("reiniciar"))) {
            limpiarSesion(session);
        }
        mostrarPantallaBusqueda(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        HttpSession session = request.getSession();

        if ("cancelar".equals(accion)) {
            limpiarSesion(session);
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        if ("buscar".equals(accion)) {
            ingresarFiltrosYBuscar(request, response, session);
            return;
        }

        if ("agregar".equals(accion)) {
            seleccionarCandidatosYAgregar(request, response, session);
            return;
        }

        if ("quitar".equals(accion)) {
            seleccionarCandidatosYQuitar(request, response, session);
            return;
        }

        if ("siguiente".equals(accion)) {
            presionarSiguienteCandidatos(request, response, session);
            return;
        }

        if ("seleccionarPuesto".equals(accion)) {
            seleccionarPuesto(request, response, session);
            return;
        }

        if ("generarEvaluacion".equals(accion)) {
            generarEvaluacion(request, response, session);
            return;
        }

        if ("finalizar".equals(accion)) {
            finalizarYGuardarEvaluacion(request, response, session);
            return;
        }

        if ("solicitarExportar".equals(accion)) {
            response.sendRedirect(request.getContextPath() + "/evaluar-candidatos/exportar?confirmar=true");
            return;
        }

        if ("descargarExcel".equals(accion)) {
            descargarExcel(request, response, session);
            return;
        }

        mostrarPantallaBusqueda(request, response);
    }

    private void ingresarFiltrosYBuscar(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        String apellido = normalizarTexto(request.getParameter("apellido"));
        String nombre = normalizarTexto(request.getParameter("nombre"));
        String nroTexto = normalizarTexto(request.getParameter("nroCandidato"));

        session.setAttribute(ATTR_SESION_FILTRO_APELLIDO, apellido);
        session.setAttribute(ATTR_SESION_FILTRO_NOMBRE, nombre);
        session.setAttribute(ATTR_SESION_FILTRO_NRO, nroTexto);

        int nroCandidato = 0;
        if (!nroTexto.isEmpty()) {
            try {
                nroCandidato = Integer.parseInt(nroTexto);
                if (nroCandidato <= 0) {
                    request.setAttribute("error", "El N\u00ba de candidato debe ser un n\u00famero entero positivo.");
                    mostrarPantallaBusqueda(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "El N\u00ba de candidato debe ser un n\u00famero entero v\u00e1lido.");
                mostrarPantallaBusqueda(request, response);
                return;
            }
        }

        List<Candidato> resultados = gestorEvaluacionCandidatos.buscarCandidatos(apellido, nombre, nroCandidato);
        session.setAttribute(ATTR_SESION_RESULTADOS, resultados);
        session.setAttribute(ATTR_SESION_BUSQUEDA_REALIZADA, Boolean.TRUE);
        mostrarPantallaBusqueda(request, response);
    }

    private void seleccionarCandidatosYAgregar(HttpServletRequest request, HttpServletResponse response,
                                               HttpSession session) throws ServletException, IOException {
        String[] seleccionados = request.getParameterValues("nroCandidatoAgregar");
        if (seleccionados == null || seleccionados.length == 0) {
            request.setAttribute("error", "Debe seleccionar al menos un candidato de los resultados para agregar.");
            mostrarPantallaBusqueda(request, response);
            return;
        }

        Set<Integer> idsAAgregar = parsearIds(seleccionados);
        List<Candidato> resultados = obtenerListaSesion(session, ATTR_SESION_RESULTADOS);
        List<Candidato> candidatosAEvaluar = new ArrayList<>(obtenerListaSesion(session, ATTR_SESION_CANDIDATOS));

        for (Candidato candidato : resultados) {
            if (idsAAgregar.contains(candidato.getNroCandidato()) && !candidatosAEvaluar.contains(candidato)) {
                candidatosAEvaluar.add(candidato);
            }
        }

        session.setAttribute(ATTR_SESION_CANDIDATOS, candidatosAEvaluar);
        mostrarPantallaBusqueda(request, response);
    }

    private void seleccionarCandidatosYQuitar(HttpServletRequest request, HttpServletResponse response,
                                              HttpSession session) throws ServletException, IOException {
        String[] seleccionados = request.getParameterValues("nroCandidatoQuitar");
        if (seleccionados == null || seleccionados.length == 0) {
            request.setAttribute("error", "Debe seleccionar al menos un candidato a evaluar para quitar.");
            mostrarPantallaBusqueda(request, response);
            return;
        }

        Set<Integer> idsAQuitar = parsearIds(seleccionados);
        List<Candidato> candidatosAEvaluar = new ArrayList<>(obtenerListaSesion(session, ATTR_SESION_CANDIDATOS));
        candidatosAEvaluar.removeIf(c -> idsAQuitar.contains(c.getNroCandidato()));

        session.setAttribute(ATTR_SESION_CANDIDATOS, candidatosAEvaluar);
        mostrarPantallaBusqueda(request, response);
    }

    private void presionarSiguienteCandidatos(HttpServletRequest request, HttpServletResponse response,
                                              HttpSession session) throws ServletException, IOException {
        List<Candidato> candidatosAEvaluar = obtenerListaSesion(session, ATTR_SESION_CANDIDATOS);
        if (candidatosAEvaluar.isEmpty()) {
            request.setAttribute("error", "Debe agregar al menos un candidato a evaluar antes de continuar.");
            mostrarPantallaBusqueda(request, response);
            return;
        }

        List<Candidato> noHabilitados = gestorEvaluacionCandidatos.validarCandidatos(candidatosAEvaluar);
        if (!noHabilitados.isEmpty()) {
            request.setAttribute("candidatosNoHabilitados", noHabilitados);
            mostrarPantallaBusqueda(request, response);
            return;
        }

        session.setAttribute(ATTR_SESION_CANDIDATOS, candidatosAEvaluar);
        session.removeAttribute(ATTR_SESION_DATOS_PUESTO);
        session.removeAttribute(ATTR_SESION_EVALUACION);
        response.sendRedirect(request.getContextPath() + "/evaluar-candidatos/puesto");
    }

    private void seleccionarPuesto(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        if (obtenerListaSesion(session, ATTR_SESION_CANDIDATOS).isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/evaluar-candidatos");
            return;
        }

        String codigoPuesto = normalizarTexto(request.getParameter("codigoPuesto"));
        if (codigoPuesto.isEmpty()) {
            request.setAttribute("error", "Debe seleccionar una funci\u00f3n antes de continuar.");
            mostrarPantallaPuestos(request, response);
            return;
        }

        DatosPuestoEvaluacionDTO datosPuesto = gestorEvaluacionCandidatos.obtenerDatosPuestoYCompetencias(codigoPuesto);
        session.setAttribute(ATTR_SESION_DATOS_PUESTO, datosPuesto);
        session.removeAttribute(ATTR_SESION_EVALUACION);
        response.sendRedirect(request.getContextPath() + "/evaluar-candidatos/competencias");
    }

    private void generarEvaluacion(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException {
        List<Candidato> candidatos = obtenerListaSesion(session, ATTR_SESION_CANDIDATOS);
        Object objDatos = session.getAttribute(ATTR_SESION_DATOS_PUESTO);
        if (candidatos.isEmpty() || !(objDatos instanceof DatosPuestoEvaluacionDTO datosPuesto)) {
            response.sendRedirect(request.getContextPath() + "/evaluar-candidatos");
            return;
        }
        if (!datosPuesto.getCompetenciasNoEvaluables().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/evaluar-candidatos/competencias");
            return;
        }

        Evaluacion evaluacion = gestorEvaluacionCandidatos.generarEvaluacion(candidatos, datosPuesto.getPuesto());
        session.setAttribute(ATTR_SESION_EVALUACION, evaluacion);
        response.sendRedirect(request.getContextPath() + "/evaluar-candidatos/claves");
    }

    private void finalizarYGuardarEvaluacion(HttpServletRequest request, HttpServletResponse response,
                                             HttpSession session) throws IOException {
        if (!(session.getAttribute(ATTR_SESION_EVALUACION) instanceof Evaluacion evaluacion)) {
            response.sendRedirect(request.getContextPath() + "/evaluar-candidatos");
            return;
        }

        gestorEvaluacionCandidatos.guardarEvaluacion(evaluacion);
        limpiarSesionFlujoActivo(session);
        session.setAttribute(ATTR_SESION_EVALUACION_GUARDADA, evaluacion);
        response.sendRedirect(request.getContextPath() + "/evaluar-candidatos/exportar");
    }

    private void descargarExcel(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException {
        if (!(session.getAttribute(ATTR_SESION_EVALUACION_GUARDADA) instanceof Evaluacion evaluacion)) {
            response.sendRedirect(request.getContextPath() + "/evaluar-candidatos");
            return;
        }

        Path tempDir = Files.createTempDirectory("cu025-claves-");
        try {
            gestorEvaluacionCandidatos.exportarClaves(evaluacion, tempDir.toString());
            Path archivoGenerado;
            try (Stream<Path> archivos = Files.list(tempDir)) {
                archivoGenerado = archivos.findFirst()
                        .orElseThrow(() -> new IllegalStateException("No se generó el archivo Excel"));
            }

            String nombreArchivo = archivoGenerado.getFileName().toString();
            String nombreSeguroHeader = nombreArchivo.replace("\"", "");
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreSeguroHeader + "\"");
            response.setContentLengthLong(Files.size(archivoGenerado));
            Files.copy(archivoGenerado, response.getOutputStream());
            response.getOutputStream().flush();
        } finally {
            eliminarDirectorioTemporal(tempDir);
        }
    }

    private void mostrarPantallaBusqueda(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Evaluar candidatos");
        request.setAttribute("paso", "candidatos");
        request.getRequestDispatcher("/WEB-INF/jsp/evaluar-candidatos.jsp").forward(request, response);
    }

    private void mostrarPantallaPuestos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Puesto> puestos = gestorEvaluacionCandidatos.listarPuestos();
        request.setAttribute("puestos", puestos);
        request.setAttribute("titulo", "Evaluar candidatos - Selección de puesto");
        request.setAttribute("paso", "puesto");
        request.getRequestDispatcher("/WEB-INF/jsp/evaluar-candidatos.jsp").forward(request, response);
    }

    @SuppressWarnings("unchecked")
    private static List<Candidato> obtenerListaSesion(HttpSession session, String atributo) {
        Object valor = session.getAttribute(atributo);
        if (valor instanceof List<?>) {
            return (List<Candidato>) valor;
        }
        return List.of();
    }

    private static void limpiarSesionFlujoActivo(HttpSession session) {
        session.removeAttribute(ATTR_SESION_CANDIDATOS);
        session.removeAttribute(ATTR_SESION_RESULTADOS);
        session.removeAttribute(ATTR_SESION_BUSQUEDA_REALIZADA);
        session.removeAttribute(ATTR_SESION_FILTRO_APELLIDO);
        session.removeAttribute(ATTR_SESION_FILTRO_NOMBRE);
        session.removeAttribute(ATTR_SESION_FILTRO_NRO);
        session.removeAttribute(ATTR_SESION_DATOS_PUESTO);
        session.removeAttribute(ATTR_SESION_EVALUACION);
    }

    private static void limpiarSesion(HttpSession session) {
        limpiarSesionFlujoActivo(session);
        session.removeAttribute(ATTR_SESION_EVALUACION_GUARDADA);
    }

    private static void eliminarDirectorioTemporal(Path dir) {
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.sorted((a, b) -> b.compareTo(a)).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {
                    // Limpieza best-effort de temporales
                }
            });
        } catch (IOException ignored) {
            // Limpieza best-effort de temporales
        }
    }

    private static Set<Integer> parsearIds(String[] valores) {
        Set<Integer> ids = new LinkedHashSet<>();
        for (String valor : valores) {
            try {
                ids.add(Integer.parseInt(valor));
            } catch (NumberFormatException ignored) {
                // Se ignoran identificadores inválidos
            }
        }
        return ids;
    }

    private static String normalizarTexto(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
