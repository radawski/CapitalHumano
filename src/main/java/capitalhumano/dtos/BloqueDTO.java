package capitalhumano.dtos;

import capitalhumano.dominio.BloqueCuestionario;
import capitalhumano.dominio.Cuestionario;
import java.util.List;
import java.util.UUID;

public class BloqueDTO {

    private UUID cuestionarioId;
    private int nroBloque;
    private boolean esUltimo;
    private List<PreguntaBloqueDTO> preguntas;

    public BloqueDTO(Cuestionario cuestionario, BloqueCuestionario bloque) {
        throw new UnsupportedOperationException("TODO #16");
    }

    public UUID getCuestionarioId() {
        return cuestionarioId;
    }

    public int getNroBloque() {
        return nroBloque;
    }

    public boolean esUltimo() {
        return esUltimo;
    }

    public List<PreguntaBloqueDTO> getPreguntas() {
        return preguntas;
    }
}
