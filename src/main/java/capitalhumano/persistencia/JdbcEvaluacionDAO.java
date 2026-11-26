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

import capitalhumano.dominio.Competencia;
import capitalhumano.dominio.Factor;
import capitalhumano.dominio.OpcionDeRespuesta;
import capitalhumano.dominio.PonderacionPuesto;
import capitalhumano.dominio.Pregunta;
import capitalhumano.dominio.PreguntaSeleccionada;
import capitalhumano.dominio.ResultadoFactor;
import capitalhumano.dominio.ValorOpcion;
import capitalhumano.enumeraciones.TipoCompetencia;
import capitalhumano.enumeraciones.TipoPregunta;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JdbcEvaluacionDAO implements EvaluacionDAO {

    private final ConexionBD conexionBD;

    private static final String SELECT_CUESTIONARIO =
            "SELECT c.id_cuestionario, c.id_evaluacion, c.clave, c.estado, c.fecha_creacion, c.fecha_hora_inicio, "
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

    /**
     * Carga el cuestionario completo (#14–#17): datos propios, configuración, candidato,
     * evaluación con su puesto y competencias, bloques con sus preguntas seleccionadas
     * (cada pregunta con factor, competencia, opción, valores y ponderaciones) y resultados.
     */
    @Override
    public Cuestionario obtenerCuestionario(UUID id) {
        String sql = SELECT_CUESTIONARIO + "WHERE c.id_cuestionario = ?";
        try (Connection con = conexionBD.getConexion()) {
            Cuestionario base;
            UUID idEvaluacion;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setObject(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    base = mapearCuestionario(rs);
                    idEvaluacion = rs.getObject("id_evaluacion", UUID.class);
                }
            }

            CatalogoCU026 catalogo = new CatalogoCU026();
            List<BloqueCuestionario> bloques = cargarBloques(con, id, catalogo);
            List<ResultadoCompetencia> resultados = cargarResultados(con, id, catalogo);

            Cuestionario cuestionario = new Cuestionario(
                    base.getId(), base.getClave(), mapearEstadoCuestionario(base.getNombreEstado().name()),
                    base.getFechaCreacion(), base.getFechaHoraInicio(), base.getFechaHoraFin(),
                    base.getUltimoIngreso(), base.getCantidadAccesos(), base.getFechaHoraCambioEstado(),
                    base.getCandidato(), base.getConfiguracion(), bloques, resultados);

            // La evaluación se arma con este cuestionario: el constructor fija la navegabilidad inversa
            cargarEvaluacion(con, idEvaluacion, cuestionario, catalogo);
            return cuestionario;
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

    /**
     * Paso 3 de CU026: persiste bloques, preguntas seleccionadas, fecha de inicio y estado
     * en una sola transacción, para que al volver el cuestionario se vea idéntico.
     */
    @Override
    public void guardarCuestionario(Cuestionario cuestionario) {
        String sqlBloque = "INSERT INTO bloque_cuestionario (id_cuestionario, nro_bloque, cantidad_preguntas, contestado) "
                         + "VALUES (?, ?, ?, ?)";
        String sqlPregunta = "INSERT INTO pregunta_seleccionada (id_cuestionario, nro_orden, nro_bloque, cod_pregunta, "
                           + "id_valor_opcion, fecha_hora_respuesta) VALUES (?, ?, ?, ?, ?, ?)";
        enTransaccion("Error al guardar el cuestionario", con -> {
            try (PreparedStatement psBloque = con.prepareStatement(sqlBloque);
                 PreparedStatement psPregunta = con.prepareStatement(sqlPregunta)) {
                for (BloqueCuestionario bloque : cuestionario.getBloques()) {
                    psBloque.setObject(1, cuestionario.getId());
                    psBloque.setInt(2, bloque.getNroBloque());
                    psBloque.setInt(3, bloque.getPreguntas().size());
                    psBloque.setBoolean(4, bloque.estaContestado());
                    psBloque.addBatch();
                    for (PreguntaSeleccionada seleccionada : bloque.getPreguntas()) {
                        psPregunta.setObject(1, cuestionario.getId());
                        psPregunta.setInt(2, seleccionada.getOrden());
                        psPregunta.setInt(3, bloque.getNroBloque());
                        psPregunta.setObject(4, seleccionada.getPregunta().getId());
                        psPregunta.setObject(5, seleccionada.getValorElegido() == null
                                ? null : seleccionada.getValorElegido().getId());
                        psPregunta.setTimestamp(6, aTimestamp(seleccionada.getFechaHora()));
                        psPregunta.addBatch();
                    }
                }
                psBloque.executeBatch();
                psPregunta.executeBatch();
            }
            actualizarCuestionario(con, cuestionario);
        });
    }

    @Override
    public void actualizarCuestionario(Cuestionario c) {
        try (Connection con = conexionBD.getConexion()) {
            actualizarCuestionario(con, c);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el cuestionario", e);
        }
    }

    private void actualizarCuestionario(Connection con, Cuestionario c) throws SQLException {
        String sql = "UPDATE cuestionario SET estado = ?, cantidad_accesos = ?, ultimo_ingreso = ?, "
                   + "fecha_hora_inicio = ?, fecha_hora_fin = ?, fecha_hora_cambio_estado = ? "
                   + "WHERE id_cuestionario = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombreEstado().name());
            ps.setInt(2, c.getCantidadAccesos());
            ps.setTimestamp(3, aTimestamp(c.getUltimoIngreso()));
            ps.setTimestamp(4, aTimestamp(c.getFechaHoraInicio()));
            ps.setTimestamp(5, aTimestamp(c.getFechaHoraFin()));
            ps.setTimestamp(6, aTimestamp(c.getFechaHoraCambioEstado()));
            ps.setObject(7, c.getId());
            ps.executeUpdate();
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

    /** Paso 7 de CU026: guarda las respuestas del bloque y lo marca contestado. */
    @Override
    public void guardarBloque(Cuestionario cuestionario, BloqueCuestionario bloque) {
        String sqlPregunta = "UPDATE pregunta_seleccionada SET id_valor_opcion = ?, fecha_hora_respuesta = ? "
                           + "WHERE id_cuestionario = ? AND nro_orden = ?";
        String sqlBloque = "UPDATE bloque_cuestionario SET contestado = ? WHERE id_cuestionario = ? AND nro_bloque = ?";
        enTransaccion("Error al guardar el bloque", con -> {
            try (PreparedStatement psPregunta = con.prepareStatement(sqlPregunta);
                 PreparedStatement psBloque = con.prepareStatement(sqlBloque)) {
                for (PreguntaSeleccionada seleccionada : bloque.getPreguntas()) {
                    psPregunta.setObject(1, seleccionada.getValorElegido() == null
                            ? null : seleccionada.getValorElegido().getId());
                    psPregunta.setTimestamp(2, aTimestamp(seleccionada.getFechaHora()));
                    psPregunta.setObject(3, cuestionario.getId());
                    psPregunta.setInt(4, seleccionada.getOrden());
                    psPregunta.addBatch();
                }
                psPregunta.executeBatch();
                psBloque.setBoolean(1, bloque.estaContestado());
                psBloque.setObject(2, cuestionario.getId());
                psBloque.setInt(3, bloque.getNroBloque());
                psBloque.executeUpdate();
            }
        });
    }

    /**
     * Paso 8 de CU026: guarda los resultados por competencia y por factor y, en la misma
     * transacción, el estado COMPLETO y la fecha de fin del cuestionario.
     */
    @Override
    public void guardarResultados(Cuestionario cuestionario, List<ResultadoCompetencia> resultados) {
        String sqlCompetencia = "INSERT INTO resultado_competencia (id_cuestionario, cod_competencia, puntaje_obtenido) "
                              + "VALUES (?, ?, ?)";
        String sqlFactor = "INSERT INTO resultado_factor (id_cuestionario, cod_competencia, cod_factor, puntaje_obtenido) "
                         + "VALUES (?, ?, ?, ?)";
        enTransaccion("Error al guardar los resultados del cuestionario", con -> {
            try (PreparedStatement psCompetencia = con.prepareStatement(sqlCompetencia);
                 PreparedStatement psFactor = con.prepareStatement(sqlFactor)) {
                for (ResultadoCompetencia resultado : resultados) {
                    String codCompetencia = resultado.getCompetencia().getCodigo();
                    psCompetencia.setObject(1, cuestionario.getId());
                    psCompetencia.setString(2, codCompetencia);
                    psCompetencia.setBigDecimal(3, aPuntaje(resultado.getPuntaje()));
                    psCompetencia.addBatch();
                    for (ResultadoFactor resultadoFactor : resultado.getResultadosFactor()) {
                        psFactor.setObject(1, cuestionario.getId());
                        psFactor.setString(2, codCompetencia);
                        psFactor.setString(3, resultadoFactor.getFactor().getCodigo());
                        psFactor.setBigDecimal(4, aPuntaje(resultadoFactor.getPuntaje()));
                        psFactor.addBatch();
                    }
                }
                psCompetencia.executeBatch();
                psFactor.executeBatch();
            }
            actualizarCuestionario(con, cuestionario);
        });
    }

    // ---------------------------------------------------------------------------------
    // Helpers de CU026 (#15–#17): transacción y carga del cuestionario completo
    // ---------------------------------------------------------------------------------

    @FunctionalInterface
    private interface TrabajoJdbc {
        void ejecutar(Connection con) throws SQLException;
    }

    private void enTransaccion(String mensajeError, TrabajoJdbc trabajo) {
        try (Connection con = conexionBD.getConexion()) {
            boolean autoCommitOriginal = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                trabajo.ejecutar(con);
                con.commit();
            } catch (SQLException | RuntimeException e) {
                try {
                    con.rollback();
                } catch (SQLException rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }
                throw e;
            } finally {
                con.setAutoCommit(autoCommitOriginal);
            }
        } catch (SQLException e) {
            throw new RuntimeException(mensajeError, e);
        }
    }

    private static BigDecimal aPuntaje(float puntaje) {
        return BigDecimal.valueOf(puntaje).setScale(2, RoundingMode.HALF_UP);
    }

    /** Cache de catálogo para que la misma competencia, factor u opción sea un único objeto. */
    private static final class CatalogoCU026 {
        final Map<String, Competencia> competencias = new HashMap<>();
        final Map<String, Factor> factores = new HashMap<>();
        final Map<String, OpcionDeRespuesta> opciones = new HashMap<>();
        final Map<UUID, Pregunta> preguntas = new HashMap<>();
    }

    private Competencia cargarCompetencia(Connection con, String codigo, CatalogoCU026 catalogo) throws SQLException {
        Competencia cacheada = catalogo.competencias.get(codigo);
        if (cacheada != null) {
            return cacheada;
        }
        String sql = "SELECT cod_competencia, nombre, descripcion, tipo, activa FROM competencia WHERE cod_competencia = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalStateException("No existe la competencia " + codigo);
                }
                Competencia competencia = new Competencia(rs.getString("cod_competencia"), rs.getString("nombre"),
                        rs.getString("descripcion"), TipoCompetencia.valueOf(rs.getString("tipo")),
                        rs.getBoolean("activa"));
                catalogo.competencias.put(codigo, competencia);
                return competencia;
            }
        }
    }

    private Factor cargarFactor(Connection con, String codigo, CatalogoCU026 catalogo) throws SQLException {
        Factor cacheado = catalogo.factores.get(codigo);
        if (cacheado != null) {
            return cacheado;
        }
        String sql = "SELECT cod_factor, cod_competencia, nombre, descripcion, nro_orden, activo FROM factor WHERE cod_factor = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalStateException("No existe el factor " + codigo);
                }
                String codCompetencia = rs.getString("cod_competencia");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                int nroOrden = rs.getInt("nro_orden");
                boolean activo = rs.getBoolean("activo");
                Factor factor = new Factor(codigo, nombre, descripcion, nroOrden, activo,
                        cargarCompetencia(con, codCompetencia, catalogo));
                catalogo.factores.put(codigo, factor);
                return factor;
            }
        }
    }

    private OpcionDeRespuesta cargarOpcion(Connection con, String codigo, CatalogoCU026 catalogo) throws SQLException {
        OpcionDeRespuesta cacheada = catalogo.opciones.get(codigo);
        if (cacheada != null) {
            return cacheada;
        }
        String sqlOpcion = "SELECT cod_opcion, nombre, descripcion, activa FROM opcion_de_respuesta WHERE cod_opcion = ?";
        String sqlValores = "SELECT id_valor_opcion, orden, descripcion, activo FROM valor_opcion "
                          + "WHERE cod_opcion = ? ORDER BY orden";
        List<ValorOpcion> valores = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sqlValores)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    valores.add(new ValorOpcion(rs.getObject("id_valor_opcion", UUID.class), rs.getInt("orden"),
                            rs.getString("descripcion"), rs.getBoolean("activo")));
                }
            }
        }
        try (PreparedStatement ps = con.prepareStatement(sqlOpcion)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalStateException("No existe la opción de respuesta " + codigo);
                }
                OpcionDeRespuesta opcion = new OpcionDeRespuesta(codigo, rs.getString("nombre"),
                        rs.getString("descripcion"), rs.getBoolean("activa"), valores);
                catalogo.opciones.put(codigo, opcion);
                return opcion;
            }
        }
    }

    private Pregunta cargarPregunta(Connection con, UUID codPregunta, CatalogoCU026 catalogo) throws SQLException {
        Pregunta cacheada = catalogo.preguntas.get(codPregunta);
        if (cacheada != null) {
            return cacheada;
        }
        String sqlPregunta = "SELECT cod_pregunta, cod_factor, cod_opcion, nombre, enunciado, descripcion, "
                           + "tipo_pregunta, activa FROM pregunta WHERE cod_pregunta = ?";
        String sqlPonderaciones = "SELECT pvo.id_valor_opcion, pvo.ponderacion FROM pregunta_valor_opcion pvo "
                                + "WHERE pvo.cod_pregunta = ?";
        String codFactor;
        String codOpcion;
        String nombre;
        String enunciado;
        String descripcion;
        TipoPregunta tipo;
        boolean activa;
        try (PreparedStatement ps = con.prepareStatement(sqlPregunta)) {
            ps.setObject(1, codPregunta);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalStateException("No existe la pregunta " + codPregunta);
                }
                codFactor = rs.getString("cod_factor");
                codOpcion = rs.getString("cod_opcion");
                nombre = rs.getString("nombre");
                enunciado = rs.getString("enunciado");
                descripcion = rs.getString("descripcion");
                tipo = TipoPregunta.valueOf(rs.getString("tipo_pregunta"));
                activa = rs.getBoolean("activa");
            }
        }
        Factor factor = cargarFactor(con, codFactor, catalogo);
        OpcionDeRespuesta opcion = cargarOpcion(con, codOpcion, catalogo);

        Map<ValorOpcion, Double> ponderaciones = new LinkedHashMap<>();
        try (PreparedStatement ps = con.prepareStatement(sqlPonderaciones)) {
            ps.setObject(1, codPregunta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ponderaciones.put(buscarValor(opcion, rs.getObject("id_valor_opcion", UUID.class)),
                            rs.getDouble("ponderacion"));
                }
            }
        }
        Pregunta pregunta = new Pregunta(codPregunta, nombre, enunciado, descripcion, tipo, activa,
                factor, opcion, ponderaciones);
        catalogo.preguntas.put(codPregunta, pregunta);
        return pregunta;
    }

    private static ValorOpcion buscarValor(OpcionDeRespuesta opcion, UUID idValor) {
        for (ValorOpcion valor : opcion.getValores()) {
            if (valor.getId().equals(idValor)) {
                return valor;
            }
        }
        throw new IllegalStateException("El valor " + idValor + " no pertenece a la opción " + opcion.getCodigo());
    }

    private List<BloqueCuestionario> cargarBloques(Connection con, UUID idCuestionario, CatalogoCU026 catalogo)
            throws SQLException {
        String sqlBloques = "SELECT nro_bloque, contestado FROM bloque_cuestionario "
                          + "WHERE id_cuestionario = ? ORDER BY nro_bloque";
        String sqlPreguntas = "SELECT nro_orden, nro_bloque, cod_pregunta, id_valor_opcion, fecha_hora_respuesta "
                            + "FROM pregunta_seleccionada WHERE id_cuestionario = ? ORDER BY nro_orden";

        Map<Integer, Boolean> contestados = new LinkedHashMap<>();
        try (PreparedStatement ps = con.prepareStatement(sqlBloques)) {
            ps.setObject(1, idCuestionario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contestados.put(rs.getInt("nro_bloque"), rs.getBoolean("contestado"));
                }
            }
        }

        record Fila(int orden, int nroBloque, UUID codPregunta, UUID idValor, LocalDateTime fechaHora) { }
        List<Fila> filas = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sqlPreguntas)) {
            ps.setObject(1, idCuestionario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Fila(rs.getInt("nro_orden"), rs.getInt("nro_bloque"),
                            rs.getObject("cod_pregunta", UUID.class), rs.getObject("id_valor_opcion", UUID.class),
                            aLocalDateTime(rs.getTimestamp("fecha_hora_respuesta"))));
                }
            }
        }

        Map<Integer, List<PreguntaSeleccionada>> porBloque = new LinkedHashMap<>();
        contestados.keySet().forEach(nro -> porBloque.put(nro, new ArrayList<>()));
        for (Fila fila : filas) {
            Pregunta pregunta = cargarPregunta(con, fila.codPregunta(), catalogo);
            ValorOpcion elegido = fila.idValor() == null ? null : buscarValor(pregunta.getOpcion(), fila.idValor());
            porBloque.computeIfAbsent(fila.nroBloque(), nro -> new ArrayList<>())
                    .add(new PreguntaSeleccionada(pregunta, fila.orden(), elegido, fila.fechaHora()));
        }

        List<BloqueCuestionario> bloques = new ArrayList<>();
        porBloque.forEach((nro, preguntas) ->
                bloques.add(new BloqueCuestionario(nro, contestados.getOrDefault(nro, false), preguntas)));
        return bloques;
    }

    private List<ResultadoCompetencia> cargarResultados(Connection con, UUID idCuestionario, CatalogoCU026 catalogo)
            throws SQLException {
        String sqlCompetencias = "SELECT cod_competencia, puntaje_obtenido FROM resultado_competencia "
                               + "WHERE id_cuestionario = ? ORDER BY cod_competencia";
        String sqlFactores = "SELECT cod_competencia, cod_factor, puntaje_obtenido FROM resultado_factor "
                           + "WHERE id_cuestionario = ? ORDER BY cod_competencia, cod_factor";

        Map<String, Float> puntajes = new LinkedHashMap<>();
        try (PreparedStatement ps = con.prepareStatement(sqlCompetencias)) {
            ps.setObject(1, idCuestionario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    puntajes.put(rs.getString("cod_competencia"), rs.getFloat("puntaje_obtenido"));
                }
            }
        }
        Map<String, List<String[]>> factoresPorCompetencia = new LinkedHashMap<>();
        try (PreparedStatement ps = con.prepareStatement(sqlFactores)) {
            ps.setObject(1, idCuestionario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    factoresPorCompetencia.computeIfAbsent(rs.getString("cod_competencia"), c -> new ArrayList<>())
                            .add(new String[] {rs.getString("cod_factor"), rs.getString("puntaje_obtenido")});
                }
            }
        }

        List<ResultadoCompetencia> resultados = new ArrayList<>();
        for (Map.Entry<String, Float> entrada : puntajes.entrySet()) {
            List<ResultadoFactor> resultadosFactor = new ArrayList<>();
            for (String[] factor : factoresPorCompetencia.getOrDefault(entrada.getKey(), List.of())) {
                resultadosFactor.add(new ResultadoFactor(cargarFactor(con, factor[0], catalogo),
                        Float.parseFloat(factor[1])));
            }
            resultados.add(new ResultadoCompetencia(cargarCompetencia(con, entrada.getKey(), catalogo),
                    entrada.getValue(), resultadosFactor));
        }
        return resultados;
    }

    private void cargarEvaluacion(Connection con, UUID idEvaluacion, Cuestionario cuestionario, CatalogoCU026 catalogo)
            throws SQLException {
        String sqlEvaluacion = "SELECT e.id_evaluacion, e.fecha_creacion, e.activa, "
                             + "p.cod_puesto, p.nombre, p.descripcion, p.empresa, p.activo "
                             + "FROM evaluacion e JOIN puesto p ON p.cod_puesto = e.cod_puesto "
                             + "WHERE e.id_evaluacion = ?";
        String sqlCompetencias = "SELECT cod_competencia, ponderacion_minima FROM puesto_competencia "
                               + "WHERE cod_puesto = ? ORDER BY cod_competencia";
        try (PreparedStatement ps = con.prepareStatement(sqlEvaluacion)) {
            ps.setObject(1, idEvaluacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalStateException("No existe la evaluación " + idEvaluacion);
                }
                String codPuesto = rs.getString("cod_puesto");
                Map<String, Double> minimos = new LinkedHashMap<>();
                try (PreparedStatement psComp = con.prepareStatement(sqlCompetencias)) {
                    psComp.setString(1, codPuesto);
                    try (ResultSet rsComp = psComp.executeQuery()) {
                        while (rsComp.next()) {
                            minimos.put(rsComp.getString("cod_competencia"), rsComp.getDouble("ponderacion_minima"));
                        }
                    }
                }
                Map<Competencia, PonderacionPuesto> ponderaciones = new LinkedHashMap<>();
                for (Map.Entry<String, Double> minimo : minimos.entrySet()) {
                    ponderaciones.put(cargarCompetencia(con, minimo.getKey(), catalogo),
                            new PonderacionPuesto(minimo.getValue()));
                }
                Puesto puesto = new Puesto(codPuesto, rs.getString("nombre"), rs.getString("descripcion"),
                        rs.getString("empresa"), rs.getBoolean("activo"), ponderaciones);
                new Evaluacion(idEvaluacion, rs.getDate("fecha_creacion").toLocalDate(), rs.getBoolean("activa"),
                        puesto, List.of(cuestionario));
            }
        }
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

    /** CU028 paso 4: candidatos distintos con cuestionario en alguna evaluación del puesto. */
    @Override
    public int contarCandidatosPorPuesto(Puesto puesto) {
        String sql = "SELECT COUNT(DISTINCT c.nro_candidato) FROM cuestionario c "
                + "JOIN evaluacion e ON e.id_evaluacion = c.id_evaluacion WHERE e.cod_puesto = ?";
        return contarPorPuesto(sql, puesto, "Error al contar los candidatos del puesto");
    }

    /** CU028 paso 4: cuestionarios en estado COMPLETO de las evaluaciones del puesto. */
    @Override
    public int contarCuestionariosCompletosPorPuesto(Puesto puesto) {
        String sql = "SELECT COUNT(*) FROM cuestionario c "
                + "JOIN evaluacion e ON e.id_evaluacion = c.id_evaluacion "
                + "WHERE e.cod_puesto = ? AND c.estado = 'COMPLETO'";
        return contarPorPuesto(sql, puesto, "Error al contar los cuestionarios completos del puesto");
    }

    private int contarPorPuesto(String sql, Puesto puesto, String mensajeError) {
        try (Connection con = conexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, puesto.getCodigo());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(mensajeError, e);
        }
    }

    @Override
    public List<Cuestionario> obtenerCuestionariosCompletosPorPuesto(Puesto puesto) {
        throw new UnsupportedOperationException("Fuera del alcance de la entrega final (CU007)");
    }
}
