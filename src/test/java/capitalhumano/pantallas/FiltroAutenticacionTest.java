package capitalhumano.pantallas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import capitalhumano.pantallas.FiltroAutenticacion.Perfil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

/** CU001: precondición "Actor autenticado" de los CU, según la ruta y el perfil de la sesión. */
class FiltroAutenticacionTest {

    private static final String[] RUTAS_CONSULTOR = {
        "/", "/index.jsp", "/preguntas", "/preguntas/nueva", "/preguntas/eliminar",
        "/evaluar-candidatos", "/evaluar-candidatos/generar", "/orden-merito", "/orden-merito/imprimir",
        "/importar-candidatos", "/exportar-resultados", "/puestos", "/reporte-comparativo"
    };
    private static final String[] RUTAS_CANDIDATO = {
        "/cuestionario", "/cuestionario/bloque", "/cuestionario/responder", "/cuestionario/completado"
    };
    private static final String[] RUTAS_PUBLICAS = {"/login", "/css/estilos.css", "/favicon.ico"};

    // ---- decidir(ruta, perfil) ----

    @Test
    void lasRutasPublicasSeAbrenConCualquierPerfil() {
        for (String ruta : RUTAS_PUBLICAS) {
            for (Perfil perfil : Perfil.values()) {
                assertNull(FiltroAutenticacion.decidir(ruta, perfil), ruta + " con " + perfil);
            }
        }
    }

    @Test
    void sinSesionTodoLoDemasVaALaEleccionDeTipoDeAcceso() {
        for (String ruta : RUTAS_CONSULTOR) {
            assertEquals("/login", FiltroAutenticacion.decidir(ruta, Perfil.SIN_SESION), ruta);
        }
        for (String ruta : RUTAS_CANDIDATO) {
            assertEquals("/login", FiltroAutenticacion.decidir(ruta, Perfil.SIN_SESION), ruta);
        }
    }

    @Test
    void elConsultorAbreSusPantallasYNoElCuestionario() {
        for (String ruta : RUTAS_CONSULTOR) {
            assertNull(FiltroAutenticacion.decidir(ruta, Perfil.CONSULTOR), ruta);
        }
        for (String ruta : RUTAS_CANDIDATO) {
            assertEquals("/", FiltroAutenticacion.decidir(ruta, Perfil.CONSULTOR), ruta);
        }
    }

    @Test
    void elCandidatoSoloAbreElCuestionario() {
        for (String ruta : RUTAS_CANDIDATO) {
            assertNull(FiltroAutenticacion.decidir(ruta, Perfil.CANDIDATO), ruta);
        }
        for (String ruta : RUTAS_CONSULTOR) {
            assertEquals("/cuestionario", FiltroAutenticacion.decidir(ruta, Perfil.CANDIDATO), ruta);
        }
    }

    @Test
    void unaRutaParecidaALaPublicaNoEsPublica() {
        assertEquals("/login", FiltroAutenticacion.decidir("/login-falso", Perfil.SIN_SESION));
        assertEquals("/login", FiltroAutenticacion.decidir("/cssx", Perfil.SIN_SESION));
        assertEquals("/cuestionario", FiltroAutenticacion.decidir("/cuestionarios", Perfil.CANDIDATO));
    }

    // ---- perfil(session) ----

    @Test
    void elPerfilSaleDeLosAtributosQueDejaElLogin() {
        assertEquals(Perfil.SIN_SESION, FiltroAutenticacion.perfil(null));
        assertEquals(Perfil.SIN_SESION, FiltroAutenticacion.perfil(sesion(Map.of())));
        assertEquals(Perfil.CONSULTOR, FiltroAutenticacion.perfil(sesion(Map.of("usuario", "consultor"))));
        assertEquals(Perfil.CANDIDATO, FiltroAutenticacion.perfil(sesion(Map.of("candidato", "DNI 35111001"))));
    }

    // ---- doFilter ----

    @Test
    void sinSesionNoSeEjecutaLaPantallaYSeRedirigeAlLogin() throws Exception {
        Respuesta respuesta = new Respuesta();
        AtomicBoolean siguio = new AtomicBoolean(false);

        new FiltroAutenticacion().doFilter(request("/preguntas", null, null), respuesta.proxy(),
                (rq, rs) -> siguio.set(true));

        assertFalse(siguio.get());
        assertEquals("/capital-humano/login", respuesta.redireccion);
    }

    @Test
    void conSesionDeConsultorSeEjecutaLaPantallaSinCache() throws Exception {
        Respuesta respuesta = new Respuesta();
        AtomicBoolean siguio = new AtomicBoolean(false);

        new FiltroAutenticacion().doFilter(
                request("/preguntas", "/eliminar", sesion(Map.of("usuario", "consultor"))),
                respuesta.proxy(), (rq, rs) -> siguio.set(true));

        assertTrue(siguio.get());
        assertNull(respuesta.redireccion);
        assertEquals("no-store", respuesta.headers.get("Cache-Control"));
    }

    @Test
    void elCandidatoQueQuiereAbrirElMenuVuelveAlCuestionario() throws Exception {
        Respuesta respuesta = new Respuesta();
        AtomicBoolean siguio = new AtomicBoolean(false);

        new FiltroAutenticacion().doFilter(request("/index.jsp", null, sesion(Map.of("candidato", "DNI 1"))),
                respuesta.proxy(), (rq, rs) -> siguio.set(true));

        assertFalse(siguio.get());
        assertEquals("/capital-humano/cuestionario", respuesta.redireccion);
    }

    // ---- dobles de la API de servlets (el proyecto no usa Mockito) ----

    private static HttpSession sesion(Map<String, Object> atributos) {
        return (HttpSession) Proxy.newProxyInstance(HttpSession.class.getClassLoader(),
                new Class<?>[] {HttpSession.class}, (p, m, args) ->
                        "getAttribute".equals(m.getName()) ? atributos.get((String) args[0]) : null);
    }

    private static HttpServletRequest request(String servletPath, String pathInfo, HttpSession session) {
        return (HttpServletRequest) Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(),
                new Class<?>[] {HttpServletRequest.class}, (p, m, args) -> switch (m.getName()) {
                    case "getServletPath" -> servletPath;
                    case "getPathInfo" -> pathInfo;
                    case "getContextPath" -> "/capital-humano";
                    case "getSession" -> session;
                    default -> null;
                });
    }

    private static final class Respuesta {
        String redireccion;
        final Map<String, String> headers = new HashMap<>();

        HttpServletResponse proxy() {
            return (HttpServletResponse) Proxy.newProxyInstance(HttpServletResponse.class.getClassLoader(),
                    new Class<?>[] {HttpServletResponse.class}, (p, m, args) -> {
                        if ("sendRedirect".equals(m.getName())) {
                            redireccion = (String) args[0];
                        } else if ("setHeader".equals(m.getName())) {
                            headers.put((String) args[0], (String) args[1]);
                        }
                        return null;
                    });
        }
    }
}
