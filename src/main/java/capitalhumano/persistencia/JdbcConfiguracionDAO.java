package capitalhumano.persistencia;

import capitalhumano.dominio.ConfiguracionCuestionario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.UUID;

public class JdbcConfiguracionDAO implements ConfiguracionDAO {

    private final ConexionBD conexionBD;

    public JdbcConfiguracionDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public ConfiguracionCuestionario obtenerConfiguracion() {
        String sql = """
                SELECT id_configuracion, instrucciones, cantidad_preguntas_por_bloque,
                       maximo_accesos, tiempo_maximo, tiempo_maximo_activo
                FROM configuracion_cuestionario
                LIMIT 1
                """;
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                throw new IllegalStateException("No existe configuración de cuestionario vigente en la base de datos");
            }
            return new ConfiguracionCuestionario(
                    rs.getObject("id_configuracion", UUID.class),
                    rs.getString("instrucciones"),
                    rs.getInt("cantidad_preguntas_por_bloque"),
                    rs.getInt("maximo_accesos"),
                    Duration.ofSeconds(rs.getLong("tiempo_maximo")),
                    Duration.ofSeconds(rs.getLong("tiempo_maximo_activo"))
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener la configuración de cuestionario", e);
        }
    }
}
