package capitalhumano.dominio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BloqueCuestionario {

    private int nroBloque;
    private int cantidadPreguntas;
    private boolean contestado;
    /** Asociación PreguntaSeleccionada "1..*" -- "1" BloqueCuestionario. */
    private List<PreguntaSeleccionada> preguntas = new ArrayList<>();

    public BloqueCuestionario(int nroBloque) {
        this.nroBloque = nroBloque;
    }

    /** Constructor de reconstrucción (no figura en el diagrama): lo usan los DAO. */
    public BloqueCuestionario(int nroBloque, boolean contestado, List<PreguntaSeleccionada> preguntas) {
        this.nroBloque = nroBloque;
        this.contestado = contestado;
        this.preguntas = new ArrayList<>(preguntas);
        this.cantidadPreguntas = preguntas.size();
    }

    public int getNroBloque() {
        return nroBloque;
    }

    public List<PreguntaSeleccionada> getPreguntas() {
        return preguntas;
    }

    public void agregarPregunta(PreguntaSeleccionada pregunta) {
        throw new UnsupportedOperationException("TODO #15");
    }

    public void responder(Map<UUID, UUID> respuestas) {
        throw new UnsupportedOperationException("TODO #16");
    }

    public boolean estaContestado() {
        throw new UnsupportedOperationException("TODO #16");
    }

    private void marcarContestado() {
        throw new UnsupportedOperationException("TODO #16");
    }
}
