package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.controladores.GestorCuestionarios;
import capitalhumano.dtos.IngresoCuestionarioDTO;
import capitalhumano.enumeraciones.ResultadoIngreso;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/** {@code <<boundary>>} Completar cuestionario. CU026 (#14, #15, #16 y #17). */
@WebServlet(urlPatterns = {"/cuestionario", "/cuestionario/*"})
public class PantallaCompletarCuestionario extends HttpServlet {

    private static final String ATTR_INGRESO = "cu026.ingreso";
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

        // Wizard: lo implementa #16
        if ("/bloque".equals(path)) {
            request.setAttribute("mensaje", "Pantalla del cuestionario pendiente (#16).");
            request.getRequestDispatcher(JSP).forward(request, response);
            return;
        }

        // Ingreso: cada ingreso con clave cuenta un acceso, por eso se resuelve una sola vez
        // y se redirige (un refresh no vuelve a llamar al gestor).
        String clave = request.getParameter("clave");
        if (clave != null && !clave.isBlank()) {
            IngresoCuestionarioDTO ingreso = gestorCuestionarios.ingresarCuestionario(clave);
            request.getSession().setAttribute(ATTR_INGRESO, ingreso);
            response.sendRedirect(request.getContextPath() + "/cuestionario");
            return;
        }

        HttpSession session = request.getSession(false);
        IngresoCuestionarioDTO ingreso = session == null ? null
                : (IngresoCuestionarioDTO) session.getAttribute(ATTR_INGRESO);

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
        String path = request.getPathInfo();
        HttpSession session = request.getSession(false);
        IngresoCuestionarioDTO ingreso = session == null ? null
                : (IngresoCuestionarioDTO) session.getAttribute(ATTR_INGRESO);

        if (ingreso == null) {
            response.sendRedirect(request.getContextPath() + "/cuestionario");
            return;
        }

        if ("/cancelar".equals(path)) {
            // Flujo 2.A: el CU termina, el cuestionario sigue ACTIVO
            session.removeAttribute(ATTR_INGRESO);
            request.setAttribute("titulo", "Completar cuestionario");
            request.setAttribute("mensaje", "Operación cancelada. Puede volver a ingresar con su clave.");
            request.getRequestDispatcher(JSP).forward(request, response);
            return;
        }

        if ("/aceptar".equals(path)) {
            // Paso 2 -> paso 3 (sorteo y armado de bloques): lo implementa #15
            try {
                gestorCuestionarios.comenzarCuestionario(ingreso.getCuestionarioId());
            } catch (UnsupportedOperationException e) {
                // TEMPORAL: borrar este try/catch cuando #15 esté mergeado
                request.setAttribute("titulo", "Completar cuestionario");
                request.setAttribute("mensaje", "Armado del cuestionario pendiente (#15).");
                request.getRequestDispatcher(JSP).forward(request, response);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/cuestionario/bloque");
            return;
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}