package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorEvaluacionCandidatos;
import capitalhumano.dominio.Candidato;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** {@code <<boundary>>} Evaluar candidatos (CU025: #10, #11, #12 y #13). */
@WebServlet(urlPatterns = {"/evaluar-candidatos", "/evaluar-candidatos/*"})
public class PantallaEvaluarCandidatos extends HttpServlet {

    public static final String ATTR_SESION_CANDIDATOS = "cu025.candidatos";
    private static final String ATTR_SESION_RESULTADOS = "cu025.resultados";
    private static final String ATTR_SESION_BUSQUEDA_REALIZADA = "cu025.busquedaRealizada";
    private static final String ATTR_SESION_FILTRO_APELLIDO = "cu025.filtroApellido";
    private static final String ATTR_SESION_FILTRO_NOMBRE = "cu025.filtroNombre";
    private static final String ATTR_SESION_FILTRO_NRO = "cu025.filtroNroCandidato";

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
            List<Candidato> candidatos = obtenerListaSesion(session, ATTR_SESION_CANDIDATOS);
            if (candidatos.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/evaluar-candidatos");
                return;
            }
            request.setAttribute("titulo", "Evaluar candidatos - Selección de puesto");
            request.setAttribute("paso", "puesto");
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
        response.sendRedirect(request.getContextPath() + "/evaluar-candidatos/puesto");
    }

    private void mostrarPantallaBusqueda(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Evaluar candidatos");
        request.setAttribute("paso", "candidatos");
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

    private static void limpiarSesion(HttpSession session) {
        session.removeAttribute(ATTR_SESION_CANDIDATOS);
        session.removeAttribute(ATTR_SESION_RESULTADOS);
        session.removeAttribute(ATTR_SESION_BUSQUEDA_REALIZADA);
        session.removeAttribute(ATTR_SESION_FILTRO_APELLIDO);
        session.removeAttribute(ATTR_SESION_FILTRO_NOMBRE);
        session.removeAttribute(ATTR_SESION_FILTRO_NRO);
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
