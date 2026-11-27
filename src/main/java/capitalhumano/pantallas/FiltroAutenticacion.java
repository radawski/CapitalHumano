package capitalhumano.pantallas;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Precondición "Actor autenticado en el sistema" de los CU de la entrega (CU001: "cualquier
 * funcionalidad del sistema debe percibirse como una extensión de este CU").
 * <p>
 * No es una clase del diseño sino infraestructura del contenedor web, como
 * {@code InicializadorAplicacion}. Decide por ruta y perfil de la sesión:
 * <ul>
 *   <li>Públicas: {@code /login} y los recursos estáticos.</li>
 *   <li>Del candidato: {@code /cuestionario} (CU026, al que se llega desde CU001 paso 3.A.3).</li>
 *   <li>Del consultor: todo lo demás, incluido el menú principal. Una pantalla nueva queda protegida
 *       sin tener que agregarla acá.</li>
 * </ul>
 * Sin sesión se vuelve a la elección del tipo de acceso; con el perfil equivocado, a la pantalla
 * inicial del propio perfil.
 */
@WebFilter(urlPatterns = {"/*"})
public class FiltroAutenticacion implements Filter {

    enum Perfil { SIN_SESION, CONSULTOR, CANDIDATO }

    static final String RUTA_LOGIN = "/login";
    static final String RUTA_MENU = "/";
    static final String RUTA_CUESTIONARIO = "/cuestionario";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String ruta = ruta(request);
        String destino = decidir(ruta, perfil(request.getSession(false)));
        if (destino != null) {
            response.sendRedirect(request.getContextPath() + destino);
            return;
        }
        if (!esPublica(ruta)) {
            // Después de Salir, "Atrás" no tiene que mostrar una pantalla protegida desde la caché
            response.setHeader("Cache-Control", "no-store");
        }
        chain.doFilter(request, response);
    }

    /**
     * Devuelve {@code null} si la ruta se puede abrir con ese perfil, o la ruta (relativa al contexto)
     * a la que hay que redirigir.
     */
    static String decidir(String ruta, Perfil perfil) {
        if (esPublica(ruta)) {
            return null;
        }
        if (perfil == Perfil.SIN_SESION) {
            return RUTA_LOGIN;
        }
        boolean delCandidato = esDelCandidato(ruta);
        if (perfil == Perfil.CANDIDATO) {
            return delCandidato ? null : RUTA_CUESTIONARIO;
        }
        return delCandidato ? RUTA_MENU : null;
    }

    static Perfil perfil(HttpSession session) {
        if (session == null) {
            return Perfil.SIN_SESION;
        }
        if (session.getAttribute(PantallaAutenticar.ATTR_USUARIO) != null) {
            return Perfil.CONSULTOR;
        }
        if (session.getAttribute(PantallaAutenticar.ATTR_CANDIDATO) != null) {
            return Perfil.CANDIDATO;
        }
        return Perfil.SIN_SESION;
    }

    /**
     * Ruta ya normalizada por el contenedor (servlet path + path info). No se usa el request URI
     * porque llega sin normalizar: {@code /css/../preguntas} pasaría como pública.
     */
    private static String ruta(HttpServletRequest request) {
        String servletPath = request.getServletPath() == null ? "" : request.getServletPath();
        String pathInfo = request.getPathInfo() == null ? "" : request.getPathInfo();
        String ruta = servletPath + pathInfo;
        return ruta.isEmpty() ? RUTA_MENU : ruta;
    }

    private static boolean esPublica(String ruta) {
        return ruta.equals(RUTA_LOGIN) || ruta.startsWith("/css/") || ruta.equals("/favicon.ico");
    }

    private static boolean esDelCandidato(String ruta) {
        return ruta.equals(RUTA_CUESTIONARIO) || ruta.startsWith(RUTA_CUESTIONARIO + "/");
    }
}
