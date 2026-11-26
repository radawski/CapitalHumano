package capitalhumano.controladores;

import capitalhumano.dominio.Cuestionario;
import capitalhumano.dtos.BloqueDTO;
import capitalhumano.dtos.CredencialesLoginDTO;
import capitalhumano.dtos.IngresoCuestionarioDTO;
import capitalhumano.dtos.RespuestasBloqueDTO;
import capitalhumano.enumeraciones.NombreEstadoCuestionario;
import capitalhumano.enumeraciones.ResultadoIngreso;
import capitalhumano.persistencia.CompetenciaDAO;
import capitalhumano.persistencia.EvaluacionDAO;
import capitalhumano.persistencia.PreguntaDAO;

import java.time.LocalDateTime;
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
        Cuestionario cuestionario = evaluacionDAO.obtenerCuestionarioPorClave(clave);
        if (cuestionario == null) {
            return new IngresoCuestionarioDTO(ResultadoIngreso.RECHAZADO,
                    "Los datos ingresados no son válidos o no existe un cuestionario para el Candidato");
        }

        cuestionario.registrarAcceso();
        NombreEstadoCuestionario estado = cuestionario.getNombreEstado();
        LocalDateTime ahora = LocalDateTime.now();
        IngresoCuestionarioDTO ingreso;

        if (cuestionario.superoMaximoAccesos()) {
            cuestionario.marcarIncompleto();
            ingreso = new IngresoCuestionarioDTO(ResultadoIngreso.RECHAZADO,
                    "Ha superado la cantidad máxima de accesos permitidos");

        } else if (estado == NombreEstadoCuestionario.ACTIVO) {
            if (cuestionario.superoTiempoActivo(ahora)) {
                cuestionario.marcarSinContestar();
                ingreso = new IngresoCuestionarioDTO(ResultadoIngreso.RECHAZADO,
                        "El plazo para iniciar el cuestionario ha vencido");
            } else {
                ingreso = new IngresoCuestionarioDTO(ResultadoIngreso.INSTRUCCIONES,
                        cuestionario.getId(),
                        cuestionario.getConfiguracion().getInstrucciones());
            }

        } else if (estado == NombreEstadoCuestionario.EN_PROCESO) {
            if (cuestionario.superoTiempoMaximo(ahora)) {
                cuestionario.marcarIncompleto();
                ingreso = new IngresoCuestionarioDTO(ResultadoIngreso.RECHAZADO,
                        "Se ha superado el tiempo máximo para completar el cuestionario");
            } else {
                ingreso = new IngresoCuestionarioDTO(ResultadoIngreso.CONTINUAR, cuestionario.getId());
            }

        } else {
            ingreso = new IngresoCuestionarioDTO(ResultadoIngreso.RECHAZADO,
                    "No posee un cuestionario disponible para completar");
        }

        evaluacionDAO.actualizarCuestionario(cuestionario);
        return ingreso;
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
