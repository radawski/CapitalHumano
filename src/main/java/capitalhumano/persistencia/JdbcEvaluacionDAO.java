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
import capitalhumano.dominio.ConfiguracionCuestionario;
import capitalhumano.dominio.Cuestionario;
import capitalhumano.dominio.EstadoActivo;
import capitalhumano.dominio.EstadoCompleto;
import capitalhumano.dominio.EstadoCuestionario;
import capitalhumano.dominio.EstadoEnProceso;
import capitalhumano.dominio.EstadoIncompleto;
import capitalhumano.dominio.EstadoSinContestar;
import capitalhumano.dominio.Evaluacion;
import capitalhumano.dominio.Puesto;
import capitalhumano.dominio.ResultadoCompetencia;
import capitalhumano.enumeraciones.NombreEstadoCuestionario;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        if (evaluacion == null) {
            throw new IllegalArgumentException("La evaluación no puede ser nula");
        }
        if (evaluacion.getPuesto() == null || evaluacion.getPuesto().getCodigo() == null) {
            throw new IllegalArgumentException("La evaluación debe tener un puesto asociado");
        }
        if (evaluacion.getCuestionarios() == null || evaluacion.getCuestionarios().isEmpty()) {
            throw new IllegalArgumentException("La evaluación debe tener al menos un cuestionario");
        }

        String sqlConfig = "SELECT id_configuracion FROM configuracion_cuestionario LIMIT 1";
        String sqlEvaluacion = """
                INSERT INTO evaluacion (id_evaluacion, cod_puesto, fecha_creacion, activa)
                VALUES (?, ?, ?, ?)
                """;
        String sqlCuestionario = """
                INSERT INTO cuestionario (
                    id_cuestionario, id_evaluacion, nro_candidato, id_configuracion, clave, estado,
                    fecha_hora_inicio, fecha_hora_fin, ultimo_ingreso, cantidad_accesos
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = conexionBD.getConexion()) {
            boolean autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                UUID idConfiguracion = obtenerIdConfiguracion(conn, sqlConfig);

                try (PreparedStatement psEval = conn.prepareStatement(sqlEvaluacion)) {
                    psEval.setObject(1, evaluacion.getId());
                    psEval.setString(2, evaluacion.getPuesto().getCodigo());
                    psEval.setDate(3, Date.valueOf(evaluacion.getFechaCreacion()));
                    psEval.setBoolean(4, evaluacion.isActiva());
                    psEval.executeUpdate();
                }

                try (PreparedStatement psCuest = conn.prepareStatement(sqlCuestionario)) {
                    for (Cuestionario cuestionario : evaluacion.getCuestionarios()) {
                        if (cuestionario.getCandidato() == null) {
                            throw new IllegalArgumentException("Cada cuestionario debe tener un candidato asociado");
                        }
                        psCuest.setObject(1, cuestionario.getId());
                        psCuest.setObject(2, evaluacion.getId());
                        psCuest.setInt(3, cuestionario.getCandidato().getNroCandidato());
                        psCuest.setObject(4, idConfiguracion);
                        psCuest.setString(5, cuestionario.getClave());
                        psCuest.setString(6, cuestionario.getNombreEstado().name());
                        psCuest.setTimestamp(7, aTimestamp(cuestionario.getFechaHoraInicio()));
                        psCuest.setTimestamp(8, aTimestamp(cuestionario.getFechaHoraFin()));
                        psCuest.setTimestamp(9, aTimestamp(cuestionario.getUltimoIngreso()));
                        psCuest.setInt(10, cuestionario.getCantidadAccesos());
                        psCuest.addBatch();
                    }
                    psCuest.executeBatch();
                }

                conn.commit();
            } catch (SQLException | RuntimeException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }
                throw e;
            } finally {
                conn.setAutoCommit(autoCommitOriginal);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear la evaluación en la base de datos", e);
        }
    }

    private static UUID obtenerIdConfiguracion(Connection conn, String sqlConfig) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sqlConfig);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                throw new IllegalStateException("No existe configuración de cuestionario vigente en la base de datos");
            }
            return rs.getObject("id_configuracion", UUID.class);
        }
    }

    private static Timestamp aTimestamp(LocalDateTime fechaHora) {
        return fechaHora != null ? Timestamp.valueOf(fechaHora) : null;
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
        if (candidato == null) {
            return List.of();
        }
        String sql = """
                SELECT c.id_cuestionario, c.clave, c.estado, c.fecha_creacion,
                       c.fecha_hora_inicio, c.fecha_hora_fin, c.ultimo_ingreso,
                       c.cantidad_accesos, c.fecha_hora_cambio_estado,
                       cfg.id_configuracion, cfg.instrucciones, cfg.cantidad_preguntas_por_bloque,
                       cfg.maximo_accesos, cfg.tiempo_maximo, cfg.tiempo_maximo_activo
                FROM cuestionario c
                JOIN configuracion_cuestionario cfg ON cfg.id_configuracion = c.id_configuracion
                WHERE c.nro_candidato = ?
                  AND c.estado IN ('ACTIVO', 'EN_PROCESO')
                ORDER BY c.fecha_creacion DESC
                """;
        List<Cuestionario> cuestionarios = new ArrayList<>();
        try (Connection conn = conexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, candidato.getNroCandidato());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ConfiguracionCuestionario configuracion = new ConfiguracionCuestionario(
                            rs.getObject("id_configuracion", UUID.class),
                            rs.getString("instrucciones"),
                            rs.getInt("cantidad_preguntas_por_bloque"),
                            rs.getInt("maximo_accesos"),
                            Duration.ofSeconds(rs.getLong("tiempo_maximo")),
                            Duration.ofSeconds(rs.getLong("tiempo_maximo_activo"))
                    );
                    EstadoCuestionario estado = mapearEstadoCuestionario(rs.getString("estado"));
                    cuestionarios.add(new Cuestionario(
                            rs.getObject("id_cuestionario", UUID.class),
                            rs.getString("clave"),
                            estado,
                            aLocalDateTime(rs.getTimestamp("fecha_creacion")),
                            aLocalDateTime(rs.getTimestamp("fecha_hora_inicio")),
                            aLocalDateTime(rs.getTimestamp("fecha_hora_fin")),
                            aLocalDateTime(rs.getTimestamp("ultimo_ingreso")),
                            rs.getInt("cantidad_accesos"),
                            aLocalDateTime(rs.getTimestamp("fecha_hora_cambio_estado")),
                            candidato,
                            configuracion,
                            List.of(),
                            List.of()
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al obtener cuestionarios activos o en proceso para el candidato "
                            + candidato.getNroCandidato(), e);
        }
        return cuestionarios;
    }

    private static EstadoCuestionario mapearEstadoCuestionario(String estado) {
        return switch (NombreEstadoCuestionario.valueOf(estado)) {
            case ACTIVO -> new EstadoActivo();
            case EN_PROCESO -> new EstadoEnProceso();
            case COMPLETO -> new EstadoCompleto();
            case INCOMPLETO -> new EstadoIncompleto();
            case SIN_CONTESTAR -> new EstadoSinContestar();
        };
    }

    private static LocalDateTime aLocalDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
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
