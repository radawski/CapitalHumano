package capitalhumano.persistencia;

import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Factor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        if (competencia == null) {
            return List.of();
        }
        String sql = """
                SELECT cod_factor, nombre, descripcion, nro_orden, activo
                FROM factor
                WHERE cod_competencia = ? AND activo = TRUE
                ORDER BY nro_orden
                """;
        List<Factor> factores = new ArrayList<>();
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, competencia.getCodigo());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    factores.add(new Factor(
                            rs.getString("cod_factor"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getInt("nro_orden"),
                            rs.getBoolean("activo"),
                            competencia
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener factores de la competencia: " + competencia.getCodigo(), e);
        }
        return factores;
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
