package capitalhumano.pantallas;

import capitalhumano.InicializadorAplicacion;
import capitalhumano.adapters.AutenticacionCandidatoClave;
import capitalhumano.adapters.AutenticacionConsultorLDAP;
import capitalhumano.controladores.GestorAutenticacion;
import capitalhumano.dtos.CredencialesLoginDTO;
import capitalhumano.dtos.ResultadoLoginDTO;
import capitalhumano.enumeraciones.TipoAcceso;
import capitalhumano.enumeraciones.TipoDocumento;
import capitalhumano.servicios.EstrategiaAutenticacion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

/**
 * {@code <<boundary>>} CU001 Autenticar usuario.
 * <ul>
 *   <li>{@code GET /login}: el usuario elige el tipo de acceso (paso 1).</li>
 *   <li>{@code GET /login?tipo=CONSULTOR|CANDIDATO}: pide los datos de ese tipo de acceso (pasos 2 y 3.A.1).</li>
 *   <li>{@code POST /login}: autentica con la estrategia del tipo de acceso (pasos 3–5, 3.A.2, 5.A).</li>
 *   <li>{@code GET /login?salir=1}: cierra la sesión.</li>
 * </ul>
 * El consultor queda en sesión con el atributo {@value #ATTR_USUARIO} (en minúscula, como lo guarda
 * la base y lo busca la auditoría). El candidato queda con {@value #ATTR_CANDIDATO} (su documento, para
 * la cabecera) y con la clave validada en {@value #ATTR_CLAVE_PENDIENTE}, que CU026 consume una sola vez:
 * la clave no viaja por la URL, así nadie entra al cuestionario sin pasar por el paso 3.A.2.
 * {@link FiltroAutenticacion} usa esos atributos para decidir qué puede abrir cada uno.
 */
@WebServlet(urlPatterns = {"/login"})
public class PantallaAutenticar extends HttpServlet {

    static final String ATTR_USUARIO = "usuario";
    static final String ATTR_CANDIDATO = "candidato";
    static final String ATTR_CLAVE_PENDIENTE = "cu001.clavePendiente";
    static final String ERROR_CONSULTOR = "El usuario o la contraseña no son válidos";
    static final String ERROR_CANDIDATO =
            "Los datos ingresados no son válidos o no existe un cuestionario para el Candidato";

    private static final String JSP = "/WEB-INF/jsp/autenticar.jsp";

    private AutenticacionConsultorLDAP autenticacionConsultor;
    private AutenticacionCandidatoClave autenticacionCandidato;

    @Override
    public void init() {
        autenticacionConsultor = InicializadorAplicacion.obtener(getServletContext(), AutenticacionConsultorLDAP.class);
        autenticacionCandidato = InicializadorAplicacion.obtener(getServletContext(), AutenticacionCandidatoClave.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getParameter("salir") != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        mostrar(request, response, parsearTipoAcceso(request.getParameter("tipo")));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        TipoAcceso tipoAcceso = parsearTipoAcceso(request.getParameter("tipo"));
        if (tipoAcceso == null) {
            mostrar(request, response, null);
            return;
        }

        CredencialesLoginDTO credenciales = leerCredenciales(request, tipoAcceso);
        // El gestor guarda la estrategia elegida: uno nuevo por intento de ingreso
        GestorAutenticacion gestorAutenticacion = new GestorAutenticacion();
        gestorAutenticacion.seleccionarEstrategia(estrategiaPara(tipoAcceso));
        ResultadoLoginDTO resultado = gestorAutenticacion.autenticar(credenciales);

        if (tipoAcceso == TipoAcceso.CONSULTOR) {
            if (resultado.isAutenticado()) {
                // Paso 5: sesión nueva con el consultor y al menú principal
                HttpSession session = renovarSesion(request);
                session.setAttribute(ATTR_USUARIO, credenciales.getUsuario().toLowerCase(Locale.ROOT));
                response.sendRedirect(request.getContextPath() + "/");
            } else {
                // 5.A: mensaje y campos en blanco
                request.setAttribute("error", ERROR_CONSULTOR);
                mostrar(request, response, TipoAcceso.CONSULTOR);
            }
            return;
        }

        if (resultado.isAutenticado()) {
            // 3.A.3: sesión nueva del candidato y directo a CU026, que registra el acceso una sola vez
            HttpSession session = renovarSesion(request);
            session.setAttribute(ATTR_CANDIDATO,
                    credenciales.getTipoDocumento() + " " + credenciales.getNroDocumento());
            session.setAttribute(ATTR_CLAVE_PENDIENTE, resultado.getClaveCuestionario());
            response.sendRedirect(request.getContextPath() + "/cuestionario");
        } else {
            // 3.A.2: se conserva el documento y se borra la clave
            request.setAttribute("error", ERROR_CANDIDATO);
            request.setAttribute("tipoDocumento", credenciales.getTipoDocumento());
            request.setAttribute("nroDocumento", credenciales.getNroDocumento());
            mostrar(request, response, TipoAcceso.CANDIDATO);
        }
    }

    private void mostrar(HttpServletRequest request, HttpServletResponse response, TipoAcceso tipoAcceso)
            throws ServletException, IOException {
        request.setAttribute("titulo", "Ingreso al sistema");
        request.setAttribute("tipoAcceso", tipoAcceso);
        request.setAttribute("tiposDocumento", TipoDocumento.values());
        request.getRequestDispatcher(JSP).forward(request, response);
    }

    private EstrategiaAutenticacion estrategiaPara(TipoAcceso tipoAcceso) {
        return tipoAcceso == TipoAcceso.CONSULTOR ? autenticacionConsultor : autenticacionCandidato;
    }

    private static CredencialesLoginDTO leerCredenciales(HttpServletRequest request, TipoAcceso tipoAcceso) {
        if (tipoAcceso == TipoAcceso.CONSULTOR) {
            return new CredencialesLoginDTO(tipoAcceso, mayusculas(request.getParameter("usuario")),
                    request.getParameter("password"), null, null, null);
        }
        return new CredencialesLoginDTO(tipoAcceso, null, null,
                parsearTipoDocumento(request.getParameter("tipoDocumento")),
                recortar(request.getParameter("nroDocumento")),
                mayusculas(request.getParameter("clave")));
    }

    /** Descarta la sesión anterior (otro usuario o un cuestionario a medias) y abre una nueva. */
    private static HttpSession renovarSesion(HttpServletRequest request) {
        HttpSession anterior = request.getSession(false);
        if (anterior != null) {
            anterior.invalidate();
        }
        return request.getSession(true);
    }

    private static TipoAcceso parsearTipoAcceso(String valor) {
        try {
            return valor == null ? null : TipoAcceso.valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static TipoDocumento parsearTipoDocumento(String valor) {
        try {
            return valor == null || valor.isBlank() ? null : TipoDocumento.valueOf(valor.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String recortar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private static String mayusculas(String valor) {
        return recortar(valor).toUpperCase(Locale.ROOT);
    }
}
