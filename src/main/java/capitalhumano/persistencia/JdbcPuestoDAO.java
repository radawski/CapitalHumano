package capitalhumano.persistencia;

import capitalhumano.dominio.Puesto;
import java.util.List;

public class JdbcPuestoDAO implements PuestoDAO {

    private final ConexionBD conexionBD;

    public JdbcPuestoDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public void guardar(Puesto puesto) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public void actualizar(Puesto puesto) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public void eliminarLogico(Puesto puesto) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final");
    }

    @Override
    public List<Puesto> buscarPorFiltros(String codigo, String nombre, String empresa) {
        throw new UnsupportedOperationException("TODO #2");
    }

    @Override
    public Puesto obtenerConCompetencias(String codigo) {
        throw new UnsupportedOperationException("TODO #2");
    }
}
