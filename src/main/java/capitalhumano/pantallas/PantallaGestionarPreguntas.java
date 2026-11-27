package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorBajasCatalogos;
import capitalhumano.controladores.GestorPreguntas;
import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Factor;
import capitalhumano.dominio.OpcionDeRespuesta;
import capitalhumano.dominio.Pregunta;
import capitalhumano.dominio.ValorOpcion;
import capitalhumano.dtos.DatosPreguntaDTO;
import capitalhumano.dtos.ResultadoPreguntaDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** {@code <<boundary>>} Gestión de preguntas (CU020: #3, CU023: #4 y CU021 en /preguntas/nueva: #8). */
@WebServlet(urlPatterns = {"/preguntas", "/preguntas/*"})
public class PantallaGestionarPreguntas extends HttpServlet {

    private static final int TAMANIO_PAGINA = 5;
    private static final int MAX_LEN_NOMBRE = 100;
    private static final int MAX_LEN_ENUNCIADO = 500;
    private static final int MAX_LEN_DESCRIPCION = 500;
    private static final String ATTR_FLASH_ELIMINADA = "cu023.preguntaEliminada";
    private static final String ATTR_FLASH_ERROR = "cu023.error";
    private static final String ATTR_FLASH_CREADA = "cu021.preguntaCreada";

    private GestorPreguntas gestorPreguntas;
    private GestorBajasCatalogos gestorBajasCatalogos;

    @Override
    public void init() {
        gestorPreguntas = InicializadorAplicacion.obtener(getServletContext(), GestorPreguntas.class);
        gestorBajasCatalogos = InicializadorAplicacion.obtener(getServletContext(), GestorBajasCatalogos.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/nueva".equals(pathInfo)) {
            mostrarPantallaAlta(request, response);
            return;
        }

        if ("/eliminar".equals(pathInfo)) {
            mostrarConfirmacionEliminar(request, response);
            return;
        }

        String accion = request.getParameter("accion");
        if ("factores".equals(accion)) {
            responderFactoresJson(request, response);
            return;
        }
        if ("valores".equals(accion)) {
            responderValoresJson(request, response);
            return;
        }
        if ("generar-ia".equals(accion)) {
            responderGenerarConIAJson(request, response);
            return;
        }

        mostrarPantallaBusqueda(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if ("/nueva".equals(pathInfo)) {
            procesarAltaPregunta(request, response);
            return;
        }
        if ("/eliminar".equals(pathInfo)) {
            procesarEliminacion(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/preguntas");
    }

    // =========================================================================
    // CU021 - Alta de pregunta (#8)
    // =========================================================================

    private void mostrarPantallaAlta(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object creada = session.getAttribute(ATTR_FLASH_CREADA);
            if (creada != null) {
                request.setAttribute("preguntaCreada", creada.toString());
                session.removeAttribute(ATTR_FLASH_CREADA);
            }
        }

        List<Competencia> competencias = gestorPreguntas.obtenerCompetencias();
        List<OpcionDeRespuesta> opciones = gestorPreguntas.obtenerOpcionesDeRespuesta();

        request.setAttribute("titulo", "Alta de preguntas");
        request.setAttribute("competencias", competencias);
        request.setAttribute("factores", List.of());
        request.setAttribute("opciones", opciones);
        request.setAttribute("valoresOpcion", List.of());
        request.setAttribute("ponderacionesIngresadas", Map.of());
        request.setAttribute("camposConError", Set.of());
        request.getRequestDispatcher("/WEB-INF/jsp/alta-pregunta.jsp").forward(request, response);
    }

    private void procesarAltaPregunta(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Competencia> competencias = gestorPreguntas.obtenerCompetencias();
        List<OpcionDeRespuesta> opciones = gestorPreguntas.obtenerOpcionesDeRespuesta();

        String codCompetencia = normalizar(request.getParameter("competencia"));
        String codFactor = normalizar(request.getParameter("factor"));
        String codOpcion = normalizar(request.getParameter("opcion"));
        String nombre = convertirAMayusculas(request.getParameter("nombre"));
        String enunciado = convertirAMayusculas(request.getParameter("enunciado"));
        String descripcion = convertirAMayusculas(request.getParameter("descripcion"));

        Competencia competencia = buscarCompetenciaPorCodigo(competencias, codCompetencia);
        List<Factor> factores = competencia != null ? gestorPreguntas.obtenerFactores(competencia) : List.of();
        Factor factor = buscarFactorPorCodigo(factores, codFactor);

        OpcionDeRespuesta opcion = buscarOpcionPorCodigo(opciones, codOpcion);
        List<ValorOpcion> valoresOpcion = opcion != null ? gestorPreguntas.obtenerValores(opcion) : List.of();

        Map<String, String> ponderacionesIngresadas = new LinkedHashMap<>();
        Map<ValorOpcion, Double> ponderaciones = new LinkedHashMap<>();
        List<String> erroresObligatorios = new ArrayList<>();
        List<String> erroresLongitud = new ArrayList<>();
        Set<String> camposConError = new HashSet<>();

        // Flujo 2.A: validación de campos obligatorios
        validarCamposObligatorios(competencia, factor, nombre, enunciado, opcion, valoresOpcion,
                request, ponderacionesIngresadas, ponderaciones, erroresObligatorios, camposConError);

        // Flujo 2.B: validación de longitudes máximas
        validarLongitudesMaximas(nombre, enunciado, descripcion, erroresLongitud, camposConError);

        if (!erroresObligatorios.isEmpty() || !erroresLongitud.isEmpty()) {
            List<String> erroresFormulario = new ArrayList<>();
            erroresFormulario.addAll(erroresObligatorios);
            erroresFormulario.addAll(erroresLongitud);

            String bannerTitulo = !erroresObligatorios.isEmpty()
                    ? "Completar campos obligatorios"
                    : "Revisar longitudes máximas";
            cargarAtributosFormularioAlta(request, competencias, factores, opciones, valoresOpcion,
                    competencia, factor, opcion, nombre, enunciado, descripcion,
                    ponderacionesIngresadas, camposConError);
            request.setAttribute("bannerTitulo", bannerTitulo);
            request.setAttribute("erroresFormulario", erroresFormulario);
            request.getRequestDispatcher("/WEB-INF/jsp/alta-pregunta.jsp").forward(request, response);
            return;
        }

        // Paso 2-3: invocación al gestor de control con DatosPreguntaDTO
        DatosPreguntaDTO datos = new DatosPreguntaDTO(factor, nombre, enunciado, descripcion, opcion, ponderaciones);
        ResultadoPreguntaDTO resultado = gestorPreguntas.darDeAltaPregunta(datos);

        if (!resultado.isExitoso()) {
            // Flujo 2.C: ponderaciones inválidas o pregunta existente
            cargarAtributosFormularioAlta(request, competencias, factores, opciones, valoresOpcion,
                    competencia, factor, opcion, nombre, enunciado, descripcion,
                    ponderacionesIngresadas, camposConError);
            request.setAttribute("erroresNegocio", resultado.getErrores());
            request.getRequestDispatcher("/WEB-INF/jsp/alta-pregunta.jsp").forward(request, response);
            return;
        }

        // Pasos 3 y 4: pregunta creada correctamente -> PRG al modal "¿Desea cargar otra?"
        request.getSession().setAttribute(ATTR_FLASH_CREADA, nombre);
        response.sendRedirect(request.getContextPath() + "/preguntas/nueva");
    }

    private void validarCamposObligatorios(Competencia competencia, Factor factor, String nombre, String enunciado,
                                           OpcionDeRespuesta opcion, List<ValorOpcion> valoresOpcion,
                                           HttpServletRequest request,
                                           Map<String, String> ponderacionesIngresadas,
                                           Map<ValorOpcion, Double> ponderaciones,
                                           List<String> errores, Set<String> camposConError) {
        if (competencia == null) {
            errores.add("Debe seleccionar una Competencia.");
            camposConError.add("competencia");
        }
        if (factor == null) {
            errores.add("Debe seleccionar un Factor.");
            camposConError.add("factor");
        }
        if (nombre.isEmpty()) {
            errores.add("Debe ingresar el Nombre de Pregunta.");
            camposConError.add("nombre");
        }
        if (enunciado.isEmpty()) {
            errores.add("Debe ingresar el enunciado de la Pregunta.");
            camposConError.add("enunciado");
        }
        if (opcion == null) {
            errores.add("Debe seleccionar una Opción de Respuesta.");
            camposConError.add("opcion");
        } else {
            boolean faltaPonderacion = false;
            boolean formatoInvalido = false;
            for (ValorOpcion valor : valoresOpcion) {
                String clave = valor.getId().toString();
                String raw = normalizar(request.getParameter("ponderacion_" + clave));
                ponderacionesIngresadas.put(clave, raw);
                if (raw.isEmpty()) {
                    faltaPonderacion = true;
                    camposConError.add("ponderacion_" + clave);
                    camposConError.add("ponderaciones");
                } else {
                    try {
                        double numero = Double.parseDouble(raw.replace(',', '.'));
                        ponderaciones.put(valor, numero);
                    } catch (NumberFormatException e) {
                        formatoInvalido = true;
                        camposConError.add("ponderacion_" + clave);
                        camposConError.add("ponderaciones");
                    }
                }
            }
            if (faltaPonderacion) {
                errores.add("Debe ingresar la ponderación para todos los valores de la opción seleccionada.");
            }
            if (formatoInvalido) {
                errores.add("Todas las ponderaciones deben ser valores numéricos válidos.");
            }
        }
    }

    private void validarLongitudesMaximas(String nombre, String enunciado, String descripcion,
                                          List<String> errores, Set<String> camposConError) {
        if (nombre.length() > MAX_LEN_NOMBRE) {
            errores.add("El Nombre de Pregunta no puede superar los " + MAX_LEN_NOMBRE + " caracteres.");
            camposConError.add("nombre");
        }
        if (enunciado.length() > MAX_LEN_ENUNCIADO) {
            errores.add("La Pregunta no puede superar los " + MAX_LEN_ENUNCIADO + " caracteres.");
            camposConError.add("enunciado");
        }
        if (descripcion.length() > MAX_LEN_DESCRIPCION) {
            errores.add("La Descripción no puede superar los " + MAX_LEN_DESCRIPCION + " caracteres.");
            camposConError.add("descripcion");
        }
    }

    private static void cargarAtributosFormularioAlta(HttpServletRequest request,
                                                      List<Competencia> competencias,
                                                      List<Factor> factores,
                                                      List<OpcionDeRespuesta> opciones,
                                                      List<ValorOpcion> valoresOpcion,
                                                      Competencia competencia,
                                                      Factor factor,
                                                      OpcionDeRespuesta opcion,
                                                      String nombre,
                                                      String enunciado,
                                                      String descripcion,
                                                      Map<String, String> ponderacionesIngresadas,
                                                      Set<String> camposConError) {
        request.setAttribute("titulo", "Alta de preguntas");
        request.setAttribute("competencias", competencias);
        request.setAttribute("factores", factores);
        request.setAttribute("opciones", opciones);
        request.setAttribute("valoresOpcion", valoresOpcion);
        request.setAttribute("competenciaSeleccionada", competencia != null ? competencia.getCodigo() : "");
        request.setAttribute("factorSeleccionado", factor != null ? factor.getCodigo() : "");
        request.setAttribute("opcionSeleccionada", opcion != null ? opcion.getCodigo() : "");
        request.setAttribute("nombrePregunta", nombre);
        request.setAttribute("enunciadoPregunta", enunciado);
        request.setAttribute("descripcionPregunta", descripcion);
        request.setAttribute("ponderacionesIngresadas", ponderacionesIngresadas);
        request.setAttribute("camposConError", camposConError);
    }

    private void responderValoresJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String codOpcion = normalizar(request.getParameter("opcion"));
        List<ValorOpcion> valores = List.of();
        if (!codOpcion.isEmpty()) {
            OpcionDeRespuesta opcion = buscarOpcionPorCodigo(gestorPreguntas.obtenerOpcionesDeRespuesta(), codOpcion);
            if (opcion != null) {
                valores = gestorPreguntas.obtenerValores(opcion);
            }
        }

        response.setContentType("application/json; charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print('[');
            for (int i = 0; i < valores.size(); i++) {
                ValorOpcion v = valores.get(i);
                if (i > 0) {
                    out.print(',');
                }
                out.print("{\"id\":\"");
                out.print(escaparJson(v.getId().toString()));
                out.print("\",\"orden\":");
                out.print(v.getOrden());
                out.print(",\"descripcion\":\"");
                out.print(escaparJson(v.getDescripcion()));
                out.print("\"}");
            }
            out.print(']');
        }
    }

    private void responderGenerarConIAJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String codCompetencia = normalizar(request.getParameter("competencia"));
        String codFactor = normalizar(request.getParameter("factor"));

        response.setContentType("application/json; charset=UTF-8");
        Competencia competencia = buscarCompetenciaPorCodigo(gestorPreguntas.obtenerCompetencias(), codCompetencia);
        Factor factor = competencia != null
                ? buscarFactorPorCodigo(gestorPreguntas.obtenerFactores(competencia), codFactor)
                : null;

        if (competencia == null || factor == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"Seleccione una Competencia y un Factor antes de generar con IA.\"}");
            }
            return;
        }

        try {
            Pregunta generada = gestorPreguntas.generarPreguntaConIA(competencia, factor);
            String nombre = convertirAMayusculas(generada.getNombre());
            String enunciado = convertirAMayusculas(generada.getEnunciado());
            String descripcion = convertirAMayusculas(generada.getDescripcion());
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"nombre\":\"");
                out.print(escaparJson(nombre));
                out.print("\",\"enunciado\":\"");
                out.print(escaparJson(enunciado));
                out.print("\",\"descripcion\":\"");
                out.print(escaparJson(descripcion));
                out.print("\"}");
            }
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"No se pudo generar la pregunta con IA. Intente nuevamente.\"}");
            }
        }
    }

    // =========================================================================
    // CU023 - Baja lógica de pregunta (#4)
    // =========================================================================

    private void mostrarConfirmacionEliminar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idTexto = normalizar(request.getParameter("id"));
        if (idTexto.isEmpty()) {
            request.setAttribute("error", "Debe indicar el identificador de la pregunta a eliminar.");
            mostrarPantallaBusqueda(request, response);
            return;
        }
        try {
            UUID id = UUID.fromString(idTexto);
            Pregunta encontrada = buscarPreguntaActivaPorId(id);
            if (encontrada == null) {
                request.setAttribute("error", "La pregunta seleccionada no existe o ya fue eliminada.");
            } else {
                request.setAttribute("preguntaAEliminar", encontrada);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "El identificador de la pregunta no es válido.");
        }
        mostrarPantallaBusqueda(request, response);
    }

    private void procesarEliminacion(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        String idTexto = normalizar(request.getParameter("id"));
        String nombreFallback = normalizar(request.getParameter("nombrePregunta"));

        try {
            UUID id = UUID.fromString(idTexto);
            Pregunta pregunta = buscarPreguntaActivaPorId(id);
            String nombreMostrar = pregunta != null ? pregunta.getNombre() : nombreFallback;
            String usuario = obtenerUsuarioActual(session);

            gestorBajasCatalogos.darDeBajaPregunta(id, usuario);
            session.setAttribute(ATTR_FLASH_ELIMINADA, nombreMostrar);
        } catch (RuntimeException e) {
            session.setAttribute(ATTR_FLASH_ERROR, e.getMessage());
        }

        response.sendRedirect(construirUrlRetorno(request));
    }

    // =========================================================================
    // CU020 - Búsqueda de preguntas (#3)
    // =========================================================================

    private void responderFactoresJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String codCompetencia = normalizar(request.getParameter("competencia"));
        List<Factor> factores = List.of();
        if (!codCompetencia.isEmpty()) {
            Competencia competencia = buscarCompetenciaPorCodigo(gestorPreguntas.obtenerCompetencias(), codCompetencia);
            if (competencia != null) {
                factores = gestorPreguntas.obtenerFactores(competencia);
            }
        }

        response.setContentType("application/json; charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print('[');
            for (int i = 0; i < factores.size(); i++) {
                Factor f = factores.get(i);
                if (i > 0) {
                    out.print(',');
                }
                out.print("{\"codigo\":\"");
                out.print(escaparJson(f.getCodigo()));
                out.print("\",\"nombre\":\"");
                out.print(escaparJson(f.getNombre()));
                out.print("\"}");
            }
            out.print(']');
        }
    }

    private void mostrarPantallaBusqueda(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        consumirMensajesFlash(request);

        List<Competencia> competencias = gestorPreguntas.obtenerCompetencias();

        String codCompetencia = normalizar(request.getParameter("competencia"));
        String codFactor = normalizar(request.getParameter("factor"));
        String nombrePregunta = convertirAMayusculas(request.getParameter("nombre"));
        boolean buscar = "1".equals(request.getParameter("buscar"));

        Competencia competenciaSeleccionada = buscarCompetenciaPorCodigo(competencias, codCompetencia);
        List<Factor> factores = competenciaSeleccionada != null
                ? gestorPreguntas.obtenerFactores(competenciaSeleccionada)
                : List.of();
        Factor factorSeleccionado = buscarFactorPorCodigo(factores, codFactor);

        if (buscar) {
            if (!validarAlMenosUnCriterio(competenciaSeleccionada, factorSeleccionado, nombrePregunta)) {
                request.setAttribute("error", "Debe ingresar al menos un criterio de búsqueda.");
            } else {
                List<Pregunta> todas = gestorPreguntas.buscarPreguntas(
                        competenciaSeleccionada, factorSeleccionado, nombrePregunta);
                int totalResultados = todas.size();
                int totalPaginas = Math.max(1, (int) Math.ceil((double) totalResultados / TAMANIO_PAGINA));
                int paginaActual = parsearPagina(request.getParameter("pagina"), totalPaginas);
                int desde = (paginaActual - 1) * TAMANIO_PAGINA;
                int hasta = Math.min(desde + TAMANIO_PAGINA, totalResultados);

                request.setAttribute("busquedaRealizada", Boolean.TRUE);
                request.setAttribute("preguntas", todas.subList(desde, hasta));
                request.setAttribute("totalResultados", totalResultados);
                request.setAttribute("paginaActual", paginaActual);
                request.setAttribute("totalPaginas", totalPaginas);
            }
        }

        request.setAttribute("titulo", "Gestión de preguntas");
        request.setAttribute("competencias", competencias);
        request.setAttribute("factores", factores);
        request.setAttribute("competenciaSeleccionada", competenciaSeleccionada != null ? competenciaSeleccionada.getCodigo() : "");
        request.setAttribute("factorSeleccionado", factorSeleccionado != null ? factorSeleccionado.getCodigo() : "");
        request.setAttribute("nombrePregunta", nombrePregunta);
        request.getRequestDispatcher("/WEB-INF/jsp/gestionar-preguntas.jsp").forward(request, response);
    }

    private Pregunta buscarPreguntaActivaPorId(UUID id) {
        for (Pregunta p : gestorPreguntas.buscarPreguntas(null, null, null)) {
            if (id.equals(p.getId())) {
                return p;
            }
        }
        return null;
    }

    private static void consumirMensajesFlash(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        Object eliminada = session.getAttribute(ATTR_FLASH_ELIMINADA);
        if (eliminada != null) {
            request.setAttribute("preguntaEliminada", eliminada.toString());
            session.removeAttribute(ATTR_FLASH_ELIMINADA);
        }
        Object error = session.getAttribute(ATTR_FLASH_ERROR);
        if (error != null) {
            request.setAttribute("error", error.toString());
            session.removeAttribute(ATTR_FLASH_ERROR);
        }
    }

    private static String obtenerUsuarioActual(HttpSession session) {
        if (session != null) {
            Object usuario = session.getAttribute("usuario");
            if (usuario != null && !usuario.toString().isBlank()) {
                return usuario.toString().trim();
            }
        }
        // No se inventa un consultor para la auditoría (CU023): FiltroAutenticacion no deja llegar sin sesión
        throw new IllegalStateException("No hay un consultor autenticado en la sesión");
    }

    private static String construirUrlRetorno(HttpServletRequest request) {
        StringBuilder url = new StringBuilder(request.getContextPath()).append("/preguntas");
        boolean buscar = "1".equals(request.getParameter("buscar"));
        if (!buscar) {
            return url.toString();
        }
        url.append("?buscar=1");
        agregarParametro(url, "competencia", request.getParameter("competencia"));
        agregarParametro(url, "factor", request.getParameter("factor"));
        agregarParametro(url, "nombre", request.getParameter("nombre"));
        agregarParametro(url, "pagina", request.getParameter("pagina"));
        return url.toString();
    }

    private static void agregarParametro(StringBuilder url, String clave, String valor) {
        if (valor != null && !valor.isBlank()) {
            url.append('&')
                    .append(clave)
                    .append('=')
                    .append(URLEncoder.encode(valor.trim(), StandardCharsets.UTF_8));
        }
    }

    private boolean validarAlMenosUnCriterio(Competencia competencia, Factor factor, String nombrePregunta) {
        return competencia != null || factor != null || (nombrePregunta != null && !nombrePregunta.isBlank());
    }

    private static Competencia buscarCompetenciaPorCodigo(List<Competencia> competencias, String codigo) {
        if (codigo.isEmpty()) {
            return null;
        }
        for (Competencia c : competencias) {
            if (codigo.equals(c.getCodigo())) {
                return c;
            }
        }
        return null;
    }

    private static Factor buscarFactorPorCodigo(List<Factor> factores, String codigo) {
        if (codigo.isEmpty()) {
            return null;
        }
        for (Factor f : factores) {
            if (codigo.equals(f.getCodigo())) {
                return f;
            }
        }
        return null;
    }

    private static OpcionDeRespuesta buscarOpcionPorCodigo(List<OpcionDeRespuesta> opciones, String codigo) {
        if (codigo.isEmpty()) {
            return null;
        }
        for (OpcionDeRespuesta o : opciones) {
            if (codigo.equals(o.getCodigo())) {
                return o;
            }
        }
        return null;
    }

    private static int parsearPagina(String valor, int totalPaginas) {
        if (valor == null || valor.isBlank()) {
            return 1;
        }
        try {
            int pagina = Integer.parseInt(valor.trim());
            if (pagina < 1) {
                return 1;
            }
            return Math.min(pagina, totalPaginas);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private static String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private static String convertirAMayusculas(String valor) {
        return normalizar(valor).toUpperCase(Locale.ROOT);
    }

    private static String escaparJson(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}
