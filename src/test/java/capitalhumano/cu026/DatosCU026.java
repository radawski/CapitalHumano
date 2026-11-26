package capitalhumano.cu026;

import capitalhumano.dominio.BloqueCuestionario;
import capitalhumano.dominio.Candidato;
import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.ConfiguracionCuestionario;
import capitalhumano.dominio.Cuestionario;
import capitalhumano.dominio.EstadoActivo;
import capitalhumano.dominio.EstadoCuestionario;
import capitalhumano.dominio.EstadoEnProceso;
import capitalhumano.dominio.Factor;
import capitalhumano.dominio.OpcionDeRespuesta;
import capitalhumano.dominio.Pregunta;
import capitalhumano.dominio.ValorOpcion;
import capitalhumano.enumeraciones.Genero;
import capitalhumano.enumeraciones.TipoCompetencia;
import capitalhumano.enumeraciones.TipoDocumento;
import capitalhumano.enumeraciones.TipoPregunta;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Datos de dominio en memoria para los tests de CU026 (#14–#17). */
public final class DatosCU026 {

    /** Opción "Malo / Regular / Bueno / Muy bueno". */
    public static final ValorOpcion MALO = new ValorOpcion(UUID.randomUUID(), 1, "MALO", true);
    public static final ValorOpcion REGULAR = new ValorOpcion(UUID.randomUUID(), 2, "REGULAR", true);
    public static final ValorOpcion BUENO = new ValorOpcion(UUID.randomUUID(), 3, "BUENO", true);
    public static final ValorOpcion MUY_BUENO = new ValorOpcion(UUID.randomUUID(), 4, "MUY BUENO", true);
    public static final OpcionDeRespuesta OPCION = new OpcionDeRespuesta("ESCALA4", "ESCALA 4", "",
            true, List.of(MALO, REGULAR, BUENO, MUY_BUENO));

    private DatosCU026() {
    }

    public static Competencia competencia(String codigo) {
        return new Competencia(codigo, "COMPETENCIA " + codigo, "", TipoCompetencia.TECNICA, true);
    }

    public static Factor factor(String codigo, Competencia competencia) {
        return new Factor(codigo, "FACTOR " + codigo, "", 1, true, competencia);
    }

    /** Pregunta cuyas ponderaciones son MALO=0, REGULAR=3, BUENO=7, MUY_BUENO=10 (suman 20 → sólo para tests). */
    public static Pregunta pregunta(String nombre, Factor factor) {
        Map<ValorOpcion, Double> ponderaciones = new LinkedHashMap<>();
        ponderaciones.put(MALO, 0.0);
        ponderaciones.put(REGULAR, 3.0);
        ponderaciones.put(BUENO, 7.0);
        ponderaciones.put(MUY_BUENO, 10.0);
        return new Pregunta(UUID.randomUUID(), nombre, "ENUNCIADO " + nombre, "", TipoPregunta.SINGLE_CHOICE,
                true, factor, OPCION, ponderaciones);
    }

    public static List<Pregunta> preguntas(Factor factor, int cantidad) {
        List<Pregunta> preguntas = new ArrayList<>();
        for (int i = 1; i <= cantidad; i++) {
            preguntas.add(pregunta(factor.getCodigo() + "-P" + i, factor));
        }
        return preguntas;
    }

    public static ConfiguracionCuestionario configuracion(int preguntasPorBloque) {
        return new ConfiguracionCuestionario(UUID.randomUUID(), "INSTRUCCIONES", preguntasPorBloque, 5,
                Duration.ofHours(2), Duration.ofDays(7));
    }

    public static Candidato candidato() {
        return new Candidato(1001, "PEREZ", "JUAN", TipoDocumento.DNI, "30111222",
                LocalDate.of(1990, 1, 1), Genero.M, "ARGENTINA", "juan@mail.com", "UNIVERSITARIO", false);
    }

    public static Cuestionario cuestionario(EstadoCuestionario estado, int preguntasPorBloque,
                                            LocalDateTime fechaCreacion, LocalDateTime inicio, int accesos) {
        return new Cuestionario(UUID.randomUUID(), "TEST0001", estado, fechaCreacion, inicio, null, null,
                accesos, fechaCreacion, candidato(), configuracion(preguntasPorBloque),
                new ArrayList<>(), new ArrayList<>());
    }

    public static Cuestionario activo(int preguntasPorBloque) {
        return cuestionario(new EstadoActivo(), preguntasPorBloque, LocalDateTime.now(), null, 0);
    }

    /** Cuestionario EN_PROCESO con las preguntas ya asignadas. */
    public static Cuestionario enProceso(int preguntasPorBloque, Map<Factor, List<Pregunta>> preguntasPorFactor) {
        Cuestionario cuestionario = cuestionario(new EstadoEnProceso(), preguntasPorBloque,
                LocalDateTime.now(), LocalDateTime.now(), 1);
        cuestionario.asignarPreguntas(preguntasPorFactor);
        return cuestionario;
    }

    /** Respuestas que eligen el mismo valor para todas las preguntas del bloque. */
    public static Map<UUID, UUID> responderTodas(BloqueCuestionario bloque, ValorOpcion valor) {
        Map<UUID, UUID> respuestas = new LinkedHashMap<>();
        bloque.getPreguntas().forEach(p -> respuestas.put(p.getPregunta().getId(), valor.getId()));
        return respuestas;
    }
}
