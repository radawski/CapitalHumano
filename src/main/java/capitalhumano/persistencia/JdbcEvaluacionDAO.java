package capitalhumano.persistencia;

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

    public JdbcEvaluacionDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public void crearEvaluacion(Evaluacion evaluacion) {
        throw new UnsupportedOperationException("TODO #12");
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
