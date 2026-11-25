package capitalhumano.controladores;

import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Factor;
import capitalhumano.dominio.OpcionDeRespuesta;
import capitalhumano.dominio.Pregunta;
import capitalhumano.dominio.ValorOpcion;
import capitalhumano.dtos.DatosPreguntaDTO;
import capitalhumano.dtos.ResultadoPreguntaDTO;
import capitalhumano.persistencia.CompetenciaDAO;
import capitalhumano.persistencia.PreguntaDAO;
import capitalhumano.servicios.ServicioGeneracionIA;
import java.util.List;
import java.util.Map;

public class GestorPreguntas {

    private CompetenciaDAO competenciaDAO;
    private PreguntaDAO preguntaDAO;
    private ServicioGeneracionIA servicioIA;

    public GestorPreguntas(CompetenciaDAO competenciaDAO, PreguntaDAO preguntaDAO, ServicioGeneracionIA servicioIA) {
        this.competenciaDAO = competenciaDAO;
        this.preguntaDAO = preguntaDAO;
        this.servicioIA = servicioIA;
    }

    public List<Competencia> obtenerCompetencias() {
        throw new UnsupportedOperationException("TODO #3");
    }

    public List<Factor> obtenerFactores(Competencia competencia) {
        throw new UnsupportedOperationException("TODO #3");
    }

    public List<Pregunta> buscarPreguntas(Competencia competencia, Factor factor, String nombrePregunta) {
        throw new UnsupportedOperationException("TODO #3");
    }

    public List<OpcionDeRespuesta> obtenerOpcionesDeRespuesta() {
        throw new UnsupportedOperationException("TODO #8");
    }

    public List<ValorOpcion> obtenerValores(OpcionDeRespuesta opcion) {
        throw new UnsupportedOperationException("TODO #8");
    }

    public ResultadoPreguntaDTO darDeAltaPregunta(DatosPreguntaDTO datos) {
        throw new UnsupportedOperationException("TODO #8");
    }

    public ResultadoPreguntaDTO modificarPregunta(Pregunta pregunta, DatosPreguntaDTO datos) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU022)");
    }

    public Pregunta generarPreguntaConIA(Competencia competencia, Factor factor) {
        throw new UnsupportedOperationException("TODO #8");
    }

    private boolean validarPonderaciones(Map<ValorOpcion, Double> ponderaciones) {
        throw new UnsupportedOperationException("TODO #8");
    }
}
