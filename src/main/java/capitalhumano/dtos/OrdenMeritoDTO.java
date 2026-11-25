package capitalhumano.dtos;

import capitalhumano.dominio.Evaluacion;
import capitalhumano.dominio.Puesto;
import capitalhumano.enumeraciones.NombreEstadoCuestionario;
import java.util.List;
import java.util.Map;

public class OrdenMeritoDTO extends Reporte {

    private String puestoCodigo;
    private String puestoNombre;
    private String empresa;
    private List<EvaluacionResumenDTO> evaluaciones;
    private List<ItemOrdenMeritoDTO> aprobados;
    private List<ItemOrdenMeritoDTO> noAprobados;
    private Map<NombreEstadoCuestionario, List<ItemOrdenMeritoDTO>> noCompletos;

    public OrdenMeritoDTO(Puesto puesto, List<Evaluacion> evaluaciones) {
        throw new UnsupportedOperationException("TODO #6");
    }

    public void agregarAprobado(ItemOrdenMeritoDTO item) {
        throw new UnsupportedOperationException("TODO #6");
    }

    public void agregarNoAprobado(ItemOrdenMeritoDTO item) {
        throw new UnsupportedOperationException("TODO #6");
    }

    public void agregarNoCompleto(NombreEstadoCuestionario estado, ItemOrdenMeritoDTO item) {
        throw new UnsupportedOperationException("TODO #6");
    }

    public void ordenar() {
        throw new UnsupportedOperationException("TODO #6");
    }

    public String getPuestoCodigo() {
        return puestoCodigo;
    }

    public String getPuestoNombre() {
        return puestoNombre;
    }

    public String getEmpresa() {
        return empresa;
    }

    public List<EvaluacionResumenDTO> getEvaluaciones() {
        return evaluaciones;
    }

    public List<ItemOrdenMeritoDTO> getAprobados() {
        return aprobados;
    }

    public List<ItemOrdenMeritoDTO> getNoAprobados() {
        return noAprobados;
    }

    public Map<NombreEstadoCuestionario, List<ItemOrdenMeritoDTO>> getNoCompletos() {
        return noCompletos;
    }
}
