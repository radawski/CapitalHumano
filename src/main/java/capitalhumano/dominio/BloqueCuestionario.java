package capitalhumano.dominio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
        if (pregunta == null) {
            throw new IllegalArgumentException("La pregunta seleccionada no puede ser nula");
        }
        preguntas.add(pregunta);
        cantidadPreguntas = preguntas.size();
    }

    /**
     * Paso 7 de CU026. respuestas = id de pregunta → id del ValorOpcion elegido.
     * Primero valida que estén todas (flujo 6.A) y recién después responde, para no dejar
     * el bloque a medias.
     */
    public void responder(Map<UUID, UUID> respuestas) {
        if (contestado) {
            throw new IllegalStateException("El bloque " + nroBloque + " ya fue contestado");
        }
        Map<PreguntaSeleccionada, ValorOpcion> elegidos = new LinkedHashMap<>();
        for (PreguntaSeleccionada seleccionada : preguntas) {
            Pregunta pregunta = seleccionada.getPregunta();
            UUID valorOpcionId = respuestas == null ? null : respuestas.get(pregunta.getId());
            if (valorOpcionId == null) {
                throw new IllegalArgumentException("Es obligatorio responder todas las preguntas del bloque");
            }
            elegidos.put(seleccionada, pregunta.getValor(valorOpcionId));
        }
        elegidos.forEach(PreguntaSeleccionada::responder);
        marcarContestado();
    }

    public boolean estaContestado() {
        return contestado;
    }

    private void marcarContestado() {
        this.contestado = true;
    }
}
