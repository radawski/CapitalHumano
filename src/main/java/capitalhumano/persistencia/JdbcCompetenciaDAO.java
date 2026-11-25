package capitalhumano.persistencia;

import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Factor;
import java.util.List;

public class JdbcCompetenciaDAO implements CompetenciaDAO {

    private final ConexionBD conexionBD;

    public JdbcCompetenciaDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public void guardar(Competencia competencia) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public void actualizar(Competencia competencia) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public void eliminarLogico(Competencia competencia) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public List<Competencia> buscarPorNombre(String nombre) {
        throw new UnsupportedOperationException("TODO #3");
    }

    @Override
    public List<Factor> obtenerFactores(Competencia competencia) {
        throw new UnsupportedOperationException("TODO #2");
    }

    @Override
    public void guardarFactor(Factor factor) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public void actualizarFactor(Factor factor) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public void eliminarLogicoFactor(Factor factor) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }
}
