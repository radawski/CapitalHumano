package capitalhumano.dtos;

import capitalhumano.dominio.Pregunta;
import capitalhumano.dominio.PreguntaSeleccionada;
import capitalhumano.dominio.ValorOpcion;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PreguntaBloqueDTO {

    private UUID preguntaId;
    private int orden;
    private String enunciado;
    private List<ValorOpcionDTO> valores;

    public PreguntaBloqueDTO(PreguntaSeleccionada preguntaSeleccionada) {
        Pregunta pregunta = preguntaSeleccionada.getPregunta();
        this.preguntaId = pregunta.getId();
        this.orden = preguntaSeleccionada.getOrden();
        this.enunciado = pregunta.getEnunciado();
        this.valores = new ArrayList<>();
        for (ValorOpcion valor : pregunta.getOpcion().getValores()) {
            this.valores.add(new ValorOpcionDTO(valor));
        }
        this.valores.sort(Comparator.comparingInt(ValorOpcionDTO::getOrden));
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
