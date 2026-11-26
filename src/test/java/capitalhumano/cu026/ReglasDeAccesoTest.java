package capitalhumano.cu026;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import capitalhumano.dominio.Cuestionario;
import capitalhumano.dominio.EstadoActivo;
import capitalhumano.dominio.EstadoEnProceso;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** #14: reglas de tiempo y de accesos (precondición, flujos 1.A y 1.B). Configuración: 5 accesos, 2 h, 7 días. */
class ReglasDeAccesoTest {

    private final LocalDateTime ahora = LocalDateTime.of(2026, 11, 26, 12, 0);

    @Test
    void plazoActivo_dentroDeLos7Dias_noVence() {
        Cuestionario c = DatosCU026.cuestionario(new EstadoActivo(), 3, ahora.minusDays(6), null, 0);
        assertFalse(c.superoTiempoActivo(ahora));
    }

    @Test
    void plazoActivo_justoEnEl7moDia_noVence() {
        Cuestionario c = DatosCU026.cuestionario(new EstadoActivo(), 3, ahora.minusDays(7), null, 0);
        assertFalse(c.superoTiempoActivo(ahora));
    }

    @Test
    void plazoActivo_creadoHace10Dias_vence_flujo1B() {
        Cuestionario c = DatosCU026.cuestionario(new EstadoActivo(), 3, ahora.minusDays(10), null, 0);
        assertTrue(c.superoTiempoActivo(ahora));
    }

    @Test
    void tiempoMaximo_iniciadoHace10Minutos_noVence() {
        Cuestionario c = DatosCU026.cuestionario(new EstadoEnProceso(), 3, ahora.minusDays(1), ahora.minusMinutes(10), 1);
        assertFalse(c.superoTiempoMaximo(ahora));
    }

    @Test
    void tiempoMaximo_iniciadoHace3Horas_vence_flujo1A() {
        Cuestionario c = DatosCU026.cuestionario(new EstadoEnProceso(), 3, ahora.minusDays(1), ahora.minusHours(3), 1);
        assertTrue(c.superoTiempoMaximo(ahora));
    }

    @Test
    void maximoAccesos_elQuintoAccesoSePermite_elSextoNo() {
        Cuestionario c = DatosCU026.cuestionario(new EstadoActivo(), 3, ahora, null, 4);
        c.registrarAcceso();
        assertFalse(c.superoMaximoAccesos());
        c.registrarAcceso();
        assertTrue(c.superoMaximoAccesos());
    }
}
