package capitalhumano.persistencia;

import capitalhumano.dominio.ConfiguracionCuestionario;

public class JdbcConfiguracionDAO implements ConfiguracionDAO {

    private final ConexionBD conexionBD;

    public JdbcConfiguracionDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public ConfiguracionCuestionario obtenerConfiguracion() {
        throw new UnsupportedOperationException("TODO #12");
    }
}
