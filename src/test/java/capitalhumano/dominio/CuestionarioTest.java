package capitalhumano.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import capitalhumano.enumeraciones.Genero;
import capitalhumano.enumeraciones.NombreEstadoCuestionario;
import capitalhumano.enumeraciones.TipoDocumento;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CuestionarioTest {

    @Test
    void generarClaveTieneLongitud8YEsAlfanumericaEnMayusculas() {
        Cuestionario cuestionario = new Cuestionario(candidatoDePrueba(1014), configuracionDePrueba());

        for (int i = 0; i < 100; i++) {
            String clave = cuestionario.generarClave();
            assertNotNull(clave);
            assertTrue(clave.matches("^[A-Z0-9]{8}$"), "Clave inválida: " + clave);
            assertEquals(clave, cuestionario.getClave());
        }
    }

    @Test
    void generarClaveProduceClavesUnicasEnLote() {
        Cuestionario cuestionario = new Cuestionario(candidatoDePrueba(1014), configuracionDePrueba());
        Set<String> claves = new HashSet<>();

        for (int i = 0; i < 50; i++) {
            assertTrue(claves.add(cuestionario.generarClave()));
        }
    }

    @Test
    void constructorInicializaCuestionarioEnEstadoActivoConClaveValida() {
        Candidato candidato = candidatoDePrueba(1007);
        ConfiguracionCuestionario configuracion = configuracionDePrueba();

        Cuestionario cuestionario = new Cuestionario(candidato, configuracion);

        assertNotNull(cuestionario.getId());
        assertEquals(NombreEstadoCuestionario.ACTIVO, cuestionario.getNombreEstado());
        assertEquals(0, cuestionario.getCantidadAccesos());
        assertEquals(candidato, cuestionario.getCandidato());
        assertEquals(configuracion, cuestionario.getConfiguracion());
        assertNotNull(cuestionario.getClave());
        assertTrue(cuestionario.getClave().matches("^[A-Z0-9]{8}$"));
    }

    @Test
    void constructorRechazaCandidatoOConfiguracionNulos() {
        ConfiguracionCuestionario configuracion = configuracionDePrueba();
        Candidato candidato = candidatoDePrueba(1014);

        assertThrows(NullPointerException.class, () -> new Cuestionario(null, configuracion));
        assertThrows(NullPointerException.class, () -> new Cuestionario(candidato, null));
    }

    private static Candidato candidatoDePrueba(int nro) {
        return new Candidato(
                nro, "CASTRO", "PAULA", TipoDocumento.DNI, "39141014",
                LocalDate.of(1996, 5, 17), Genero.M, "ARGENTINA",
                "paula.castro@mail.com", "UNIVERSITARIO COMPLETO", false);
    }

    private static ConfiguracionCuestionario configuracionDePrueba() {
        return new ConfiguracionCuestionario(
                UUID.fromString("30000000-0000-0000-0000-000000000001"),
                "Instrucciones de prueba", 3, 5, Duration.ofHours(2), Duration.ofDays(7));
    }
}
