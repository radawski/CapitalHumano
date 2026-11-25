package capitalhumano.dominio;

import java.util.Objects;
import java.util.UUID;

public class ValorOpcion {

    private UUID id;
    private int orden;
    private String descripcion;
    private boolean activo;

    /** Constructor de reconstrucción (no figura en el diagrama): lo usan los DAO. */
    public ValorOpcion(UUID id, int orden, String descripcion, boolean activo) {
        this.id = id;
        this.orden = orden;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public UUID getId() {
        return id;
    }

    public int getOrden() {
        return orden;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ValorOpcion otro && Objects.equals(id, otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
