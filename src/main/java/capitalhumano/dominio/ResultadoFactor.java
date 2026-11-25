package capitalhumano.dominio;

public class ResultadoFactor {

    private float puntajeObtenido;
    /** Asociación ResultadoFactor "0..*" --> "1" Factor. */
    private Factor factor;

    public ResultadoFactor(Factor factor, float puntaje) {
        this.factor = factor;
        this.puntajeObtenido = puntaje;
    }

    public Factor getFactor() {
        return factor;
    }

    public float getPuntaje() {
        return puntajeObtenido;
    }
}
