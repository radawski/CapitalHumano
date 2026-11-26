package capitalhumano.cu026;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import capitalhumano.dominio.BloqueCuestionario;
import capitalhumano.dominio.Cuestionario;
import capitalhumano.dominio.Factor;
import capitalhumano.dominio.Pregunta;
import capitalhumano.dominio.PreguntaSeleccionada;
import capitalhumano.dtos.BloqueDTO;
import capitalhumano.dtos.PreguntaBloqueDTO;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** #16: responder bloques, flujo 6.A, flujo 8.A y retomar en el bloque pendiente (1.C). */
class WizardDeBloquesTest {

    private Cuestionario cuestionario;

    @BeforeEach
    void armar() {
        Factor f1 = DatosCU026.factor("F1", DatosCU026.competencia("C1"));
        Factor f2 = DatosCU026.factor("F2", DatosCU026.competencia("C1"));
        Map<Factor, List<Pregunta>> mapa = new LinkedHashMap<>();
        mapa.put(f1, DatosCU026.preguntas(f1, 3));
        mapa.put(f2, DatosCU026.preguntas(f2, 3));
        cuestionario = DatosCU026.enProceso(3, mapa); // 4 preguntas → bloques de 3 y 1
    }

    @Test
    void responderBloque_guardaValorYFecha_yAvanzaAlSiguiente() {
        BloqueCuestionario primero = cuestionario.getBloquePendiente();
        BloqueCuestionario respondido = cuestionario.responderBloque(1,
                DatosCU026.responderTodas(primero, DatosCU026.BUENO));

        assertTrue(respondido.estaContestado());
        for (PreguntaSeleccionada p : respondido.getPreguntas()) {
            assertTrue(p.estaRespondida());
            assertEquals(DatosCU026.BUENO, p.getValorElegido());
            assertNotNull(p.getFechaHora());
        }
        assertEquals(2, cuestionario.getBloquePendiente().getNroBloque());
    }

    @Test
    void faltaUnaRespuesta_flujo6A_elBloqueQuedaSinContestarYSinCambios() {
        BloqueCuestionario primero = cuestionario.getBloquePendiente();
        Map<UUID, UUID> respuestas = DatosCU026.responderTodas(primero, DatosCU026.BUENO);
        respuestas.remove(primero.getPreguntas().get(2).getPregunta().getId());

        assertThrows(IllegalArgumentException.class, () -> cuestionario.responderBloque(1, respuestas));
        assertFalse(primero.estaContestado());
        assertTrue(primero.getPreguntas().stream().noneMatch(PreguntaSeleccionada::estaRespondida));
    }

    @Test
    void valorQueNoEsDeLaOpcion_lanzaExcepcion() {
        BloqueCuestionario primero = cuestionario.getBloquePendiente();
        Map<UUID, UUID> respuestas = DatosCU026.responderTodas(primero, DatosCU026.BUENO);
        respuestas.put(primero.getPreguntas().get(0).getPregunta().getId(), UUID.randomUUID());
        assertThrows(IllegalArgumentException.class, () -> cuestionario.responderBloque(1, respuestas));
    }

    @Test
    void unBloqueContestadoNoSeVuelveAResponder() {
        BloqueCuestionario primero = cuestionario.getBloquePendiente();
        cuestionario.responderBloque(1, DatosCU026.responderTodas(primero, DatosCU026.BUENO));
        assertThrows(IllegalStateException.class,
                () -> cuestionario.responderBloque(1, DatosCU026.responderTodas(primero, DatosCU026.MALO)));
    }

    @Test
    void cuestionarioActivo_noAdmiteRespuestas() {
        Cuestionario activo = DatosCU026.activo(3);
        assertThrows(IllegalStateException.class, () -> activo.responderBloque(1, Map.of()));
    }

    @Test
    void todosContestados_noQuedaBloquePendiente() {
        while (cuestionario.getBloquePendiente() != null) {
            BloqueCuestionario b = cuestionario.getBloquePendiente();
            cuestionario.responderBloque(b.getNroBloque(), DatosCU026.responderTodas(b, DatosCU026.REGULAR));
        }
        assertNull(cuestionario.getBloquePendiente());
    }

    @Test
    void bloqueDTO_indicaSiEsElUltimo_yTraeLasOpciones() {
        BloqueDTO primero = new BloqueDTO(cuestionario, cuestionario.getBloquePendiente());
        assertEquals(1, primero.getNroBloque());
        assertFalse(primero.esUltimo());
        assertEquals(3, primero.getPreguntas().size());
        PreguntaBloqueDTO pregunta = primero.getPreguntas().get(0);
        assertEquals(4, pregunta.getValores().size());
        assertEquals("MALO", pregunta.getValores().get(0).getDescripcion());

        BloqueCuestionario b1 = cuestionario.getBloquePendiente();
        cuestionario.responderBloque(1, DatosCU026.responderTodas(b1, DatosCU026.BUENO));
        BloqueDTO segundo = new BloqueDTO(cuestionario, cuestionario.getBloquePendiente());
        assertEquals(2, segundo.getNroBloque());
        assertTrue(segundo.esUltimo());
    }
}
