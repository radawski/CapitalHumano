package capitalhumano.dtos;

import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Puesto;
import java.util.List;
import java.util.Map;

public class DatosPuestoEvaluacionDTO {

    private Puesto puesto;
    private Map<Competencia, Double> ponderacionesRequeridas;
    private List<Competencia> competenciasNoEvaluables;

    public DatosPuestoEvaluacionDTO(Puesto puesto, Map<Competencia, Double> ponderaciones,
                                    List<Competencia> competenciasNoEvaluables) {
        throw new UnsupportedOperationException("TODO #11");
    }

    public Puesto getPuesto() {
        return puesto;
    }

    public Map<Competencia, Double> getPonderacionesRequeridas() {
        return ponderacionesRequeridas;
    }

    public List<Competencia> getCompetenciasNoEvaluables() {
        return competenciasNoEvaluables;
    }
}
