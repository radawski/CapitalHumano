package capitalhumano.dtos;

import capitalhumano.dominio.BloqueCuestionario;
import capitalhumano.dominio.Cuestionario;
import capitalhumano.dominio.PreguntaSeleccionada;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BloqueDTO {

    private UUID cuestionarioId;
    private int nroBloque;
    private boolean esUltimo;
    private List<PreguntaBloqueDTO> preguntas;

    public BloqueDTO(Cuestionario cuestionario, BloqueCuestionario bloque) {
        this.cuestionarioId = cuestionario.getId();
        this.nroBloque = bloque.getNroBloque();
        // Es el último si no queda ningún otro bloque sin contestar
        this.esUltimo = cuestionario.getBloques().stream()
                .noneMatch(otro -> otro != bloque && !otro.estaContestado());
        this.preguntas = new ArrayList<>();
        for (PreguntaSeleccionada preguntaSeleccionada : bloque.getPreguntas()) {
            this.preguntas.add(new PreguntaBloqueDTO(preguntaSeleccionada));
        }
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
