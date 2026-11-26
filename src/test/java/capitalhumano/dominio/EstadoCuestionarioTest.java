package capitalhumano.dominio;

import capitalhumano.enumeraciones.NombreEstadoCuestionario;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EstadoCuestionarioTest {

    private Cuestionario nuevo() {
        return new Cuestionario(UUID.randomUUID(), "TEST0001", new EstadoActivo(),
                LocalDateTime.now(), null, null, null, 0, null,
                null, null, new ArrayList<>(), new ArrayList<>());
    }

    @Test
    void activo_iniciar_pasaAEnProceso() {
        Cuestionario c = nuevo();
        c.iniciar();
        assertEquals(NombreEstadoCuestionario.EN_PROCESO, c.getNombreEstado());
        assertNotNull(c.getFechaHoraInicio());
    }

    @Test
    void activo_vencido_pasaASinContestar() {
        Cuestionario c = nuevo();
        c.marcarSinContestar();
        assertEquals(NombreEstadoCuestionario.SIN_CONTESTAR, c.getNombreEstado());
    }

    @Test
    void activo_maximoAccesos_pasaAIncompleto() {
        Cuestionario c = nuevo();
        c.marcarIncompleto();
        assertEquals(NombreEstadoCuestionario.INCOMPLETO, c.getNombreEstado());
    }

    @Test
    void enProceso_completar_pasaACompleto() {
        Cuestionario c = nuevo();
        c.iniciar();
        c.completar();
        assertEquals(NombreEstadoCuestionario.COMPLETO, c.getNombreEstado());
        assertNotNull(c.getFechaHoraFin());
    }

    @Test
    void activo_completar_esInvalido() {
        assertThrows(IllegalStateException.class, () -> nuevo().completar());
    }

    @Test
    void completo_noAdmiteNingunaTransicion() {
        Cuestionario c = nuevo();
        c.iniciar();
        c.completar();
        assertThrows(IllegalStateException.class, c::marcarIncompleto);
        assertThrows(IllegalStateException.class, c::iniciar);
    }

    @Test
    void registrarAcceso_sumaYActualizaUltimoIngreso() {
        Cuestionario c = nuevo();
        c.registrarAcceso();
        assertEquals(1, c.getCantidadAccesos());
        assertNotNull(c.getUltimoIngreso());
    }
}