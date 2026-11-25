package capitalhumano.dtos;

import capitalhumano.dominio.PreguntaSeleccionada;
import java.util.List;
import java.util.UUID;

public class PreguntaBloqueDTO {

    private UUID preguntaId;
    private int orden;
    private String enunciado;
    private List<ValorOpcionDTO> valores;

    public PreguntaBloqueDTO(PreguntaSeleccionada preguntaSeleccionada) {
        throw new UnsupportedOperationException("TODO #16");
    }

    public UUID getPreguntaId() {
        return preguntaId;
    }

    public int getOrden() {
        return orden;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public List<ValorOpcionDTO> getValores() {
        return valores;
    }
}
