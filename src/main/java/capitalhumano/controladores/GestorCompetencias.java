package capitalhumano.controladores;

import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Factor;
import capitalhumano.persistencia.CompetenciaDAO;
import java.util.List;

public class GestorCompetencias {

    private CompetenciaDAO competenciaDAO;

    public GestorCompetencias(CompetenciaDAO competenciaDAO) {
        this.competenciaDAO = competenciaDAO;
    }

    public List<Competencia> buscarCompetencias(String nombre) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    public void darDeAltaCompetencia(Competencia competencia) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    public void modificarCompetencia(Competencia competencia) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    public List<Factor> obtenerFactores(Competencia competencia) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    public void darDeAltaFactor(Factor factor) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    public void modificarFactor(Factor factor) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }
}
