package capitalhumano.dominio;

/** Clase de asociación (Pregunta, ValorOpcion). */
public class PreguntaValorOpcion {

    private double ponderacion;
    private Pregunta pregunta;
    private ValorOpcion valor;

    public PreguntaValorOpcion(Pregunta pregunta, ValorOpcion valor, double ponderacion) {
        this.pregunta = pregunta;
        this.valor = valor;
        this.ponderacion = ponderacion;
    }

    public double getPonderacion() {
        return ponderacion;
    }

    public void setPonderacion(double valor) {
        this.ponderacion = valor;
    }
}
