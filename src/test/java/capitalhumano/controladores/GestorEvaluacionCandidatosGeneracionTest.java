package capitalhumano.controladores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import capitalhumano.adapters.FabricaCuestionarios;
import capitalhumano.dominio.BloqueCuestionario;
import capitalhumano.dominio.Candidato;
import capitalhumano.dominio.ConfiguracionCuestionario;
import capitalhumano.dominio.Cuestionario;
import capitalhumano.dominio.Evaluacion;
import capitalhumano.dominio.Puesto;
import capitalhumano.dominio.ResultadoCompetencia;
import capitalhumano.dtos.ClaveCandidatoDTO;
import capitalhumano.enumeraciones.Genero;
import capitalhumano.enumeraciones.NombreEstadoCuestionario;
import capitalhumano.enumeraciones.TipoDocumento;
import capitalhumano.persistencia.ConfiguracionDAO;
import capitalhumano.persistencia.EvaluacionDAO;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class GestorEvaluacionCandidatosGeneracionTest {

    @Test
    void generaEvaluacionConCuestionariosActivosClavesUnicasYPersisteAlGuardar() {
        Candidato sanchez = new Candidato(
                1007, "SANCHEZ", "DIEGO", TipoDocumento.DNI, "33777007",
                LocalDate.of(1989, 2, 25), Genero.H, "ARGENTINA",
                "diego.sanchez@mail.com", "UNIVERSITARIO COMPLETO", false);
        Candidato castro = new Candidato(
                1014, "CASTRO", "PAULA", TipoDocumento.DNI, "39141014",
                LocalDate.of(1996, 5, 17), Genero.M, "ARGENTINA",
                "paula.castro@mail.com", "UNIVERSITARIO COMPLETO", false);
        Puesto p001 = new Puesto(
                "P001", "DESARROLLADOR JAVA SENIOR", "DESARROLLO BACKEND EN JAVA 21 Y POSTGRESQL",
                "TECH SOLUTIONS SA", true, Map.of());
        ConfiguracionCuestionario configSeed = new ConfiguracionCuestionario(
                UUID.fromString("30000000-0000-0000-0000-000000000001"),
                "Instrucciones", 3, 5, Duration.ofHours(2), Duration.ofDays(7));

        AtomicReference<Evaluacion> evaluacionPersistida = new AtomicReference<>();
        ConfiguracionDAO configuracionDAO = () -> configSeed;
        EvaluacionDAO evaluacionDAO = crearEvaluacionDaoStub(evaluacionPersistida);

        GestorEvaluacionCandidatos gestor = new GestorEvaluacionCandidatos(
                null, null, evaluacionDAO, configuracionDAO, null, null, null, new FabricaCuestionarios());

        Evaluacion evaluacion = gestor.generarEvaluacion(List.of(sanchez, castro), p001);

        assertSame(p001, evaluacion.getPuesto());
        assertTrue(evaluacion.isActiva());
        assertEquals(2, evaluacion.getCuestionarios().size());
        assertEquals(null, evaluacionPersistida.get(), "No debe persistir hasta guardarEvaluacion");

        Set<String> claves = new HashSet<>();
        for (Cuestionario c : evaluacion.getCuestionarios()) {
            assertEquals(NombreEstadoCuestionario.ACTIVO, c.getNombreEstado());
            assertSame(evaluacion, c.getEvaluacion());
            assertTrue(c.getClave().matches("^[A-Z0-9]{8}$"));
            assertTrue(claves.add(c.getClave()));
        }

        List<ClaveCandidatoDTO> dtos = gestor.obtenerClavesCandidatos(evaluacion);
        assertEquals(2, dtos.size());
        assertEquals("SANCHEZ", dtos.get(0).getApellido());
        assertEquals("33777007", dtos.get(0).getNroDocumento());
        assertEquals(evaluacion.getCuestionarios().get(0).getClave(), dtos.get(0).getClave());
        assertEquals("CASTRO", dtos.get(1).getApellido());
        assertEquals(evaluacion.getCuestionarios().get(1).getClave(), dtos.get(1).getClave());

        gestor.guardarEvaluacion(evaluacion);
        assertSame(evaluacion, evaluacionPersistida.get());
    }

    @Test
    void rechazaEntradasNulasOVacias() {
        Puesto p001 = new Puesto("P001", "DEV", "", "TECH", true, Map.of());
        Candidato castro = new Candidato(
                1014, "CASTRO", "PAULA", TipoDocumento.DNI, "39141014",
                LocalDate.of(1996, 5, 17), Genero.M, "ARGENTINA",
                "paula.castro@mail.com", "UNIVERSITARIO COMPLETO", false);
        GestorEvaluacionCandidatos gestor = new GestorEvaluacionCandidatos(
                null, null, null, null, null, null, null, new FabricaCuestionarios());

        assertThrows(IllegalArgumentException.class, () -> gestor.generarEvaluacion(null, p001));
        assertThrows(IllegalArgumentException.class, () -> gestor.generarEvaluacion(List.of(), p001));
        assertThrows(IllegalArgumentException.class, () -> gestor.generarEvaluacion(List.of(castro), null));
        assertThrows(IllegalArgumentException.class, () -> gestor.obtenerClavesCandidatos(null));
        assertThrows(IllegalArgumentException.class, () -> gestor.guardarEvaluacion(null));
    }

    private static EvaluacionDAO crearEvaluacionDaoStub(AtomicReference<Evaluacion> evaluacionPersistida) {
        return new EvaluacionDAO() {
            @Override
            public void crearEvaluacion(Evaluacion evaluacion) {
                evaluacionPersistida.set(evaluacion);
            }

            @Override public Cuestionario obtenerCuestionarioPorClave(String clave) { throw new UnsupportedOperationException(); }
            @Override public Cuestionario obtenerCuestionario(UUID id) { throw new UnsupportedOperationException(); }
            @Override public List<Cuestionario> obtenerCuestionariosActivosOEnProceso(Candidato candidato) { throw new UnsupportedOperationException(); }
            @Override public void guardarCuestionario(Cuestionario cuestionario) { throw new UnsupportedOperationException(); }
            @Override public void actualizarCuestionario(Cuestionario cuestionario) { throw new UnsupportedOperationException(); }
            @Override public void guardarBloque(Cuestionario cuestionario, BloqueCuestionario bloque) { throw new UnsupportedOperationException(); }
            @Override public void guardarResultados(Cuestionario cuestionario, List<ResultadoCompetencia> resultados) { throw new UnsupportedOperationException(); }
            @Override public List<Cuestionario> obtenerVencidos(LocalDateTime fechaHora) { throw new UnsupportedOperationException(); }
            @Override public List<Evaluacion> obtenerEvaluacionesPorPuesto(Puesto puesto) { throw new UnsupportedOperationException(); }
            @Override public List<Evaluacion> obtenerEvaluaciones(List<UUID> ids) { throw new UnsupportedOperationException(); }
            @Override public int contarCandidatosPorPuesto(Puesto puesto) { throw new UnsupportedOperationException(); }
            @Override public int contarCuestionariosCompletosPorPuesto(Puesto puesto) { throw new UnsupportedOperationException(); }
            @Override public List<Cuestionario> obtenerCuestionariosCompletosPorPuesto(Puesto puesto) { throw new UnsupportedOperationException(); }
        };
    }
}
