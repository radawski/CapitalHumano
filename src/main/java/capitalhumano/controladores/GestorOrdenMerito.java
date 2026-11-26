package capitalhumano.controladores;

import capitalhumano.dominio.Candidato;
import capitalhumano.dominio.Cuestionario;
import capitalhumano.dominio.Evaluacion;
import capitalhumano.dominio.Puesto;
import capitalhumano.dtos.EvaluacionResumenDTO;
import capitalhumano.dtos.ItemOrdenMeritoDTO;
import capitalhumano.dtos.OrdenMeritoDTO;
import capitalhumano.dtos.PuestoResumenDTO;
import capitalhumano.enumeraciones.NombreEstadoCuestionario;
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

    /** CU028 pasos 5–6: evaluaciones del puesto (lista vacía dispara el flujo 6.A en la pantalla). */
    public List<EvaluacionResumenDTO> obtenerEvaluaciones(String puestoCodigo) {
        Puesto puesto = puestoDAO.obtenerConCompetencias(puestoCodigo);
        List<EvaluacionResumenDTO> resumenes = new ArrayList<>();
        if (puesto == null) {
            return resumenes;
        }
        for (Evaluacion evaluacion : evaluacionDAO.obtenerEvaluacionesPorPuesto(puesto)) {
            resumenes.add(new EvaluacionResumenDTO(evaluacion));
        }
        return resumenes;
    }

    /**
     * CU028 paso 7: clasifica a los candidatos de las evaluaciones elegidas. Los cuestionarios
     * COMPLETOS van a aprobados o no aprobados según {@code alcanzaMinimos}; el resto se agrupa
     * por estado y sin puntaje (Observación 1).
     */
    public OrdenMeritoDTO generarOrdenMerito(String puestoCodigo, List<UUID> evaluacionIds) {
        Puesto puesto = puestoDAO.obtenerConCompetencias(puestoCodigo);
        if (puesto == null) {
            throw new IllegalArgumentException("No existe el puesto " + puestoCodigo);
        }
        List<Evaluacion> evaluaciones = evaluacionDAO.obtenerEvaluaciones(evaluacionIds);
        OrdenMeritoDTO ordenMerito = new OrdenMeritoDTO(puesto, evaluaciones);
        for (Evaluacion evaluacion : evaluaciones) {
            for (Cuestionario cuestionario : evaluacion.getCuestionarios()) {
                Candidato candidato = cuestionario.getCandidato();
                NombreEstadoCuestionario estado = cuestionario.getNombreEstado();
                if (estado == NombreEstadoCuestionario.COMPLETO) {
                    float puntaje = cuestionario.getPuntajeTotal();
                    ItemOrdenMeritoDTO item = new ItemOrdenMeritoDTO(candidato, cuestionario, puntaje);
                    if (cuestionario.alcanzaMinimos(puesto)) {
                        ordenMerito.agregarAprobado(item);
                    } else {
                        ordenMerito.agregarNoAprobado(item);
                    }
                } else {
                    ordenMerito.agregarNoCompleto(estado, new ItemOrdenMeritoDTO(candidato, cuestionario, null));
                }
            }
        }
        ordenMerito.ordenar();
        return ordenMerito;
    }
}
