package capitalhumano.controladores;

import capitalhumano.adapters.FabricaCuestionarios;
import capitalhumano.dominio.Candidato;
import capitalhumano.dominio.Evaluacion;
import capitalhumano.dominio.Puesto;
import capitalhumano.dtos.ClaveCandidatoDTO;
import capitalhumano.dtos.DatosPuestoEvaluacionDTO;
import capitalhumano.persistencia.CandidatoDAO;
import capitalhumano.persistencia.CompetenciaDAO;
import capitalhumano.persistencia.ConfiguracionDAO;
import capitalhumano.persistencia.EvaluacionDAO;
import capitalhumano.persistencia.PreguntaDAO;
import capitalhumano.persistencia.PuestoDAO;
import capitalhumano.servicios.ServicioExportacionClaves;
import java.util.List;

public class GestorEvaluacionCandidatos {

    private CandidatoDAO candidatoDAO;
    private PuestoDAO puestoDAO;
    private EvaluacionDAO evaluacionDAO;
    private ConfiguracionDAO configuracionDAO;
    private CompetenciaDAO competenciaDAO;
    private PreguntaDAO preguntaDAO;
    private ServicioExportacionClaves servicioExportacionClaves;
    private FabricaCuestionarios fabricaCuestionarios;

    public GestorEvaluacionCandidatos(CandidatoDAO candidatoDAO, PuestoDAO puestoDAO, EvaluacionDAO evaluacionDAO,
                                      ConfiguracionDAO configuracionDAO, CompetenciaDAO competenciaDAO,
                                      PreguntaDAO preguntaDAO, ServicioExportacionClaves servicioExportacionClaves,
                                      FabricaCuestionarios fabricaCuestionarios) {
        this.candidatoDAO = candidatoDAO;
        this.puestoDAO = puestoDAO;
        this.evaluacionDAO = evaluacionDAO;
        this.configuracionDAO = configuracionDAO;
        this.competenciaDAO = competenciaDAO;
        this.preguntaDAO = preguntaDAO;
        this.servicioExportacionClaves = servicioExportacionClaves;
        this.fabricaCuestionarios = fabricaCuestionarios;
    }

    public List<Candidato> buscarCandidatos(String apellido, String nombre, int nroCandidato) {
        throw new UnsupportedOperationException("TODO #10");
    }

    public List<Candidato> validarCandidatos(List<Candidato> candidatos) {
        throw new UnsupportedOperationException("TODO #10");
    }

    public List<Puesto> listarPuestos() {
        throw new UnsupportedOperationException("TODO #11");
    }

    public DatosPuestoEvaluacionDTO obtenerDatosPuestoYCompetencias(String puestoCodigo) {
        throw new UnsupportedOperationException("TODO #11");
    }

    public Evaluacion generarEvaluacion(List<Candidato> candidatos, Puesto puesto) {
        throw new UnsupportedOperationException("TODO #12");
    }

    public List<ClaveCandidatoDTO> obtenerClavesCandidatos(Evaluacion evaluacion) {
        throw new UnsupportedOperationException("TODO #12");
    }

    public void guardarEvaluacion(Evaluacion evaluacion) {
        throw new UnsupportedOperationException("TODO #12");
    }

    public void exportarClaves(Evaluacion evaluacion, String carpeta) {
        throw new UnsupportedOperationException("TODO #13");
    }
}
