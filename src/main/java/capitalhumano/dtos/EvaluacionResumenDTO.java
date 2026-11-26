package capitalhumano.dtos;

import capitalhumano.dominio.Evaluacion;
import java.time.LocalDate;
import java.util.UUID;

public class EvaluacionResumenDTO {

    private UUID evaluacionId;
    private LocalDate fechaCreacion;
    private int cantidadCandidatos;

    public EvaluacionResumenDTO(Evaluacion evaluacion) {
        this.evaluacionId = evaluacion.getId();
        this.fechaCreacion = evaluacion.getFechaCreacion();
        this.cantidadCandidatos = evaluacion.getCuestionarios().size();
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
