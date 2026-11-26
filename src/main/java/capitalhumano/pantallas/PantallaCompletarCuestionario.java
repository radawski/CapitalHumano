package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorCuestionarios;
import capitalhumano.dtos.BloqueDTO;
import capitalhumano.dtos.IngresoCuestionarioDTO;
import capitalhumano.dtos.PreguntaBloqueDTO;
import capitalhumano.dtos.RespuestasBloqueDTO;
import capitalhumano.enumeraciones.ResultadoIngreso;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** {@code <<boundary>>} Completar cuestionario. CU026 (#14, #15, #16 y #17). */
@WebServlet(urlPatterns = {"/cuestionario", "/cuestionario/*"})
public class PantallaCompletarCuestionario extends HttpServlet {

    private static final String ATTR_INGRESO = "cu026.ingreso";
    /** Bloque que se está mostrando en el wizard (para validar las respuestas en el flujo 6.A). */
    private static final String ATTR_BLOQUE = "cu026.bloque";
    private static final String PARAM_RESPUESTA = "respuesta_";
    private static final String JSP = "/WEB-INF/jsp/completar-cuestionario.jsp";

    private GestorCuestionarios gestorCuestionarios;

    @Override
    public void init() {
        gestorCuestionarios = InicializadorAplicacion.obtener(getServletContext(), GestorCuestionarios.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Completar cuestionario");
        String path = request.getPathInfo();

        if ("/bloque".equals(path)) {
            mostrarBloquePendiente(request, response);
            return;
        }
        if ("/completado".equals(path)) {
            request.setAttribute("exito", "El Cuestionario ha sido completado con éxito");
            request.getRequestDispatcher(JSP).forward(request, response);
            return;
        }

        // Ingreso: cada ingreso con clave cuenta un acceso, por eso se resuelve una sola vez
        // y se redirige (un refresh no vuelve a llamar al gestor).
        String clave = request.getParameter("clave");
        if (clave != null && !clave.isBlank()) {
            IngresoCuestionarioDTO ingreso = gestorCuestionarios.ingresarCuestionario(clave);
            HttpSession session = request.getSession();
            session.removeAttribute(ATTR_BLOQUE);
            session.setAttribute(ATTR_INGRESO, ingreso);
            response.sendRedirect(request.getContextPath() + "/cuestionario");
            return;
        }

        IngresoCuestionarioDTO ingreso = obtenerIngreso(request);

        if (ingreso == null) {
            request.setAttribute("mensaje", "Ingrese con su clave de cuestionario.");
        } else if (ingreso.getResultado() == ResultadoIngreso.RECHAZADO) {
            request.setAttribute("mensaje", ingreso.getMensaje());
        } else if (ingreso.getResultado() == ResultadoIngreso.CONTINUAR) {
            // Flujo 1.C: retoma el cuestionario en proceso
            response.sendRedirect(request.getContextPath() + "/cuestionario/bloque");
            return;
        } else {
            request.setAttribute("instrucciones", ingreso.getInstrucciones());
        }
        request.getRequestDispatcher(JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Completar cuestionario");
        String path = request.getPathInfo();
        IngresoCuestionarioDTO ingreso = obtenerIngreso(request);

        if (ingreso == null || ingreso.getCuestionarioId() == null) {
            response.sendRedirect(request.getContextPath() + "/cuestionario");
            return;
        }
        HttpSession session = request.getSession();

        if ("/cancelar".equals(path)) {
            // Flujo 2.A: el CU termina, el cuestionario sigue ACTIVO
            session.removeAttribute(ATTR_INGRESO);
            request.setAttribute("mensaje", "Operación cancelada. Puede volver a ingresar con su clave.");
            request.getRequestDispatcher(JSP).forward(request, response);
            return;
        }

        if ("/aceptar".equals(path)) {
            // Paso 3: sorteo de preguntas y armado de bloques
            BloqueDTO bloque = gestorCuestionarios.comenzarCuestionario(ingreso.getCuestionarioId());
            session.setAttribute(ATTR_BLOQUE, bloque);
            response.sendRedirect(request.getContextPath() + "/cuestionario/bloque");
            return;
        }

        if ("/responder".equals(path)) {
            responderBloque(request, response, ingreso);
            return;
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /** Paso 4 y flujo 1.C: muestra el bloque pendiente. */
    private void mostrarBloquePendiente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        IngresoCuestionarioDTO ingreso = obtenerIngreso(request);
        if (ingreso == null || ingreso.getCuestionarioId() == null) {
            response.sendRedirect(request.getContextPath() + "/cuestionario");
            return;
        }
        HttpSession session = request.getSession();
        BloqueDTO bloque = (BloqueDTO) session.getAttribute(ATTR_BLOQUE);
        if (bloque == null) {
            bloque = gestorCuestionarios.obtenerBloquePendiente(ingreso.getCuestionarioId());
        }
        if (bloque == null) {
            session.removeAttribute(ATTR_INGRESO);
            request.setAttribute("mensaje", "No posee un cuestionario disponible para completar");
            request.getRequestDispatcher(JSP).forward(request, response);
            return;
        }
        session.setAttribute(ATTR_BLOQUE, bloque);
        request.setAttribute("bloque", bloque);
        request.setAttribute("respuestas", Map.of());
        request.getRequestDispatcher(JSP).forward(request, response);
    }

    /** Pasos 5 a 8: valida (6.A), guarda el bloque y avanza (8.A) o termina (paso 8). */
    private void responderBloque(HttpServletRequest request, HttpServletResponse response,
                                 IngresoCuestionarioDTO ingreso) throws ServletException, IOException {
        HttpSession session = request.getSession();
        BloqueDTO bloque = (BloqueDTO) session.getAttribute(ATTR_BLOQUE);
        if (bloque == null) {
            response.sendRedirect(request.getContextPath() + "/cuestionario/bloque");
            return;
        }

        Map<UUID, UUID> respuestas = new LinkedHashMap<>();
        Map<String, String> respuestasTexto = new HashMap<>();
        for (PreguntaBloqueDTO pregunta : bloque.getPreguntas()) {
            String valor = request.getParameter(PARAM_RESPUESTA + pregunta.getPreguntaId());
            if (valor != null && !valor.isBlank()) {
                try {
                    respuestas.put(pregunta.getPreguntaId(), UUID.fromString(valor));
                    respuestasTexto.put(pregunta.getPreguntaId().toString(), valor);
                } catch (IllegalArgumentException e) {
                    // valor manipulado: se trata como sin responder
                }
            }
        }

        List<UUID> sinResponder = validarTodasRespondidas(bloque, respuestas);
        if (!sinResponder.isEmpty()) {
            // Flujo 6.A: se mantiene el bloque con las respuestas ya dadas
            request.setAttribute("bloque", bloque);
            request.setAttribute("respuestas", respuestasTexto);
            request.setAttribute("sinResponder", sinResponder.stream().map(UUID::toString).toList());
            request.setAttribute("error", "Es obligatorio responder todas las preguntas");
            request.getRequestDispatcher(JSP).forward(request, response);
            return;
        }

        RespuestasBloqueDTO respuestasBloque =
                new RespuestasBloqueDTO(ingreso.getCuestionarioId(), bloque.getNroBloque(), respuestas);
        BloqueDTO siguiente = gestorCuestionarios.responderBloque(respuestasBloque);

        if (siguiente != null) {
            // Flujo 8.A: siguiente pantalla del wizard
            session.setAttribute(ATTR_BLOQUE, siguiente);
            response.sendRedirect(request.getContextPath() + "/cuestionario/bloque");
        } else {
            // Paso 8: cuestionario completo
            session.removeAttribute(ATTR_BLOQUE);
            session.removeAttribute(ATTR_INGRESO);
            response.sendRedirect(request.getContextPath() + "/cuestionario/completado");
        }
    }

    private List<UUID> validarTodasRespondidas(BloqueDTO bloque, Map<UUID, UUID> respuestas) {
        List<UUID> sinResponder = new ArrayList<>();
        for (PreguntaBloqueDTO pregunta : bloque.getPreguntas()) {
            if (!respuestas.containsKey(pregunta.getPreguntaId())) {
                sinResponder.add(pregunta.getPreguntaId());
            }
        }
        return sinResponder;
    }

    private IngresoCuestionarioDTO obtenerIngreso(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (IngresoCuestionarioDTO) session.getAttribute(ATTR_INGRESO);
    }
}
