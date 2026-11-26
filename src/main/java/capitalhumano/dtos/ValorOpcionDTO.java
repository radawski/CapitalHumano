package capitalhumano.dtos;

import capitalhumano.dominio.ValorOpcion;
import java.util.UUID;

public class ValorOpcionDTO {

    private UUID valorOpcionId;
    private int orden;
    private String descripcion;

    public ValorOpcionDTO(ValorOpcion valor) {
        this.valorOpcionId = valor.getId();
        this.orden = valor.getOrden();
        this.descripcion = valor.getDescripcion();
    }

    public UUID getValorOpcionId() {
        return valorOpcionId;
    }

    public int getOrden() {
        return orden;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
