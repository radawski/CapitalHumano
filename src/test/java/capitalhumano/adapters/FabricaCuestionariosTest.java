package capitalhumano.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import capitalhumano.dominio.Candidato;
import capitalhumano.dominio.ConfiguracionCuestionario;
import capitalhumano.dominio.Cuestionario;
import capitalhumano.enumeraciones.Genero;
import capitalhumano.enumeraciones.NombreEstadoCuestionario;
import capitalhumano.enumeraciones.TipoDocumento;
import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FabricaCuestionariosTest {

    @Test
    void crearCuestionarioConstruyeCuestionarioActivoConClaveUnica() {
        FabricaCuestionarios fabrica = new FabricaCuestionarios();
        Candidato candidato1 = candidato(1007, "SANCHEZ", "DIEGO", "33777007");
        Candidato candidato2 = candidato(1014, "CASTRO", "PAULA", "39141014");
        ConfiguracionCuestionario configuracion = configuracion();

        Cuestionario c1 = fabrica.crearCuestionario(candidato1, configuracion);
        Cuestionario c2 = fabrica.crearCuestionario(candidato2, configuracion);

        assertNotNull(c1.getId());
        assertEquals(NombreEstadoCuestionario.ACTIVO, c1.getNombreEstado());
        assertEquals(candidato1, c1.getCandidato());
        assertEquals(configuracion, c1.getConfiguracion());
        assertTrue(c1.getClave().matches("^[A-Z0-9]{8}$"));

        assertEquals(NombreEstadoCuestionario.ACTIVO, c2.getNombreEstado());
        assertTrue(c2.getClave().matches("^[A-Z0-9]{8}$"));
        assertNotEquals(c1.getId(), c2.getId());
        assertNotEquals(c1.getClave(), c2.getClave());
    }

    @Test
    void builderFallaSiFaltaCandidatoOConfiguracion() {
        Candidato candidato = candidato(1014, "CASTRO", "PAULA", "39141014");
        ConfiguracionCuestionario configuracion = configuracion();

        assertThrows(IllegalStateException.class,
                () -> new CuestionarioBuilder().conConfiguracion(configuracion).construir());
        assertThrows(IllegalStateException.class,
                () -> new CuestionarioBuilder().conCandidato(candidato).construir());
    }

    private static Candidato candidato(int nro, String apellido, String nombre, String dni) {
        return new Candidato(
                nro, apellido, nombre, TipoDocumento.DNI, dni,
                LocalDate.of(1992, 1, 1), Genero.M, "ARGENTINA",
                nombre.toLowerCase() + "@mail.com", "UNIVERSITARIO COMPLETO", false);
    }

    private static ConfiguracionCuestionario configuracion() {
        return new ConfiguracionCuestionario(
                UUID.fromString("30000000-0000-0000-0000-000000000001"),
                "Instrucciones", 3, 5, Duration.ofHours(2), Duration.ofDays(7));
    }
}
