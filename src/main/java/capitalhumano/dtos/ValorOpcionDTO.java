package capitalhumano.dtos;

import capitalhumano.dominio.ValorOpcion;
import java.util.UUID;

public class ValorOpcionDTO {

    private UUID valorOpcionId;
    private int orden;
    private String descripcion;

    public ValorOpcionDTO(ValorOpcion valor) {
        throw new UnsupportedOperationException("TODO #16");
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
