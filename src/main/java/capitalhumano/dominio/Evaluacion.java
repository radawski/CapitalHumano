package capitalhumano.dominio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Evaluacion {

    private UUID id;
    private LocalDate fechaCreacion;
    private boolean activa;
    /** Asociación Puesto "1" -- "0..*" Evaluacion. */
    private Puesto puesto;
    /** Composición Evaluacion "1" *-- "1..*" Cuestionario. */
    private List<Cuestionario> cuestionarios = new ArrayList<>();

    public Evaluacion(Puesto puesto) {
        this.id = UUID.randomUUID();
        this.fechaCreacion = LocalDate.now();
        this.activa = true;
        this.puesto = puesto;
    }

    /**
     * Constructor de reconstrucción (no figura en el diagrama): lo usan los DAO.
     */
    public Evaluacion(UUID id, LocalDate fechaCreacion, boolean activa, Puesto puesto,
            List<Cuestionario> cuestionarios) {
        this.id = id;
        this.fechaCreacion = fechaCreacion;
        this.activa = activa;
        this.puesto = puesto;
        for (Cuestionario cuestionario : cuestionarios) {
            this.cuestionarios.add(cuestionario);
            cuestionario.setEvaluacion(this);
        }
    }

    public void agregarCuestionario(Cuestionario cuestionario) {
        if (cuestionario == null) {
            throw new IllegalArgumentException("El cuestionario no puede ser nulo");
        }
        this.cuestionarios.add(cuestionario);
        cuestionario.setEvaluacion(this);
    }

    public UUID getId() {
        return id;
    }

    public List<Cuestionario> getCuestionarios() {
        return cuestionarios;
    }

    public Puesto getPuesto() {
        return puesto;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public boolean isActiva() {
        return activa;
    }
}
