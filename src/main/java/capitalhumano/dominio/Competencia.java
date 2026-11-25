package capitalhumano.dominio;

import capitalhumano.enumeraciones.TipoCompetencia;
import java.util.Objects;

public class Competencia extends SujetoAuditable {

    private String codigo;
    private String nombre;
    private String descripcion;
    private TipoCompetencia tipo;
    private boolean activa;

    /** Constructor de reconstrucción (no figura en el diagrama): lo usan los DAO. */
    public Competencia(String codigo, String nombre, String descripcion, TipoCompetencia tipo, boolean activa) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.activa = activa;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoCompetencia getTipo() {
        return tipo;
    }

    @Override
    public void darDeBaja(String usuario) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Competencia otra && Objects.equals(codigo, otra.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}
