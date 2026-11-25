package capitalhumano.dtos;

import capitalhumano.enumeraciones.TipoCompetencia;

public class FilaComparativaDTO {

    private String competenciaCodigo;
    private String competenciaNombre;
    private TipoCompetencia tipoCompetencia;
    private double requerido;
    private float obtenido;
    private boolean cumple;

    public String getCompetenciaCodigo() {
        return competenciaCodigo;
    }

    public String getCompetenciaNombre() {
        return competenciaNombre;
    }

    public TipoCompetencia getTipoCompetencia() {
        return tipoCompetencia;
    }

    public double getRequerido() {
        return requerido;
    }

    public float getObtenido() {
        return obtenido;
    }

    public boolean getCumple() {
        return cumple;
    }
}
