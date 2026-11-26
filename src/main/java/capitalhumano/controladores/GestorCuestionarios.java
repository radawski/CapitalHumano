package capitalhumano.controladores;

import capitalhumano.dominio.BloqueCuestionario;
import capitalhumano.dominio.Candidato;
import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Cuestionario;
import capitalhumano.dominio.Evaluacion;
import capitalhumano.dominio.Factor;
import capitalhumano.dominio.Pregunta;
import capitalhumano.dominio.Puesto;
import capitalhumano.dominio.ResultadoCompetencia;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    /**
     * Lo usa el login del candidato (CU001, AutenticacionCandidatoClave). Devuelve el cuestionario
     * de la clave si pertenece al candidato con ese tipo y número de documento, o null si no.
     */
    public Cuestionario validarClaveCandidato(CredencialesLoginDTO credenciales) {
        if (credenciales == null || credenciales.getClave() == null || credenciales.getClave().isBlank()
                || credenciales.getTipoDocumento() == null || credenciales.getNroDocumento() == null) {
            return null;
        }
        Cuestionario cuestionario = evaluacionDAO.obtenerCuestionarioPorClave(credenciales.getClave());
        if (cuestionario == null || cuestionario.getCandidato() == null) {
            return null;
        }
        Candidato candidato = cuestionario.getCandidato();
        boolean mismoDocumento = candidato.getTipoDocumento() == credenciales.getTipoDocumento()
                && candidato.getNroDocumento().equals(credenciales.getNroDocumento().trim());
        return mismoDocumento ? cuestionario : null;
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

    /** Paso 3 de CU026: sortea las preguntas, arma los bloques y pasa el cuestionario a EN_PROCESO. */
    public BloqueDTO comenzarCuestionario(UUID cuestionarioId) {
        Cuestionario cuestionario = evaluacionDAO.obtenerCuestionario(cuestionarioId);
        if (cuestionario == null) {
            throw new IllegalArgumentException("No existe el cuestionario " + cuestionarioId);
        }
        if (cuestionario.getNombreEstado() == NombreEstadoCuestionario.EN_PROCESO) {
            // Aceptar presionado dos veces: ya se armó, se retoma el bloque pendiente
            return armarBloqueDTO(cuestionario, cuestionario.getBloquePendiente());
        }

        Evaluacion evaluacion = cuestionario.getEvaluacion();
        Puesto puesto = evaluacion.getPuesto();
        List<Competencia> competencias = puesto.getCompetencias();

        Map<Factor, List<Pregunta>> preguntasPorFactor = new LinkedHashMap<>();
        for (Competencia competencia : competencias) {
            List<Factor> factores = competenciaDAO.obtenerFactores(competencia);
            for (Factor factor : factores) {
                List<Pregunta> preguntas = preguntaDAO.obtenerPreguntasActivasPorFactor(factor);
                // Observación 1: sólo se evalúan los factores con al menos 2 preguntas
                if (preguntas.size() >= 2) {
                    preguntasPorFactor.put(factor, preguntas);
                }
            }
        }

        cuestionario.asignarPreguntas(preguntasPorFactor);
        cuestionario.iniciar();
        evaluacionDAO.guardarCuestionario(cuestionario);

        BloqueCuestionario bloque = cuestionario.getBloquePendiente();
        return new BloqueDTO(cuestionario, bloque);
    }

    /** Flujo 1.C: devuelve el primer bloque sin contestar, o null si no queda ninguno. */
    public BloqueDTO obtenerBloquePendiente(UUID cuestionarioId) {
        Cuestionario cuestionario = evaluacionDAO.obtenerCuestionario(cuestionarioId);
        if (cuestionario == null || cuestionario.getNombreEstado() != NombreEstadoCuestionario.EN_PROCESO) {
            return null;
        }
        BloqueCuestionario bloque = cuestionario.getBloquePendiente();
        return armarBloqueDTO(cuestionario, bloque);
    }

    /**
     * Pasos 7 y 8 de CU026. Guarda el bloque y devuelve el siguiente (flujo 8.A). Si era el último,
     * calcula los resultados, completa el cuestionario y devuelve null.
     */
    public BloqueDTO responderBloque(RespuestasBloqueDTO respuestas) {
        Cuestionario cuestionario = evaluacionDAO.obtenerCuestionario(respuestas.getCuestionarioId());
        if (cuestionario == null) {
            throw new IllegalArgumentException("No existe el cuestionario " + respuestas.getCuestionarioId());
        }

        BloqueCuestionario bloque = cuestionario.responderBloque(respuestas.getNroBloque(), respuestas.getRespuestas());
        evaluacionDAO.guardarBloque(cuestionario, bloque);

        BloqueCuestionario siguiente = cuestionario.getBloquePendiente();
        if (siguiente != null) {
            return new BloqueDTO(cuestionario, siguiente);
        }

        // Paso 8: no quedan bloques
        cuestionario.calcularResultados();
        cuestionario.completar();
        List<ResultadoCompetencia> resultados = cuestionario.getResultados();
        // guardarResultados persiste resultados y estado COMPLETO en una sola transacción
        evaluacionDAO.guardarResultados(cuestionario, resultados);
        evaluacionDAO.actualizarCuestionario(cuestionario);
        return null;
    }

    private BloqueDTO armarBloqueDTO(Cuestionario cuestionario, BloqueCuestionario bloque) {
        return bloque == null ? null : new BloqueDTO(cuestionario, bloque);
    }

    public void finalizarCuestionariosVencidos() {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU032)");
    }
}
