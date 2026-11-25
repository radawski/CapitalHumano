package capitalhumano.dtos;

import java.util.Map;
import java.util.UUID;

public class RespuestasBloqueDTO {

    private UUID cuestionarioId;
    private int nroBloque;
    private Map<UUID, UUID> respuestas;

    public RespuestasBloqueDTO(UUID cuestionarioId, int nroBloque, Map<UUID, UUID> respuestas) {
        this.cuestionarioId = cuestionarioId;
        this.nroBloque = nroBloque;
        this.respuestas = respuestas;
    }

    public UUID getCuestionarioId() {
        return cuestionarioId;
    }

    public int getNroBloque() {
        return nroBloque;
    }

    public Map<UUID, UUID> getRespuestas() {
        return respuestas;
    }
}
