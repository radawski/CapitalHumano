package capitalhumano.dominio;

import java.time.LocalDateTime;

/** Clase de asociación (Cuestionario, Pregunta). */
public class PreguntaSeleccionada {

    private int orden;
    private ValorOpcion valorElegido;
    private LocalDateTime fechaHora;
    private Pregunta pregunta;

    public PreguntaSeleccionada(Pregunta pregunta, int orden) {
        this.pregunta = pregunta;
        this.orden = orden;
    }

    /** Constructor de reconstrucción (no figura en el diagrama): lo usan los DAO. */
    public PreguntaSeleccionada(Pregunta pregunta, int orden, ValorOpcion valorElegido, LocalDateTime fechaHora) {
        this.pregunta = pregunta;
        this.orden = orden;
        this.valorElegido = valorElegido;
        this.fechaHora = fechaHora;
    }

    public int getOrden() {
        return orden;
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

    public void responder(ValorOpcion valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Es obligatorio responder la pregunta");
        }
        this.valorElegido = valor;
        this.fechaHora = LocalDateTime.now();
    }

    public boolean estaRespondida() {
        return valorElegido != null;
    }

    public ValorOpcion getValorElegido() {
        return valorElegido;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public double getPonderacionObtenida() {
        if (!estaRespondida()) {
            throw new IllegalStateException("La pregunta " + orden + " todavía no fue respondida");
        }
        return pregunta.getPonderacion(valorElegido);
    }
}
