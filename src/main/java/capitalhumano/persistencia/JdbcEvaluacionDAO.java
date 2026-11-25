package capitalhumano.persistencia;

import capitalhumano.dominio.BloqueCuestionario;
import capitalhumano.dominio.Candidato;
import capitalhumano.dominio.Cuestionario;
import capitalhumano.dominio.Evaluacion;
import capitalhumano.dominio.Puesto;
import capitalhumano.dominio.ResultadoCompetencia;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class JdbcEvaluacionDAO implements EvaluacionDAO {

    private final ConexionBD conexionBD;

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
        throw new UnsupportedOperationException("TODO #14");
    }

    @Override
    public Cuestionario obtenerCuestionario(UUID id) {
        throw new UnsupportedOperationException("TODO #14");
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
    public void actualizarCuestionario(Cuestionario cuestionario) {
        throw new UnsupportedOperationException("TODO #14");
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
