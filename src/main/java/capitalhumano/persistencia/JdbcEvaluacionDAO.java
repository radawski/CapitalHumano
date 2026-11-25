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

public class JdbcEvaluacionDAO implements EvaluacionDAO {

    private final ConexionBD conexionBD;

    public JdbcEvaluacionDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public void crearEvaluacion(Evaluacion evaluacion) {
        throw new UnsupportedOperationException("TODO #12");
    }

    @Override
    public Cuestionario obtenerCuestionarioPorClave(String clave) {
        throw new UnsupportedOperationException("TODO #14");
    }

    @Override
    public Cuestionario obtenerCuestionario(UUID id) {
        throw new UnsupportedOperationException("TODO #14");
    }

    @Override
    public List<Cuestionario> obtenerCuestionariosActivosOEnProceso(Candidato candidato) {
        throw new UnsupportedOperationException("TODO #10");
    }

    @Override
    public void guardarCuestionario(Cuestionario cuestionario) {
        throw new UnsupportedOperationException("TODO #15");
    }

    @Override
    public void actualizarCuestionario(Cuestionario cuestionario) {
        throw new UnsupportedOperationException("TODO #14");
    }

    @Override
    public void guardarBloque(Cuestionario cuestionario, BloqueCuestionario bloque) {
        throw new UnsupportedOperationException("TODO #16");
    }

    @Override
    public void guardarResultados(Cuestionario cuestionario, List<ResultadoCompetencia> resultados) {
        throw new UnsupportedOperationException("TODO #17");
    }

    @Override
    public List<Cuestionario> obtenerVencidos(LocalDateTime fechaHora) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU032)");
    }

    @Override
    public List<Evaluacion> obtenerEvaluacionesPorPuesto(Puesto puesto) {
        throw new UnsupportedOperationException("TODO #6");
    }

    @Override
    public List<Evaluacion> obtenerEvaluaciones(List<UUID> ids) {
        throw new UnsupportedOperationException("TODO #6");
    }

    @Override
    public int contarCandidatosPorPuesto(Puesto puesto) {
        throw new UnsupportedOperationException("TODO #5");
    }

    @Override
    public int contarCuestionariosCompletosPorPuesto(Puesto puesto) {
        throw new UnsupportedOperationException("TODO #5");
    }

    @Override
    public List<Cuestionario> obtenerCuestionariosCompletosPorPuesto(Puesto puesto) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU007)");
    }
}
