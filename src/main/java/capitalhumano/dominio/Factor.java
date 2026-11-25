package capitalhumano.dominio;

import java.util.List;
import java.util.Objects;

public class Factor extends SujetoAuditable {

    private String codigo;
    private String nombre;
    private String descripcion;
    private int nroOrden;
    private boolean activo;
    /** Extremo de la asociación Competencia "1" *-- "0..*" Factor. */
    private Competencia competencia;

    /** Constructor de reconstrucción (no figura en el diagrama): lo usan los DAO. */
    public Factor(String codigo, String nombre, String descripcion, int nroOrden, boolean activo, Competencia competencia) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.nroOrden = nroOrden;
        this.activo = activo;
        this.competencia = competencia;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public Competencia getCompetencia() {
        return competencia;
    }

    public float calcularPuntaje(List<PreguntaSeleccionada> preguntas) {
        throw new UnsupportedOperationException("TODO #17");
    }

    @Override
    public void darDeBaja(String usuario) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Factor otro && Objects.equals(codigo, otro.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}
