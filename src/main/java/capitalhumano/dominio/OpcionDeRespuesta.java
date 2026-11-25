package capitalhumano.dominio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OpcionDeRespuesta extends SujetoAuditable {

    private String codigo;
    private String nombre;
    private String descripcion;
    private boolean activa;
    /** Composición OpcionDeRespuesta "1" *-- "1..*" ValorOpcion. */
    private List<ValorOpcion> valores;

    /** Constructor de reconstrucción (no figura en el diagrama): lo usan los DAO. */
    public OpcionDeRespuesta(String codigo, String nombre, String descripcion, boolean activa, List<ValorOpcion> valores) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activa = activa;
        this.valores = new ArrayList<>(valores);
    }

    public String getNombre() {
        return nombre;
    }

    public List<ValorOpcion> getValores() {
        return valores;
    }

    @Override
    public void darDeBaja(String usuario) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof OpcionDeRespuesta otra && Objects.equals(codigo, otra.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}
