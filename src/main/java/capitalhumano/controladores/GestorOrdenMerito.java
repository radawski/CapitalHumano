package capitalhumano.controladores;

import capitalhumano.dtos.EvaluacionResumenDTO;
import capitalhumano.dtos.OrdenMeritoDTO;
import capitalhumano.dtos.PuestoResumenDTO;
import capitalhumano.persistencia.EvaluacionDAO;
import capitalhumano.persistencia.PuestoDAO;
import java.util.List;
import java.util.UUID;

public class GestorOrdenMerito {

    private PuestoDAO puestoDAO;
    private EvaluacionDAO evaluacionDAO;

    public GestorOrdenMerito(PuestoDAO puestoDAO, EvaluacionDAO evaluacionDAO) {
        this.puestoDAO = puestoDAO;
        this.evaluacionDAO = evaluacionDAO;
    }

    public List<PuestoResumenDTO> buscarPuestos(String codigo, String nombre, String empresa) {
        throw new UnsupportedOperationException("TODO #5");
    }

    public List<EvaluacionResumenDTO> obtenerEvaluaciones(String puestoCodigo) {
        throw new UnsupportedOperationException("TODO #6");
    }

    public OrdenMeritoDTO generarOrdenMerito(String puestoCodigo, List<UUID> evaluacionIds) {
        throw new UnsupportedOperationException("TODO #6");
    }
}
