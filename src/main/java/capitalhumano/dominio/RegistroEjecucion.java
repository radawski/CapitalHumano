package capitalhumano.dominio;

import capitalhumano.enumeraciones.AccionEjecucion;
import java.time.LocalDateTime;

public class RegistroEjecucion {

    private LocalDateTime fechaHora;
    private AccionEjecucion accion;

    /** Constructor de reconstrucción (no figura en el diagrama). */
    public RegistroEjecucion(LocalDateTime fechaHora, AccionEjecucion accion) {
        this.fechaHora = fechaHora;
        this.accion = accion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public AccionEjecucion getAccion() {
        return accion;
    }
}
