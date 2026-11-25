package capitalhumano.persistencia;

import capitalhumano.dominio.BloqueCuestionario;
import capitalhumano.dominio.Candidato;
import capitalhumano.dominio.Cuestionario;
import capitalhumano.dominio.Evaluacion;
import capitalhumano.dominio.Puesto;
import capitalhumano.dominio.ResultadoCompetencia;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EvaluacionDAO {

    void crearEvaluacion(Evaluacion evaluacion);

    Cuestionario obtenerCuestionarioPorClave(String clave);

    Cuestionario obtenerCuestionario(UUID id);

    List<Cuestionario> obtenerCuestionariosActivosOEnProceso(Candidato candidato);

    void guardarCuestionario(Cuestionario cuestionario);

    void actualizarCuestionario(Cuestionario cuestionario);

    void guardarBloque(Cuestionario cuestionario, BloqueCuestionario bloque);

    void guardarResultados(Cuestionario cuestionario, List<ResultadoCompetencia> resultados);

    List<Cuestionario> obtenerVencidos(LocalDateTime fechaHora);

    List<Evaluacion> obtenerEvaluacionesPorPuesto(Puesto puesto);

    List<Evaluacion> obtenerEvaluaciones(List<UUID> ids);

    int contarCandidatosPorPuesto(Puesto puesto);

    int contarCuestionariosCompletosPorPuesto(Puesto puesto);

    List<Cuestionario> obtenerCuestionariosCompletosPorPuesto(Puesto puesto);
}
