package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorBajasCatalogos;
import capitalhumano.controladores.GestorPreguntas;
import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Factor;
import capitalhumano.dominio.Pregunta;
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
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** {@code <<boundary>>} Gestión de preguntas (CU020: #3, CU023: #4 y CU021 en /preguntas/nueva: #8). */
@WebServlet(urlPatterns = {"/preguntas", "/preguntas/*"})
public class PantallaGestionarPreguntas extends HttpServlet {

    private static final int TAMANIO_PAGINA = 5;
    private static final String USUARIO_POR_DEFECTO = "consultor";
    private static final String ATTR_FLASH_ELIMINADA = "cu023.preguntaEliminada";
    private static final String ATTR_FLASH_ERROR = "cu023.error";

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
            request.setAttribute("titulo", "Alta de preguntas");
            request.setAttribute("vistaPlaceholder", "CU021 (Alta de pregunta) se implementa en el issue #8.");
            request.getRequestDispatcher("/WEB-INF/jsp/gestionar-preguntas.jsp").forward(request, response);
            return;
        }

        if ("/eliminar".equals(pathInfo)) {
            mostrarConfirmacionEliminar(request, response);
            return;
        }

        if ("factores".equals(request.getParameter("accion"))) {
            responderFactoresJson(request, response);
            return;
        }

        mostrarPantallaBusqueda(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if ("/eliminar".equals(pathInfo)) {
            procesarEliminacion(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/preguntas");
    }

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
        String nombrePregunta = normalizar(request.getParameter("nombre")).toUpperCase(Locale.ROOT);
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
        return USUARIO_POR_DEFECTO;
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
