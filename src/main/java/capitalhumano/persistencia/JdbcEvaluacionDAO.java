package capitalhumano.persistencia;

import capitalhumano.enumeraciones.Genero;
import capitalhumano.enumeraciones.TipoDocumento;
import capitalhumano.dominio.ConfiguracionCuestionario;
import capitalhumano.dominio.EstadoActivo;
import capitalhumano.dominio.EstadoCompleto;
import capitalhumano.dominio.EstadoCuestionario;
import capitalhumano.dominio.EstadoEnProceso;
import capitalhumano.dominio.EstadoIncompleto;
import capitalhumano.dominio.EstadoSinContestar;
import capitalhumano.enumeraciones.NombreEstadoCuestionario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
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

    private static final String SELECT_CUESTIONARIO =
            "SELECT c.id_cuestionario, c.clave, c.estado, c.fecha_creacion, c.fecha_hora_inicio, "
          + "c.fecha_hora_fin, c.ultimo_ingreso, c.cantidad_accesos, c.fecha_hora_cambio_estado, "
          + "cfg.id_configuracion, cfg.instrucciones, cfg.cantidad_preguntas_por_bloque, "
          + "cfg.maximo_accesos, cfg.tiempo_maximo, cfg.tiempo_maximo_activo, "
          + "cand.nro_candidato, cand.apellido, cand.nombre, cand.tipo_documento, cand.nro_documento, "
          + "cand.fecha_nacimiento, cand.genero, cand.nacionalidad, cand.email, cand.escolaridad, cand.eliminado "
          + "FROM cuestionario c "
          + "JOIN configuracion_cuestionario cfg ON cfg.id_configuracion = c.id_configuracion "
          + "JOIN candidato cand ON cand.nro_candidato = c.nro_candidato ";

    public JdbcEvaluacionDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public void crearEvaluacion(Evaluacion evaluacion) {
        throw new UnsupportedOperationException("TODO #12");
    }

    @Override
    public Cuestionario obtenerCuestionarioPorClave(String clave) {
        String sql = SELECT_CUESTIONARIO + "WHERE c.clave = ?";
        try (Connection con = conexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, clave == null ? null : clave.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapearCuestionario(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener el cuestionario por clave", e);
        }
    }

    @Override
    public Cuestionario obtenerCuestionario(UUID id) {
        String sql = SELECT_CUESTIONARIO + "WHERE c.id_cuestionario = ?";
        try (Connection con = conexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapearCuestionario(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener el cuestionario", e);
        }
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
    public void actualizarCuestionario(Cuestionario c) {
        String sql = "UPDATE cuestionario SET estado = ?, cantidad_accesos = ?, ultimo_ingreso = ?, "
                   + "fecha_hora_inicio = ?, fecha_hora_fin = ?, fecha_hora_cambio_estado = ? "
                   + "WHERE id_cuestionario = ?";
        try (Connection con = conexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombreEstado().name());
            ps.setInt(2, c.getCantidadAccesos());
            ps.setTimestamp(3, aTimestamp(c.getUltimoIngreso()));
            ps.setTimestamp(4, aTimestamp(c.getFechaHoraInicio()));
            ps.setTimestamp(5, aTimestamp(c.getFechaHoraFin()));
            ps.setTimestamp(6, aTimestamp(c.getFechaHoraCambioEstado()));
            ps.setObject(7, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el cuestionario", e);
        }
    }

    //helpers (privados, del issue #14)

    private Cuestionario mapearCuestionario(ResultSet rs) throws SQLException {
        ConfiguracionCuestionario config = new ConfiguracionCuestionario(
                rs.getObject("id_configuracion", UUID.class),
                rs.getString("instrucciones"),
                rs.getInt("cantidad_preguntas_por_bloque"),
                rs.getInt("maximo_accesos"),
                Duration.ofSeconds(rs.getLong("tiempo_maximo")),
                Duration.ofSeconds(rs.getLong("tiempo_maximo_activo")));

        Candidato candidato = new Candidato(
                rs.getInt("nro_candidato"),
                rs.getString("apellido"),
                rs.getString("nombre"),
                TipoDocumento.valueOf(rs.getString("tipo_documento")),
                rs.getString("nro_documento"),
                rs.getDate("fecha_nacimiento").toLocalDate(),
                Genero.valueOf(rs.getString("genero")),
                rs.getString("nacionalidad"),
                rs.getString("email"),
                rs.getString("escolaridad"),
                rs.getBoolean("eliminado"));

        // Los bloques y resultados se cargan en #16 y #17 (ver nota).
        return new Cuestionario(
                rs.getObject("id_cuestionario", UUID.class),
                rs.getString("clave"),
                estadoDesdeNombre(NombreEstadoCuestionario.valueOf(rs.getString("estado"))),
                aLocalDateTime(rs.getTimestamp("fecha_creacion")),
                aLocalDateTime(rs.getTimestamp("fecha_hora_inicio")),
                aLocalDateTime(rs.getTimestamp("fecha_hora_fin")),
                aLocalDateTime(rs.getTimestamp("ultimo_ingreso")),
                rs.getInt("cantidad_accesos"),
                aLocalDateTime(rs.getTimestamp("fecha_hora_cambio_estado")),
                candidato,
                config,
                new ArrayList<>(),
                new ArrayList<>());
    }

    private EstadoCuestionario estadoDesdeNombre(NombreEstadoCuestionario nombre) {
        switch (nombre) {
            case ACTIVO:        return new EstadoActivo();
            case EN_PROCESO:    return new EstadoEnProceso();
            case COMPLETO:      return new EstadoCompleto();
            case INCOMPLETO:    return new EstadoIncompleto();
            case SIN_CONTESTAR: return new EstadoSinContestar();
            default: throw new IllegalArgumentException("Estado desconocido: " + nombre);
        }
    }

    private static Timestamp aTimestamp(LocalDateTime t) {
        return t == null ? null : Timestamp.valueOf(t);
    }

    private static LocalDateTime aLocalDateTime(Timestamp t) {
        return t == null ? null : t.toLocalDateTime();
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
