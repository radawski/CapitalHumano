package capitalhumano.cu026;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import capitalhumano.dominio.BloqueCuestionario;
import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Cuestionario;
import capitalhumano.dominio.EstadoEnProceso;
import capitalhumano.dominio.Factor;
import capitalhumano.dominio.Pregunta;
import capitalhumano.dominio.PreguntaSeleccionada;
import capitalhumano.dominio.ResultadoCompetencia;
import capitalhumano.dominio.ResultadoFactor;
import capitalhumano.dominio.ValorOpcion;
import capitalhumano.enumeraciones.NombreEstadoCuestionario;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * #17: Observación 3. Ponderaciones de prueba: MALO=0, REGULAR=3, BUENO=7, MUY BUENO=10.
 * Factor = suma / 2; competencia = promedio de sus factores.
 */
class CalculoDeResultadosTest {

    private final Competencia c1 = DatosCU026.competencia("C1");
    private final Competencia c2 = DatosCU026.competencia("C2");
    private final Factor f1 = DatosCU026.factor("F1", c1);
    private final Factor f2 = DatosCU026.factor("F2", c1);
    private final Factor f3 = DatosCU026.factor("F3", c2);

    @Test
    void factor_promediaLasPonderacionesDeSus2Preguntas() {
        List<Pregunta> preguntas = DatosCU026.preguntas(f1, 2);
        PreguntaSeleccionada a = new PreguntaSeleccionada(preguntas.get(0), 1);
        PreguntaSeleccionada b = new PreguntaSeleccionada(preguntas.get(1), 2);
        a.responder(DatosCU026.MUY_BUENO); // 10
        b.responder(DatosCU026.REGULAR);   // 3
        assertEquals(6.5f, f1.calcularPuntaje(List.of(a, b)), 0.0001);
    }

    @Test
    void preguntaSinResponder_noTienePonderacion() {
        PreguntaSeleccionada a = new PreguntaSeleccionada(DatosCU026.pregunta("X", f1), 1);
        assertThrows(IllegalStateException.class, a::getPonderacionObtenida);
    }

    @Test
    void cuestionarioCompleto_conValoresConocidos() {
        Map<Factor, List<Pregunta>> mapa = new LinkedHashMap<>();
        mapa.put(f1, DatosCU026.preguntas(f1, 2));
        mapa.put(f2, DatosCU026.preguntas(f2, 2));
        mapa.put(f3, DatosCU026.preguntas(f3, 2));
        Cuestionario c = DatosCU026.cuestionario(new EstadoEnProceso(), 3, LocalDateTime.now(),
                LocalDateTime.now(), 1);
        c.asignarPreguntas(mapa);

        // F1: 10 y 10 → 10 | F2: 3 y 7 → 5 | F3: 0 y 7 → 3.5
        Map<String, List<ValorOpcion>> elegidos = Map.of(
                "F1", new java.util.ArrayList<>(List.of(DatosCU026.MUY_BUENO, DatosCU026.MUY_BUENO)),
                "F2", new java.util.ArrayList<>(List.of(DatosCU026.REGULAR, DatosCU026.BUENO)),
                "F3", new java.util.ArrayList<>(List.of(DatosCU026.MALO, DatosCU026.BUENO)));
        while (c.getBloquePendiente() != null) {
            BloqueCuestionario bloque = c.getBloquePendiente();
            Map<UUID, UUID> respuestas = new LinkedHashMap<>();
            for (PreguntaSeleccionada p : bloque.getPreguntas()) {
                ValorOpcion valor = elegidos.get(p.getPregunta().getFactor().getCodigo()).remove(0);
                respuestas.put(p.getPregunta().getId(), valor.getId());
            }
            c.responderBloque(bloque.getNroBloque(), respuestas);
        }

        c.calcularResultados();
        c.completar();

        assertEquals(NombreEstadoCuestionario.COMPLETO, c.getNombreEstado());
        assertEquals(2, c.getResultados().size());
        ResultadoCompetencia rc1 = resultado(c, c1);
        ResultadoCompetencia rc2 = resultado(c, c2);
        assertEquals(10f, puntajeFactor(rc1, f1), 0.0001);
        assertEquals(5f, puntajeFactor(rc1, f2), 0.0001);
        assertEquals(7.5f, rc1.getPuntaje(), 0.0001);   // (10 + 5) / 2
        assertEquals(3.5f, puntajeFactor(rc2, f3), 0.0001);
        assertEquals(3.5f, rc2.getPuntaje(), 0.0001);
        assertEquals(LocalDateTime.now().toLocalDate(), c.getFechaHoraFin().toLocalDate());
    }

    @Test
    void competencia_sinFactores_noSePuedeCalcular() {
        assertThrows(IllegalStateException.class, () -> new ResultadoCompetencia(c1).calcularPuntaje());
    }

    private static ResultadoCompetencia resultado(Cuestionario c, Competencia competencia) {
        return c.getResultados().stream().filter(r -> r.getCompetencia().equals(competencia)).findFirst().orElseThrow();
    }

    private static float puntajeFactor(ResultadoCompetencia rc, Factor factor) {
        return rc.getResultadosFactor().stream().filter(r -> r.getFactor().equals(factor))
                .map(ResultadoFactor::getPuntaje).findFirst().orElseThrow();
    }
}
