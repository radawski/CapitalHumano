package capitalhumano.cu001;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import capitalhumano.adapters.AdaptadorLDAP;
import capitalhumano.adapters.AutenticacionCandidatoClave;
import capitalhumano.adapters.AutenticacionConsultorLDAP;
import capitalhumano.controladores.GestorAutenticacion;
import capitalhumano.controladores.GestorCuestionarios;
import capitalhumano.cu026.DatosCU026;
import capitalhumano.dominio.Consultor;
import capitalhumano.dominio.Cuestionario;
import capitalhumano.dominio.EstadoCompleto;
import capitalhumano.dtos.CredencialesLoginDTO;
import capitalhumano.dtos.ResultadoLoginDTO;
import capitalhumano.enumeraciones.TipoAcceso;
import capitalhumano.enumeraciones.TipoDocumento;
import capitalhumano.persistencia.CompetenciaDAO;
import capitalhumano.persistencia.EvaluacionDAO;
import capitalhumano.persistencia.PreguntaDAO;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

/** CU001 (#18): estrategias de autenticación del consultor y del candidato, y el gestor que las usa. */
class AutenticacionTest {

    // ---- Consultor (LDAP simulado: consultor / consultor) ----

    private final AutenticacionConsultorLDAP consultorLDAP = new AutenticacionConsultorLDAP(new AdaptadorLDAP());

    @Test
    void consultorConCredencialesValidasQuedaAutenticado() {
        ResultadoLoginDTO resultado = consultorLDAP.autenticar(consultor("consultor", "consultor"));

        assertTrue(resultado.isAutenticado());
        assertEquals(TipoAcceso.CONSULTOR, resultado.getTipoAcceso());
        assertNull(resultado.getConsultorId());
        assertNull(resultado.getCuestionarioId());
    }

    @Test
    void consultorConContrasenaIncorrectaNoQuedaAutenticado() {
        assertFalse(consultorLDAP.autenticar(consultor("consultor", "otra")).isAutenticado());
        assertFalse(consultorLDAP.autenticar(consultor("consultor", "CONSULTOR")).isAutenticado());
        assertFalse(consultorLDAP.autenticar(consultor("otro", "consultor")).isAutenticado());
        assertFalse(consultorLDAP.autenticar(consultor(null, null)).isAutenticado());
    }

    @Test
    void consultorConUsuarioEnMayusculasQuedaAutenticado() {
        // CU001 pide el usuario siempre en mayúscula
        assertTrue(consultorLDAP.autenticar(consultor("CONSULTOR", "consultor")).isAutenticado());
    }

    @Test
    void consultorDelDominioSeAutenticaSiTieneNombreDeUsuario() {
        assertTrue(new Consultor(UUID.randomUUID(), "consultor", "ANA", "LOPEZ", "ana@mail.com").autenticar());
        assertFalse(new Consultor(UUID.randomUUID(), " ", "ANA", "LOPEZ", "ana@mail.com").autenticar());
        assertFalse(new Consultor(UUID.randomUUID(), null, "ANA", "LOPEZ", "ana@mail.com").autenticar());
    }

    // ---- Candidato (documento + clave, contra GestorCuestionarios real) ----

    @Test
    void candidatoConDocumentoYClaveValidosQuedaAutenticado() {
        Cuestionario cuestionario = DatosCU026.activo(3);
        AutenticacionCandidatoClave estrategia = estrategiaCandidato(cuestionario);

        ResultadoLoginDTO resultado = estrategia.autenticar(
                candidato(TipoDocumento.DNI, "30111222", cuestionario.getClave()));

        assertTrue(resultado.isAutenticado());
        assertEquals(TipoAcceso.CANDIDATO, resultado.getTipoAcceso());
        assertEquals(cuestionario.getId(), resultado.getCuestionarioId());
        assertEquals(cuestionario.getClave(), resultado.getClaveCuestionario());
    }

    @Test
    void candidatoConDocumentoDistintoNoQuedaAutenticado() {
        Cuestionario cuestionario = DatosCU026.activo(3);
        AutenticacionCandidatoClave estrategia = estrategiaCandidato(cuestionario);

        assertFalse(estrategia.autenticar(
                candidato(TipoDocumento.DNI, "99999999", cuestionario.getClave())).isAutenticado());
        assertFalse(estrategia.autenticar(
                candidato(TipoDocumento.PP, "30111222", cuestionario.getClave())).isAutenticado());
        assertFalse(estrategia.autenticar(
                candidato(TipoDocumento.DNI, "30111222", "NOEXISTE")).isAutenticado());
    }

    @Test
    void candidatoConCuestionarioCompletoNoQuedaAutenticado() {
        // 3.A.2: sólo se ingresa a cuestionarios "Activo" o "En proceso"
        Cuestionario completo = DatosCU026.cuestionario(new EstadoCompleto(), 3,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(1), 1);
        AutenticacionCandidatoClave estrategia = estrategiaCandidato(completo);

        ResultadoLoginDTO resultado = estrategia.autenticar(
                candidato(TipoDocumento.DNI, "30111222", completo.getClave()));

        assertFalse(resultado.isAutenticado());
        assertNull(resultado.getCuestionarioId());
        assertNull(resultado.getClaveCuestionario());
    }

    // ---- GestorAutenticacion (contexto del Strategy) ----

    @Test
    void gestorDelegaEnLaEstrategiaSeleccionada() {
        CredencialesLoginDTO credenciales = consultor("consultor", "consultor");
        ResultadoLoginDTO esperado = new ResultadoLoginDTO(true, TipoAcceso.CONSULTOR, null, "consultor", null, null);
        CredencialesLoginDTO[] recibidas = new CredencialesLoginDTO[1];

        GestorAutenticacion gestor = new GestorAutenticacion();
        gestor.seleccionarEstrategia(c -> {
            recibidas[0] = c;
            return esperado;
        });

        assertSame(esperado, gestor.autenticar(credenciales));
        assertSame(credenciales, recibidas[0]);
    }

    @Test
    void gestorSinEstrategiaLanzaExcepcion() {
        GestorAutenticacion gestor = new GestorAutenticacion();
        assertThrows(IllegalStateException.class, () -> gestor.autenticar(consultor("consultor", "consultor")));
    }

    // ---- Ayudantes ----

    private static CredencialesLoginDTO consultor(String usuario, String password) {
        return new CredencialesLoginDTO(TipoAcceso.CONSULTOR, usuario, password, null, null, null);
    }

    private static CredencialesLoginDTO candidato(TipoDocumento tipo, String nro, String clave) {
        return new CredencialesLoginDTO(TipoAcceso.CANDIDATO, null, null, tipo, nro, clave);
    }

    private static AutenticacionCandidatoClave estrategiaCandidato(Cuestionario cuestionario) {
        EvaluacionDAO evaluacionDAO = fake(EvaluacionDAO.class, Map.of(
                "obtenerCuestionarioPorClave",
                args -> cuestionario.getClave().equals(args[0]) ? cuestionario : null));
        GestorCuestionarios gestor = new GestorCuestionarios(evaluacionDAO,
                fake(CompetenciaDAO.class, Map.of()), fake(PreguntaDAO.class, Map.of()));
        return new AutenticacionCandidatoClave(gestor);
    }

    @SuppressWarnings("unchecked")
    private static <T> T fake(Class<T> tipo, Map<String, Function<Object[], Object>> metodos) {
        Map<String, Function<Object[], Object>> copia = new HashMap<>(metodos);
        return (T) Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[] {tipo}, (proxy, metodo, args) -> {
            Function<Object[], Object> f = copia.get(metodo.getName());
            if (f == null) {
                throw new UnsupportedOperationException(metodo.getName() + " no se usa en este test");
            }
            return f.apply(args);
        });
    }
}
