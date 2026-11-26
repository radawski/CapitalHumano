package capitalhumano.cu028;

import capitalhumano.dominio.Candidato;
import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.ConfiguracionCuestionario;
import capitalhumano.dominio.Cuestionario;
import capitalhumano.dominio.EstadoActivo;
import capitalhumano.dominio.EstadoCompleto;
import capitalhumano.dominio.EstadoCuestionario;
import capitalhumano.dominio.EstadoEnProceso;
import capitalhumano.dominio.EstadoIncompleto;
import capitalhumano.dominio.EstadoSinContestar;
import capitalhumano.dominio.Evaluacion;
import capitalhumano.dominio.PonderacionPuesto;
import capitalhumano.dominio.Puesto;
import capitalhumano.dominio.ResultadoCompetencia;
import capitalhumano.enumeraciones.Genero;
import capitalhumano.enumeraciones.TipoCompetencia;
import capitalhumano.enumeraciones.TipoDocumento;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Datos de dominio en memoria para los tests de CU028 (#6), con los mismos números que el seed:
 * P001 exige COMP01 ≥ 6 y COMP02 ≥ 5.
 */
final class DatosCU028 {

    static final Competencia COMP01 =
            new Competencia("COMP01", "PROGRAMACION JAVA", "", TipoCompetencia.TECNICA, true);
    static final Competencia COMP02 =
            new Competencia("COMP02", "TRABAJO EN EQUIPO", "", TipoCompetencia.TECNICA, true);

    private static int siguienteNroCandidato = 2000;

    private DatosCU028() {
    }

    /** P001 del seed: COMP01 con mínimo 6 y COMP02 con mínimo 5. */
    static Puesto p001() {
        Map<Competencia, PonderacionPuesto> ponderaciones = new LinkedHashMap<>();
        ponderaciones.put(COMP01, new PonderacionPuesto(6.0));
        ponderaciones.put(COMP02, new PonderacionPuesto(5.0));
        return new Puesto("P001", "DESARROLLADOR JAVA SENIOR", "", "TECH SOLUTIONS SA", true, ponderaciones);
    }

    static Candidato candidato(String apellido, String nombre) {
        return new Candidato(siguienteNroCandidato++, apellido, nombre, TipoDocumento.DNI,
                String.valueOf(30000000 + siguienteNroCandidato), LocalDate.of(1990, 1, 1), Genero.M,
                "ARGENTINA", "mail@mail.com", "UNIVERSITARIO", false);
    }

    /** Cuestionario COMPLETO con un resultado por competencia (puntajes en el orden COMP01, COMP02). */
    static Cuestionario completo(String apellido, float puntajeComp01, float puntajeComp02) {
        List<ResultadoCompetencia> resultados = new ArrayList<>();
        resultados.add(new ResultadoCompetencia(COMP01, puntajeComp01, List.of()));
        resultados.add(new ResultadoCompetencia(COMP02, puntajeComp02, List.of()));
        return cuestionario(new EstadoCompleto(), apellido, resultados);
    }

    /** Cuestionario COMPLETO solo con el resultado de COMP01 (le falta COMP02). */
    static Cuestionario completoSinComp02(String apellido, float puntajeComp01) {
        List<ResultadoCompetencia> resultados = new ArrayList<>();
        resultados.add(new ResultadoCompetencia(COMP01, puntajeComp01, List.of()));
        return cuestionario(new EstadoCompleto(), apellido, resultados);
    }

    static Cuestionario activo(String apellido) {
        return cuestionario(new EstadoActivo(), apellido, new ArrayList<>());
    }

    static Cuestionario enProceso(String apellido) {
        return cuestionario(new EstadoEnProceso(), apellido, new ArrayList<>());
    }

    static Cuestionario incompleto(String apellido) {
        return cuestionario(new EstadoIncompleto(), apellido, new ArrayList<>());
    }

    static Cuestionario sinContestar(String apellido) {
        return cuestionario(new EstadoSinContestar(), apellido, new ArrayList<>());
    }

    static Evaluacion evaluacion(Puesto puesto, Cuestionario... cuestionarios) {
        return new Evaluacion(UUID.randomUUID(), LocalDate.now(), true, puesto, List.of(cuestionarios));
    }

    private static Cuestionario cuestionario(EstadoCuestionario estado, String apellido,
            List<ResultadoCompetencia> resultados) {
        LocalDateTime ahora = LocalDateTime.now();
        ConfiguracionCuestionario configuracion = new ConfiguracionCuestionario(UUID.randomUUID(),
                "INSTRUCCIONES", 3, 5, Duration.ofHours(2), Duration.ofDays(7));
        return new Cuestionario(UUID.randomUUID(), "TEST" + apellido.substring(0, 4), estado, ahora.minusDays(1),
                ahora.minusHours(2), ahora.minusHours(1), ahora.minusHours(1), 1, ahora,
                candidato(apellido, "NOMBRE"), configuracion, new ArrayList<>(), resultados);
    }
}
