package capitalhumano.dtos;

import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Puesto;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatosPuestoEvaluacionDTO {

    private Puesto puesto;
    private Map<Competencia, Double> ponderacionesRequeridas;
    private List<Competencia> competenciasNoEvaluables;

    public DatosPuestoEvaluacionDTO(Puesto puesto, Map<Competencia, Double> ponderaciones,
            List<Competencia> competenciasNoEvaluables) {
        if (puesto == null) {
            throw new IllegalArgumentException("El puesto no puede ser nulo.");
        }
        if (ponderaciones == null) {
            throw new IllegalArgumentException("Las ponderaciones no pueden ser nulas.");
        }
        if (competenciasNoEvaluables == null) {
            throw new IllegalArgumentException("La lista de competencias no evaluables no puede ser nula.");
        }

        this.puesto = puesto;
        this.ponderacionesRequeridas = new LinkedHashMap<>(ponderaciones);
        this.competenciasNoEvaluables = List.copyOf(competenciasNoEvaluables);
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
