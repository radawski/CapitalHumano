package capitalhumano.dtos;

import capitalhumano.dominio.Evaluacion;
import java.time.LocalDate;
import java.util.UUID;

public class EvaluacionResumenDTO {

    private UUID evaluacionId;
    private LocalDate fechaCreacion;
    private int cantidadCandidatos;

    public EvaluacionResumenDTO(Evaluacion evaluacion) {
        throw new UnsupportedOperationException("TODO #6");
    }

    public UUID getEvaluacionId() {
        return evaluacionId;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public int getCantidadCandidatos() {
        return cantidadCandidatos;
    }
}
