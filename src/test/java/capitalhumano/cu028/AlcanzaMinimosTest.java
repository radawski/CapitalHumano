package capitalhumano.cu028;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import capitalhumano.dominio.Puesto;
import org.junit.jupiter.api.Test;

/** CU028 (#6): {@code Cuestionario.getPuntajeTotal} y {@code alcanzaMinimos} (P001: COMP01 ≥ 6, COMP02 ≥ 5). */
class AlcanzaMinimosTest {

    private final Puesto p001 = DatosCU028.p001();

    @Test
    void getPuntajeTotal_esElPromedioDeLasCompetencias() {
        assertEquals(5.50f, DatosCU028.completo("TORRES", 3f, 8f).getPuntajeTotal(), 1e-4);
        assertEquals(7.25f, DatosCU028.completo("RUIZ", 7f, 7.5f).getPuntajeTotal(), 1e-4);
    }

    @Test
    void getPuntajeTotal_sinResultados_esCero() {
        assertEquals(0f, DatosCU028.activo("GOMEZ").getPuntajeTotal());
    }

    @Test
    void alcanzaMinimos_todasLasCompetenciasPorEncima_esVerdadero() {
        assertTrue(DatosCU028.completo("ROMERO", 8f, 8f).alcanzaMinimos(p001));
    }

    @Test
    void alcanzaMinimos_competenciaJustoEnElMinimo_esVerdadero() {
        assertTrue(DatosCU028.completo("SANCHEZ", 6f, 5f).alcanzaMinimos(p001));
    }

    @Test
    void alcanzaMinimos_puntajeConDecimalesIgualAlMinimo_esVerdadero() {
        // 5.1f como double es 5.0999999: con la tolerancia igual alcanza un mínimo de 5.1.
        Puesto puesto = new Puesto("PX", "PUESTO", "", "EMPRESA", true, java.util.Map.of(
                DatosCU028.COMP01, new capitalhumano.dominio.PonderacionPuesto(5.1),
                DatosCU028.COMP02, new capitalhumano.dominio.PonderacionPuesto(5.1)));
        assertTrue(DatosCU028.completo("PEREZ", 5.1f, 5.1f).alcanzaMinimos(puesto));
    }

    @Test
    void alcanzaMinimos_unaCompetenciaDebajo_esFalso() {
        // El promedio (5,50) no importa: COMP01 = 3 no llega al mínimo de 6.
        assertFalse(DatosCU028.completo("TORRES", 3f, 8f).alcanzaMinimos(p001));
    }

    @Test
    void alcanzaMinimos_faltaElResultadoDeUnaCompetencia_esFalso() {
        assertFalse(DatosCU028.completoSinComp02("DIAZ", 9f).alcanzaMinimos(p001));
    }

    @Test
    void alcanzaMinimos_cuestionarioNoCompleto_esFalso() {
        assertFalse(DatosCU028.incompleto("LOPEZ").alcanzaMinimos(p001));
        assertFalse(DatosCU028.enProceso("MOLINA").alcanzaMinimos(p001));
    }
}
