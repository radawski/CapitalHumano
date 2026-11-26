package capitalhumano.cu026;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import capitalhumano.dominio.BloqueCuestionario;
import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Cuestionario;
import capitalhumano.dominio.Factor;
import capitalhumano.dominio.Pregunta;
import capitalhumano.dominio.PreguntaSeleccionada;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** #15: sorteo de 2 preguntas por factor, mezcla y armado de bloques. */
class SorteoDePreguntasTest {

    private final Competencia c1 = DatosCU026.competencia("C1");
    private final Competencia c2 = DatosCU026.competencia("C2");
    private final Factor f1 = DatosCU026.factor("F1", c1);
    private final Factor f2 = DatosCU026.factor("F2", c1);
    private final Factor f3 = DatosCU026.factor("F3", c2);

    private Map<Factor, List<Pregunta>> preguntasPorFactor() {
        Map<Factor, List<Pregunta>> mapa = new LinkedHashMap<>();
        mapa.put(f1, DatosCU026.preguntas(f1, 5));
        mapa.put(f2, DatosCU026.preguntas(f2, 2));
        mapa.put(f3, DatosCU026.preguntas(f3, 3));
        return mapa;
    }

    private static List<PreguntaSeleccionada> todas(Cuestionario c) {
        return c.getBloques().stream().flatMap(b -> b.getPreguntas().stream()).toList();
    }

    @Test
    void eligeExactamente2PreguntasDistintasPorFactor() {
        for (int intento = 0; intento < 20; intento++) {
            Cuestionario c = DatosCU026.activo(3);
            Map<Factor, List<Pregunta>> mapa = preguntasPorFactor();
            c.asignarPreguntas(mapa);

            List<PreguntaSeleccionada> seleccionadas = todas(c);
            assertEquals(6, seleccionadas.size());

            Map<Factor, Integer> porFactor = new HashMap<>();
            Set<Pregunta> distintas = new HashSet<>();
            for (PreguntaSeleccionada s : seleccionadas) {
                Pregunta p = s.getPregunta();
                assertTrue(mapa.get(p.getFactor()).contains(p), "La pregunta no pertenece a su factor");
                porFactor.merge(p.getFactor(), 1, Integer::sum);
                distintas.add(p);
            }
            assertEquals(Map.of(f1, 2, f2, 2, f3, 2), porFactor);
            assertEquals(6, distintas.size());
        }
    }

    @Test
    void armaBloquesDelTamanioDeLaConfiguracion_yNumeraEnOrden() {
        Cuestionario c = DatosCU026.activo(4);
        c.asignarPreguntas(preguntasPorFactor());

        List<BloqueCuestionario> bloques = c.getBloques();
        assertEquals(2, bloques.size());
        assertEquals(1, bloques.get(0).getNroBloque());
        assertEquals(4, bloques.get(0).getPreguntas().size());
        assertEquals(2, bloques.get(1).getNroBloque());
        assertEquals(2, bloques.get(1).getPreguntas().size());

        List<PreguntaSeleccionada> seleccionadas = todas(c);
        for (int i = 0; i < seleccionadas.size(); i++) {
            assertEquals(i + 1, seleccionadas.get(i).getOrden());
        }
        assertTrue(bloques.stream().noneMatch(BloqueCuestionario::estaContestado));
        assertEquals(bloques.get(0), c.getBloquePendiente());
    }

    @Test
    void mezclaPreguntasDeDistintosFactores() {
        // Con 3 factores, si nunca se mezclara el primer par siempre sería del mismo factor
        boolean mezclo = false;
        for (int intento = 0; intento < 50 && !mezclo; intento++) {
            Cuestionario c = DatosCU026.activo(3);
            c.asignarPreguntas(preguntasPorFactor());
            List<PreguntaSeleccionada> s = todas(c);
            mezclo = !s.get(0).getPregunta().getFactor().equals(s.get(1).getPregunta().getFactor());
        }
        assertTrue(mezclo);
    }

    @Test
    void sinFactoresEvaluables_lanzaExcepcion() {
        Cuestionario c = DatosCU026.activo(3);
        assertThrows(IllegalArgumentException.class, () -> c.asignarPreguntas(Map.of()));
    }

    @Test
    void factorConMenosDe2Preguntas_lanzaExcepcion() {
        Cuestionario c = DatosCU026.activo(3);
        assertThrows(IllegalArgumentException.class,
                () -> c.asignarPreguntas(Map.of(f1, DatosCU026.preguntas(f1, 1))));
    }

    @Test
    void noSePuedenAsignarDosVeces() {
        Cuestionario c = DatosCU026.activo(3);
        c.asignarPreguntas(preguntasPorFactor());
        assertThrows(IllegalStateException.class, () -> c.asignarPreguntas(preguntasPorFactor()));
    }

    @Test
    void sinBloques_noHayBloquePendiente() {
        assertNull(DatosCU026.activo(3).getBloquePendiente());
    }
}
