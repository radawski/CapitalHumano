package capitalhumano.dominio;

import java.util.ArrayList;
import java.util.List;

public class ResultadoCompetencia {

    private float puntajeObtenido;
    /** Asociación ResultadoCompetencia "0..*" --> "1" Competencia. */
    private Competencia competencia;
    /** Composición ResultadoCompetencia "1" *-- "1..*" ResultadoFactor. */
    private List<ResultadoFactor> resultadosFactor = new ArrayList<>();

    public ResultadoCompetencia(Competencia competencia) {
        this.competencia = competencia;
    }

    /** Constructor de reconstrucción (no figura en el diagrama): lo usan los DAO. */
    public ResultadoCompetencia(Competencia competencia, float puntajeObtenido, List<ResultadoFactor> resultadosFactor) {
        this.competencia = competencia;
        this.puntajeObtenido = puntajeObtenido;
        this.resultadosFactor = new ArrayList<>(resultadosFactor);
    }

    public void agregarResultadoFactor(Factor factor, float puntaje) {
        throw new UnsupportedOperationException("TODO #17");
    }

    public void calcularPuntaje() {
        throw new UnsupportedOperationException("TODO #17");
    }

    public Competencia getCompetencia() {
        return competencia;
    }

    public float getPuntaje() {
        return puntajeObtenido;
    }

    public List<ResultadoFactor> getResultadosFactor() {
        return resultadosFactor;
    }
}
