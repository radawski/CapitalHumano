package capitalhumano.persistencia;

import capitalhumano.dominio.RegistroAuditoria;
import capitalhumano.dominio.RegistroEjecucion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

public class JdbcAuditoriaDAO implements AuditoriaDAO {

    private final ConexionBD conexionBD;

    public JdbcAuditoriaDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public void registrar(RegistroAuditoria registro) {
        if (registro == null) {
            throw new IllegalArgumentException("El registro de auditoría no puede ser nulo");
        }
        String sqlConsultor = "SELECT id_consultor FROM consultor WHERE nombre_usuario = ?";
        String sqlInsert = """
                INSERT INTO registro_auditoria (id_registro, id_consultor, fecha_hora, operacion, entidad, id_entidad)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = conexionBD.getConexion()) {
            UUID idConsultor;
            try (PreparedStatement psCons = conn.prepareStatement(sqlConsultor)) {
                psCons.setString(1, registro.getUsuario());
                try (ResultSet rs = psCons.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalStateException(
                                "No existe un consultor registrado con nombre de usuario: " + registro.getUsuario());
                    }
                    idConsultor = rs.getObject("id_consultor", UUID.class);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setObject(1, UUID.randomUUID());
                ps.setObject(2, idConsultor);
                ps.setTimestamp(3, Timestamp.valueOf(registro.getFechaHora()));
                ps.setString(4, registro.getOperacion());
                ps.setString(5, registro.getEntidad());
                ps.setString(6, registro.getIdEntidad());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar auditoría", e);
        }
    }

    @Override
    public void registrarEjecucion(RegistroEjecucion registro) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU032)");
    }
}
