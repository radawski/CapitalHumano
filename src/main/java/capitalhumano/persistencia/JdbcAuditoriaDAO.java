package capitalhumano.persistencia;

import capitalhumano.dominio.RegistroAuditoria;
import capitalhumano.dominio.RegistroEjecucion;

public class JdbcAuditoriaDAO implements AuditoriaDAO {

    private final ConexionBD conexionBD;

    public JdbcAuditoriaDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public void registrar(RegistroAuditoria registro) {
        throw new UnsupportedOperationException("TODO #4");
    }

    @Override
    public void registrarEjecucion(RegistroEjecucion registro) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU032)");
    }
}
