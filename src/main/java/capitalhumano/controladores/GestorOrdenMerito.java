package capitalhumano.controladores;

import capitalhumano.dominio.Puesto;
import capitalhumano.dtos.EvaluacionResumenDTO;
import capitalhumano.dtos.OrdenMeritoDTO;
import capitalhumano.dtos.PuestoResumenDTO;
import capitalhumano.persistencia.EvaluacionDAO;
import capitalhumano.persistencia.PuestoDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GestorOrdenMerito {

    private PuestoDAO puestoDAO;
    private EvaluacionDAO evaluacionDAO;

    public GestorOrdenMerito(PuestoDAO puestoDAO, EvaluacionDAO evaluacionDAO) {
        this.puestoDAO = puestoDAO;
        this.evaluacionDAO = evaluacionDAO;
    }

    /**
     * CU028 pasos 3–4: puestos que cumplen los filtros, cada uno con la cantidad de candidatos
     * evaluados y de cuestionarios completos. La pantalla valida que haya al menos un criterio.
     */
    public List<PuestoResumenDTO> buscarPuestos(String codigo, String nombre, String empresa) {
        List<PuestoResumenDTO> resumenes = new ArrayList<>();
        for (Puesto puesto : puestoDAO.buscarPorFiltros(codigo, nombre, empresa)) {
            int cantidadCandidatos = evaluacionDAO.contarCandidatosPorPuesto(puesto);
            int evaluacionesCompletas = evaluacionDAO.contarCuestionariosCompletosPorPuesto(puesto);
            resumenes.add(new PuestoResumenDTO(puesto, cantidadCandidatos, evaluacionesCompletas));
        }
        return resumenes;
    }

    public List<EvaluacionResumenDTO> obtenerEvaluaciones(String puestoCodigo) {
        throw new UnsupportedOperationException("TODO #6");
    }

    public OrdenMeritoDTO generarOrdenMerito(String puestoCodigo, List<UUID> evaluacionIds) {
        throw new UnsupportedOperationException("TODO #6");
    }
}
