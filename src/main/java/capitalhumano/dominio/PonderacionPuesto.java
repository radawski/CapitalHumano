package capitalhumano.dominio;

/** Clase de asociación (Puesto, Competencia). */
public class PonderacionPuesto {

    private double ponderacionMinima;

    /** Constructor de reconstrucción (no figura en el diagrama). */
    public PonderacionPuesto(double ponderacionMinima) {
        this.ponderacionMinima = ponderacionMinima;
    }

    public double getPonderacion() {
        return ponderacionMinima;
    }

    public void setPonderacion(double valor) {
        this.ponderacionMinima = valor;
    }
}
