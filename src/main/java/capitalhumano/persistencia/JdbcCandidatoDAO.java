package capitalhumano.persistencia;

import capitalhumano.dominio.Candidato;
import capitalhumano.dtos.RegistroImportacionDTO;
import capitalhumano.enumeraciones.Genero;
import capitalhumano.enumeraciones.TipoDocumento;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcCandidatoDAO implements CandidatoDAO {

    private final ConexionBD conexionBD;

    public JdbcCandidatoDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public RegistroImportacionDTO importarMasivo(List<Candidato> candidatos) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU006)");
    }

    @Override
    public List<Candidato> buscarPorFiltros(String apellido, String nombre, int nroCandidato) {
        StringBuilder sql = new StringBuilder("""
                SELECT nro_candidato, apellido, nombre, tipo_documento, nro_documento,
                       fecha_nacimiento, genero, nacionalidad, email, escolaridad, eliminado
                FROM candidato
                WHERE eliminado = FALSE
                """);
        List<Object> parametros = new ArrayList<>();

        if (apellido != null && !apellido.isBlank()) {
            sql.append(" AND apellido ILIKE ?");
            parametros.add("%" + apellido.trim() + "%");
        }
        if (nombre != null && !nombre.isBlank()) {
            sql.append(" AND nombre ILIKE ?");
            parametros.add("%" + nombre.trim() + "%");
        }
        if (nroCandidato > 0) {
            sql.append(" AND nro_candidato = ?");
            parametros.add(nroCandidato);
        }
        sql.append(" ORDER BY apellido, nombre, nro_candidato");

        List<Candidato> candidatos = new ArrayList<>();
        try (Connection conn = conexionBD.getConexion();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date fechaNac = rs.getDate("fecha_nacimiento");
                    candidatos.add(new Candidato(
                            rs.getInt("nro_candidato"),
                            rs.getString("apellido"),
                            rs.getString("nombre"),
                            TipoDocumento.valueOf(rs.getString("tipo_documento")),
                            rs.getString("nro_documento"),
                            fechaNac != null ? fechaNac.toLocalDate() : null,
                            Genero.valueOf(rs.getString("genero")),
                            rs.getString("nacionalidad"),
                            rs.getString("email"),
                            rs.getString("escolaridad"),
                            rs.getBoolean("eliminado")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar candidatos por filtros", e);
        }
        return candidatos;
    }

    @Override
    public Candidato buscarPorNroCandidato(int nroCandidato) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU027)");
    }
}
