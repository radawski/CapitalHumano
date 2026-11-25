package capitalhumano.controladores;

import capitalhumano.dominio.Cuestionario;
import capitalhumano.dtos.BloqueDTO;
import capitalhumano.dtos.CredencialesLoginDTO;
import capitalhumano.dtos.IngresoCuestionarioDTO;
import capitalhumano.dtos.RespuestasBloqueDTO;
import capitalhumano.persistencia.CompetenciaDAO;
import capitalhumano.persistencia.EvaluacionDAO;
import capitalhumano.persistencia.PreguntaDAO;
import java.util.UUID;

public class GestorCuestionarios {

    private EvaluacionDAO evaluacionDAO;
    private CompetenciaDAO competenciaDAO;
    private PreguntaDAO preguntaDAO;

    public GestorCuestionarios(EvaluacionDAO evaluacionDAO, CompetenciaDAO competenciaDAO, PreguntaDAO preguntaDAO) {
        this.evaluacionDAO = evaluacionDAO;
        this.competenciaDAO = competenciaDAO;
        this.preguntaDAO = preguntaDAO;
    }

    public Cuestionario validarClaveCandidato(CredencialesLoginDTO credenciales) {
        throw new UnsupportedOperationException("TODO #14");
    }

    public IngresoCuestionarioDTO ingresarCuestionario(String clave) {
        throw new UnsupportedOperationException("TODO #14");
    }

    public BloqueDTO comenzarCuestionario(UUID cuestionarioId) {
        throw new UnsupportedOperationException("TODO #15");
    }

    public BloqueDTO obtenerBloquePendiente(UUID cuestionarioId) {
        throw new UnsupportedOperationException("TODO #16");
    }

    public BloqueDTO responderBloque(RespuestasBloqueDTO respuestas) {
        throw new UnsupportedOperationException("TODO #16");
    }

    public void finalizarCuestionariosVencidos() {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU032)");
    }
}
